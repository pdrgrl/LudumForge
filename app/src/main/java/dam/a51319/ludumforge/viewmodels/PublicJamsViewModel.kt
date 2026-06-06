package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam.a51319.ludumforge.data.repositories.ItchRepository
import dam.a51319.ludumforge.data.repositories.ProjectRepository
import dam.a51319.ludumforge.models.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicJamsViewModel : ViewModel() {

    private val itchRepo = ItchRepository()
    private val projectRepository = ProjectRepository()

    private val _publicJams = MutableStateFlow<List<Project>>(emptyList())
    val publicJams: StateFlow<List<Project>> = _publicJams.asStateFlow()

    // State to track loading UI
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchLiveJams(force = false)
    }

    fun refreshJams() {
        fetchLiveJams(force = true)
    }

    private fun fetchLiveJams(force: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            
            // 1. Try to get from Firestore first
            val firestoreJams = projectRepository.getPublicJamsFromFirestore()
            val lastUpdate = projectRepository.getPublicJamsLastUpdated()
            val isStale = (System.currentTimeMillis() - lastUpdate) > (30L * 60 * 1000) // 30 mins

            if (!force && firestoreJams.isNotEmpty() && !isStale) {
                _publicJams.value = firestoreJams
            } else {
                // 2. Scrape and update Firestore if stale or forced
                val scrapedJams = itchRepo.getLiveJams(forceRefresh = true)
                if (scrapedJams.isNotEmpty()) {
                    projectRepository.savePublicJamsToFirestore(scrapedJams)
                    _publicJams.value = scrapedJams
                } else if (firestoreJams.isNotEmpty()) {
                    // Fallback to old data if scrape failed
                    _publicJams.value = firestoreJams
                }
            }

            _isLoading.value = false
        }
    }
}