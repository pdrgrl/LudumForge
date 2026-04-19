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
import dam.a51319.ludumforge.data.repositories.AuthRepository
import dam.a51319.ludumforge.models.User

class PersonalDashboardViewModel : ViewModel() {


    private val authRepo = AuthRepository()

    // Timer State: Representing seconds left in the Game Jam
    private val _timeLeftInSeconds = MutableStateFlow(48 * 3600L) // 48 Hours
    val timeLeftInSeconds: StateFlow<Long> = _timeLeftInSeconds.asStateFlow()

    // Tasks State: Specific to the logged-in user
    private val _myTasks = MutableStateFlow<List<Task>>(emptyList())
    val myTasks: StateFlow<List<Task>> = _myTasks.asStateFlow()

    init {
        loadMyTasks()
        startTimer()
    }


    private fun loadMyTasks() {
        // Dummy data simulating a database/API fetch
        _myTasks.value = listOf(
            Task("t1", "p1", "Implement dialogue tree", TaskCategory.CODE, "u1", 120, TaskStatus.IN_PROGRESS),
            Task("t2", "p1", "Fix collision in level 2", TaskCategory.QA, "u1", 60, TaskStatus.TODO),
            Task("t3", "p2", "Design main menu UI", TaskCategory.DESIGN, "u1", 180, TaskStatus.TODO)
        )
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_timeLeftInSeconds.value > 0) {
                delay(1000L) // 1 second tick
                _timeLeftInSeconds.value -= 1
            }
        }
    }
}