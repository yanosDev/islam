package de.yanos.islam.util

import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import de.yanos.islam.di.AudioPlayer
import de.yanos.islam.di.VideoPlayer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppContainer @Inject constructor(
    @AudioPlayer var mediaControllerFuture: ListenableFuture<MediaController>,
    @VideoPlayer var videoMediaControllerFuture: ListenableFuture<MediaController>
) {
    var audioController: MediaController? = null
    var videoController: MediaController? = null
}