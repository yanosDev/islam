package de.yanos.islam

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import de.yanos.core.BuildConfig
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class IslamApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.INFO else Log.ERROR)
            .setWorkerFactory(workerFactory)
            .build()

    }
}