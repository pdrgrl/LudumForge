package dam.a51319.ludumforge.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
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

// Sealed class representing the different states of the AI Generation process
sealed class RoadmapUiState {
    object Idle : RoadmapUiState()
    object Loading : RoadmapUiState()
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

    // Pass the API Key and Premium status from the UI
    fun onGenerateClicked(userApiKey: String, isPremium: Boolean) {
        if (gameTitle.value.isBlank()) {
            _uiState.value = RoadmapUiState.Error("Project vision cannot be empty.")
            return
        }

        // Tier Check Logic
        val finalApiKey = if (isPremium) {
            "YOUR_APP_INTERNAL_PREMIUM_API_KEY" // Replace with your actual paid key later
        } else {
            userApiKey
        }

        if (finalApiKey.isBlank()) {
            _uiState.value = RoadmapUiState.Error("Free users must add a Gemini API Key in Settings.")
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

                // ROBUST JSON EXTRACTION: Find the first '[' and the last ']'
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

                    // Safely parse the category with a fallback to CODE
                    val categoryString = obj.optString("category", "CODE").uppercase()
                    val safeCategory = try {
                        TaskCategory.valueOf(categoryString)
                    } catch (e: Exception) {
                        TaskCategory.CODE // Fallback if AI hallucinates
                    }

                    val task = Task(
                        id = "ai_${i}",
                        projectId = "p1", // Hardcoded to active project
                        title = obj.getString("title"),
                        category = safeCategory, // <-- Use the safe category
                        estimatedMinutes = obj.getInt("estimatedMinutes"),
                        status = TaskStatus.TODO
                    )
                    generatedTasks.add(task)

                    // Automatically save generated tasks to Firestore!
                    taskRepository.addTask(task.projectId, task.title, task.category, task.estimatedMinutes, null)
                }

                _uiState.value = RoadmapUiState.Success(generatedTasks)

                // Log the event to Room immediately
                try {
                    val context = getApplication<Application>().applicationContext
                    val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
                    val actionRepo = ActionLogRepository(dao)
                    val user = FirebaseAuth.getInstance().currentUser
                    val userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "A developer"
                    actionRepo.addSystemEvent(
                        "p1",
                        "$userName generated AI roadmap for '${gameTitle.value}' — ${generatedTasks.size} tasks created"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } catch (e: Exception) {
                _uiState.value = RoadmapUiState.Error("AI Generation Failed: ${e.localizedMessage}")
            }
        }
    }
}