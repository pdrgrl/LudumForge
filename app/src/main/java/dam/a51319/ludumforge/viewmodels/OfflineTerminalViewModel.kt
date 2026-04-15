package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

class OfflineTerminalViewModel : ViewModel() {

    // Terminal command / Note input field
    val noteText = MutableStateFlow("")

    // Session Timer tracking how long the user has been offline (in seconds)
    private val _sessionTimerSeconds = MutableStateFlow(0L)
    val sessionTimerSeconds: StateFlow<Long> = _sessionTimerSeconds.asStateFlow()

    init {
        startSessionTimer()
    }

    private fun startSessionTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000L) // Wait exactly 1 second
                _sessionTimerSeconds.value += 1
            }
        }
    }

    // Stub to clear text when "Send" is clicked
    fun submitNote() {
        if (noteText.value.isNotBlank()) {
            // Later: Save note to Room Database
            noteText.value = ""
        }
    }
}