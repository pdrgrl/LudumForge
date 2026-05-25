package dam.a51319.ludumforge.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import dam.a51319.ludumforge.data.repositories.ItchRepository
import dam.a51319.ludumforge.models.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class JoinJamState {
    object LoadingSummary : JoinJamState()
    data class InputIdea(val theme: String, val rules: String) : JoinJamState()
    object LoadingFeedback : JoinJamState()
    data class BrainstormResult(val theme: String, val rules: String, val feedback: String) :
        JoinJamState()

    data class Error(val message: String) : JoinJamState()
}

class JoinJamViewModel(application: Application) : AndroidViewModel(application) {

    private val itchRepository = ItchRepository()

    private val _state = MutableStateFlow<JoinJamState>(JoinJamState.LoadingSummary)
    val state: StateFlow<JoinJamState> = _state

    // Reads the API key directly from your existing LudumForgePrefs
    private fun getSavedApiKey(): String {
        val sharedPrefs = getApplication<Application>()
            .getSharedPreferences("LudumForgePrefs", Context.MODE_PRIVATE)

        return sharedPrefs.getString("gemini_api_key", "") ?: ""
    }

    // Safely builds the Gemini model only if the key exists
    private fun buildGeminiModel(): GenerativeModel? {
        val savedApiKey = getSavedApiKey()
        if (savedApiKey.isBlank()) return null

        return GenerativeModel(
            modelName = "gemini-3.1-flash-lite",
            apiKey = savedApiKey
        )
    }

    fun extractJamInfo(jam: Project) {
        _state.value = JoinJamState.LoadingSummary

        viewModelScope.launch {
            val model = buildGeminiModel()
            if (model == null) {
                _state.value =
                    JoinJamState.Error("No Gemini API key found. Please add it in the AI Generator first.")
                return@launch
            }

            val jamUrl = jam.jamUrl
            if (jamUrl.isNullOrBlank()) {
                _state.value = JoinJamState.Error("This jam does not have a valid itch.io URL.")
                return@launch
            }

            val rawText = itchRepository.getJamDescriptionText(jamUrl)

            android.util.Log.d("JoinJam", "jamUrl=$jamUrl rawTextLength=${rawText.length}")
            android.util.Log.d("JoinJam", "rawTextPreview=${rawText.take(500)}")

            if (rawText.isBlank() || rawText.contains("Could not fetch")) {
                _state.value = JoinJamState.InputIdea(
                    theme = "Unknown",
                    rules = "Could not extract rules automatically. You can still brainstorm manually."
                )
                return@launch
            }

            try {
                val prompt = """
                You are an AI assisting a game developer.
                Read the following Game Jam description and extract:
                1. The official theme. If no theme is announced, write "Not yet announced".
                2. The 3 most important rules or constraints.

                Format exactly like:
                THEME: [theme]
                RULES:
                - [rule 1]
                - [rule 2]
                - [rule 3]

                Jam Description:
                ${rawText.take(5000)}
            """.trimIndent()

                val resultText = model.generateContent(prompt).text.orEmpty()

                android.util.Log.d("JoinJam", "geminiResult=$resultText")

                val theme = Regex("THEME:(.*?)\\nRULES:", RegexOption.DOT_MATCHES_ALL)
                    .find(resultText)
                    ?.groupValues?.getOrNull(1)
                    ?.trim()
                    .orEmpty()
                    .ifBlank { "Not yet announced" }

                val rules = Regex("RULES:\\s*(.*)", RegexOption.DOT_MATCHES_ALL)
                    .find(resultText)
                    ?.groupValues?.getOrNull(1)
                    ?.trim()
                    .orEmpty()
                    .ifBlank { "No clear rules extracted." }

                _state.value = JoinJamState.InputIdea(theme, rules)
            } catch (e: Exception) {
                e.printStackTrace()
                android.util.Log.e("JoinJam", "Gemini extract failed", e)
                _state.value = JoinJamState.Error(
                    "Failed to analyze jam page: ${e.javaClass.simpleName}: ${e.message}"
                )
            }
        }
    }

    fun brainstormIdea(theme: String, rules: String, userIdea: String, teamSize: Int) {
        if (userIdea.isBlank()) return

        _state.value = JoinJamState.LoadingFeedback

        viewModelScope.launch {
            val model = buildGeminiModel()
            if (model == null) {
                _state.value = JoinJamState.Error("No Gemini API key found. Please add it in the AI Generator first.")
                return@launch
            }

            try {
                val prompt = """
                Game Jam Theme: $theme
                Jam Rules: $rules
                Team Size: $teamSize
                My Game Idea: $userIdea

                Give very short feedback in 2-3 sentences:
                - Does it fit the theme?
                - Does it respect the rules?
                - Is it realistic for this team size?

                End with:
                Would you like me to generate a complete roadmap for this idea?
            """.trimIndent()

                val feedback = model.generateContent(prompt).text ?: "No feedback generated."
                _state.value = JoinJamState.BrainstormResult(theme, rules, feedback)
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = JoinJamState.Error(
                    "Failed to brainstorm idea: ${e.javaClass.simpleName}: ${e.message}"
                )
            }
        }
    }

    fun refineIdea(theme: String, rules: String) {
        _state.value = JoinJamState.InputIdea(theme, rules)
    }
}