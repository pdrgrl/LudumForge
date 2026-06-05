package dam.a51319.ludumforge.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import dam.a51319.ludumforge.data.ActionLog
import dam.a51319.ludumforge.data.LudumForgeDatabase
import dam.a51319.ludumforge.data.repositories.ActionLogRepository
import dam.a51319.ludumforge.data.repositories.TaskRepository
import dam.a51319.ludumforge.models.TaskStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dam.a51319.ludumforge.data.SessionManager
import dam.a51319.ludumforge.models.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import org.json.JSONObject

sealed class PanicTerminalState {
    object Idle : PanicTerminalState()
    object Analyzing : PanicTerminalState()
    data class Suggestion(val tasksToDrop: List<Task>, val rationale: String) : PanicTerminalState()
}

class OfflineTerminalViewModel : ViewModel() {

    private var repository: ActionLogRepository? = null
    private val taskRepository = TaskRepository()

    // Terminal command / Note input field
    val noteText = MutableStateFlow("")

    // The live list of logs from Room
    private val _logs = MutableStateFlow<List<ActionLog>>(emptyList())
    val logs: StateFlow<List<ActionLog>> = _logs.asStateFlow()

    // Session Timer
    private val _sessionTimerSeconds = MutableStateFlow(0L)
    val sessionTimerSeconds: StateFlow<Long> = _sessionTimerSeconds.asStateFlow()

    // Panic State
    private val _panicState = MutableStateFlow<PanicTerminalState>(PanicTerminalState.Idle)
    val panicState: StateFlow<PanicTerminalState> = _panicState.asStateFlow()

    init {
        startSessionTimer()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun initializeDatabase(context: Context) {
        if (repository == null) {
            val dao = LudumForgeDatabase.getDatabase(context).actionLogDao()
            repository = ActionLogRepository(dao)

            viewModelScope.launch {
                SessionManager.activeJamId
                    .flatMapLatest { jamId ->
                        if (jamId != null) repository!!.getLogsForProject(jamId)
                        else flowOf(emptyList())
                    }
                    .collect { localLogs -> _logs.value = localLogs }
            }

            viewModelScope.launch {
                while (true) {
                    repository?.syncPendingLogs()
                    delay(10000L)
                }
            }
        }
    }

    fun submitNote(userApiKey: String, isPremium: Boolean) {
        val text = noteText.value.trim()
        val currentJamId = SessionManager.activeJamId.value ?: return
        val jamName = SessionManager.activeJamName.value ?: "current jam"

        if (text.isNotBlank()) {
            viewModelScope.launch {
                // Handle Commands
                if (text.lowercase() == "panic") {
                    triggerPanicAnalysis(userApiKey, jamName, currentJamId)
                    noteText.value = ""
                } else if (text.lowercase() == "confirm" && _panicState.value is PanicTerminalState.Suggestion) {
                    executePanicDrop(currentJamId)
                    noteText.value = ""
                } else if (text.lowercase() == "abort") {
                    _panicState.value = PanicTerminalState.Idle
                    repository?.addSystemEvent(currentJamId, "Panic mode aborted by user.")
                    noteText.value = ""
                } else {
                    // Regular Note
                    repository?.addManualNote(currentJamId, text)
                    noteText.value = ""
                }
            }
        }
    }

    private suspend fun triggerPanicAnalysis(apiKey: String, jamName: String, jamId: String) {
        if (apiKey.isBlank()) {
            repository?.addSystemEvent(jamId, "ERROR: Gemini API Key missing. Check Settings.")
            return
        }

        _panicState.value = PanicTerminalState.Analyzing
        repository?.addSystemEvent(jamId, "INITIATING PANIC ANALYSIS...")

        try {
            val pendingTasks = taskRepository.getTasksForProject(jamId).first()
                .filter { it.status != TaskStatus.DONE }

            if (pendingTasks.isEmpty()) {
                repository?.addSystemEvent(jamId, "Analysis complete: No pending tasks to drop.")
                _panicState.value = PanicTerminalState.Idle
                return
            }

            val generativeModel = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = apiKey)
            val tasksJson = pendingTasks.joinToString("\n") { "- [${it.id}] ${it.title} (${it.category})" }
            
            val prompt = """
                You are a veteran Game Jam Producer. Team is in PANIC for '$jamName'.
                Identify non-essential tasks to drop for an MVP survival list.
                TASKS:
                $tasksJson
                Return ONLY raw JSON: {"rationale": "one sentence", "idsToDrop": ["id1", "id2"]}
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val rawText = response.text ?: throw Exception("No response")
            val json = JSONObject(rawText.substring(rawText.indexOf('{'), rawText.lastIndexOf('}') + 1))
            
            val rationale = json.getString("rationale")
            val ids = json.getJSONArray("idsToDrop")
            val toDrop = mutableListOf<Task>()
            for (i in 0 until ids.length()) {
                pendingTasks.find { it.id == ids.getString(i) }?.let { toDrop.add(it) }
            }

            _panicState.value = PanicTerminalState.Suggestion(toDrop, rationale)
            
            repository?.addSystemEvent(jamId, "AI RATIONALE: $rationale")
            repository?.addSystemEvent(jamId, "SUGGESTION: Drop ${toDrop.size} tasks. Type 'CONFIRM' to proceed.")

        } catch (e: Exception) {
            repository?.addSystemEvent(jamId, "CRITICAL ERROR: ${e.localizedMessage}")
            _panicState.value = PanicTerminalState.Idle
        }
    }

    private suspend fun executePanicDrop(jamId: String) {
        val state = _panicState.value
        if (state is PanicTerminalState.Suggestion) {
            repository?.addSystemEvent(jamId, "EXECUTING MVP TRIMMING...")
            state.tasksToDrop.forEach { task ->
                taskRepository.deleteTask(task.id)
            }
            val user = FirebaseAuth.getInstance().currentUser
            val userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "Dev"
            repository?.addSystemEvent(jamId, "🚨 $userName executed PANIC MODE. ${state.tasksToDrop.size} tasks purged.")
            _panicState.value = PanicTerminalState.Idle
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