package de.yanos.islam.service

import android.os.Bundle
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.util.AppContainer
import de.yanos.islam.util.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ExoLearningCallback(
    private val appContainer: AppContainer,
    private val dispatcher: CoroutineDispatcher,
    private val repository: QuranRepository,
) : MediaSession.Callback {

    private val job = Job()
    private val scope by lazy {
        CoroutineScope(
            dispatcher + job
        )
    }
    private var mediaController: MediaController? = appContainer.audioController

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        when (customCommand.customAction) {
            Constants.CHANNEL_COMMAND_NEXT -> {
                mediaController?.seekToNextMediaItem()
                scope.launch {
                    mediaController?.currentMediaItem?.let {
                        repository.loadMedia(it.mediaId, it.requestMetadata.mediaUri?.toString() ?: "")
                    }
                }
            }

            Constants.CHANNEL_COMMAND_PREVIOUS -> {
                mediaController?.seekToPreviousMediaItem()
                scope.launch {
                    mediaController?.currentMediaItem?.let {
                        repository.loadMedia(it.mediaId, it.requestMetadata.mediaUri?.toString() ?: "")
                    }
                }
            }
        }
        return Futures.immediateFuture(
            SessionResult(SessionResult.RESULT_SUCCESS)
        )
    }
}
