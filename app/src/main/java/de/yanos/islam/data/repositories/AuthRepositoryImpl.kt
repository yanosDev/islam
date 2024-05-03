package de.yanos.islam.data.repositories

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import de.yanos.islam.util.states.AuthStateResponse
import de.yanos.islam.util.states.FirebaseSignInResponse
import de.yanos.islam.util.states.LoadState
import de.yanos.islam.util.states.SignOutResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {
    override fun getAuthState(viewModelScope: CoroutineScope): AuthStateResponse {
        return callbackFlow {
            // 1.
            val authStateListener = FirebaseAuth.AuthStateListener { auth ->
                // 4.
                trySend(auth.currentUser)
                Timber.i("User: ${auth.currentUser?.uid ?: "Not authenticated"}")
            }
            // 2.
            auth.addAuthStateListener(authStateListener)
            // 3.
            awaitClose {
                auth.removeAuthStateListener(authStateListener)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), auth.currentUser)
    }

    override suspend fun signInAnonymously(): FirebaseSignInResponse {
        return try {
            val authResult = auth.signInAnonymously().await()
            authResult?.user?.let { user ->
                Timber.i("FirebaseAuthSuccess: Anonymous UID: ${user.uid}")
            }
            LoadState.Data(authResult)
        } catch (error: Exception) {
            Timber.e("FirebaseAuthError: Failed to Sign in anonymously")
            LoadState.Failure(error)
        }
    }

    // 1.
    private suspend fun authenticateUser(credential: AuthCredential): FirebaseSignInResponse {
        // If we have auth user, link accounts, otherwise sign in.
        return auth.currentUser?.let {
            authLink(it, credential)
        } ?: authSignIn(credential)
    }

    // 2.
    private suspend fun authSignIn(credential: AuthCredential): FirebaseSignInResponse {
        return try {
            val authResult = auth.signInWithCredential(credential).await()
            Timber.i("User: ${authResult?.user?.uid}")
            LoadState.Data(authResult)
        } catch (error: Exception) {
            LoadState.Failure(error)
        }
    }

    // 3.
    private suspend fun authLink(user: FirebaseUser, credential: AuthCredential): FirebaseSignInResponse {
        return try {
            val authResult = user.linkWithCredential(credential).await()
            Timber.i("User: ${authResult?.user?.uid}")
            LoadState.Data(authResult)
        } catch (error: Exception) {
            LoadState.Failure(error)
        }
    }


    override suspend fun signOut(): SignOutResponse {
        return try {
            auth.signOut()
            LoadState.Data(true)
        } catch (e: java.lang.Exception) {
            LoadState.Failure(e)
        }
    }
}