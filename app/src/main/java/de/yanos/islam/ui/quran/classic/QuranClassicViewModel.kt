@file:OptIn(ExperimentalFoundationApi::class)

package de.yanos.islam.ui.quran.classic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuranClassicViewModel @Inject constructor(
    private val appSettings: AppSettings,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val repository: QuranRepository,
) : ViewModel() {
    var referenceAyah by mutableStateOf<Ayah?>(null)
    var nowPlayingAyah by mutableStateOf<Ayah?>(null)
    val currentAyah get() = nowPlayingAyah ?: referenceAyah

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

    fun onSelectionChange(selection: QuranSelection) {
        viewModelScope.launch(dispatcher) {
            val newAyah = when (selection) {
                is AyahSelection -> repository.loadAyahById(selection.ayahId)
                is SurahSelection -> repository.loadFirstAyahBySurahId(selection.surahId)
                is PageSelection -> repository.loadFirstAyahByPageId(selection.page)
                is JuzSelection -> repository.loadFirstAyahByJuz(selection.juz)
                else -> null
            }
            if (referenceAyah == null)
                referenceAyah = newAyah
            else nowPlayingAyah = newAyah
        }
    }

    fun clearAyahs() {
        nowPlayingAyah = null
        referenceAyah = null
    }
}

interface QuranSelection
data class AyahSelection(val ayahId: Int) : QuranSelection
data class SurahSelection(val surahId: Int) : QuranSelection
data class PageSelection(val page: Int) : QuranSelection
data class JuzSelection(val juz: Int) : QuranSelection
