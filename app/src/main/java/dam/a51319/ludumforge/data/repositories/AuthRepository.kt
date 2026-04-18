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
import kotlinx.coroutines.tasks.await
import dam.a51319.ludumforge.models.User
import dam.a51319.ludumforge.models.UserRole

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getCurrentUser(): FirebaseUser? = try { auth.currentUser } catch (_: Exception) { null }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            // Default username to the part of the email before the @
            saveUserToFirestore(user, username = email.substringBefore("@"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(context: Context, webClientId: String): Result<FirebaseUser> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
//                .setAutoSelectEnabled(true)
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

            // Only write to Firestore if this is a brand-new Google account signup
            if (result.additionalUserInfo?.isNewUser == true) {
                saveUserToFirestore(user, username = user.displayName ?: "New User")
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveUserToFirestore(user: FirebaseUser, username: String) {
        val userMap = hashMapOf(
            "id" to user.uid,
            "email" to (user.email ?: ""),
            "username" to username,
            "role" to "DEVELOPER" // Default role
        )
        // Save to "users" collection using the UID as the document ID
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
                    role = UserRole.valueOf(snapshot.getString("role") ?: "DEVELOPER")
                )
            } else {
                // Document doesn't exist (e.g., old Google login). Create it now!
                val email = firebaseUser.email ?: ""
                val defaultUsername = if (email.contains("@")) email.substringBefore("@") else "Architect"

                val newUser = User(
                    id = firebaseUser.uid,
                    username = defaultUsername,
                    email = email,
                    role = UserRole.DEVELOPER
                )

                val userMap = hashMapOf(
                    "id" to newUser.id,
                    "email" to newUser.email,
                    "username" to newUser.username,
                    "role" to newUser.role.name
                )
                docRef.set(userMap).await()
                newUser
            }
        } catch (e: Exception) {
            null
        }
    }


    fun signOut() {
        try { auth.signOut() } catch (_: Exception) {}
    }
}