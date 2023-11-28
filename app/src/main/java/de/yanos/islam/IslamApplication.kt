package de.yanos.islam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import de.yanos.core.BuildConfig
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.util.Constants
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class IslamApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var notificationManager: NotificationManager
    @Inject @IODispatcher lateinit var dispatcher: CoroutineDispatcher

    override fun onCreate() {
        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork() // or .detectAll() for all detectable problems
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build()
        )
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        createAlarmChannel()
        createDownloadChannel()
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