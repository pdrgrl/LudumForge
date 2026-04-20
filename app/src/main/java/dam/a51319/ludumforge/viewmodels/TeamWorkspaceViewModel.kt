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
import dam.a51319.ludumforge.data.repositories.AuthRepository
import dam.a51319.ludumforge.models.User


class TeamWorkspaceViewModel : ViewModel() {

    private val taskRepository = TaskRepository()
    private val authRepository = AuthRepository()

    private val _teamTasks = MutableStateFlow<List<Task>>(emptyList())
    val teamTasks: StateFlow<List<Task>> = _teamTasks.asStateFlow()

//    State to hold all real users
    private val _teamMembers = MutableStateFlow<List<User>>(emptyList())
    val teamMembers: StateFlow<List<User>> = _teamMembers.asStateFlow()


    // For now, hardcode the active project ID. Later, we'll pass this based on user selection.
    private val activeProjectId = "p1"

    init {
        loadTeamTasks()
        loadTeamMembers()
    }

    private fun loadTeamMembers() {
        viewModelScope.launch {
            _teamMembers.value = authRepository.getAllUsers()
        }
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
            // 1. Log it to the local Dev Log FIRST! (Immediate offline feedback)
            val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
            val actionRepo = ActionLogRepository(dao)

            // Get the username (defaulting to the first part of the email if displayName is null)
            val user = FirebaseAuth.getInstance().currentUser
            val userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "A developer"

            val logMessage = when (newStatus) {
                TaskStatus.DONE -> "🏁 $userName completed '$taskTitle'"
                TaskStatus.IN_PROGRESS -> "$userName started '$taskTitle'"
                TaskStatus.TODO -> "$userName moved '$taskTitle' back to TODO"
                else -> {"$userName updated '$taskTitle' to ${newStatus.name}"}
            }
            actionRepo.addSystemEvent(activeProjectId, logMessage)

            // 2. Update Firestore (Firebase will handle its own offline queue internally)
            try {
                taskRepository.updateTaskStatus(taskId, newStatus)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addTask(title: String, category: TaskCategory, estimatedMinutes: Int, context: Context) {
        if (title.isBlank()) return

        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            val userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "A developer"
            val currentUserId = user?.uid

            // 1. Log FIRST — works offline immediately
            val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
            val actionRepo = ActionLogRepository(dao)
            actionRepo.addSystemEvent(activeProjectId, "$userName forged task '$title' [${category.name}]")

            // 2. Then push to Firestore
            try {
                taskRepository.addTask(activeProjectId, title, category, estimatedMinutes, currentUserId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}