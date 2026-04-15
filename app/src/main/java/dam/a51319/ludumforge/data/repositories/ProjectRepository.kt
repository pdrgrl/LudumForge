package dam.a51319.ludumforge.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import dam.a51319.ludumforge.models.Project
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProjectRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val projectsCollection = firestore.collection("projects")

    /**
     * Creates a new project document in Firestore.
     */
    suspend fun createProject(project: Project): Result<Unit> {
        return try {
            // Uses the project.id as the document ID
            projectsCollection.document(project.id).set(project).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches all projects associated with a specific user.
     * Note: This assumes you eventually add an `ownerId` or `members` array to your Project model.
     * For now, it performs a basic fetch (you can adapt the whereEqualTo query to your exact data structure).
     */
    suspend fun getProjectsByUser(userId: String): Result<List<Project>> {
        return try {
            val snapshot = projectsCollection
                // .whereArrayContains("membersList", userId) // Uncomment when your data structure links users to projects
                .get()
                .await()

            val projects = snapshot.toObjects(Project::class.java)
            Result.success(projects)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Real-time listener for a single project document.
     * Returns a Flow that emits a new Project object every time the document changes on the server.
     */
    fun listenToProject(projectId: String): Flow<Project?> = callbackFlow {
        val listenerRegistration = projectsCollection.document(projectId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val project = snapshot.toObject(Project::class.java)
                    trySend(project)
                } else {
                    trySend(null)
                }
            }

        // Suspends until the Flow collector is cancelled, then cleans up the Firestore listener
        awaitClose {
            listenerRegistration.remove()
        }
    }
}