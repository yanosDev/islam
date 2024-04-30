package de.yanos.islam.util.states

sealed interface LoadState<T> {
    data class Data<T>(val data: T) : LoadState<T>
    data class Failure<T>(val e: Exception, val code: Int = 0) : LoadState<T>
}