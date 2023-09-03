package de.yanos.islam.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import de.yanos.islam.R
import de.yanos.islam.util.Constants
import timber.log.Timber


class PrayerTimeAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.e("I was triggered")
        val message = intent?.getStringExtra(ID) ?: return
        context?.let { ctx ->
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val taskDetailIntent = Intent(
                Intent.ACTION_VIEW,
                "yanos://de.islam/praying".toUri()
            )
            val pending: PendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(taskDetailIntent)
                getPendingIntent(message.hashCode(), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
            }
            val builder = NotificationCompat.Builder(ctx, Constants.CHANNEL_ID_ALARM)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pending)
                .setContentTitle(context.getString(R.string.notification_alarm_title))
                .setContentText(context.getString(R.string.notification_alarm_content, message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
                .setSound(Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/raw/azan"))
            notificationManager.notify(1, builder.build())
        }
    }

    companion object {
        const val ID = "id"
    }
}