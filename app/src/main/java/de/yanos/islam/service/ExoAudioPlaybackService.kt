package de.yanos.islam.service

import android.content.Intent
import android.os.Bundle
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.di.AudioPlayer
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class ExoAudioPlaybackService : MediaSessionService(), MediaSession.Callback {
    @Inject @IODispatcher lateinit var dispatcher: CoroutineDispatcher
    @AudioPlayer @Inject lateinit var mediaSession: MediaSession

    override fun onCustomCommand(session: MediaSession, controller: MediaSession.ControllerInfo, customCommand: SessionCommand, args: Bundle): ListenableFuture<SessionResult> {
        return when (customCommand.customAction) {
            else -> super.onCustomCommand(session, controller, customCommand, args)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession.player
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession.run {
            player.release()
            release()
        }
        super.onDestroy()

    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }
}

