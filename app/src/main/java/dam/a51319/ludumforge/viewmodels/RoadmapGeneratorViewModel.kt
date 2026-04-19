package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
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

class RoadmapGeneratorViewModel : ViewModel() {

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
                    Generate a project roadmap as a raw JSON array. Do not use markdown blocks.
                    Format: [{"title": "Setup repository", "category": "CODE", "estimatedMinutes": 60}, ...]
                    Categories must be exactly one of: CODE, ART, AUDIO, DESIGN, QA, MARKETING.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val rawText = response.text ?: throw Exception("No response from AI.")

                // Clean the response (Gemini sometimes wraps JSON in ```json ... ```)
                val cleanJson = rawText.substringAfter("[").substringBeforeLast("]") + "]"

                val jsonArray = JSONArray(cleanJson)
                val generatedTasks = mutableListOf<Task>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val task = Task(
                        id = "ai_${i}",
                        projectId = "p1", // Hardcoded to current active project for now
                        title = obj.getString("title"),
                        category = TaskCategory.valueOf(obj.getString("category")),
                        estimatedMinutes = obj.getInt("estimatedMinutes"),
                        status = TaskStatus.TODO
                    )
                    generatedTasks.add(task)

                    // Automatically save generated tasks to Firestore!
                    taskRepository.addTask(task.projectId, task.title, task.category, task.estimatedMinutes, null)
                }

                _uiState.value = RoadmapUiState.Success(generatedTasks)
            } catch (e: Exception) {
                _uiState.value = RoadmapUiState.Error("AI Generation Failed: ${e.localizedMessage}")
            }
        }
    }
}