package de.yanos.islam.service

import android.os.Bundle
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.util.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch

class ExoMediaSessionCallback(
    private val mediaControllerFuture: ListenableFuture<MediaController>,
    private val dispatcher: CoroutineDispatcher,
    private val repository: QuranRepository,
) : MediaSession.Callback {

    private val job = Job()
    private val scope by lazy {
        CoroutineScope(
            dispatcher + job
        )
    }
    private var mediaController: MediaController? = null

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
            Constants.CHANNEL_COMMAND_NEXT -> {
                mediaController?.seekToNextMediaItem()
                scope.launch {
                    repository.loadAyahById(mediaController?.currentMediaItem?.mediaId?.toInt() ?: -1)?.let { ayah ->
                        repository.loadAyahAudio(ayah.id, ayah.audio)
                    }
                }
            }

            Constants.CHANNEL_COMMAND_PREVIOUS -> {
                mediaController?.seekToPreviousMediaItem()
                scope.launch {
                    repository.loadAyahById(mediaController?.currentMediaItem?.mediaId?.toInt() ?: -1)?.let { ayah ->
                        repository.loadAyahAudio(ayah.id, ayah.audio)
                    }
                }
            }
        }
        return Futures.immediateFuture(
            SessionResult(SessionResult.RESULT_SUCCESS)
        )
    }
}
