package de.yanos.islam.ui.quran.learning

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.data.database.dao.VideoDao
import de.yanos.islam.data.model.VideoLearning
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.di.VideoPlayer
import de.yanos.islam.util.AppContainer
import de.yanos.islam.util.settings.AppSettings
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranLearningViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val appContainer: AppContainer,
    private val dao: VideoDao,
    private val quranRepository: QuranRepository,
    @VideoPlayer val player: ExoPlayer
) : ViewModel() {
    internal val learnings = mutableStateListOf<VideoLearning>()
    var index by mutableIntStateOf(appSettings.lastPlayedLearningIndex)

    init {
        viewModelScope.launch {
            dao.loadVideos().collect {
                learnings.clear()
                learnings.addAll(it)
            }
        }
    }

    fun loadVideo(learning: VideoLearning) {
        viewModelScope.launch {
            index = learnings.indexOf(learning)
            quranRepository.loadMedia(learning.id, learning.remoteUrl)
            appContainer.videoController?.seekTo(index, 0)
        }
    }
}