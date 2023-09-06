package de.yanos.islam.ui.quran.partial

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.QuranFontStyle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranPartialViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val dao: QuranDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    fun updateTranslationsVisibility(showTranslation: Boolean) {
        sure = sure.copy(showTranslation = showTranslation)
        appSettings.showTranslations = showTranslation
    }

    fun updatePronunciationsVisibility(showPronunciation: Boolean) {
        sure = sure.copy(showPronunciation = showPronunciation)
        appSettings.showPronunciations = showPronunciation
    }

    private val sureName = savedStateHandle.get<String>("name")!!.trim()
    var sure by mutableStateOf(
        SureData(
            sureName,
            showTranslation = appSettings.showTranslations,
            showPronunciation = appSettings.showPronunciations,
            translations = emptyList(),
            pronunciations = emptyList(),
            originals = emptyList()
        )
    )
    val quranFontStyle get() = QuranFontStyle.values()[appSettings.quranStyle].fontId

    init {
        viewModelScope.launch {
            dao.loadSure(sureName).distinctUntilChanged().collect { ayetList ->
                sure = sure.copy(
                    translations = ayetList.map { it.suretur },
                    pronunciations = ayetList.map { it.suretrans },
                    originals = ayetList.map { it.surear })
            }
        }
    }
}

data class SureData(
    val name: String,
    val showTranslation: Boolean,
    val showPronunciation: Boolean,
    val translations: List<String>,
    val pronunciations: List<String>,
    val originals: List<String>
)