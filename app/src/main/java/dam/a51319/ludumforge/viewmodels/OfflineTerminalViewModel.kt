package dam.a51319.ludumforge.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam.a51319.ludumforge.data.ActionLog
import dam.a51319.ludumforge.data.LudumForgeDatabase
import dam.a51319.ludumforge.data.repositories.ActionLogRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OfflineTerminalViewModel : ViewModel() {

    private var repository: ActionLogRepository? = null

    // Terminal command / Note input field
    val noteText = MutableStateFlow("")

    // The live list of logs from Room
    private val _logs = MutableStateFlow<List<ActionLog>>(emptyList())
    val logs: StateFlow<List<ActionLog>> = _logs.asStateFlow()

    // Session Timer
    private val _sessionTimerSeconds = MutableStateFlow(0L)
    val sessionTimerSeconds: StateFlow<Long> = _sessionTimerSeconds.asStateFlow()

    private val activeProjectId = "p1" // Hardcoded for now

    init {
        startSessionTimer()
    }

    // Call this once from the UI to hook up the database
    fun initializeDatabase(context: Context) {
        if (repository == null) {
            val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
            repository = ActionLogRepository(dao)

            // 1. Observe local logs
            viewModelScope.launch {
                repository?.getLogsForProject(activeProjectId)?.collect { localLogs ->
                    _logs.value = localLogs
                }
            }

            // 2. Start the Background Sync Loop
            viewModelScope.launch {
                while (true) {
                    repository?.syncPendingLogs()
                    delay(10000L) // Try to sync every 10 seconds
                }
            }
        }
    }

    fun submitNote() {
        val text = noteText.value
        if (text.isNotBlank()) {
            viewModelScope.launch {
                // Save it instantly to Room (Offline)
                repository?.addManualNote(activeProjectId, text)
                noteText.value = "" // Clear the input field
            }
        }
    }

    private fun startSessionTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                _sessionTimerSeconds.value += 1
            }
        }
    }
}