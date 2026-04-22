package dam.a51319.ludumforge.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {
    private val _activeJamId = MutableStateFlow<String?>("p1")
    val activeJamId: StateFlow<String?> = _activeJamId.asStateFlow()

    // NEW: Also store the name so the TopBar can display it without a DB call
    private val _activeJamName = MutableStateFlow<String?>(null)
    val activeJamName: StateFlow<String?> = _activeJamName.asStateFlow()

    fun setActiveJam(jamId: String, jamName: String) {
        _activeJamId.value = jamId
        _activeJamName.value = jamName
    }
}