@file:UnstableApi

package de.yanos.islam.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.util.AppSettings
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val downloadManager: DownloadManager,
    private val quranRepository: QuranRepository
) : ViewModel() {
    private var timer: Timer? = null
    private val ayahs: MutableList<Pair<Int, String>> = mutableListOf()
    var audioDownloadState: AudioDownloadState by mutableStateOf(AudioDownloadState.IsIdle)
    var audioStateString by mutableStateOf("")

    init {
        timer = Timer()
        timer?.scheduleAtFixedRate(
            timerTask()
            {
                viewModelScope.launch {
                    val queuedSize = downloadManager.downloadIndex.getDownloads(
                        Download.STATE_COMPLETED,
                        Download.STATE_QUEUED,
                        Download.STATE_DOWNLOADING,
                        Download.STATE_STOPPED,
                        Download.STATE_FAILED,
                        Download.STATE_REMOVING,
                        Download.STATE_RESTARTING
                    ).count
                    val completedSize = downloadManager.downloadIndex.getDownloads(Download.STATE_COMPLETED).count
                    val stoppedSize = downloadManager.downloadIndex.getDownloads(Download.STATE_STOPPED).count
                    audioDownloadState = when {
                        completedSize == ayahs.size -> AudioDownloadState.IsDownloaded
                        queuedSize == ayahs.size -> AudioDownloadState.IsDownloading
                        stoppedSize + completedSize == ayahs.size -> AudioDownloadState.IsPaused
                        else -> AudioDownloadState.IsIdle
                    }
                    audioStateString = "$completedSize/$queuedSize"
                }
            }, 1500, 5000
        )
        viewModelScope.launch {
            quranRepository.loadAyahs().collect {
                ayahs.clear()
                ayahs.addAll(it.map { ayah -> Pair(ayah.id, ayah.audio) })
            }
        }
    }

    fun queueDownloadAllAudio() {
        viewModelScope.launch {
            audioDownloadState = AudioDownloadState.IsDownloading
            async {
                ayahs.forEach { (id, uri) ->
                    quranRepository.loadAudioAlt(id, uri)
                }
            }
        }
    }

    fun resumeDownloadingAllAudio() {
        audioDownloadState = AudioDownloadState.IsDownloading
        downloadManager.resumeDownloads()
    }

    fun pauseDownloadingAllAudio() {
        audioDownloadState = AudioDownloadState.IsPaused
        downloadManager.pauseDownloads()
    }

    fun updateFontSize(size: Int) {
        fontSize = size
        appSettings.fontSizeFactor = size
    }

    fun updateFontStyle(style: Int) {
        fontStyle = style
        appSettings.fontStyle = style
    }

    fun updateQuranFontSize(size: Int) {
        quranFontSize = size
        appSettings.quranSizeFactor = size
    }

    fun updateQuranFontStyle(style: Int) {
        quranFontStyle = style
        appSettings.quranStyle = style
    }

    var fontSize by mutableIntStateOf(appSettings.fontSizeFactor)
    var fontStyle by mutableIntStateOf(appSettings.fontStyle)
    var quranFontSize by mutableIntStateOf(appSettings.quranSizeFactor)
    var quranFontStyle by mutableIntStateOf(appSettings.quranStyle)

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
        timer = null
    }
}

sealed interface AudioDownloadState {
    object IsDownloaded : AudioDownloadState
    object IsDownloading : AudioDownloadState
    object IsPaused : AudioDownloadState
    object IsIdle : AudioDownloadState
}