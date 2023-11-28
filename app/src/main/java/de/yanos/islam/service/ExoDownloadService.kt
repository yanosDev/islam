package de.yanos.islam.service

import android.app.Notification
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.Requirements
import androidx.media3.exoplayer.scheduler.Scheduler
import dagger.hilt.android.AndroidEntryPoint
import de.yanos.islam.R
import de.yanos.islam.util.Constants
import de.yanos.islam.util.humanReadableByteCountSI
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
    @Inject lateinit var notificationHelper: DownloadNotificationHelper
    @Inject lateinit var manager: DownloadManager

    override fun getDownloadManager(): DownloadManager {
        manager.maxParallelDownloads = 5
        return manager
    }

    override fun getScheduler(): Scheduler? {
        return null
    }

    override fun getForegroundNotification(downloads: MutableList<Download>, notMetRequirements: Int): Notification {
        return notificationHelper.buildProgressNotification(
            applicationContext,
            R.drawable.ic_launcher,
            null,
            getString(R.string.download_number, downloads.size.toString(), (downloads.size * 125000L).humanReadableByteCountSI()),
            downloads,
            Requirements.NETWORK
        )
    }
}