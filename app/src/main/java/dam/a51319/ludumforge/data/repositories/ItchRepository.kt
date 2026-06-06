package dam.a51319.ludumforge.data.repositories

import dam.a51319.ludumforge.models.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.time.OffsetDateTime
import java.util.Date

class ItchRepository {

    companion object {
        @Volatile
        private var cachedJams: List<Project>? = null
        @Volatile
        private var lastFetchTime: Long = 0L
        private const val CACHE_DURATION_MS = 15 * 60 * 1000 // 15 minutes
    }

    suspend fun getLiveJams(forceRefresh: Boolean = false): List<Project> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val cached = cachedJams
        if (!forceRefresh && cached != null && (now - lastFetchTime) < CACHE_DURATION_MS) {
            return@withContext cached
        }

        // Step 1: scrape the calendar list for titles, URLs, and participant counts
        data class RawJam(
            val title: String, 
            val url: String, 
            val participants: Int, 
            val status: dam.a51319.ludumforge.models.ProjectStatus,
            val date: Date? = null
        )

        val rawList = mutableListOf<RawJam>()
        try {
            val document = Jsoup.connect("https://itch.io/jams")
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get()

            // Try different selectors in case itch.io updated their structure
            val jamCells = document.select(".jam_cell, .jam_list_cell")
            
            jamCells.forEach { element ->
                val linkElement = element.select("a").first() ?: return@forEach
                val title = linkElement.text().takeIf { it.isNotBlank() } ?: return@forEach
                val jamUrl = linkElement.attr("href").takeIf { it.isNotBlank() } ?: return@forEach
                val joinedText = element.select(".joined_count, .participant_count").first()?.text() ?: "(0 joined)"
                val participants = joinedText.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0
                
                val initialMetaElement = element.select(".jam_meta, .meta_tag")
                val initialMetaText = initialMetaElement.text()
                
                // Try to find a timestamp - itch.io often uses a span with title or data-time
                var parsedDate: Date? = null
                
                // Strategy 1: Look for specific time elements and their attributes
                val timeElements = element.select("span.date_format, time, span[data-time], .date_format")
                for (te in timeElements) {
                    val candidate = (te.attr("title") + " " + te.attr("datetime") + " " + te.attr("data-time") + " " + te.text()).trim()
                    val isoPattern = java.util.regex.Pattern.compile("\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}:\\d{2}")
                    val matcher = isoPattern.matcher(candidate)
                    if (matcher.find()) {
                        val match = matcher.group().replace(" ", "T") + (if (candidate.contains("Z")) "Z" else "")
                        try {
                            parsedDate = if (match.endsWith("Z")) {
                                Date.from(OffsetDateTime.parse(match).toInstant())
                            } else {
                                // Try parsing without Z
                                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
                                sdf.parse(match)
                            }
                            if (parsedDate != null) break
                        } catch (_: Exception) {}
                    }
                }

                // Strategy 2: If still null, search the entire element text for a date pattern
                if (parsedDate == null) {
                    val allText = element.text()
                    val pattern = java.util.regex.Pattern.compile("\\d{4}-\\d{2}-\\d{2}[T ]\\d{2}:\\d{2}:\\d{2}")
                    val matcher = pattern.matcher(allText)
                    if (matcher.find()) {
                        val match = matcher.group().replace(" ", "T")
                        try {
                            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
                            parsedDate = sdf.parse(match)
                        } catch (_: Exception) {}
                    }
                }
                
                val finalMetaElement = element.select(".jam_meta, .meta_tag, .jam_status")
                val finalMetaText = if (finalMetaElement.isEmpty()) initialMetaText else finalMetaElement.text()
                
                val status = when {
                    finalMetaText.contains("Submission closes", ignoreCase = true) || 
                    finalMetaText.contains("Submissions close", ignoreCase = true) ||
                    finalMetaText.contains("Live", ignoreCase = true) ||
                    finalMetaText.contains("Ongoing", ignoreCase = true) -> dam.a51319.ludumforge.models.ProjectStatus.ACTIVE
                    finalMetaText.contains("Starts in", ignoreCase = true) ||
                    finalMetaText.contains("Upcoming", ignoreCase = true) ||
                    finalMetaText.contains("Starts", ignoreCase = true) -> dam.a51319.ludumforge.models.ProjectStatus.PLANNING
                    else -> if (finalMetaText.contains("ago", ignoreCase = true)) dam.a51319.ludumforge.models.ProjectStatus.COMPLETED
                            else dam.a51319.ludumforge.models.ProjectStatus.PLANNING
                }
                
                rawList.add(RawJam(title, jamUrl, participants, status, parsedDate))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Step 2: for the top 30 jams, fetch each jam page in parallel to grab the OG cover image
        val result = rawList.take(30).map { raw ->
            async {
                val imageUrl: String? = try {
                    val jamDoc = Jsoup.connect("https://itch.io${raw.url}")
                        .userAgent("Mozilla/5.0")
                        .timeout(6000)
                        .get()

                    jamDoc.select("meta[property=og:image]").attr("content")
                        .takeIf { it.isNotBlank() }
                        ?: jamDoc.select(".jam_banner img, .game_cover img, .header_image img")
                            .attr("src")
                            .takeIf { it.isNotBlank() }
                } catch (e: Exception) {
                    null 
                }

                // If we found a date in the list, use it. 
                // For PLANNING, it's the start date. For ACTIVE, it's the end date.
                val fallbackDate = Date()
                val startDate = if (raw.status == dam.a51319.ludumforge.models.ProjectStatus.PLANNING) (raw.date ?: fallbackDate) else Date(System.currentTimeMillis() - 86400000)
                val endDate = if (raw.status == dam.a51319.ludumforge.models.ProjectStatus.ACTIVE) (raw.date ?: Date(System.currentTimeMillis() + 86400000)) else (raw.date ?: fallbackDate)

                Project(
                    id = "jam_${raw.title.hashCode()}",
                    name = raw.title,
                    theme = "Featured itch.io Jam",
                    teamSize = raw.participants,
                    coverImageUrl = imageUrl,
                    jamUrl = if (raw.url.startsWith("http")) raw.url else "https://itch.io${raw.url}",
                    status = raw.status,
                    startDate = startDate,
                    endDate = endDate
                )
            }
        }.awaitAll()

        if (result.isNotEmpty()) {
            cachedJams = result
            lastFetchTime = System.currentTimeMillis()
        }
        result
    }

    suspend fun getJamDescriptionText(jamUrl: String): String = withContext(Dispatchers.IO) {
        try {
            val url = if (jamUrl.startsWith("http")) jamUrl else "https://itch.io$jamUrl"
            val doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(8000)
                .get()
            // .text() strips HTML tags and gives clean text
            return@withContext doc.select(".jam_content").text()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Could not fetch jam details."
        }
    }
}