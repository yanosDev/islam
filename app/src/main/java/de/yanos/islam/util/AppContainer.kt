package de.yanos.islam.util

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.di.AudioPlayer
import de.yanos.islam.di.VideoPlayer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asExecutor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppContainer @Inject constructor(
    private val appSettings: AppSettings,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    @AudioPlayer private val mediaControllerFuture: ListenableFuture<MediaController>,
    @VideoPlayer private val videoMediaControllerFuture: ListenableFuture<MediaController>
) {
    var audioController: MediaController? = null
    var videoController: MediaController? = null

    init {
        mediaControllerFuture.addListener({
            audioController = mediaControllerFuture.get()
            audioController?.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    appSettings.lastPlayedAyahIndex = audioController?.currentMediaItemIndex ?: 0

                }
            })
        }, dispatcher.asExecutor())
        videoMediaControllerFuture.addListener({
            videoController = videoMediaControllerFuture.get()
            videoController?.addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    appSettings.lastPlayedLearningIndex = videoController?.currentMediaItemIndex ?: 0

                }
            })
        }, dispatcher.asExecutor())
    }
}