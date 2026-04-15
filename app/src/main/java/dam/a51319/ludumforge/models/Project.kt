package dam.a51319.ludumforge.models

import java.util.Date

enum class ProjectStatus {
    PLANNING,
    ACTIVE,
    SUBMITTED,
    COMPLETED,
    CANCELLED
}

/**
 * Represents a Game Jam project.
 */
data class Project(
    val id: String = "",
    val name: String = "",
    val theme: String = "",
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val teamSize: Int = 1,
    val status: ProjectStatus = ProjectStatus.PLANNING
)