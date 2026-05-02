package dam.a51319.ludumforge.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class ProjectStatus {
    PLANNING, ACTIVE, SUBMITTED, COMPLETED, CANCELLED
}

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val theme: String = "",
    val startDate: Date = Date(),
    val endDate: Date = Date(),
    val teamSize: Int = 1,
    val status: ProjectStatus = ProjectStatus.PLANNING,
    val creatorId: String = "",
    // UIDs of collaborators who joined via invite link (not the creator)
    val memberIds: List<String> = emptyList()
)
