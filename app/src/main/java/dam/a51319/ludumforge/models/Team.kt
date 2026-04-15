package dam.a51319.ludumforge.models

/**
 * Represents a group of users working on a specific Game Jam project.
 */
data class Team(
    val id: String = "",
    val projectId: String = "",
    val membersList: List<String> = emptyList() // List of User IDs
)