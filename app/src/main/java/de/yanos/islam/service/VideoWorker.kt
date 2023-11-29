package de.yanos.islam.service

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.hilt.work.HiltWorker
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.maxrave.kotlinyoutubeextractor.State
import com.maxrave.kotlinyoutubeextractor.YTExtractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import de.yanos.core.utils.IODispatcher
import de.yanos.core.utils.MainDispatcher
import de.yanos.islam.data.database.dao.VideoDao
import de.yanos.islam.data.model.VideoLearning
import de.yanos.islam.util.AppContainer
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.safeLet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID

@HiltWorker
class VideoWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val appContainer: AppContainer,
    private val appSettings: AppSettings,
    private val dao: VideoDao
) : CoroutineWorker(appContext, params) {
    private val controller get() = appContainer.videoController

    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            while (controller == null)
                delay(1000)

            if (dao.loadAll().isEmpty()) {
                val yt = YTExtractor(con = applicationContext, CACHING = false, LOGGING = true, retryCount = 3)
                basics.mapIndexedNotNull { index, s ->
                    extractVideo(s, index, yt)
                }.let {
                    dao.insertImmediate(it)
                }
                tecvids.mapIndexedNotNull { index, s ->
                    extractVideo(s, index + basics.size, yt)
                }.let {
                    dao.insertImmediate(it)
                }
            }
            val currentList = dao.loadAll()
            if (currentList.isEmpty())
                Result.retry()
            else {
                val items = currentList.map {
                    it.toMedia()
                }
                withContext(mainDispatcher) {
                    items.forEach {
                        controller?.addMediaItem(it)
                        delay(100)
                    }
                    controller?.seekTo(appSettings.lastPlayedLearningIndex, 0)
                    controller?.prepare()
                }
                Result.success()
            }
        }
    }

    private suspend fun extractVideo(it: String, index: Int, yt: YTExtractor): VideoLearning? {
        yt.extract(it)
        if (yt.state != State.SUCCESS) return null
        return safeLet(yt.getYTFiles()?.get(22), yt.getVideoMeta()) { file, meta ->
            VideoLearning(
                id = meta.videoId ?: UUID.randomUUID().toString(),
                index = index,
                remoteUrl = file.url ?: "",
                thumbRemoteUrl = meta.thumbUrl.replace("http:", "https:"),
                title = meta.title ?: "",
                description = meta.shortDescription ?: "",
                author = meta.author ?: ""
            )
        }
    }

    companion object {
        private val tecvids = mutableStateListOf(
            "dDtzLHC4U_4", "vKPeJqFiiig",
            "ltzIYbqWpJQ", "a4couhHIX8I",
            "12bn2RQ0M9Y", "v_3edYHVzO0",
            "yqAFqln5_Mw", "oArREfO1bg8",
            "Q9AUm8a7eKM", "wlqn4ldcah0",
            "dhGCeC4-I_k", "pF8cB0hKcwU",
            "bt9bOYpyVQM", "sXjZ-cSr3oI",
        )
        private val basics = mutableStateListOf(
            "3ZIjLikIin0", "nOmoQPzii9c",
            "c-IHoD9eDN4", "Qsavtk3P1R4",
            "w-kKDf43tdY", "QK3i7h24Hbk",
            "tCcPWw72QwM", "tV60vMO2-VY",
            "5aAICtquAfQ", "2_0VSQWxUU4",
            "mKSLYTV4DUA", "dFxMhRAhpJQ",
            "dwKP3yVPZgo", "OkPOmzEJ-4g",
        )
    }
}

fun WorkManager.queueVideoWorker() {
    val uniqueWork = OneTimeWorkRequestBuilder<VideoWorker>()
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .build()
    enqueueUniqueWork(VideoWorker::class.java.name, ExistingWorkPolicy.REPLACE, uniqueWork)
}

fun VideoLearning.toMedia() = MediaItem.Builder()
    .setMediaId(id)
    .setUri(Uri.parse(remoteUrl))
    .setMediaMetadata(
        MediaMetadata.Builder()
            .setArtworkUri(Uri.parse(thumbRemoteUrl))
            .setTitle(title)
            .setSubtitle(description)
            .setArtist(author)
            .build()
    )
    .build()