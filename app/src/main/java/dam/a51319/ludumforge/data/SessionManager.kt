package dam.a51319.ludumforge.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {
    private val _activeJamId = MutableStateFlow<String?>(null)
    val activeJamId: StateFlow<String?> = _activeJamId.asStateFlow()

    private val _activeJamName = MutableStateFlow<String?>(null)
    val activeJamName: StateFlow<String?> = _activeJamName.asStateFlow()

    private val _pendingRoadmapIdea = MutableStateFlow<String?>(null)
    val pendingRoadmapIdea: StateFlow<String?> = _pendingRoadmapIdea.asStateFlow()

    private val _pendingRoadmapTeamSize = MutableStateFlow<String?>(null)
    val pendingRoadmapTeamSize: StateFlow<String?> = _pendingRoadmapTeamSize.asStateFlow()

    private val _pendingRoadmapDuration = MutableStateFlow<String?>(null)
    val pendingRoadmapDuration: StateFlow<String?> = _pendingRoadmapDuration.asStateFlow()

    fun setActiveJam(jamId: String, jamName: String) {
        _activeJamId.value = jamId
        _activeJamName.value = jamName
    }

    fun seedRoadmapInput(
        idea: String,
        teamSize: String,
        duration: String = "48 hours"
    ) {
        _pendingRoadmapIdea.value = idea
        _pendingRoadmapTeamSize.value = teamSize
        _pendingRoadmapDuration.value = duration
    }

    fun clearRoadmapSeed() {
        _pendingRoadmapIdea.value = null
        _pendingRoadmapTeamSize.value = null
        _pendingRoadmapDuration.value = null
    }

    fun clearActiveJam() {
        _activeJamId.value = null
        _activeJamName.value = null
    }

    private val _isDarkTheme = MutableStateFlow<Boolean>(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
    }
}