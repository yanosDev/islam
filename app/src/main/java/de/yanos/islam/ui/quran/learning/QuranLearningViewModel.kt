package de.yanos.islam.ui.quran.learning

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.islam.di.VideoPlayer
import javax.inject.Inject

@HiltViewModel
class QuranLearningViewModel @Inject constructor(
    @ApplicationContext context: Context,
    @VideoPlayer internal val player: ExoPlayer
) : ViewModel() {
    internal val learnings = mutableStateListOf<Learning>()

    init {
        for (i in 0 until player.mediaItemCount) {
            val item = player.getMediaItemAt(i)
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

data class Learning(
    val id: String,
    val thumbUri: Uri,
    val title: String,
    val subTitle: String,
    val author: String

)