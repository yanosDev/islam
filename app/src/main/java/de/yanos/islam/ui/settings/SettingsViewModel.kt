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
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.util.settings.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettings: AppSettings,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val downloadManager: DownloadManager,
    private val quranRepository: QuranRepository,
) : ViewModel() {
    private var timer: Timer? = null
    var downloadState: AudioDownloadState by mutableStateOf(AudioDownloadState.IsIdle)
    var progress by mutableIntStateOf(0)
    var max by mutableIntStateOf(0)

    fun startTimer() {
        if (timer == null) {
            timer = Timer()
            timer?.scheduleAtFixedRate(
                timerTask()
                {
                    viewModelScope.launch(dispatcher) {
                        val queuedSize = downloadManager.downloadIndex.getDownloads(
                            Download.STATE_COMPLETED,
                            Download.STATE_QUEUED,
                            Download.STATE_DOWNLOADING,
                            Download.STATE_STOPPED,
                            Download.STATE_FAILED,
                            Download.STATE_REMOVING,
                            Download.STATE_RESTARTING
                        )
                        val completedSize = downloadManager.downloadIndex.getDownloads(Download.STATE_COMPLETED)
                        val stoppedSize = downloadManager.downloadIndex.getDownloads(Download.STATE_STOPPED)
                        val download = downloadManager.downloadIndex.getDownloads(Download.STATE_DOWNLOADING)
                        downloadState = when {
                            completedSize.count == queuedSize.count && queuedSize.count > 0 -> AudioDownloadState.IsDownloaded
                            download.count > 0 -> AudioDownloadState.IsDownloading
                            completedSize.count != queuedSize.count && queuedSize.count > 0 && download.count == 0 -> AudioDownloadState.IsPaused
                            else -> AudioDownloadState.IsIdle
                        }
                        progress = completedSize.count
                        max = queuedSize.count
                        Timber.e("Queued: ${queuedSize.count}, Completed: ${completedSize.count}, Downloading: ${download.count}")
                        completedSize.close()
                        queuedSize.close()
                        stoppedSize.close()
                        download.close()
                    }
                }, 0, 5000
            )
        }
    }

    fun clearTimer() {
        timer?.cancel()
        timer = null
    }

    fun queueDownloadAll() {
        viewModelScope.launch {
            downloadState = AudioDownloadState.IsDownloading
            quranRepository.loadAllAyahAudio()
            downloadManager.resumeDownloads()
        }
    }

    fun pauseDownloadingAll() {
        viewModelScope.launch(dispatcher) {
            downloadState = AudioDownloadState.IsPaused
            downloadManager.pauseDownloads()
        }
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
        clearTimer()
        super.onCleared()
    }
}

sealed interface AudioDownloadState {
    object IsDownloaded : AudioDownloadState
    object IsDownloading : AudioDownloadState
    object IsPaused : AudioDownloadState
    object IsIdle : AudioDownloadState
}