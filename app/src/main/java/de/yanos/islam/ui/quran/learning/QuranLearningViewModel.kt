package de.yanos.islam.ui.quran.learning

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.di.VideoPlayer
import de.yanos.islam.util.AppContainer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranLearningViewModel @Inject constructor(
    @ApplicationContext context: Context,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val appContainer: AppContainer,
    @VideoPlayer val player: ExoPlayer
) : ViewModel() {
    internal val learnings = mutableStateListOf<Learning>()
    val controller get() = appContainer.videoController

    init {
        viewModelScope.launch(dispatcher) {
            while (controller == null)
                delay(100)

            for (i in 0 until controller!!.mediaItemCount) {
                val item = controller!!.getMediaItemAt(i)
                learnings.add(
                    Learning(
                        id = item.mediaId,
                        thumbUri = item.mediaMetadata.artworkUri!!,
                        title = item.mediaMetadata.title!!.toString(),
                        subTitle = item.mediaMetadata.subtitle!!.toString(),
                        author = item.mediaMetadata.artist!!.toString(),
                    )
                )
            }
        }
    }
}

data class Learning(
    val id: String,
    val thumbUri: Uri,
    val title: String,
    val subTitle: String,
    val author: String

)