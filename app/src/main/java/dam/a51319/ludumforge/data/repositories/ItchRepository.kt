package dam.a51319.ludumforge.data.repositories

import dam.a51319.ludumforge.models.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

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
        data class RawJam(val title: String, val url: String, val participants: Int)

        val rawList = mutableListOf<RawJam>()
        try {
            val document = Jsoup.connect("https://itch.io/jams")
                .userAgent("Mozilla/5.0")
                .timeout(8000)
                .get()

            document.select(".jam_cell").forEach { element ->
                val linkElement = element.select("a").first() ?: return@forEach
                val title = linkElement.text().takeIf { it.isNotBlank() } ?: return@forEach
                val jamUrl = linkElement.attr("href").takeIf { it.isNotBlank() } ?: return@forEach
                val joinedText = element.select(".joined_count").first()?.text() ?: "(0 joined)"
                val participants = joinedText.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0
                rawList.add(RawJam(title, jamUrl, participants))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Step 2: for the top 20 jams, fetch each jam page in parallel to grab the OG cover image
        val result = rawList.take(20).map { raw ->
            async {
                val imageUrl: String? = try {
                    val jamDoc = Jsoup.connect("https://itch.io${raw.url}")
                        .userAgent("Mozilla/5.0")
                        .timeout(5000)
                        .get()

                    // Primary: OpenGraph image (used by itch.io for jam banners)
                    jamDoc.select("meta[property=og:image]").attr("content")
                        .takeIf { it.isNotBlank() }
                    // Fallback: inline jam banner or game cover image
                        ?: jamDoc.select(".jam_banner img, .game_cover img, .header_image img")
                            .attr("src")
                            .takeIf { it.isNotBlank() }
                } catch (e: Exception) {
                    null // image fetch failed — card will render with placeholder
                }

                Project(
                    id = "jam_${raw.title.hashCode()}",
                    name = raw.title,
                    theme = "Featured itch.io Jam",
                    teamSize = raw.participants,
                    coverImageUrl = imageUrl,
                    jamUrl = "https://itch.io${raw.url}"
                )
            }
        }.awaitAll()

        if (result.isNotEmpty()) {
            cachedJams = result
            lastFetchTime = System.currentTimeMillis()
        }
        result
    }
}