package dam.a51319.ludumforge.models

enum class UserRole {
    ADMIN,
    DEVELOPER,
    ARTIST,
    AUDIO_ENGINEER,
    GAME_DESIGNER
}

/**
 * Represents a developer or jam participant.
 */
data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val role: UserRole = UserRole.DEVELOPER
)