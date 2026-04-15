package dam.a51319.ludumforge.data.daos

import androidx.room.*
import dam.a51319.ludumforge.models.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)

    @Query("SELECT * FROM projects WHERE id = :projectId")
    fun getProjectById(projectId: String): Flow<Project?>

    @Query("SELECT * FROM projects ORDER BY startDate DESC")
    fun getAllProjects(): Flow<List<Project>>
}