package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam.a51319.ludumforge.data.repositories.TaskRepository
import dam.a51319.ludumforge.models.Task
import dam.a51319.ludumforge.models.TaskCategory
import dam.a51319.ludumforge.models.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import android.content.Context
import dam.a51319.ludumforge.data.LudumForgeDatabase
import dam.a51319.ludumforge.data.repositories.ActionLogRepository

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

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus, taskTitle: String, context: Context) {
        viewModelScope.launch {
            // 1. Update the actual task in Firestore
            taskRepository.updateTaskStatus(taskId, newStatus)

            // 2. Log it to the Dev Log!
            val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
            val actionRepo = ActionLogRepository(dao)

            // We use the current user's name if we have it, else a generic string
            val userName = FirebaseAuth.getInstance().currentUser?.displayName ?: "A developer"
            val logMessage = "$userName moved '$taskTitle' to ${newStatus.name}"

            actionRepo.addSystemEvent(activeProjectId, logMessage)
        }
    }

    fun addTask(title: String, category: TaskCategory, estimatedMinutes: Int) {
        if (title.isBlank()) return

        viewModelScope.launch {
            // Get the current user's UID to assign the task to them
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            taskRepository.addTask(
                projectId = activeProjectId,
                title = title,
                category = category,
                estimatedMinutes = estimatedMinutes,
                assignedTo = currentUserId // <--- Pass the ID here
            )
        }
    }
}