package de.yanos.islam.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import dagger.hilt.android.AndroidEntryPoint
import de.yanos.islam.R
import de.yanos.islam.di.AzanPlayer
import de.yanos.islam.util.constants.Constants
import javax.inject.Inject


@AndroidEntryPoint
class PrayerTimeAlarmReceiver : BroadcastReceiver() {
    @Inject lateinit var notificationManager: NotificationManager
    @AzanPlayer @Inject lateinit var mediaPlayer: MediaPlayer

    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.getStringExtra(ID) ?: return
        context?.let { ctx ->
            val taskDetailIntent = Intent(Intent.ACTION_VIEW, "yanos://de.islam/praying".toUri())
            val pending: PendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(taskDetailIntent)
                getPendingIntent(id.hashCode(), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            }
            notificationManager.notify(id.hashCode(), notification(ctx, id, pending))
            mediaPlayer.start()
        }
    }

    private fun notification(context: Context, id: String, pending: PendingIntent): Notification {
        val piDismiss = PendingIntent.getBroadcast(
            context,
            100,
            Intent(context, PrayerAzanCancelReceiver::class.java).apply {
                putExtra(ID, id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        return NotificationCompat.Builder(context, Constants.CHANNEL_ID_ALARM)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pending)
            .setContentTitle(context.getString(R.string.notification_alarm_title))
            .setContentText(context.getString(R.string.notification_alarm_content, id))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, context.getString(R.string.close_alarm), piDismiss).build()
    }

    companion object {
        const val ID = "id"
    }
}