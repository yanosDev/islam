package de.yanos.islam.data.repositories.source

import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.api.TanzilApi
import de.yanos.islam.data.model.tanzil.TanzilQuranResponse
import de.yanos.islam.data.model.tanzil.TanzilSureResponse
import de.yanos.islam.util.LoadState
import de.yanos.islam.util.localResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import timber.log.Timber
import javax.inject.Inject

interface RemoteQuranSource {
    suspend fun loadQuranSummary(): LoadState<TanzilQuranResponse>
    suspend fun loadSure(nr: String): LoadState<TanzilSureResponse>
}


class RemoteQuranSourceImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val api: TanzilApi
) : RemoteQuranSource {
    override suspend fun loadQuranSummary(): LoadState<TanzilQuranResponse> {
        return withContext(dispatcher) {
            try {
                val response = api.loadQuran().awaitResponse()
                localResponse(response)
            } catch (e: Exception) {
                Timber.e(e)
                LoadState.Failure(e)
            }
        }
    }

    override suspend fun loadSure(nr: String): LoadState<TanzilSureResponse> {
        return withContext(dispatcher) {
            try {
                val response = api.loadSure(nr).awaitResponse()
                localResponse(response)
            } catch (e: Exception) {
                Timber.e(e)
                LoadState.Failure(e)
            }
        }
    }

}