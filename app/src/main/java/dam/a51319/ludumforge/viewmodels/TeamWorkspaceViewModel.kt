package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import dam.a51319.ludumforge.models.Task
import dam.a51319.ludumforge.models.TaskCategory
import dam.a51319.ludumforge.models.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TeamWorkspaceViewModel : ViewModel() {

    // All tasks for the current active project
    private val _teamTasks = MutableStateFlow<List<Task>>(emptyList())
    val teamTasks: StateFlow<List<Task>> = _teamTasks.asStateFlow()

    init {
        loadTeamTasks()
    }

    private fun loadTeamTasks() {
        // Dummy data simulating a database/API fetch for the whole team
        _teamTasks.value = listOf(
            Task("t1", "p1", "Refactor shader pipeline for mobile optimization", TaskCategory.CODE, "u1,u2", 120, TaskStatus.TODO),
            Task("t3", "p1", "Implementing dynamic pathfinding for avian NPCs", TaskCategory.CODE, "u4", 240, TaskStatus.IN_PROGRESS),
            Task("t5", "p1", "Dialogue tree integration for Act 1 prologue", TaskCategory.CODE, "u6", 120, TaskStatus.DONE)
        )
    }

    // Helper function to update a task's status (e.g., Drag and Drop Kanban)
    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        _teamTasks.value = _teamTasks.value.map { task ->
            if (task.id == taskId) task.copy(status = newStatus) else task
        }
    }
}