package de.yanos.islam.service

import android.app.Notification
import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.Requirements
import androidx.media3.exoplayer.scheduler.Scheduler
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.islam.R
import de.yanos.islam.util.Constants
import timber.log.Timber
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class ExoDownloadService @Inject constructor(
) : DownloadService(
    1,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    Constants.CHANNEL_ID_DOWNLOAD,
    R.string.notification_channel_download_name,
    R.string.notification_channel_download_description
) {

    @ApplicationContext private lateinit var context: Context
    private lateinit var notificationHelper: DownloadNotificationHelper
    private lateinit var manager: DownloadManager


    override fun getDownloadManager(): DownloadManager {
        //Set the maximum number of parallel downloads
        manager.maxParallelDownloads = 5
        manager.addListener(object : DownloadManager.Listener {
            override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
                // Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            }

            override fun onDownloadsPausedChanged(downloadManager: DownloadManager, downloadsPaused: Boolean) {
                if (downloadsPaused) {
                    Timber.e("Downloads paused")
                } else {
                    Timber.e("Downloads resumed")
                }
            }

            override fun onDownloadChanged(downloadManager: DownloadManager, download: Download, finalException: Exception?) {
                super.onDownloadChanged(downloadManager, download, finalException)
            }

        })
        // return (application as App).appContainer.downloadManager
        return manager
    }

    override fun getScheduler(): Scheduler? {
        return null
    }


    override fun getForegroundNotification(downloads: MutableList<Download>, notMetRequirements: Int): Notification {
        return notificationHelper.buildProgressNotification(context, R.drawable.ic_launcher, null, getString(R.string.app_name), downloads, Requirements.NETWORK)
    }
}