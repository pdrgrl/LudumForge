package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam.a51319.ludumforge.models.Task
import dam.a51319.ludumforge.models.TaskCategory
import dam.a51319.ludumforge.models.TaskStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

// Sealed class representing the different states of the AI Generation process
sealed class RoadmapUiState {
    object Idle : RoadmapUiState()
    object Loading : RoadmapUiState()
    data class Success(val tasks: List<Task>) : RoadmapUiState()
    data class Error(val message: String) : RoadmapUiState()
}

class RoadmapGeneratorViewModel : ViewModel() {

    // Form State mapped directly to the UI TextFields
    val gameTitle = MutableStateFlow("")
    val teamSize = MutableStateFlow("")
    val duration = MutableStateFlow("")

    // Generation UI State
    private val _uiState = MutableStateFlow<RoadmapUiState>(RoadmapUiState.Idle)
    val uiState: StateFlow<RoadmapUiState> = _uiState.asStateFlow()

    /**
     * Triggers the generation process. Handles its own coroutine scope.
     */
    fun onGenerateClicked() {
        if (gameTitle.value.isBlank()) {
            _uiState.value = RoadmapUiState.Error("Project vision cannot be empty.")
            return
        }

        viewModelScope.launch {
            _uiState.value = RoadmapUiState.Loading

            try {
                // Call the suspend stub
                val tasks = generateRoadmap()
                _uiState.value = RoadmapUiState.Success(tasks)
            } catch (e: Exception) {
                _uiState.value = RoadmapUiState.Error("Failed to generate roadmap: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Suspend stub simulating a network call to an LLM API.
     */
    private suspend fun generateRoadmap(): List<Task> {
        delay(2000L) // Simulate network latency (2 seconds)

        // Return dummy generated tasks
        return listOf(
            Task("t1", "p_new", "Setup base project repository & engine", TaskCategory.CODE, estimatedMinutes = 60, status = TaskStatus.TODO),
            Task("t2", "p_new", "Create placeholder player sprite/capsule", TaskCategory.ART, estimatedMinutes = 30, status = TaskStatus.TODO),
            Task("t3", "p_new", "Implement basic movement & physics", TaskCategory.CODE, estimatedMinutes = 120, status = TaskStatus.TODO),
            Task("t4", "p_new", "Draft core gameplay loop document", TaskCategory.DESIGN, estimatedMinutes = 90, status = TaskStatus.TODO)
        )
    }

    fun resetState() {
        _uiState.value = RoadmapUiState.Idle
    }
}