package dam.a51319.ludumforge.data.repositories

import com.google.firebase.firestore.FieldValue
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

    /**
     * Returns all jams the user owns OR was invited to, as a single
     * deduplicated real-time Flow.
     * Two separate Firestore listeners are merged because Firestore does not
     * support OR queries across different fields.
     */
    fun getMyJams(userId: String): Flow<List<Project>> = callbackFlow {
        val combined = mutableMapOf<String, Project>()

        fun parse(documents: List<com.google.firebase.firestore.DocumentSnapshot>): List<Project> =
            documents.mapNotNull { doc ->
                try {
                    @Suppress("UNCHECKED_CAST")
                    Project(
                        id = doc.id,
                        name = doc.getString("name") ?: "Untitled Jam",
                        theme = doc.getString("theme") ?: "",
                        startDate = doc.getDate("startDate") ?: Date(),
                        endDate = doc.getDate("endDate") ?: Date(),
                        teamSize = doc.getLong("teamSize")?.toInt() ?: 1,
                        status = ProjectStatus.valueOf(doc.getString("status") ?: "PLANNING"),
                        creatorId = doc.getString("creatorId") ?: "",
                        memberIds = (doc.get("memberIds") as? List<String>) ?: emptyList()
                    )
                } catch (e: Exception) { null }
            }

        // Listener 1 — jams created by this user
        val ownerListener = db.collection("projects")
            .whereEqualTo("creatorId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot == null) return@addSnapshotListener
                parse(snapshot.documents).forEach { combined[it.id] = it }
                trySend(combined.values.toList().sortedByDescending { it.startDate })
            }

        // Listener 2 — jams where this user is in memberIds
        val memberListener = db.collection("projects")
            .whereArrayContains("memberIds", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot == null) return@addSnapshotListener
                parse(snapshot.documents).forEach { combined[it.id] = it }
                trySend(combined.values.toList().sortedByDescending { it.startDate })
            }

        awaitClose {
            ownerListener.remove()
            memberListener.remove()
        }
    }

    /**
     * Appends the current user's UID to a jam's memberIds array.
     * Called when the user opens a ludumforge://join?jamId=... link.
     */
    suspend fun acceptJamInvite(jamId: String, userId: String) {
        db.collection("projects").document(jamId)
            .update("memberIds", FieldValue.arrayUnion(userId))
            .await()
    }

    /**
     * Returns the jam name for a given projectId (used in the invite confirmation).
     */
    suspend fun getJamById(projectId: String): Project? {
        return try {
            val doc = db.collection("projects").document(projectId).get().await()
            if (!doc.exists()) return null
            @Suppress("UNCHECKED_CAST")
            Project(
                id = doc.id,
                name = doc.getString("name") ?: "Untitled Jam",
                theme = doc.getString("theme") ?: "",
                startDate = doc.getDate("startDate") ?: Date(),
                endDate = doc.getDate("endDate") ?: Date(),
                teamSize = doc.getLong("teamSize")?.toInt() ?: 1,
                status = ProjectStatus.valueOf(doc.getString("status") ?: "PLANNING"),
                creatorId = doc.getString("creatorId") ?: "",
                memberIds = (doc.get("memberIds") as? List<String>) ?: emptyList()
            )
        } catch (e: Exception) { null }
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
            "creatorId" to creatorId,
            "memberIds" to emptyList<String>()
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
