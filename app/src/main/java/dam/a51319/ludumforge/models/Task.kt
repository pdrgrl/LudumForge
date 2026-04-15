package dam.a51319.ludumforge.models

enum class TaskCategory {
    ART,
    CODE,
    AUDIO,
    DESIGN,
    QA,
    OTHER
}

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    REVIEW,
    DONE
}

/**
 * Represents a single task or issue within a Game Jam project.
 */
data class Task(
    val id: String = "",
    val projectId: String = "",
    val title: String = "",
    val category: TaskCategory = TaskCategory.CODE,
    val assignedTo: String? = null, // Stores the User ID of the assignee
    val estimatedMinutes: Int = 0,
    val status: TaskStatus = TaskStatus.TODO
)