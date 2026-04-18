package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam.a51319.ludumforge.data.repositories.TaskRepository
import dam.a51319.ludumforge.models.Task
import dam.a51319.ludumforge.models.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamWorkspaceViewModel : ViewModel() {

    private val taskRepository = TaskRepository()

    private val _teamTasks = MutableStateFlow<List<Task>>(emptyList())
    val teamTasks: StateFlow<List<Task>> = _teamTasks.asStateFlow()

    // For now, hardcode the active project ID. Later, we'll pass this based on user selection.
    private val activeProjectId = "p1"

    init {
        loadTeamTasks()
    }

    private fun loadTeamTasks() {
        viewModelScope.launch {
            // Collect the real-time Flow from Firestore
            taskRepository.getTasksForProject(activeProjectId).collect { tasks ->
                _teamTasks.value = tasks
            }
        }
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        viewModelScope.launch {
            // Updates Firestore. The snapshot listener will automatically
            // trigger and update the UI!
            taskRepository.updateTaskStatus(taskId, newStatus)
        }
    }
}