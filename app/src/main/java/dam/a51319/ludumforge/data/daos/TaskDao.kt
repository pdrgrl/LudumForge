package dam.a51319.ludumforge.data.daos

import androidx.room.*
import dam.a51319.ludumforge.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY status ASC")
    fun getTasksForProject(projectId: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE assignedTo LIKE '%' || :userId || '%'")
    fun getTasksForUser(userId: String): Flow<List<Task>>
}