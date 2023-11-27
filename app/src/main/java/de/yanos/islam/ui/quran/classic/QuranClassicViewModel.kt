@file:OptIn(ExperimentalFoundationApi::class)

package de.yanos.islam.ui.quran.classic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.model.quran.Page
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.ui.quran.classic.audio.AudioViewModel
import de.yanos.islam.util.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuranClassicViewModel @Inject constructor(
    private val appSettings: AppSettings,
    controllerFuture: ListenableFuture<MediaController>,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val repository: QuranRepository,
) : AudioViewModel(controllerFuture, repository, dispatcher) {
    var pages = mutableStateListOf<Page>()
    val quranStyle get() = appSettings.quranStyle
    val quranSizeFactor get() = appSettings.quranSizeFactor


    init {
        viewModelScope.launch(dispatcher) {
            repository.loadPages().collect {
                withContext(Dispatchers.Main) {
                    pages.clear()
                    pages.addAll(it)
                }
            }
        }
    }
}

interface QuranSelection
data class AyahSelection(val ayahId: Int) : QuranSelection
data class SurahSelection(val surahId: Int) : QuranSelection
data class PageSelection(val page: Int) : QuranSelection
data class JuzSelection(val juz: Int) : QuranSelection

sealed interface AudioEvents {
    object PlayAudio : AudioEvents
    object PauseAudio : AudioEvents
    object CloseAudio : AudioEvents
    object PlayPrevious : AudioEvents
    object PlayNext : AudioEvents
    data class UpdateProgress(val newProgress: Float) : AudioEvents
}