package de.yanos.islam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import de.yanos.core.BuildConfig
import de.yanos.islam.util.Constants
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class IslamApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var notificationManager: NotificationManager
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        createAlarmChannel()
        createDownloadChannel()
        notificationManager.cancelAll()
    }

    private fun createDownloadChannel() {
        val channel = NotificationChannel(
            Constants.CHANNEL_ID_DOWNLOAD,
            Constants.CHANNEL_NAME_DOWNLOAD,
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(channel)

    }

    private fun createAlarmChannel() {
        val channel = NotificationChannel(
            Constants.CHANNEL_ID_ALARM,
            Constants.CHANNEL_NAME_ALARM,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.INFO else Log.ERROR)
            .setWorkerFactory(workerFactory)
            .build()

    }
}