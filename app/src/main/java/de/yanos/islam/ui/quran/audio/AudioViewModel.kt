@file:OptIn(SavedStateHandleSaveableApi::class)

package de.yanos.islam.ui.quran.audio

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.repositories.QuranRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Timer
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.timerTask

@HiltViewModel
class AudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val controllerFuture: ListenableFuture<MediaController>,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle,
    private val repository: QuranRepository
) : ViewModel() {

    var duration by savedStateHandle.saveable { mutableLongStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableFloatStateOf(0F) }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }

    var currentAyah: Ayah? by mutableStateOf(null)
    var playerState: PlayerState by mutableStateOf(PlayerState.Downloadable)
    private var timer: Timer? = null

    var controller: MediaController? = null

    init {
        controllerFuture.addListener({
            controller = controllerFuture.get()
        }, dispatcher.asExecutor())
    }

    private fun startTimer() {
        timer = Timer()
        timer?.scheduleAtFixedRate(
            timerTask()
            {
                viewModelScope.launch {
                    calculateProgressValue()
                }
            }, 0, 50
        )
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }


    fun playAyah(ayah: Ayah, playAfterDownload: Boolean) {
        viewModelScope.launch {
            if (currentAyah != ayah) {
                prepareAyah(ayah, playAfterDownload)
            }
        }
    }

    private fun prepareAyah(ayah: Ayah, playAfterDownload: Boolean) {
        controller?.let {
            it.setMediaItem(
                MediaItem.Builder()
                    .setMediaId(ayah.id.toString())
                    .setUri(ayah.audio)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setAlbumTitle(ayah.sureName)
                            .setDisplayTitle(ayah.sureName)
                            .setSubtitle(ayah.id.toString())
                            .build()
                    )
                    .build()
            )
            it.prepare()
            duration = it.duration
            currentAyah = ayah
            playerState = if (ayah.localAudio == null) PlayerState.Downloadable else PlayerState.Paused
            if (playAfterDownload)
                onAudioEvents(if (ayah.localAudio == null) AudioEvents.StartDownload else AudioEvents.PlayAudio)
        }
    }

    private suspend fun calculateProgressValue() {
        controller?.let {
            duration = it.duration
            progress = ((it.currentPosition.toFloat() / it.duration.toFloat()) * 100F)
            progressString = formatDuration(it.currentPosition)
            if (progress >= 100)
                onAudioEvents(AudioEvents.PlayNext)
        }
    }

    private fun formatDuration(duration: Long): String {
        val minute = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (minute) - minute * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
        return String.format("%02d:%02d", minute, seconds)
    }

    fun onAudioEvents(event: AudioEvents) {
        currentAyah?.let { ayah ->
            controller?.let {
                Timber.e(event.javaClass.toString())
                viewModelScope.launch {
                    when (event) {
                        /*AudioEvents.StartDownload -> {
                            playerState = PlayerState.Downloading
                            repository.loadAyahById(ayah.id)?.let { refreshedAyah ->
                                prepareAyah(refreshedAyah, true)
                            }
                        }
*/
                        AudioEvents.PlayAudio, AudioEvents.StartDownload -> {
                            repository.loadAudioAlt(ayah)
                            startTimer()
                            it.play()
                            playerState = PlayerState.Playing
                        }

                        AudioEvents.PauseAudio -> {
                            stopTimer()
                            it.pause()
                            playerState = PlayerState.Paused
                        }

                        is AudioEvents.UpdateProgress -> {
                            it.seekTo((event.newProgress / 100 * it.duration).toLong())
                            calculateProgressValue()
                        }

                        is AudioEvents.PlayPrevious -> {
                            it.pause()
                            repository.loadAyahById(ayah.id - 1)?.let { refreshedAyah ->
                                prepareAyah(refreshedAyah, playerState == PlayerState.Playing)
                            }
                        }

                        is AudioEvents.PlayNext -> {
                            it.pause()
                            repository.loadAyahById(ayah.id + 1)?.let { refreshedAyah ->
                                prepareAyah(refreshedAyah, playerState == PlayerState.Playing)
                            }
                        }

                        is AudioEvents.CloseAudio -> {
                            stopTimer()
                            it.pause()
                            duration = 0
                            progress = 0F
                            progressString = "00:00"
                            currentAyah = null
                            playerState = PlayerState.Downloadable
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
//        exoPlayer.release()
//        mediaSession.release()
    }
}

sealed interface AudioEvents {
    object StartDownload : AudioEvents
    object PlayAudio : AudioEvents
    object PauseAudio : AudioEvents
    object CloseAudio : AudioEvents
    object PlayPrevious : AudioEvents
    object PlayNext : AudioEvents
    data class UpdateProgress(val newProgress: Float) : AudioEvents
}

sealed interface PlayerState {
    object Downloadable : PlayerState
    object Downloading : PlayerState
    object Paused : PlayerState
    object Playing : PlayerState
}