package dam.a51319.ludumforge.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dam.a51319.ludumforge.data.LudumForgeDatabase
import dam.a51319.ludumforge.data.SessionManager
import dam.a51319.ludumforge.data.repositories.ActionLogRepository
import dam.a51319.ludumforge.data.repositories.AuthRepository
import dam.a51319.ludumforge.data.repositories.ProjectRepository
import dam.a51319.ludumforge.data.repositories.TaskRepository
import dam.a51319.ludumforge.models.Project
import dam.a51319.ludumforge.models.Task
import dam.a51319.ludumforge.models.TaskCategory
import dam.a51319.ludumforge.models.TaskStatus
import dam.a51319.ludumforge.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class TeamWorkspaceViewModel : ViewModel() {

    private val taskRepository = TaskRepository()
    private val authRepository = AuthRepository()
    private val projectRepository = ProjectRepository()

    private val _teamTasks = MutableStateFlow<List<Task>>(emptyList())
    val teamTasks: StateFlow<List<Task>> = _teamTasks.asStateFlow()

    private val _teamMembers = MutableStateFlow<List<User>>(emptyList())
    val teamMembers: StateFlow<List<User>> = _teamMembers.asStateFlow()

    init {
        loadTeamTasks()
        observeTeamMembers()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTeamMembers() {
        viewModelScope.launch {
            SessionManager.activeJamId
                .flatMapLatest { jamId ->
                    if (jamId != null) projectRepository.listenToProject(jamId)
                    else flowOf<Project?>(null)
                }
                .collect { project ->
                    if (project == null) {
                        _teamMembers.value = emptyList()
                    } else {
                        val ids = (listOf(project.creatorId) + project.memberIds).distinct().filter { it.isNotBlank() }
                        val users = authRepository.getUsersByIds(ids).toMutableList()
                        
                        // Safety: Ensure the current user is in the list if they are part of the team
                        // This helps if the batch fetch failed for some reason (like permissions)
                        val currentUserProfile = authRepository.getUserProfile()
                        if (currentUserProfile != null && ids.contains(currentUserProfile.id)) {
                            if (users.none { it.id == currentUserProfile.id }) {
                                users.add(currentUserProfile)
                            }
                        }

                        _teamMembers.value = users
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadTeamTasks() {
        viewModelScope.launch {
            SessionManager.activeJamId
                .flatMapLatest { jamId ->
                    if (jamId != null) taskRepository.getTasksForProject(jamId)
                    else flowOf(emptyList())
                }
                .collect { tasks -> _teamTasks.value = tasks }
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
            actionRepo.addSystemEvent(currentJamId, logMessage)
            try { taskRepository.updateTaskStatus(taskId, newStatus) } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun addTask(title: String, category: TaskCategory, estimatedMinutes: Int, context: Context, assignedTo: String?) {
        val currentJamId = SessionManager.activeJamId.value ?: return
        if (title.isBlank()) return
        viewModelScope.launch {
            val user = FirebaseAuth.getInstance().currentUser
            val userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "A developer"
            val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
            val actionRepo = ActionLogRepository(dao)
            actionRepo.addSystemEvent(currentJamId, "$userName forged task '$title' [${category.name}]")
            try { taskRepository.addTask(currentJamId, title, category, estimatedMinutes, assignedTo) }
            catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun updateTask(
        taskId: String,
        title: String,
        category: TaskCategory,
        estimatedMinutes: Int,
        assignedTo: String?,
        context: Context
    ) {
        val currentJamId = SessionManager.activeJamId.value ?: return
        viewModelScope.launch {
            val updates: Map<String, Any?> = mapOf(
                "title" to title,
                "category" to category.name,
                "estimatedMinutes" to estimatedMinutes,
                "assignedTo" to assignedTo
            )
            try {
                taskRepository.updateTask(taskId, updates)
                val user = FirebaseAuth.getInstance().currentUser
                val userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "A developer"
                val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
                val actionRepo = ActionLogRepository(dao)
                actionRepo.addSystemEvent(currentJamId, "✏️ $userName edited task '$title'")
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun deleteTask(taskId: String, taskTitle: String, context: Context) {
        val currentJamId = SessionManager.activeJamId.value ?: return
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(taskId)
                val user = FirebaseAuth.getInstance().currentUser
                val userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "A developer"
                val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
                val actionRepo = ActionLogRepository(dao)
                actionRepo.addSystemEvent(currentJamId, "🗑️ $userName deleted task '$taskTitle'")
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}
