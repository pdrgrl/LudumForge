package dam.a51319.ludumforge.data.daos

import androidx.room.*
import dam.a51319.ludumforge.models.Team
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: Team)

    @Update
    suspend fun updateTeam(team: Team)

    @Delete
    suspend fun deleteTeam(team: Team)

    @Query("SELECT * FROM teams WHERE projectId = :projectId")
    fun getTeamForProject(projectId: String): Flow<Team?>
}