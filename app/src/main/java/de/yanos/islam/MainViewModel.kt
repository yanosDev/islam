package de.yanos.islam

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.media.MediaPlayer
import android.net.Uri
import androidx.annotation.RawRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.common.util.concurrent.ListenableFuture
import com.maxrave.kotlinyoutubeextractor.State
import com.maxrave.kotlinyoutubeextractor.YTExtractor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.IslamDatabase
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.data.model.Schedule
import de.yanos.islam.data.model.Topic
import de.yanos.islam.data.model.TopicResource
import de.yanos.islam.data.model.TopicType
import de.yanos.islam.data.repositories.AwqatRepository
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.di.AzanPlayer
import de.yanos.islam.di.VideoPlayer
import de.yanos.islam.service.DailyScheduleWorker
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.getCurrentLocation
import de.yanos.islam.util.hasLocationPermission
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.Duration
import java.time.LocalDateTime
import java.util.Timer
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.timerTask

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class MainViewModel @Inject constructor(
    val appSettings: AppSettings,
    private val awqatRepository: AwqatRepository,
    @ApplicationContext private val context: Context,
    private val controllerFuture: ListenableFuture<MediaController>,
    private val db: IslamDatabase,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val geocoder: Geocoder,
    private val quranRepository: QuranRepository,
    @AzanPlayer private val mediaPlayer: MediaPlayer,
    @VideoPlayer private val videoPlayer: ExoPlayer,
    private val workManager: WorkManager
) : ViewModel() {
    var isReady: Boolean by mutableStateOf(false)
    private var timer: Timer? = null
    private var controller: MediaController? = null
    private val yt = YTExtractor(con = context, CACHING = true, LOGGING = true, retryCount = 3)
    private val tecvidList = mutableStateListOf(
        "dDtzLHC4U_4",
        "vKPeJqFiiig",
        "ltzIYbqWpJQ",
        "a4couhHIX8I",
        "12bn2RQ0M9Y",
        "v_3edYHVzO0",
        "yqAFqln5_Mw",
        "oArREfO1bg8",
        "Q9AUm8a7eKM",
        "wlqn4ldcah0",
        "dhGCeC4-I_k",
        "pF8cB0hKcwU",
        "bt9bOYpyVQM",
        "sXjZ-cSr3oI",
    )
    private val quranList = mutableStateListOf(
        "3ZIjLikIin0",
        "nOmoQPzii9c",
        "c-IHoD9eDN4",
        "Qsavtk3P1R4",
        "w-kKDf43tdY",
        "QK3i7h24Hbk",
        "tCcPWw72QwM",
        "tV60vMO2-VY",
        "5aAICtquAfQ",
        "2_0VSQWxUU4",
        "mKSLYTV4DUA",
        "dFxMhRAhpJQ",
        "dwKP3yVPZgo",
        "OkPOmzEJ-4g",
    )

    init {
        controllerFuture.addListener({
            controller = controllerFuture.get()
        }, dispatcher.asExecutor())

        viewModelScope.launch {
            while (controller == null)
                delay(100)
            quranRepository.loadAyahs().collect { ayahs ->
                controller?.let {
                    ayahs.groupBy { ayah -> ayah.page }.forEach { (_, value) ->
                        it.addMediaItems(value.map { ayah ->
                            MediaItem.Builder()
                                .setMediaId(ayah.id.toString())
                                .setUri(ayah.audio)
                                .setMediaMetadata(
                                    MediaMetadata.Builder()
                                        .setTitle(
                                            context.getString(R.string.sure_list_page, ayah.page.toString())
                                                    + ", "
                                                    + context.getString(R.string.sure_list_cuz, ayah.juz.toString())
                                                    + ", "
                                                    + context.getString(R.string.sure_ayet, ayah.number)
                                        )
                                        .setArtist(ayah.sureName)
                                        .build()
                                )
                                .build()
                        })

                        delay(100)
                    }


                    it.prepare()
                }
            }
        }
        viewModelScope.launch {
            tecvidList.forEach { id ->
                withContext(dispatcher) {
                    yt.extract(id)
                }
                if (yt.state == State.SUCCESS) {
                    yt.getYTFiles()?.get(22)?.let { file ->
                        yt.getVideoMeta()?.let { meta ->
                            videoPlayer.addMediaItem(
                                MediaItem.Builder()
                                    .setMediaId(id)
                                    .setUri(Uri.parse(file.url))
                                    .setMediaMetadata(
                                        MediaMetadata.Builder()
                                            .setArtworkUri(Uri.parse(meta.sdImageUrl))
                                            .setTitle(meta.title)
                                            .setSubtitle(meta.shortDescription)
                                            .setArtist(meta.author)
                                            .build()
                                    )
                                    .build()
                            )
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            quranList.forEach { id ->
                withContext(dispatcher) {
                    yt.extract(id)
                }
                if (yt.state == State.SUCCESS) {
                    yt.getYTFiles()?.get(22)?.let { file ->
                        yt.getVideoMeta()?.let { meta ->
                            videoPlayer.addMediaItem(
                                MediaItem.Builder()
                                    .setMediaId(id)
                                    .setUri(Uri.parse(file.url))
                                    .setMediaMetadata(
                                        MediaMetadata.Builder()
                                            .setArtworkUri(Uri.parse(meta.sdImageUrl))
                                            .setTitle(meta.title)
                                            .setSubtitle(meta.shortDescription)
                                            .setArtist(meta.author)
                                            .build()
                                    )
                                    .build()
                            )
                        }
                    }
                }
            }
        }
    }

    fun startSchedule() {
        timer = Timer()
        timer?.scheduleAtFixedRate(
            timerTask()
            {
                viewModelScope.launch(Dispatchers.Main) {
                    if (hasLocationPermission(context))
                        getCurrentLocation(context = context) { lat, lon ->
                            @Suppress("DEPRECATION")
                            geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()?.let { address ->
                                (address.subAdminArea ?: address.adminArea)?.let { name ->
                                    appSettings.lastLocation = name
                                    loadLocationDependentData()
                                }
                            }
                        }
                    loadLocationIndependentData()
                }
            }, 0, 60000
        )
    }

    fun cancelSchedule() {
        timer?.cancel()
    }

    fun cancelAllNotifications() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
    }

    private fun loadLocationDependentData() {
        if (isReady)
            viewModelScope.launch {
                Timber.e("MAIN: loadLocationDependentData")
                appSettings.lastLocation.takeIf { it.isNotBlank() }?.let {
                    awqatRepository.fetchCityData(it)
                }
            }
    }

    private var isLoading = false
    private suspend fun loadLocationIndependentData() {
        viewModelScope.launch {
            if (!isReady && !isLoading) {
                isLoading = true
                listOf(
                    async { initDailyWorker() },
                    async { loadDailyAwqatList() },
                    async { loadQuran() },
                ).awaitAll()
                isReady = if (!appSettings.isDBInitialized) {
                    listOf(
                        async { initDB() }
                    ).awaitAll()
                    true
                } else true
            }
        }
    }

    private suspend fun loadQuran() {
        return withContext(dispatcher) {
            if (!quranRepository.isWholeQuranFetched())
                quranRepository.fetchQuran()
        }
    }

    private fun initDailyWorker(): Boolean {
        val now = LocalDateTime.now()
        val delay = when {
            now.hour < 1 -> Duration.ofHours(0L)
            else -> Duration.ofHours(24L - now.hour)
        }.plusMinutes(20)
        val periodicWorkRequest = PeriodicWorkRequestBuilder<DailyScheduleWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay)
            .build()
        workManager.enqueueUniquePeriodicWork("daily", ExistingPeriodicWorkPolicy.UPDATE, periodicWorkRequest)
        return true
    }

    private suspend fun loadDailyAwqatList() {
        withContext(dispatcher) {
            awqatRepository.fetchAwqatLocationIndependentData()
        }
    }

    private suspend fun initDB() {
        withContext(dispatcher) {
            if (!appSettings.isDBInitialized) {
                TopicResource.values().forEach { topic ->
                    topic.raw?.let { raw ->
                        createQuizByTopic(raw, topic.id)
                    }
                    db.topicDao().insert(
                        Topic(
                            id = topic.id, title = topic.title, ordinal = topic.ordinal, parentId = topic.parent, type = when {
                                topic.parent == null && topic.raw != null -> TopicType.MAIN
                                topic.parent == null -> TopicType.GROUP
                                else -> TopicType.SUB
                            }
                        )
                    )
                }

                db.awqatDao().insertSchedules(
                    listOf(
                        Schedule(
                            id = "fajr",
                            ordinal = 0
                        ),
                        Schedule(
                            id = "sunrise",
                            ordinal = 1,
                            enabled = true,
                            relativeTime = -45
                        ),
                        Schedule(
                            id = "dhuhr",
                            ordinal = 2
                        ),
                        Schedule(
                            id = "asr",
                            ordinal = 3
                        ),
                        Schedule(
                            id = "maghrib",
                            ordinal = 4,
                            enabled = true,
                            relativeTime = -5
                        ),
                        Schedule(
                            id = "isha",
                            ordinal = 5
                        ),
                    )
                )
                appSettings.isDBInitialized = true
            }
        }
    }

    private fun createQuizByTopic(@RawRes topicRaw: Int, topicId: Int) {
        val regex = Regex("""\d+|\D+""")
        val inputStream: InputStream = context.resources.openRawResource(topicRaw)
        val br = BufferedReader(InputStreamReader(inputStream))
        val lines = br.readLines()
        lines.forEachIndexed { index, line ->
            if (line.trim().startsWith("*")) {
                val actualLine = mergeLines(lines, index)
                    .replace("*", "")
                    .replace("  ", " ")
                    .trim()
                try {
                    actualLine.split("?").let { (question, answer) ->
                        val parts = regex.findAll(answer).map { it.groupValues.first() }.toList()
                        val builder = StringBuilder()
                        parts.forEach {
                            val text = it.trim()
                            if (text.isNotBlank())
                                when {
                                    !it.matches(Regex("""\d+""")) || builder.isEmpty() -> builder.append(" $text")
                                    else -> builder.append("\n$text ")
                                }
                        }
                        db.quizDao().insert(Quiz(question = question, answer = builder.toString().replace("  ", " ").trim(), topicId = topicId, difficulty = 0))
                    }
                } catch (e: Exception) {
                    Timber.e(actualLine)
                    e.localizedMessage?.let { Timber.e("e", it) }
                }
            }
        }
    }

    private fun mergeLines(lines: List<String>, index: Int): String {
        val currentLine = lines[index]
        return if (index == lines.size - 1 || lines[index + 1].trim().startsWith("*")) {
            currentLine
        } else currentLine.trim() + " " + mergeLines(lines, index + 1).trim()
    }
}