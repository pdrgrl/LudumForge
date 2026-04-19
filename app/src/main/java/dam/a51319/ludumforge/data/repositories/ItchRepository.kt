package dam.a51319.ludumforge.data.repositories

import dam.a51319.ludumforge.models.Project
import dam.a51319.ludumforge.models.ProjectStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.Date

class ItchRepository {

    /**
     * Scrapes the live itch.io/jams page and parses the active/upcoming jams.
     */
    suspend fun getLiveJams(): List<Project> = withContext(Dispatchers.IO) {
        val jamsList = mutableListOf<Project>()
        try {
            // Fetch the live HTML from itch.io
            val document = Jsoup.connect("https://itch.io/jams").get()

            // Itch.io keeps jams in a div with class "jam_grid_widget" -> "jam_cell"
            val jamElements = document.select(".jam_cell")

            for (element in jamElements) {
                val title = element.select(".primary_info a").text()
                val host = element.select(".hosted_by a").text()
                val participantsText = element.select(".stat_box .stat_value").firstOrNull()?.text() ?: "0"
                val participants = participantsText.replace(",", "").toIntOrNull() ?: 0

                // Parse status (Active vs Upcoming)
                val dateInfo = element.select(".date_countdown").text()
                val status = if (dateInfo.contains("Ends", ignoreCase = true)) {
                    ProjectStatus.ACTIVE
                } else if (dateInfo.contains("Starts", ignoreCase = true)) {
                    ProjectStatus.PLANNING
                } else {
                    ProjectStatus.COMPLETED
                }

                jamsList.add(
                    Project(
                        id = "jam_${title.hashCode()}",
                        name = title,
                        theme = "Hosted by $host", // We use the theme field for the host
                        startDate = Date(), // We use current date as a placeholder for the UI
                        endDate = Date(),
                        teamSize = participants, // We repurpose teamSize to hold participant count for the UI
                        status = status
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext jamsList
    }
}