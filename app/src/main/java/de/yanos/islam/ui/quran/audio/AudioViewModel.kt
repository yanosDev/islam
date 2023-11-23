@file:OptIn(SavedStateHandleSaveableApi::class)

package de.yanos.islam.ui.quran.audio

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.util.IsLoading
import de.yanos.islam.util.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle,
    private val quranDao: QuranDao
) : ViewModel() {
    private val ayahId: Int? = savedStateHandle["ayahId"]
    private val surahId: Int? = savedStateHandle["sureId"]

    var duration by savedStateHandle.saveable { mutableLongStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableFloatStateOf(0F) }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    var currentPlaying by mutableStateOf<Ayah?>(null)
    var ayahs = mutableStateListOf<Ayah>()

    private val _uiState: MutableStateFlow<ScreenState> = MutableStateFlow(IsLoading)
    val uiState: StateFlow<ScreenState> = _uiState
    private val exoPlayer = ExoPlayer.Builder(context).build()
    private val mediaSession = MediaSession.Builder(context, exoPlayer).build()

    init {
        loadAudioData(surahId, ayahId)
    }

    fun loadAudioData(surahId: Int?, ayahId: Int?) {
        viewModelScope.launch {
            surahId?.let {
                quranDao.loadSurah(it).collect { ayahs ->
                    this@AudioViewModel.ayahs.clear()
                    this@AudioViewModel.ayahs.addAll(ayahs)

                    setMediaItems()
                }
            } ?: ayahId?.let {
                quranDao.loadAyah(it).collect { ayah ->
                    this@AudioViewModel.ayahs.clear()
                    this@AudioViewModel.ayahs.add(ayah)

                    setMediaItems()
                }
            }
        }
    }

    private fun setMediaItems() {
        exoPlayer.setMediaItems(
            ayahs.map { ayah ->
                MediaItem.Builder()
                    .setUri(ayah.audio)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setAlbumTitle(ayah.sureName)
                            .setDisplayTitle(ayah.sureName)
                            .setSubtitle(ayah.id.toString())
                            .build()
                    )
                    .build()
            }
        )
        exoPlayer.prepare()
        currentPlaying = ayahs.first()
    }

    private fun calculateProgressValue(currentProgress: Long) {
        progress = if (currentProgress > 0) ((currentProgress.toFloat() / duration.toFloat()) * 100F) else 0F
        progressString = formatDuration(currentProgress)
    }

    private fun formatDuration(duration: Long): String {
        val minute = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (minute) - minute * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
        return String.format("%02d:%02d", minute, seconds)
    }

    fun onAudioEvents(event: AudioEvents) {
        when (event) {
            AudioEvents.PlayPause -> if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
            AudioEvents.Backward -> exoPlayer.seekBack()
            AudioEvents.Forward -> exoPlayer.seekForward()
            AudioEvents.SeekToNext -> exoPlayer.seekToNext()
            else -> {}
        }
    }
}

sealed class AudioEvents {
    object PlayPause : AudioEvents()
    object SeekToNext : AudioEvents()
    object Backward : AudioEvents()
    object Forward : AudioEvents()
    data class UpdateProgress(val newProgress: Float) : AudioEvents()
    data class SeekTo(val newProgress: Float) : AudioEvents()
}