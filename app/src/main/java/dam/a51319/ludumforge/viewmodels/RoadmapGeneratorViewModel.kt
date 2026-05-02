package dam.a51319.ludumforge.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import dam.a51319.ludumforge.data.LudumForgeDatabase
import dam.a51319.ludumforge.data.repositories.ActionLogRepository
import dam.a51319.ludumforge.data.repositories.TaskRepository
import dam.a51319.ludumforge.models.Task
import dam.a51319.ludumforge.models.TaskCategory
import dam.a51319.ludumforge.models.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import dam.a51319.ludumforge.data.SessionManager

sealed class RoadmapUiState {
    object Idle : RoadmapUiState()
    object Loading : RoadmapUiState()
    object Pushing : RoadmapUiState()
    data class Success(val tasks: List<Task>) : RoadmapUiState()
    data class Error(val message: String) : RoadmapUiState()
}

class RoadmapGeneratorViewModel(application: Application) : AndroidViewModel(application) {

    private val taskRepository = TaskRepository()

    val gameTitle = MutableStateFlow("")
    val teamSize = MutableStateFlow("")
    val duration = MutableStateFlow("")

    private val _uiState = MutableStateFlow<RoadmapUiState>(RoadmapUiState.Idle)
    val uiState: StateFlow<RoadmapUiState> = _uiState.asStateFlow()

    /**
     * [isPremium] controls whether the API key field is hidden in the UI.
     * Both tiers use the same user-saved key from SharedPreferences.
     * The premium benefit is seamless UX (no key input) — a separate
     * server-side key is out of scope for this academic build.
     */
    fun onGenerateClicked(userApiKey: String, isPremium: Boolean) {
        val currentJamId = SessionManager.activeJamId.value ?: run {
            _uiState.value = RoadmapUiState.Error("Please select an active Jam in the Planning tab first.")
            return
        }

        // Both FREE and PREMIUM use the saved key for now.
        // Premium users won't see the key input field in the UI, but the app
        // still needs the key to be set once in Settings.
        val finalApiKey = userApiKey

        if (finalApiKey.isBlank()) {
            _uiState.value = if (isPremium) {
                RoadmapUiState.Error("Add your Gemini API Key once in Settings to activate AI generation.")
            } else {
                RoadmapUiState.Error("Please add your Gemini API Key in Settings to use the Roadmap Generator.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = RoadmapUiState.Loading
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.5-flash-lite",
                    apiKey = finalApiKey
                )

                val prompt = """
                    You are an expert Game Producer. I am building a game called '${gameTitle.value}'. 
                    My team has ${teamSize.value} people and we have ${duration.value} to finish.
                    Generate a project roadmap strictly as a raw JSON array.
                    Format exactly like this: [{"title": "Setup repository", "category": "CODE", "estimatedMinutes": 60}]
                    Categories must be EXACTLY one of: CODE, ART, AUDIO, DESIGN, QA.
                    Do not return a single object. Return an array of objects. Do not include markdown formatting.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val rawText = response.text ?: throw Exception("No response from AI.")

                val startIndex = rawText.indexOf('[')
                val endIndex = rawText.lastIndexOf(']')
                if (startIndex == -1 || endIndex == -1 || startIndex > endIndex) {
                    throw Exception("AI did not return a valid JSON array.")
                }

                val cleanJson = rawText.substring(startIndex, endIndex + 1)
                val jsonArray = JSONArray(cleanJson)
                val generatedTasks = mutableListOf<Task>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val categoryString = obj.optString("category", "CODE").uppercase()
                    val safeCategory = try { TaskCategory.valueOf(categoryString) } catch (e: Exception) { TaskCategory.CODE }
                    generatedTasks.add(
                        Task(
                            id = "ai_$i",
                            projectId = currentJamId,
                            title = obj.getString("title"),
                            category = safeCategory,
                            estimatedMinutes = obj.getInt("estimatedMinutes"),
                            status = TaskStatus.TODO
                        )
                    )
                }
                _uiState.value = RoadmapUiState.Success(generatedTasks)

            } catch (e: Exception) {
                _uiState.value = RoadmapUiState.Error("AI Generation Failed: ${e.localizedMessage}")
            }
        }
    }

    fun pushSelectedTasksToWorkspace(tasks: List<Task>, context: Context) {
        val currentJamId = SessionManager.activeJamId.value ?: return
        if (tasks.isEmpty()) return
        viewModelScope.launch {
            _uiState.value = RoadmapUiState.Pushing
            tasks.forEach { task ->
                taskRepository.addTask(currentJamId, task.title, task.category, task.estimatedMinutes, null)
            }
            try {
                val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
                val actionRepo = ActionLogRepository(dao)
                val user = FirebaseAuth.getInstance().currentUser
                val userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "A developer"
                actionRepo.addSystemEvent(currentJamId, "⚡ $userName forged ${tasks.size} AI tasks into the workspace")
            } catch (e: Exception) { e.printStackTrace() }
            _uiState.value = RoadmapUiState.Idle
            gameTitle.value = ""
            teamSize.value = ""
            duration.value = ""
        }
    }

    fun discard() {
        _uiState.value = RoadmapUiState.Idle
    }
}
