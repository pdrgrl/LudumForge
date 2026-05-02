package dam.a51319.ludumforge.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import dam.a51319.ludumforge.models.Project
import dam.a51319.ludumforge.models.ProjectStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

class ProjectRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getMyJams(userId: String): Flow<List<Project>> = callbackFlow {
        val listener = db.collection("projects")
            .whereEqualTo("creatorId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                if (snapshot != null) {
                    val jams = snapshot.documents.mapNotNull { doc ->
                        try {
                            Project(
                                id = doc.id,
                                name = doc.getString("name") ?: "Untitled Jam",
                                theme = doc.getString("theme") ?: "",
                                startDate = doc.getDate("startDate") ?: Date(),
                                endDate = doc.getDate("endDate") ?: Date(),
                                teamSize = doc.getLong("teamSize")?.toInt() ?: 1,
                                status = ProjectStatus.valueOf(doc.getString("status") ?: "PLANNING"),
                                creatorId = doc.getString("creatorId") ?: ""
                            )
                        } catch (e: Exception) { null }
                    }
                    trySend(jams)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Returns how many jams this user created in the current calendar month.
     * Used to enforce the FREE tier 2-jam/month limit.
     */
    suspend fun getJamsCreatedThisMonth(userId: String): Int {
        return try {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val monthStart = cal.time

            val snapshot = db.collection("projects")
                .whereEqualTo("creatorId", userId)
                .whereGreaterThanOrEqualTo("startDate", monthStart)
                .get()
                .await()
            snapshot.size()
        } catch (e: Exception) { 0 }
    }

    suspend fun renameJam(projectId: String, newName: String) {
        db.collection("projects").document(projectId).update("name", newName).await()
    }

    suspend fun deleteJam(projectId: String) {
        db.collection("projects").document(projectId).delete().await()
    }

    suspend fun createJam(name: String, theme: String, durationDays: Int, teamSize: Int, creatorId: String): String {
        val newJam = hashMapOf(
            "name" to name,
            "theme" to theme,
            "startDate" to Date(),
            "endDate" to Date(System.currentTimeMillis() + (durationDays.toLong() * 24 * 60 * 60 * 1000)),
            "teamSize" to teamSize,
            "status" to ProjectStatus.PLANNING.name,
            "creatorId" to creatorId
        )
        val docRef = db.collection("projects").add(newJam).await()
        return docRef.id
    }

    fun listenToProject(projectId: String): Flow<Project?> = callbackFlow {
        val listener = db.collection("projects").document(projectId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.toObject(Project::class.java))
            }
        awaitClose { listener.remove() }
    }
}
