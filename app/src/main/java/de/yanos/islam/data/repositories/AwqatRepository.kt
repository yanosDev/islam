package de.yanos.islam.data.repositories

import de.yanos.islam.data.repositories.source.LocalAwqatSource
import de.yanos.islam.data.repositories.source.RemoteAwqatSource
import de.yanos.islam.util.LoadState
import timber.log.Timber
import javax.inject.Inject

interface AwqatRepository {
    suspend fun auth()

    suspend fun fetchDailyContent()
}

class AwqatRepositoryImpl @Inject constructor(
    private val localSource: LocalAwqatSource,
    private val remoteSource: RemoteAwqatSource
) : AwqatRepository {
    override suspend fun auth() {
        remoteSource.auth()
    }

    override suspend fun fetchDailyContent() {
        val response = remoteSource.fetchDailyContent()
        (response as? LoadState.Data)?.let {
            localSource.insertDailyContent(it.data)
        }
        (response as? LoadState.Failure)?.let {
            Timber.e(it.e)
        }
    }

}