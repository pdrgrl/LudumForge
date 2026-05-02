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

    fun getTasksForProject(projectId: String): Flow<List<Task>> = callbackFlow {
        val listener = db.collection("tasks")
            .whereEqualTo("projectId", projectId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
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
        awaitClose { listener.remove() }
    }

    suspend fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        try {
            db.collection("tasks").document(taskId)
                .update("status", newStatus.name)
                .await()
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun updateTask(taskId: String, updates: Map<String, Any?>) {
        try {
            db.collection("tasks").document(taskId)
                .update(updates)
                .await()
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun deleteTask(taskId: String) {
        try {
            db.collection("tasks").document(taskId)
                .delete()
                .await()
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun addTask(
        projectId: String,
        title: String,
        category: TaskCategory,
        estimatedMinutes: Int,
        assignedTo: String?
    ) {
        try {
            val newTask = hashMapOf(
                "projectId" to projectId,
                "title" to title,
                "category" to category.name,
                "status" to TaskStatus.TODO.name,
                "estimatedMinutes" to estimatedMinutes,
                "assignedTo" to assignedTo
            )
            db.collection("tasks").add(newTask).await()
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun getTasksForUser(userId: String): Flow<List<Task>> = callbackFlow {
        val listener = db.collection("tasks")
            .whereEqualTo("assignedTo", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
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
        awaitClose { listener.remove() }
    }

    suspend fun deleteTasksForProject(projectId: String) {
        try {
            val snapshot = db.collection("tasks")
                .whereEqualTo("projectId", projectId)
                .get()
                .await()
            if (snapshot.isEmpty) return
            snapshot.documents.chunked(400).forEach { chunk ->
                val batch = db.batch()
                chunk.forEach { doc -> batch.delete(doc.reference) }
                batch.commit().await()
            }
        } catch (e: Exception) { e.printStackTrace() }
    }
}
