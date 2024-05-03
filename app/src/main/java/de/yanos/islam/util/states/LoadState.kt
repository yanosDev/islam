package de.yanos.islam.util.states

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

sealed interface LoadState<T> {
    data class Data<T>(val data: T) : LoadState<T>
    data class Failure<T>(val e: Exception, val code: Int = 0) : LoadState<T>
}

typealias FirebaseSignInResponse = LoadState<AuthResult>
typealias SignOutResponse = LoadState<Boolean>
typealias AuthStateResponse = StateFlow<FirebaseUser?>
