package de.yanos.islam.service

import android.content.Context
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
import com.maxrave.kotlinyoutubeextractor.YTExtractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import de.yanos.core.utils.IODispatcher
import de.yanos.core.utils.MainDispatcher
import de.yanos.islam.R
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.data.model.VideoLearning
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.util.AppContainer
import de.yanos.islam.util.safeLet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID

@HiltWorker
class AudioWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val dao: QuranDao,
    private val appContainer: AppContainer,
) : CoroutineWorker(appContext, params) {

    private val controller get() = appContainer.audioController
    override suspend fun doWork(): Result {
        return withContext(dispatcher) {
            while (controller == null) {
                delay(1000L)
            }
            if (dao.ayahSize() != 6236) {
                delay(5000)
                Result.retry()
            } else {
                val items = dao.ayahList().map {
                    it.toMedia(applicationContext)
                }
                withContext(mainDispatcher) {
                    Timber.e("PERFORMANCE: ${System.currentTimeMillis()}")
                    items.groupBy { it.mediaId.toInt() / 100 }.forEach { (_, subItems) ->
                        controller?.addMediaItems(subItems)
                        delay(100)
                    }
                    controller?.prepare()
                    Timber.e("PERFORMANCE END: ${System.currentTimeMillis()}")
                }
                Result.success()
            }
        }
    }

    private suspend fun extractVideo(it: String, yt: YTExtractor): VideoLearning? {
        yt.extract(it)
        return safeLet(yt.getYTFiles()?.get(22), yt.getVideoMeta()) { file, meta ->
            VideoLearning(
                id = meta.videoId ?: UUID.randomUUID().toString(),
                remoteUrl = file.url ?: "",
                thumbRemoteUrl = meta.thumbUrl,
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