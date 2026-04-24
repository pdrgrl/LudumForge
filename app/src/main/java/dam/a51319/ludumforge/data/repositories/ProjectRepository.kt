package dam.a51319.ludumforge.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import dam.a51319.ludumforge.models.Project
import dam.a51319.ludumforge.models.ProjectStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class ProjectRepository {
    private val db = FirebaseFirestore.getInstance()

    // Real-time listener scoped to the current user's jams
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
    suspend fun renameJam(projectId: String, newName: String) {
        db.collection("projects").document(projectId)
            .update("name", newName)
            .await()
    }

    suspend fun deleteJam(projectId: String) {
        db.collection("projects").document(projectId)
            .delete()
            .await()
    }
    // Create a new Jam
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

    // Keep the existing listenToProject for future use
    fun listenToProject(projectId: String): Flow<Project?> = callbackFlow {
        val listener = db.collection("projects").document(projectId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.toObject(Project::class.java))
            }
        awaitClose { listener.remove() }
    }
}