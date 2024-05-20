package de.yanos.islam.ui.settings.cache

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(UnstableApi::class)
@HiltViewModel
class CacheSettingViewModel
@Inject constructor(
    private val downloadManager: DownloadManager,
) : ViewModel() {

}