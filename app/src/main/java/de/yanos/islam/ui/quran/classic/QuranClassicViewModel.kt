@file:OptIn(ExperimentalFoundationApi::class)

package de.yanos.islam.ui.quran.classic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Page
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.util.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Timer
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.timerTask
import kotlin.math.max

@HiltViewModel
class QuranClassicViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val controllerFuture: ListenableFuture<MediaController>,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val repository: QuranRepository,
) : ViewModel() {
    var duration by mutableLongStateOf(0L)
    var progress by mutableFloatStateOf(0F)
    var progressString by mutableStateOf("00:00")
    var isPlaying by mutableStateOf(false)

    private var timer: Timer? = null
    private var controller: MediaController? = null

    var showDetailSheet by mutableStateOf(false)
    var referenceAyah by mutableStateOf<Ayah?>(null)

    var pages = mutableStateListOf<Page>()

    val quranStyle get() = appSettings.quranStyle
    val quranSizeFactor get() = appSettings.quranSizeFactor

    init {
        controllerFuture.addListener({
            controller = controllerFuture.get()
            controller?.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    viewModelScope.launch {
                        refreshData()
                    }
                }
            })
        }, dispatcher.asExecutor())
        viewModelScope.launch(dispatcher) {
            repository.loadPages().collect {
                withContext(Dispatchers.Main) {
                    pages.clear()
                    pages.addAll(it)
                }
            }
        }
    }

    private fun startTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(
            timerTask()
            {
                viewModelScope.launch {
                    refreshData()
                }
            }, 0, 50
        )
    }

    private suspend fun refreshData() {
        controller?.let {
            it.currentMediaItem?.mediaId?.toInt()?.let { id ->
                if (id != referenceAyah?.id)
                    referenceAyah = repository.loadAyahById(id)
            }
            duration = it.duration
            progress = max(((it.currentPosition.toFloat() / it.duration.toFloat()) * 100F), 5F)
            progressString = formatDuration(it.currentPosition)
            isPlaying = it.isPlaying
        }
    }

    private fun formatDuration(duration: Long): String {
        val minute = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (minute) - minute * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
        return String.format("%02d:%02d", minute, seconds)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    fun onAudioEvents(event: AudioEvents) {
        controller?.let {
            Timber.e(event.javaClass.toString())
            viewModelScope.launch {
                when (event) {
                    AudioEvents.PlayAudio -> {
                        startTimer()
                        it.play()
                    }

                    AudioEvents.PauseAudio -> {
                        it.pause()
                    }

                    is AudioEvents.UpdateProgress -> {
                        it.seekTo((event.newProgress / 100 * it.duration).toLong())
                    }

                    is AudioEvents.PlayPrevious -> {
                        controller?.seekToPreviousMediaItem()
                        repository.loadAyahById(controller?.currentMediaItem?.mediaId?.toInt() ?: -1)?.let { ayah ->
                            repository.loadAudioAlt(ayah)
                        }
                    }

                    is AudioEvents.PlayNext -> {
                        controller?.seekToNextMediaItem()
                        repository.loadAyahById(controller?.currentMediaItem?.mediaId?.toInt() ?: -1)?.let { ayah ->
                            repository.loadAudioAlt(ayah)
                        }
                    }

                    is AudioEvents.CloseAudio -> {
                        stopTimer()
                        it.pause()
                        duration = 0
                        progress = 0F
                        progressString = "00:00"
                        referenceAyah = null
                        showDetailSheet = false
                    }
                }
            }
        }
    }

    fun onSelectionChange(selection: QuranSelection) {
        viewModelScope.launch {
            when (selection) {
                is AyahSelection -> repository.loadAyahById(selection.ayahId)
                is SurahSelection -> repository.loadFirstAyahBySurahId(selection.surahId)
                is PageSelection -> repository.loadFirstAyahByPageId(selection.page)
                is JuzSelection -> repository.loadFirstAyahByJuz(selection.juz)
                else -> null
            }?.let { ayah ->
                referenceAyah = ayah
                controller?.seekTo(ayah.id - 1, 0)
                if (controller?.isPlaying == false)
                    onAudioEvents(AudioEvents.PlayAudio)
            }
            showDetailSheet = referenceAyah != null
        }
    }
}

interface QuranSelection
data class AyahSelection(val ayahId: Int) : QuranSelection
data class SurahSelection(val surahId: Int) : QuranSelection
data class PageSelection(val page: Int) : QuranSelection
data class JuzSelection(val juz: Int) : QuranSelection

sealed interface AudioEvents {
    object PlayAudio : AudioEvents
    object PauseAudio : AudioEvents
    object CloseAudio : AudioEvents
    object PlayPrevious : AudioEvents
    object PlayNext : AudioEvents
    data class UpdateProgress(val newProgress: Float) : AudioEvents
}