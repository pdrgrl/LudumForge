package dam.a51319.ludumforge.data.repositories

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dam.a51319.ludumforge.models.User
import dam.a51319.ludumforge.models.UserPlan
import dam.a51319.ludumforge.models.UserRole
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUser(): FirebaseUser? = try { auth.currentUser } catch (_: Exception) { null }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun signUp(email: String, password: String, username: String, role: UserRole): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            saveUserToFirestore(user, username, role)
            Result.success(user)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun signInWithGoogle(context: Context, webClientId: String): Result<FirebaseUser> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val credentialManager = CredentialManager.create(context)
            val response = credentialManager.getCredential(context, request)
            val googleIdToken = GoogleIdTokenCredential.createFrom(response.credential.data).idToken
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val result = auth.signInWithCredential(firebaseCredential).await()
            val user = result.user!!
            if (result.additionalUserInfo?.isNewUser == true) {
                saveUserToFirestore(user, username = user.displayName ?: "New User", role = UserRole.DEVELOPER)
            }
            Result.success(user)
        } catch (e: Exception) { Result.failure(e) }
    }

    private suspend fun saveUserToFirestore(user: FirebaseUser, username: String, role: UserRole) {
        val userMap = hashMapOf(
            "id" to user.uid,
            "email" to (user.email ?: ""),
            "username" to username,
            "role" to role.name,
            "plan" to "FREE"
        )
        db.collection("users").document(user.uid).set(userMap).await()
    }

    suspend fun getUserProfile(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return try {
            val docRef = db.collection("users").document(firebaseUser.uid)
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                User(
                    id = snapshot.getString("id") ?: firebaseUser.uid,
                    username = snapshot.getString("username") ?: "Unknown",
                    email = snapshot.getString("email") ?: firebaseUser.email ?: "",
                    role = UserRole.valueOf(snapshot.getString("role") ?: "DEVELOPER"),
                    plan = UserPlan.valueOf(snapshot.getString("plan") ?: "FREE")
                )
            } else {
                val email = firebaseUser.email ?: ""
                val defaultUsername = firebaseUser.displayName
                    ?: if (email.contains("@")) email.substringBefore("@") else "Architect"
                val newUser = User(
                    id = firebaseUser.uid,
                    username = defaultUsername,
                    email = email,
                    role = UserRole.DEVELOPER,
                    plan = UserPlan.FREE
                )
                val userMap = hashMapOf(
                    "id" to newUser.id,
                    "email" to newUser.email,
                    "username" to newUser.username,
                    "role" to newUser.role.name,
                    "plan" to newUser.plan.name
                )
                docRef.set(userMap).await()
                newUser
            }
        } catch (e: Exception) { null }
    }

    suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = db.collection("users").get().await()
            snapshot.documents.mapNotNull { doc ->
                User(
                    id = doc.getString("id") ?: doc.id,
                    username = doc.getString("username") ?: "Unknown",
                    email = doc.getString("email") ?: "",
                    role = UserRole.valueOf(doc.getString("role") ?: "DEVELOPER"),
                    plan = UserPlan.valueOf(doc.getString("plan") ?: "FREE")
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    /** Flip the caller's Firestore doc to PREMIUM. Call this after payment confirmation. */
    suspend fun upgradeToPremium(): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Not signed in"))
        return try {
            db.collection("users").document(uid)
                .update("plan", "PREMIUM")
                .await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    fun signOut() { try { auth.signOut() } catch (_: Exception) {} }
}
