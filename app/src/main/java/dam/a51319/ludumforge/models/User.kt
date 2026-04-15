package dam.a51319.ludumforge.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    ADMIN, DEVELOPER, ARTIST, AUDIO_ENGINEER, GAME_DESIGNER
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val role: UserRole = UserRole.DEVELOPER
)