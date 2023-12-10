@file:UnstableApi

package de.yanos.islam.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import de.yanos.core.utils.IODispatcher
import de.yanos.core.utils.MainDispatcher
import de.yanos.islam.R
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.util.AppContainer
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@UnstableApi
@HiltWorker
class AudioWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val quranRepository: QuranRepository,
    private val downloadManager: DownloadManager,
    private val appContainer: AppContainer,
    private val appSettings: AppSettings,
) : CoroutineWorker(appContext, params) {
    private val controller get() = appContainer.audioController
    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            while (controller == null || quranRepository.ayahSize() != Constants.AYAH_TOTAL)
                delay(1000L)

            val items = quranRepository.ayahList().map {
                it.toMedia(applicationContext)
            }
            withContext(mainDispatcher) {
                items.groupBy { it.mediaId.toInt() / 100 }.forEach { (_, subItems) ->
                    controller?.addMediaItems(subItems)
                    delay(100)
                }
                //controller?.seekTo(appSettings.lastPlayedAyahIndex, 0)
                controller?.prepare()
            }
            rescheduleFailedDownloads()
            Result.success()
        }
    }

    private suspend fun rescheduleFailedDownloads() {
        val failedDownloads = mutableListOf<DownloadRequest>()
        val fails = downloadManager.downloadIndex.getDownloads(Download.STATE_FAILED)
        if (fails.count > 0) {
            for (i in 0..<fails.count) {
                fails.moveToPosition(i)
                failedDownloads.add(fails.download.request)
            }
            failedDownloads.forEach {
                downloadManager.removeDownload(it.id)
            }
            quranRepository.ayahList().filter { ayah -> failedDownloads.any { f -> f.id == ayah.id.toString() } }.forEach {
                quranRepository.loadMedia(it.id.toString(), it.audioAlt ?: it.audio)
            }
        }
    }
}


fun WorkManager.queueAudioWorker() {
    val uniqueWork = OneTimeWorkRequestBuilder<AudioWorker>()
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .build()
    enqueueUniqueWork(AudioWorker::class.java.name, ExistingWorkPolicy.REPLACE, uniqueWork)
}

fun Ayah.toMedia(context: Context) = MediaItem.Builder()
    .setMediaId(id.toString())
    .setUri(audio)
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setDescription(Constants.AUDIO)
            .setTitle(
                context.getString(R.string.sure_list_page, page.toString())
                        + ", "
                        + context.getString(R.string.sure_list_cuz, juz.toString())
                        + ", "
                        + context.getString(R.string.sure_ayet, number)
            )
            .setArtist(sureName)
            .build()
    )
    .build()