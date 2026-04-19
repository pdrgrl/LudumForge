package dam.a51319.ludumforge.data.repositories

import dam.a51319.ludumforge.data.ActionLog
import dam.a51319.ludumforge.data.daos.ActionLogDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class ActionLogRepository(private val dao: ActionLogDao) {

    private val db = FirebaseFirestore.getInstance()
    // Returns a continuous stream of logs from the local database
    fun getLogsForProject(projectId: String): Flow<List<ActionLog>> {
        return dao.getLogsForProject(projectId)
    }

    // Inserts a new note immediately into local storage
    suspend fun addManualNote(projectId: String, noteText: String) {
        val log = ActionLog(
            id = java.util.UUID.randomUUID().toString(), // Generates a unique string ID
            projectId = projectId,
            message = noteText,
            type = "DEV_NOTE",
            timestamp = System.currentTimeMillis(),
            isSynced = false // It's offline until the sync worker picks it up later
        )
        dao.insertLog(log)
    }

    // We will use this later when generating AI roadmaps or completing tasks!
    suspend fun addSystemEvent(projectId: String, eventMessage: String) {
        val log = ActionLog(
            id = java.util.UUID.randomUUID().toString(),
            projectId = projectId,
            message = eventMessage,
            type = "SYSTEM",
            timestamp = System.currentTimeMillis(),
            isSynced = false
        )
        dao.insertLog(log)
    }

    //Sync function
    suspend fun syncPendingLogs() {
        val unsyncedLogs = dao.getUnsyncedLogs()
        if (unsyncedLogs.isEmpty()) return

        for (log in unsyncedLogs) {
            try {
                val logMap = hashMapOf(
                    "id" to log.id,
                    "projectId" to log.projectId,
                    "message" to log.message,
                    "type" to log.type,
                    "timestamp" to log.timestamp
                )
                // Push to Firestore
                db.collection("action_logs").document(log.id).set(logMap).await()

                // If successful, mark as synced in local Room DB
                dao.markAsSynced(log.id)
            } catch (e: Exception) {
                // If the internet is down, the await() fails and we drop to the catch block.
                // The log stays isSynced = false, ready for the next try.
                e.printStackTrace()
            }
        }
    }
}