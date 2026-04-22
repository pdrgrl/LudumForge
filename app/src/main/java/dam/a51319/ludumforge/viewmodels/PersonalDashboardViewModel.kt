package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dam.a51319.ludumforge.data.SessionManager
import dam.a51319.ludumforge.data.repositories.ProjectRepository
import dam.a51319.ludumforge.data.repositories.TaskRepository
import dam.a51319.ludumforge.models.Project
import dam.a51319.ludumforge.models.Task
import dam.a51319.ludumforge.models.TaskStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalDashboardViewModel : ViewModel() {

    private val taskRepository = TaskRepository()
    private val projectRepository = ProjectRepository()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Timer State
    private val _timeLeftInSeconds = MutableStateFlow(48 * 3600L)
    val timeLeftInSeconds: StateFlow<Long> = _timeLeftInSeconds.asStateFlow()

    // My Tasks State
    private val _myTasks = MutableStateFlow<List<Task>>(emptyList())
    val myTasks: StateFlow<List<Task>> = _myTasks.asStateFlow()

    // My Jams State (replaces dummyProjects!)
    private val _myJams = MutableStateFlow<List<Project>>(emptyList())
    val myJams: StateFlow<List<Project>> = _myJams.asStateFlow()

    // Completion ratio per project: projectId → 0.0f..1.0f
    private val _completionRatios = MutableStateFlow<Map<String, Float>>(emptyMap())
    val completionRatios: StateFlow<Map<String, Float>> = _completionRatios.asStateFlow()

    init {
        loadMyTasks()
        loadMyJams()
        loadAllTasksForJams()
        startTimer()
    }

    private fun loadMyJams() {
        if (currentUserId == null) return
        viewModelScope.launch {
            projectRepository.getMyJams(currentUserId).collect { jams ->
                _myJams.value = jams
            }
        }
    }

    fun createNewJam(name: String, theme: String) {
        if (currentUserId == null || name.isBlank()) return
        viewModelScope.launch {
            try {
                projectRepository.createJam(name, theme, 1, currentUserId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadMyTasks() {
        if (currentUserId == null) return
        viewModelScope.launch {
            taskRepository.getTasksForUser(currentUserId).collect { allMyTasks ->
                _myTasks.value = allMyTasks.filter { it.status != TaskStatus.DONE }
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_timeLeftInSeconds.value > 0) {
                delay(1000L)
                _timeLeftInSeconds.value -= 1
            }
        }
    }
    fun updateTaskStatus(taskId: String, newTaskStatus: TaskStatus) {
        viewModelScope.launch {
            try {
                taskRepository.updateTaskStatus(taskId, newTaskStatus)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun renameJam(projectId: String, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            try {
                projectRepository.renameJam(projectId, newName)
                // If the renamed jam is currently active, update the TopBar name too
                if (SessionManager.activeJamId.value == projectId) {
                    SessionManager.setActiveJam(projectId, newName)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun deleteJam(projectId: String) {
        viewModelScope.launch {
            try {
                projectRepository.deleteJam(projectId)
                // If the deleted jam was active, clear the session
                if (SessionManager.activeJamId.value == projectId) {
                    SessionManager.clearActiveJam()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun loadAllTasksForJams() {
        viewModelScope.launch {
            // Re-compute whenever myJams changes
            _myJams.collect { jams ->
                val ratios = mutableMapOf<String, Float>()
                jams.forEach { jam ->
                    taskRepository.getTasksForProject(jam.id).collect { tasks ->
                        if (tasks.isEmpty()) {
                            ratios[jam.id] = 0f
                        } else {
                            val done = tasks.count { it.status == TaskStatus.DONE }
                            ratios[jam.id] = done.toFloat() / tasks.size.toFloat()
                        }
                        _completionRatios.value = ratios.toMap()
                    }
                }
            }
        }
    }

}