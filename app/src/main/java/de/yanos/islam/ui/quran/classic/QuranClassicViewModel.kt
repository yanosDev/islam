@file:OptIn(ExperimentalFoundationApi::class)

package de.yanos.islam.ui.quran.classic

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Page
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.IsLoading
import de.yanos.islam.util.ScreenState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranClassicViewModel @Inject constructor(
    private val appSettings: AppSettings,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val repository: QuranRepository,
) : ViewModel() {

    val quranStyle get() = appSettings.quranStyle
    val quranSizeFactor get() = appSettings.quranSizeFactor
    var state: ScreenState by mutableStateOf(IsLoading)
    var uri: Uri? = null

    init {
        viewModelScope.launch {
            repository.loadPages().collect {
                state = QuranState(it)
            }
        }
    }

    fun onAudioChange(onAudioInteraction: OnAudioInteraction) {
        viewModelScope.launch {
            when (onAudioInteraction) {
                is DownloadAudio -> repository.loadAudio(onAudioInteraction.ayah)?.let { uri = Uri.fromFile(it) }
            }
        }
    }
}

data class QuranState(val pages: List<Page>, val currentPage: Int = 1) : ScreenState


interface OnAudioInteraction

class DownloadAudio(val ayah: Ayah) : OnAudioInteraction