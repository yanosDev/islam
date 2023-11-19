package de.yanos.islam.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.util.AppSettings
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettings: AppSettings
) : ViewModel() {
    fun updateFontSize(size: Int) {
        fontSize = size
        appSettings.fontSizeFactor = size
    }

    fun updateFontStyle(style: Int) {
        fontStyle = style
        appSettings.fontStyle = style
    }

    fun updateQuranFontSize(size: Int) {
        quranFontSize = size
        appSettings.quranSizeFactor = if (size > 0) 10 * size else size
    }

    fun updateQuranFontStyle(style: Int) {
        quranFontStyle = style
        appSettings.quranStyle = style
    }

    var fontSize by mutableStateOf(appSettings.fontSizeFactor)
    var fontStyle by mutableStateOf(appSettings.fontStyle)
    var quranFontSize by mutableStateOf(appSettings.quranSizeFactor / 10)
    var quranFontStyle by mutableStateOf(appSettings.quranStyle)
}