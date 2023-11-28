package de.yanos.islam.ui.settings

import android.app.Activity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.FontStyle
import de.yanos.islam.util.IslamRadio
import de.yanos.islam.util.Lottie
import de.yanos.islam.util.QuranFontStyle
import de.yanos.islam.util.titleSmall
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    vm: SettingsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    DisposableEffect(scope) {
        scope.launch {
            vm.startTimer()
        }
        onDispose {
            vm.clearTimer()
        }
    }
    var recreate by remember { mutableStateOf(false) }
    if (recreate) {
        (LocalContext.current as? Activity)?.recreate()
        recreate = false
    }
    LazyColumn(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Lottie(modifier = Modifier.height(128.dp), resId = R.raw.lottie_config, applyColor = false)
        }
        item {
            MediaCard(
                modifier = Modifier,
                state = vm.downloadState,
                downloadProgress = vm.progress,
                downloadMax = vm.max,
                pauseDownload = { vm.pauseDownloadingAll() },
                resumeDownload = { vm.resumeDownloadingAll() },
                startDownload = { vm.queueDownloadAll() })
        }
        item {
            FontSettings(
                icon = Icons.Rounded.FormatSize,
                currentSize = vm.fontSize,
                fontStyleId = R.string.setting_font_style_title,
                fontSizeId = R.string.setting_font_size_title,
                onFontSizeChange = {
                    vm.updateFontSize(it)
                    recreate = true
                },
                currentFontIndex = vm.fontStyle,
                onFontStyleChange = {
                    vm.updateFontStyle(it)
                    recreate = true
                },
                fonts = FontStyle.values().map { it.textId }
            )
        }
        item {
            FontSettings(
                icon = Icons.Rounded.FormatSize,
                currentSize = vm.quranFontSize,
                fontStyleId = R.string.setting_quran_style_title,
                fontSizeId = R.string.setting_quran_size_title,
                onFontSizeChange = {
                    vm.updateQuranFontSize(it)
                    recreate = true
                },
                currentFontIndex = vm.quranFontStyle,
                onFontStyleChange = {
                    vm.updateQuranFontStyle(it)
                    recreate = true
                },
                fonts = QuranFontStyle.values().map { it.textId }
            )
        }
    }
}

@Composable
fun MediaCard(
    modifier: Modifier,
    state: AudioDownloadState,
    downloadProgress: Int,
    downloadMax: Int,
    pauseDownload: () -> Unit,
    resumeDownload: () -> Unit,
    startDownload: () -> Unit,
) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(text = stringResource(id = R.string.setting_download_title), style = titleSmall())
        Text(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .alpha(0.4f), text = stringResource(id = R.string.setting_download_description), style = titleSmall()
        )
        IconButton(onClick = {
            when (state) {
                AudioDownloadState.IsDownloading -> pauseDownload()
                AudioDownloadState.IsIdle -> startDownload()
                AudioDownloadState.IsPaused -> resumeDownload()
                else -> {}
            }
        }) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = when (state) {
                        AudioDownloadState.IsDownloading -> Icons.Rounded.Pause
                        AudioDownloadState.IsDownloaded -> Icons.Rounded.DownloadDone
                        AudioDownloadState.IsPaused -> Icons.Rounded.PlayArrow
                        else -> Icons.Rounded.Download
                    }, contentDescription = ""
                )
                if (downloadMax > 0 && downloadMax != downloadProgress)
                    LinearProgressIndicator(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FontSettings(
    modifier: Modifier = Modifier,
    @StringRes fontSizeId: Int,
    @StringRes fontStyleId: Int,
    icon: ImageVector,
    currentSize: Int,
    onFontSizeChange: (Int) -> Unit,
    currentFontIndex: Int,
    onFontStyleChange: (Int) -> Unit,
    fonts: List<Int>
) {
    Column(modifier = modifier.padding(8.dp)) {
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        FontSizeSetting(icon = icon, titleId = fontSizeId, currentSize = currentSize, onFontSizeChange = onFontSizeChange)
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        FontStyleSetting(currentFontIndex = currentFontIndex, titleId = fontStyleId, onFontStyleChange = onFontStyleChange, fonts = fonts)
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
    }
}

@Composable
private fun FontStyleSetting(
    modifier: Modifier = Modifier,
    @StringRes titleId: Int,
    currentFontIndex: Int,
    onFontStyleChange: (Int) -> Unit,
    fonts: List<Int>
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(modifier = Modifier.padding(horizontal = 8.dp), text = stringResource(id = titleId), style = titleSmall())
        fonts.groupBy { fonts.indexOf(it) / 2 }.forEach { (_, pair) ->
            val actualIndexFirst = fonts.indexOf(pair.first())
            Row(modifier = Modifier.fillMaxWidth()) {
                IslamRadio(
                    modifier = Modifier.weight(1f),
                    isSelected = currentFontIndex == actualIndexFirst,
                    text = fonts[actualIndexFirst]
                ) { onFontStyleChange(actualIndexFirst) }
                (if (pair.size > 1) fonts.indexOf(pair[1]) else null)?.let {
                    IslamRadio(modifier = Modifier.weight(1f), isSelected = currentFontIndex == it, text = fonts[it]) { onFontStyleChange(it) }
                }
            }
        }

    }
}

@Composable
private fun FontSizeSetting(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    @StringRes titleId: Int,
    currentSize: Int,
    onFontSizeChange: (Int) -> Unit
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Icon(modifier = Modifier.size(48.dp), imageVector = icon, contentDescription = "")
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(modifier = Modifier.padding(horizontal = 8.dp), text = stringResource(id = titleId), style = titleSmall())
            Slider(
                value = currentSize.toFloat(),
                valueRange = -2f..2f,
                steps = 3,
                onValueChange = {
                    onFontSizeChange(it.toInt())
                }
            )
        }
    }
}