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
import dam.a51319.ludumforge.data.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

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

    init {
        startSessionTimer()
    }

    // Call this once from the UI to hook up the database
    @OptIn(ExperimentalCoroutinesApi::class)
    fun initializeDatabase(context: Context) {
        if (repository == null) {
            val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
            repository = ActionLogRepository(dao)

            // 1. Observe local logs based on the GLOBAL Jam ID!
            viewModelScope.launch {
                SessionManager.activeJamId
                    .flatMapLatest { jamId ->
                        if (jamId != null) {
                            repository!!.getLogsForProject(jamId)
                        } else {
                            flowOf(emptyList()) // No jam selected
                        }
                    }
                    .collect { localLogs ->
                        _logs.value = localLogs
                    }
            }

            // 2. Start the Background Sync Loop
            viewModelScope.launch {
                while (true) {
                    repository?.syncPendingLogs()
                    delay(10000L)
                }
            }
        }
    }

    fun submitNote() {
        val text = noteText.value
        val currentJamId = SessionManager.activeJamId.value ?: return // <-- GET GLOBAL ID

        if (text.isNotBlank()) {
            viewModelScope.launch {
                repository?.addManualNote(currentJamId, text) // Use it here!
                noteText.value = ""
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

    // Sync state for the Retry button
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    fun manualSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            repository?.syncPendingLogs()
            _isSyncing.value = false
        }
    }
}