package dam.a51319.ludumforge.viewmodels

import androidx.lifecycle.ViewModel
import dam.a51319.ludumforge.models.Project
import dam.a51319.ludumforge.models.ProjectStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import androidx.lifecycle.viewmodel.compose.viewModel

class PublicJamsViewModel : ViewModel() {

    private val _publicJams = MutableStateFlow<List<Project>>(emptyList())
    val publicJams: StateFlow<List<Project>> = _publicJams.asStateFlow()

    init {
        loadDummyJams()
    }

    private fun loadDummyJams() {
        // Mock data to populate the screen until the Firebase API is wired up
        _publicJams.value = listOf(
            Project("j1", "Cyberpunk Jam 2026", "High Tech, Low Life", Date(), Date(), 4, ProjectStatus.ACTIVE),
            Project("j2", "Cozy Autumn Jam", "Harvest & Hearth", Date(), Date(), 2, ProjectStatus.PLANNING),
            Project("j3", "Ludum Dare 58", "Running out of space", Date(), Date(), 5, ProjectStatus.COMPLETED)
        )
    }
}