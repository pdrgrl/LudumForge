package dam.a51319.ludumforge.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {
    // Start with "p1" so your existing dummy data still loads initially
    private val _activeJamId = MutableStateFlow<String?>("null")
    val activeJamId: StateFlow<String?> = _activeJamId.asStateFlow()

    fun setActiveJam(jamId: String) {
        _activeJamId.value = jamId
    }
}