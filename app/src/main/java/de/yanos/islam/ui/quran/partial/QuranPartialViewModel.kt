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
import de.yanos.islam.util.QuranFontStyle
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
    private val sureName = savedStateHandle.get<String>("name")!!.trim()
    var sure by mutableStateOf(
        SureData(
            sureName,
            "",
            showTranslation = appSettings.showTranslations,
            showPronunciation = appSettings.showPronunciations,
            translations = emptyList(),
            pronunciations = emptyList(),
            originals = emptyList()
        )
    )
    val quranFontStyle get() = QuranFontStyle.values()[appSettings.quranStyle].fontId
    var previousSure by mutableStateOf<String?>(null)
    var nextSure by mutableStateOf<String?>(null)

    init {
        loadSure(sureName)
    }

    fun loadSure(name: String) {
        viewModelScope.launch {
            dao.loadSure(name).distinctUntilChanged().collect { ayetList ->
                sure = sure.copy(
                    name = name,
                    translations = ayetList.map { it.suretur },
                    pronunciations = ayetList.map { it.suretrans },
                    originals = ayetList.map { it.surear })
                val sureList = withContext(dispatcher) { dao.sureList() }.sortSure(sortBy)
                sureList.find { it.sureaditr.trim() == name.trim() }?.let { sure ->
                    val asInt = when (sortBy) {
                        SureSorting.ORIGINAL -> sure.kuransira
                        SureSorting.ALPHABETICAL -> sure.alfabesira
                        else -> sure.inissira
                    }.toInt() - 1
                    if (asInt > 0) {
                        previousSure = sureList[asInt - 1].sureaditr.trim()
                    }
                    if (asInt < sureList.size - 1) {
                        nextSure = sureList[asInt + 1].sureaditr.trim()
                    }
                }
            }
        }
    }

    fun updateTranslationsVisibility(showTranslation: Boolean) {
        sure = sure.copy(showTranslation = showTranslation)
        appSettings.showTranslations = showTranslation
    }

    fun updatePronunciationsVisibility(showPronunciation: Boolean) {
        sure = sure.copy(showPronunciation = showPronunciation)
        appSettings.showPronunciations = showPronunciation
    }
}

data class SureData(
    val name: String,
    val nameAr: String,
    val showTranslation: Boolean,
    val showPronunciation: Boolean,
    val translations: List<String>,
    val pronunciations: List<String>,
    val originals: List<String>
)