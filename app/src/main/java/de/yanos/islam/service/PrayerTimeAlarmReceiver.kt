package de.yanos.islam.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
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
            val builder = NotificationCompat.Builder(ctx, Constants.CHANNEL_ID_ALARM)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Alarm Demo")
                .setContentText("Notification sent with message $message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(Uri.parse("android.resource://de.yanos.islam/raw/ezan"))
            notificationManager.notify(1, builder.build())
        }
    }

    companion object {
        const val ID = "id"
    }
}