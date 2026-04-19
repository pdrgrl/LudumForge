package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam.a51319.ludumforge.data.repositories.ItchRepository
import dam.a51319.ludumforge.models.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicJamsViewModel : ViewModel() {

    private val itchRepo = ItchRepository()

    private val _publicJams = MutableStateFlow<List<Project>>(emptyList())
    val publicJams: StateFlow<List<Project>> = _publicJams.asStateFlow()

    // State to track loading UI
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchLiveJams()
    }

    private fun fetchLiveJams() {
        viewModelScope.launch {
            _isLoading.value = true
            val jams = itchRepo.getLiveJams()
            _publicJams.value = jams
            _isLoading.value = false
        }
    }
}