package de.yanos.islam.data.repositories

import de.yanos.islam.util.states.AuthStateResponse
import de.yanos.islam.util.states.FirebaseSignInResponse
import de.yanos.islam.util.states.SignOutResponse
import kotlinx.coroutines.CoroutineScope

interface AuthRepository {
    fun getAuthState(viewModelScope: CoroutineScope): AuthStateResponse

    suspend fun signInAnonymously(): FirebaseSignInResponse

    suspend fun signOut(): SignOutResponse
}