package dam.a51319.ludumforge.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import dam.a51319.ludumforge.models.Task
import dam.a51319.ludumforge.models.TaskCategory
import dam.a51319.ludumforge.models.TaskStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepository {

    private val db = FirebaseFirestore.getInstance()

    // Real-time listener for a specific project's tasks
    fun getTasksForProject(projectId: String): Flow<List<Task>> = callbackFlow {
        val listener = db.collection("tasks")
            .whereEqualTo("projectId", projectId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val tasks = snapshot.documents.map { doc ->
                        Task(
                            id = doc.id,
                            projectId = doc.getString("projectId") ?: "",
                            title = doc.getString("title") ?: "Untitled",
                            category = TaskCategory.valueOf(doc.getString("category") ?: "CODE"),
                            assignedTo = doc.getString("assignedTo"),
                            estimatedMinutes = doc.getLong("estimatedMinutes")?.toInt() ?: 0,
                            status = TaskStatus.valueOf(doc.getString("status") ?: "TODO")
                        )
                    }
                    trySend(tasks)
                }
            }

        // Remove listener when the flow is closed (e.g., user leaves screen)
        awaitClose { listener.remove() }
    }

    // Update just the status of a task (for drag-and-drop / clicking)
    suspend fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        try {
            db.collection("tasks").document(taskId)
                .update("status", newStatus.name)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}