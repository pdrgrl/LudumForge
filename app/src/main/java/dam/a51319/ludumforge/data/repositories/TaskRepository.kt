package dam.a51319.ludumforge.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import dam.a51319.ludumforge.models.Task
import dam.a51319.ludumforge.models.TaskStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Adds a task to a specific project's sub-collection: projects/{projectId}/tasks/{taskId}
     */
    suspend fun addTask(task: Task): Result<Unit> {
        return try {
            firestore.collection("projects")
                .document(task.projectId)
                .collection("tasks")
                .document(task.id)
                .set(task)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates only the status field of a specific task.
     * Useful for drag-and-drop Kanban boards.
     */
    suspend fun updateTaskStatus(projectId: String, taskId: String, newStatus: TaskStatus): Result<Unit> {
        return try {
            firestore.collection("projects")
                .document(projectId)
                .collection("tasks")
                .document(taskId)
                .update("status", newStatus.name) // Using Enum's name as string
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Real-time listener for all tasks inside a specific project.
     * Automatically emits a new list of Tasks whenever anyone in the team adds, edits, or deletes a task.
     */
    fun listenToTasks(projectId: String): Flow<List<Task>> = callbackFlow {
        val listenerRegistration = firestore.collection("projects")
            .document(projectId)
            .collection("tasks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val tasks = snapshot.toObjects(Task::class.java)
                    trySend(tasks)
                }
            }

        // Suspends until the Flow collector is cancelled, then cleans up the Firestore listener
        awaitClose {
            listenerRegistration.remove()
        }
    }
}