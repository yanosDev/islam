package de.yanos.islam.ui.quran.partial

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.ui.quran.list.sure.SureSorting
import de.yanos.islam.ui.quran.list.sure.sortSure
import de.yanos.islam.util.AppSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuranPartialViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val dao: QuranDao,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private var sortBy by mutableStateOf(SureSorting.values()[appSettings.sortByOrdinal])
    private val id = savedStateHandle.get<Int>("id")!!
    var surah by mutableStateOf(
        SurahData(
            id,
            "",
            showTranslation = appSettings.showTranslations,
            showPronunciation = appSettings.showPronunciations,
            translations = emptyList(),
            pronunciations = emptyList(),
            originals = emptyList()
        )
    )
    val quranStyle get() = appSettings.quranStyle
    val quranSizeFactor get() = appSettings.quranSizeFactor
    var previousSurahId by mutableStateOf<Int?>(null)
    var nextSurahId by mutableStateOf<Int?>(null)

    init {
        loadSurah(id)
    }

    fun loadSurah(id: Int) {
        viewModelScope.launch {
            dao.loadSurah(id).distinctUntilChanged().collect { ayahs ->
                val surahs = withContext(dispatcher) { dao.sureList() }.sortSure(sortBy)
                val currentIndex = surahs.indexOfFirst { it.id == id }
                if (currentIndex > 0) {
                    previousSurahId = surahs[currentIndex - 1].id
                }
                if (currentIndex < surahs.size - 1) {
                    nextSurahId = surahs[currentIndex + 1].id
                }
                surah = surah.copy(
                    id = id,
                    name = surahs[currentIndex].engName,
                    translations = ayahs.map { it.translationTr },
                    pronunciations = ayahs.map { it.transliterationEn },
                    originals = ayahs.map { it.text })
            }
        }
    }

    fun updateTranslationsVisibility(showTranslation: Boolean) {
        surah = surah.copy(showTranslation = showTranslation)
        appSettings.showTranslations = showTranslation
    }

    fun updatePronunciationsVisibility(showPronunciation: Boolean) {
        surah = surah.copy(showPronunciation = showPronunciation)
        appSettings.showPronunciations = showPronunciation
    }
}

data class SurahData(
    val id: Int,
    val name: String,
    val showTranslation: Boolean,
    val showPronunciation: Boolean,
    val translations: List<String>,
    val pronunciations: List<String>,
    val originals: List<String>
)