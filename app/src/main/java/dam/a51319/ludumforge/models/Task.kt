package dam.a51319.ludumforge.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TaskCategory {
    ART, CODE, AUDIO, DESIGN, QA, OTHER
}

enum class TaskStatus {
    TODO, IN_PROGRESS, REVIEW, DONE
}

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE // If project is deleted, delete its tasks
        )
    ],
    indices = [Index("projectId")] // Recommended by Room for foreign key columns
)
data class Task(
    @PrimaryKey
    val id: String = "",
    val projectId: String = "",
    val title: String = "",
    val category: TaskCategory = TaskCategory.CODE,
    val assignedTo: String? = null,
    val estimatedMinutes: Int = 0,
    val status: TaskStatus = TaskStatus.TODO
)