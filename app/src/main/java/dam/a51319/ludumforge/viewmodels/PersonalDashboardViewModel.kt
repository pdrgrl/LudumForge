package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dam.a51319.ludumforge.data.SessionManager
import dam.a51319.ludumforge.data.repositories.AuthRepository
import dam.a51319.ludumforge.data.repositories.ProjectRepository
import dam.a51319.ludumforge.data.repositories.TaskRepository
import dam.a51319.ludumforge.models.Project
import dam.a51319.ludumforge.models.Task
import dam.a51319.ludumforge.models.TaskStatus
import dam.a51319.ludumforge.models.UserPlan
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

class PersonalDashboardViewModel : ViewModel() {

    private val taskRepository = TaskRepository()
    private val projectRepository = ProjectRepository()
    private val authRepository = AuthRepository()

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    // ── Subscription state ──────────────────────────────────────────────────────
    private val _currentPlan = MutableStateFlow(UserPlan.FREE)
    val currentPlan: StateFlow<UserPlan> = _currentPlan.asStateFlow()

    /**
     * Derived directly from _myJams.size so it is always in sync with the
     * live Firestore stream. No separate query, no race condition.
     */
    private val _jamsThisMonth = MutableStateFlow(0)
    val jamsThisMonth: StateFlow<Int> = _jamsThisMonth.asStateFlow()

    private val _jamLimitReached = MutableSharedFlow<Unit>()
    val jamLimitReached: SharedFlow<Unit> = _jamLimitReached.asSharedFlow()

    companion object {
        const val FREE_JAM_LIMIT = 2
    }

    // ── Timer state ─────────────────────────────────────────────────────────────
    private val _timeLeftInSeconds = MutableStateFlow(48 * 3600L)
    val timeLeftInSeconds: StateFlow<Long> = _timeLeftInSeconds.asStateFlow()

    // ── Task / project state ────────────────────────────────────────────────────
    private val _myTasks = MutableStateFlow<List<Task>>(emptyList())
    val myTasks: StateFlow<List<Task>> = _myTasks.asStateFlow()

    private val _myJams = MutableStateFlow<List<Project>>(emptyList())
    val myJams: StateFlow<List<Project>> = _myJams.asStateFlow()

    private val _completionRatios = MutableStateFlow<Map<String, Float>>(emptyMap())
    val completionRatios: StateFlow<Map<String, Float>> = _completionRatios.asStateFlow()

    init {
        loadMyTasks()
        loadMyJams()
        loadAllTasksForJams()
        observeActiveJamTimer()
        refreshSubscriptionState()
    }

    /**
     * Refreshes the plan from Firestore.
     * Does NOT touch _jamsThisMonth — that is derived from _myJams automatically.
     */
    fun refreshSubscriptionState() {
        viewModelScope.launch {
            val user = authRepository.getUserProfile()
            _currentPlan.value = user?.plan ?: UserPlan.FREE
        }
    }

    suspend fun upgradeToPremium(): Result<Unit> {
        val result = authRepository.upgradeToPremium()
        if (result.isSuccess) {
            _currentPlan.value = UserPlan.PREMIUM
        }
        return result
    }

    private fun loadMyJams() {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            projectRepository.getMyJams(uid).collect { jams ->
                _myJams.value = jams
                // Always keep jamsThisMonth in sync with the live active jam count
                _jamsThisMonth.value = jams.size
            }
        }
    }

    fun createNewJam(name: String, theme: String, durationDays: Int = 7) {
        val uid = currentUserId ?: return
        if (name.isBlank()) return
        viewModelScope.launch {
            // Gate on current active jam count (live, no extra Firestore query needed)
            val activeCount = _myJams.value.size
            if (_currentPlan.value == UserPlan.FREE && activeCount >= FREE_JAM_LIMIT) {
                _jamLimitReached.emit(Unit)
                return@launch
            }
            try {
                projectRepository.createJam(name, theme, durationDays, 1, uid)
                // _jamsThisMonth will auto-update when the Firestore snapshot fires
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun loadMyTasks() {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            taskRepository.getTasksForUser(uid).collect { allMyTasks ->
                _myTasks.value = allMyTasks.filter { it.status != TaskStatus.DONE }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeActiveJamTimer() {
        viewModelScope.launch {
            SessionManager.activeJamId
                .combine(_myJams) { activeId, jams -> activeId to jams }
                .collect { (activeId, jams) ->
                    val activeJam = jams.find { it.id == activeId }
                    val endTimeMs = activeJam?.endDate?.time
                    if (endTimeMs == null) {
                        _timeLeftInSeconds.value = -1L
                    } else {
                        val secondsLeft = ((endTimeMs - System.currentTimeMillis()) / 1000L).coerceAtLeast(0L)
                        _timeLeftInSeconds.value = secondsLeft
                        while (_timeLeftInSeconds.value > 0) {
                            delay(1000L)
                            _timeLeftInSeconds.value = (_timeLeftInSeconds.value - 1L).coerceAtLeast(0L)
                        }
                    }
                }
        }
    }

    fun updateTaskStatus(taskId: String, newTaskStatus: TaskStatus) {
        viewModelScope.launch {
            try { taskRepository.updateTaskStatus(taskId, newTaskStatus) } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun renameJam(projectId: String, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch {
            try {
                projectRepository.renameJam(projectId, newName)
                if (SessionManager.activeJamId.value == projectId) {
                    SessionManager.setActiveJam(projectId, newName)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun deleteJam(projectId: String) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTasksForProject(projectId)
                projectRepository.deleteJam(projectId)
                if (SessionManager.activeJamId.value == projectId) {
                    SessionManager.clearActiveJam()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun loadAllTasksForJams() {
        viewModelScope.launch {
            _myJams.collect { jams ->
                val ratios = mutableMapOf<String, Float>()
                jams.forEach { jam ->
                    taskRepository.getTasksForProject(jam.id).collect { tasks ->
                        ratios[jam.id] = if (tasks.isEmpty()) 0f
                        else tasks.count { it.status == TaskStatus.DONE }.toFloat() / tasks.size.toFloat()
                        _completionRatios.value = ratios.toMap()
                    }
                }
            }
        }
    }
}
