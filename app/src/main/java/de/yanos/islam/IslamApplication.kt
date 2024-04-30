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
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import de.yanos.core.BuildConfig
import de.yanos.islam.service.queueAudioWorker
import de.yanos.islam.service.queuePeriodicDailyWorker
import de.yanos.islam.service.queueVideoWorker
import de.yanos.islam.util.Constants
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class IslamApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var notificationManager: NotificationManager
    @Inject lateinit var workManager: WorkManager
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.INFO else Log.ERROR)
            .setWorkerFactory(workerFactory)
            .build()
    override fun onCreate() {
        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectAll() // or .detectAll() for all detectable problems
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        createAlarmChannel()
        createDownloadChannel()
        workManager.queuePeriodicDailyWorker()
        workManager.queueVideoWorker()
        workManager.queueAudioWorker()
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
}