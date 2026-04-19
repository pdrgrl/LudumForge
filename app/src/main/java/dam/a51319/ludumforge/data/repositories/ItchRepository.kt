package dam.a51319.ludumforge.data.repositories

import dam.a51319.ludumforge.models.Project
import dam.a51319.ludumforge.models.ProjectStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.Date

class ItchRepository {

    suspend fun getLiveJams(): List<Project> = withContext(Dispatchers.IO) {
        val jamsList = mutableListOf<Project>()
        try {
            // Fetch the live HTML from itch.io/jams
            val document = Jsoup.connect("https://itch.io/jams").get()

            // Itch.io currently renders jams as simple .jam_cell divs inside the calendar
            val jamElements = document.select(".jam_cell")

            for (element in jamElements) {
                // 1. Get the Title and Link
                val linkElement = element.select("a").first()
                val title = linkElement?.text() ?: "Unknown Jam"
                val jamUrl = linkElement?.attr("href") ?: ""

                // 2. Get Participants
                // Currently rendered as <span class="joined_count">(45 joined)</span>
                val joinedElement = element.select(".joined_count").first()
                val joinedText = joinedElement?.text() ?: "(0 joined)"

                // Extract just the number from "(45 joined)" using Regex
                val participantsRaw = joinedText.replace("[^0-9]".toRegex(), "")
                val participants = participantsRaw.toIntOrNull() ?: 0

                // Because the calendar list doesn't expose host or exact dates in this view,
                // we format the theme as the jam's URL slug and default to UPCOMING.
                if (title != "Unknown Jam") {
                    jamsList.add(
                        Project(
                            id = "jam_${title.hashCode()}",
                            name = title,
                            theme = "itch.io$jamUrl", // Show the URL path as the theme/description
                            startDate = Date(),
                            endDate = Date(),
                            teamSize = participants, // Pass participants to UI
                            status = ProjectStatus.PLANNING // Default status for calendar items
                        )
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Return the first 50 so we don't overwhelm the UI with 500+ micro-jams
        return@withContext jamsList.take(50)
    }
}