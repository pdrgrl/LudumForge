package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dam.a51319.ludumforge.data.repositories.TaskRepository
import dam.a51319.ludumforge.models.Task
import dam.a51319.ludumforge.models.TaskStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalDashboardViewModel : ViewModel() {

    private val taskRepository = TaskRepository()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Timer State
    private val _timeLeftInSeconds = MutableStateFlow(48 * 3600L) // 48 Hours
    val timeLeftInSeconds: StateFlow<Long> = _timeLeftInSeconds.asStateFlow()

    // Tasks State
    private val _myTasks = MutableStateFlow<List<Task>>(emptyList())
    val myTasks: StateFlow<List<Task>> = _myTasks.asStateFlow()

    init {
        loadMyTasks()
        startTimer()
    }

    private fun loadMyTasks() {
        if (currentUserId == null) return

        viewModelScope.launch {
            taskRepository.getTasksForUser(currentUserId).collect { allMyTasks ->
                // Filter out DONE tasks so the dashboard only shows what needs attention
                val activeTasks = allMyTasks.filter { it.status != TaskStatus.DONE }
                _myTasks.value = activeTasks
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
}