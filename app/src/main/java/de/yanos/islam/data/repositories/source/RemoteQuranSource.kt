@file:UnstableApi

package de.yanos.islam.data.repositories.source

import android.content.Context
import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.api.QuranApi
import de.yanos.islam.data.model.alquran.QuranAudioResponse
import de.yanos.islam.data.model.alquran.QuranTextResponse
import de.yanos.islam.service.ExoDownloadService
import de.yanos.islam.util.LoadState
import de.yanos.islam.util.localResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import timber.log.Timber
import javax.inject.Inject

interface RemoteQuranSource {
    suspend fun loadQuranTranslation(): LoadState<QuranTextResponse>
    suspend fun loadQuranTransliteration(): LoadState<QuranTextResponse>
    suspend fun loadQuranAudio(): LoadState<QuranAudioResponse>
    suspend fun loadMedia(id: String, url: String)
}

class RemoteQuranSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: QuranApi,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val downloadManager: DownloadManager
) : RemoteQuranSource {

    override suspend fun loadQuranTranslation(): LoadState<QuranTextResponse> {
        return withContext(dispatcher) {
            try {
                val response = api.loadQuranTranslation().awaitResponse()
                localResponse(response)
            } catch (e: Exception) {
                Timber.e(e)
                LoadState.Failure(e)
            }
        }
    }

    override suspend fun loadQuranTransliteration(): LoadState<QuranTextResponse> {
        return withContext(dispatcher) {
            try {
                val response = api.loadQuranTransliteration().awaitResponse()
                localResponse(response)
            } catch (e: Exception) {
                Timber.e(e)
                LoadState.Failure(e)
            }
        }
    }

    override suspend fun loadQuranAudio(): LoadState<QuranAudioResponse> {
        return withContext(dispatcher) {
            try {
                val response = api.loadQuranAudio().awaitResponse()
                localResponse(response)
            } catch (e: Exception) {
                Timber.e(e)
                LoadState.Failure(e)
            }
        }
    }

    override suspend fun loadMedia(id: String, url: String) {
        withContext(dispatcher) {
            val download = downloadManager.downloadIndex.getDownload(id)
            if (download != null) {
                val downloadRequest: DownloadRequest = DownloadRequest.Builder(id, Uri.parse(url)).build()
                DownloadService.sendAddDownload(
                    context,
                    ExoDownloadService::class.java,
                    downloadRequest,
                    false
                )
            }
        }
    }
}