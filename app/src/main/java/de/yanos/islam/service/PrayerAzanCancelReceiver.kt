package de.yanos.islam.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import dagger.hilt.android.AndroidEntryPoint
import de.yanos.islam.di.AzanPlayer
import javax.inject.Inject

@AndroidEntryPoint
class PrayerAzanCancelReceiver : BroadcastReceiver() {
    @AzanPlayer @Inject lateinit var mediaPlayer: MediaPlayer
    @Inject lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.getStringExtra(PrayerTimeAlarmReceiver.ID) ?: return
        notificationManager.cancel(id.hashCode())
        mediaPlayer.stop()
    }
}