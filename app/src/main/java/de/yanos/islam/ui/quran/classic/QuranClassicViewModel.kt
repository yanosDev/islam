package de.yanos.islam.ui.quran.classic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.model.QuranBookmark
import de.yanos.islam.data.model.quran.Page
import de.yanos.islam.data.repositories.QuranRepository
import de.yanos.islam.ui.quran.classic.audio.AudioViewModel
import de.yanos.islam.util.AppContainer
import de.yanos.islam.util.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuranClassicViewModel @Inject constructor(
    private val appContainer: AppContainer,
    private val appSettings: AppSettings,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val repository: QuranRepository,
) : AudioViewModel(appContainer, repository) {
    var pages = mutableStateListOf<Page>()
    val quranStyle get() = appSettings.quranStyle
    val quranSizeFactor get() = appSettings.quranSizeFactor
    val quickMarks = repository.loadBookmarks()
    val initPage by mutableIntStateOf(appSettings.lastOpenPage)

    init {
        viewModelScope.launch(dispatcher) {
            repository.loadAyahs().collect { ayahs ->
                ayahs.groupBy { it.page }.map {
                    Page(it.key, it.value.first().sureName, it.value.first().sureId, it.value)
                }.let {
                    withContext(Dispatchers.Main) {
                        pages.clear()
                        pages.addAll(it)
                    }
                }
            }
        }
    }

    fun createBookmark(page: Page) {
        viewModelScope.launch {
            repository.createBookmarkByPage(page, referenceAyah)
        }
    }

    fun updateCurrentPage(page: Int) {
        appSettings.lastOpenPage = page
    }
}

interface QuranSelection
data class AyahSelection(val ayahId: Int) : QuranSelection
data class SurahSelection(val surahId: Int) : QuranSelection
data class PageSelection(val page: Int) : QuranSelection
data class JuzSelection(val juz: Int) : QuranSelection
data class BookmarkSelection(val mark: QuranBookmark) : QuranSelection

sealed interface AudioEvents {
    data object PlayAudio : AudioEvents
    data object PauseAudio : AudioEvents
    data object CloseAudio : AudioEvents
    data object PlayPrevious : AudioEvents
    data object PlayNext : AudioEvents
    data class UpdateProgress(val newProgress: Float) : AudioEvents
}