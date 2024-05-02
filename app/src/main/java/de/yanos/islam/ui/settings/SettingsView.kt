package de.yanos.islam.ui.settings

import android.app.Activity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.islam.R
import de.yanos.islam.util.constants.FontStyle
import de.yanos.islam.util.constants.Method
import de.yanos.islam.util.constants.NavigationAction
import de.yanos.islam.util.helper.IslamRadio
import de.yanos.islam.util.helper.Lottie
import de.yanos.islam.util.constants.QuranFontStyle
import de.yanos.islam.util.helper.IslamDivider
import de.yanos.islam.util.helper.IslamDropDown
import de.yanos.islam.util.helper.headlineMedium
import de.yanos.islam.util.helper.labelLarge
import de.yanos.islam.util.helper.titleSmall
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    vm: SettingsViewModel = hiltViewModel(),
    onNavigationChange: (NavigationAction) -> Unit ={}
) {
    /*var recreate by remember { mutableStateOf(false) }
    if (recreate) {
        (LocalContext.current as? Activity)?.recreate()
        recreate = false
    }
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(vertical = 16.dp), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.SpaceBetween) {
        item { SettingsHeader(modifier = Modifier.padding(horizontal = 16.dp)) }
        item { PrayerHeader(modifier = Modifier.padding(horizontal = 24.dp)) }
        item { PrayerMethod(modifier = Modifier.padding(horizontal = 16.dp), currentSelection = stringResource(id = vm.method.res)) }
        item { NotificationTime(modifier = Modifier.padding(horizontal = 16.dp)) }
        item { FontHeader(modifier = Modifier.padding(horizontal = 16.dp)) }
        item {
            FontSettings(
                modifier = Modifier.padding(horizontal = 16.dp),
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
    }*/


}

@Composable
@Preview
private fun NotificationTime(
    modifier: Modifier = Modifier
) {

}

@Composable
@Preview
private fun PrayerMethod(
    currentSelection: String,
    modifier: Modifier = Modifier
) {
    IslamDropDown(
        modifier = modifier,
        selectedValue = currentSelection,
        options = Method.entries.map { stringResource(id = it.res) },
        label = stringResource(id = R.string.settings_method_label),
        onValueChangedEvent = {}
    )
}

@Composable
@Preview
private fun FontHeader(modifier: Modifier = Modifier) {
    Text(modifier = modifier, text = stringResource(id = R.string.settings_section_font), style = labelLarge())
}

@Composable
@Preview
private fun PrayerHeader(modifier: Modifier = Modifier) {
    Text(modifier = modifier, text = stringResource(id = R.string.settings_section_prayer), style = labelLarge())
}

@Composable
@Preview
private fun SettingsHeader(modifier: Modifier = Modifier) {
    Text(modifier = modifier, text = stringResource(id = R.string.settings_title), style = headlineMedium())
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
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top
    ) {
        IconButton(onClick = {
            when (state) {
                AudioDownloadState.IsDownloading -> pauseDownload()
                AudioDownloadState.IsIdle -> startDownload()
                AudioDownloadState.IsPaused -> resumeDownload()
                else -> {}
            }
        }) {
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = when (state) {
                    AudioDownloadState.IsDownloading -> Icons.Rounded.Pause
                    AudioDownloadState.IsDownloaded -> Icons.Rounded.DownloadDone
                    AudioDownloadState.IsPaused -> Icons.Rounded.PlayArrow
                    else -> Icons.Rounded.Download
                }, contentDescription = ""
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = stringResource(id = R.string.setting_download_title), style = titleSmall())
            Text(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .alpha(0.4f), text = stringResource(id = R.string.setting_download_description), style = titleSmall()
            )
            if (downloadMax > 0 && downloadMax != downloadProgress)
                LinearProgressIndicator(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .height(16.dp)
                )
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