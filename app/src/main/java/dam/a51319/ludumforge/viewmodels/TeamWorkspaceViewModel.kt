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
import dam.a51319.ludumforge.data.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf


class TeamWorkspaceViewModel : ViewModel() {

    private val taskRepository = TaskRepository()
    private val authRepository = AuthRepository()

    private val _teamTasks = MutableStateFlow<List<Task>>(emptyList())
    val teamTasks: StateFlow<List<Task>> = _teamTasks.asStateFlow()

//    State to hold all real users
    private val _teamMembers = MutableStateFlow<List<User>>(emptyList())
    val teamMembers: StateFlow<List<User>> = _teamMembers.asStateFlow()


    init {
        loadTeamTasks()
        loadTeamMembers()
    }

    private fun loadTeamMembers() {
        viewModelScope.launch {
            _teamMembers.value = authRepository.getAllUsers()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadTeamTasks() {
        viewModelScope.launch {
            // This is the magic! It listens to the SessionManager.
            // If activeJamId changes, it automatically cancels the old Firestore listener and starts a new one!
            SessionManager.activeJamId
                .flatMapLatest { jamId ->
                    if (jamId != null) {
                        taskRepository.getTasksForProject(jamId)
                    } else {
                        flowOf(emptyList()) // No jam selected, return empty tasks
                    }
                }
                .collect { tasks ->
                    _teamTasks.value = tasks
                }
        }
    }
    fun updateTaskStatus(taskId: String, newStatus: TaskStatus, taskTitle: String, context: Context) {
        val currentJamId = SessionManager.activeJamId.value ?: return

        viewModelScope.launch {
            val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
            val actionRepo = ActionLogRepository(dao)

            val user = FirebaseAuth.getInstance().currentUser
            val userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "A developer"

            val logMessage = when (newStatus) {
                TaskStatus.DONE -> "🏁 $userName completed '$taskTitle'"
                TaskStatus.IN_PROGRESS -> "$userName started '$taskTitle'"
                TaskStatus.TODO -> "$userName moved '$taskTitle' back to TODO"
                else -> "$userName moved '$taskTitle' to ${newStatus.name}"
            }
            // Use currentJamId here!
            actionRepo.addSystemEvent(currentJamId, logMessage)

            try {
                taskRepository.updateTaskStatus(taskId, newStatus)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addTask(title: String, category: TaskCategory, estimatedMinutes: Int, context: Context, assignedTo: String?) {
        val currentJamId = SessionManager.activeJamId.value ?: return
        if (title.isBlank()) return

        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            val userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "A developer"
            val currentUserId = user?.uid

            // 1. Log FIRST — works offline immediately
            val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
            val actionRepo = ActionLogRepository(dao)
            actionRepo.addSystemEvent(currentJamId, "$userName forged task '$title' [${category.name}]")

            // 2. Then push to Firestore
            try {
                taskRepository.addTask(currentJamId, title, category, estimatedMinutes, assignedTo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}