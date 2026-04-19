package dam.a51319.ludumforge.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "action_logs")
data class ActionLog(
    @PrimaryKey val id: String, // e.g., UUID
    val projectId: String,
    val message: String,
    val type: String, // "SYSTEM", "USER_NOTE", "TASK_UPDATE"
    val timestamp: Long,
    val isSynced: Boolean = false // THIS is the key to the offline requirement!
)