package dam.a51319.ludumforge.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dam.a51319.ludumforge.data.ActionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionLogDao {
    // Reads all logs for the current project, newest first.
    // Returns a Flow so the UI updates instantly when a new log is inserted!
    @Query("SELECT * FROM action_logs WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getLogsForProject(projectId: String): Flow<List<ActionLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActionLog)

    @Query("SELECT * FROM action_logs WHERE isSynced = 0")
    suspend fun getUnsyncedLogs(): List<ActionLog>

    @Query("UPDATE action_logs SET isSynced = 1 WHERE id = :logId")
    suspend fun markAsSynced(logId: String)
}