package de.yanos.islam.service

import android.os.Bundle
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import de.yanos.islam.util.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asExecutor

class ExoMediaSessionCallback(private val mediaControllerFuture: ListenableFuture<MediaController>, val dispatcher: CoroutineDispatcher) : MediaSession.Callback {
    var mediaController: MediaController? = null

    init {
        mediaControllerFuture.addListener(
            {
                mediaController = mediaControllerFuture.get()
            },
            dispatcher.asExecutor()
        )
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        when (customCommand.customAction) {
            Constants.CHANNEL_COMMAND_NEXT -> mediaController?.seekToNextMediaItem()
            Constants.CHANNEL_COMMAND_PREVIOUS -> mediaController?.seekToPreviousMediaItem()
        }
        return Futures.immediateFuture(
            SessionResult(SessionResult.RESULT_SUCCESS)
        )
    }
}
