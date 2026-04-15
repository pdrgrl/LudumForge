package dam.a51319.ludumforge.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import android.content.Context
import com.google.firebase.auth.GoogleAuthProvider

class AuthRepository {

    /**
     * Gets the currently logged-in user, or null if no one is logged in.
     */
    fun getCurrentUser(): FirebaseUser? {
        return try {
            FirebaseAuth.getInstance().currentUser
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Signs in an existing user with email and password.
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val auth = FirebaseAuth.getInstance()
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User data is null after sign in.")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registers a new user with email and password.
     */
    suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val auth = FirebaseAuth.getInstance()
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User data is null after sign up.")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs out the current user.
     */
    fun signOut() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (_: Exception) {
            // Ignore when Firebase is unavailable; this keeps the app from crashing at startup.
        }
    }

    suspend fun signInWithGoogle(context: Context, webClientId: String): Result<FirebaseUser> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // show all accounts, not just previously used
                .setServerClientId(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(context)
            val response = credentialManager.getCredential(context, request)
            val credential = response.credential

            val googleIdToken = GoogleIdTokenCredential
                .createFrom(credential.data)
                .idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val auth = FirebaseAuth.getInstance()
            val result = auth.signInWithCredential(firebaseCredential).await()
            val user = result.user ?: throw Exception("Google sign-in returned null user")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)

        }
        }
}