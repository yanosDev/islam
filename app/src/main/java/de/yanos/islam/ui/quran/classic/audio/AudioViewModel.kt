package de.yanos.islam.ui.quran.classic.audio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.ui.quran.classic.AudioEvents
import de.yanos.islam.ui.quran.classic.AyahSelection
import de.yanos.islam.ui.quran.classic.JuzSelection
import de.yanos.islam.ui.quran.classic.PageSelection
import de.yanos.islam.ui.quran.classic.QuranSelection
import de.yanos.islam.ui.quran.classic.SurahSelection
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.timerTask
import kotlin.math.max

open class AudioViewModel(
    private val controllerFuture: ListenableFuture<MediaController>,
    private val repository: QuranRepository,
    dispatcher: CoroutineDispatcher
) : ViewModel() {
    var showDetailSheet by mutableStateOf(false)
    var referenceAyah by mutableStateOf<Ayah?>(null)

    var progress by mutableFloatStateOf(0F)
    var isPlaying by mutableStateOf(false)

    private var timer: Timer? = null
    private var controller: MediaController? = null

    private val controllerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            viewModelScope.launch {
                refreshData()
            }
        }
    }

    init {
        controllerFuture.addListener({
            controller = controllerFuture.get()
            viewModelScope.launch {
                if (controller?.isPlaying == true)
                    refreshData()
            }
            controller?.addListener(controllerListener)
        }, dispatcher.asExecutor())
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
                if (ayah != referenceAyah) {
                    referenceAyah = ayah
                    controller?.seekTo(ayah.id - 1, 0)
                }
            }
            showDetailSheet = referenceAyah != null
            if (showDetailSheet)
                startTimer()
        }
    }

    private fun startTimer() {
        if (timer == null) {
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
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    fun onAudioEvents(event: AudioEvents) {
        controller?.let {
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
                            repository.loadAudioAlt(ayah.id, ayah.audio)
                        }
                    }

                    is AudioEvents.PlayNext -> {
                        controller?.seekToNextMediaItem()
                        repository.loadAyahById(controller?.currentMediaItem?.mediaId?.toInt() ?: -1)?.let { ayah ->
                            repository.loadAudioAlt(ayah.id, ayah.audio)
                        }
                    }

                    is AudioEvents.CloseAudio -> {
                        stopTimer()
                        progress = 0F
                        showDetailSheet = false
                    }
                }
            }
        }
    }

    private suspend fun refreshData() {
        controller?.let {
            it.currentMediaItem?.mediaId?.toInt()?.let { id ->
                if (id != referenceAyah?.id)
                    referenceAyah = repository.loadAyahById(id)
            }
            progress = max(((it.currentPosition.toFloat() / it.duration.toFloat()) * 100F), 5F)
            isPlaying = it.isPlaying
        }
    }

    override fun onCleared() {
        stopTimer()
        controller?.removeListener(controllerListener)
        super.onCleared()
    }
}