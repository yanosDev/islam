package de.yanos.islam.ui.quran.classic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.data.model.quran.Page
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.IsLoading
import de.yanos.islam.util.ScreenState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranClassicViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val dao: QuranDao
) : ViewModel() {

    val quranStyle get() = appSettings.quranStyle
    val quranSizeFactor get() = appSettings.quranSizeFactor
    var state: ScreenState by mutableStateOf(IsLoading)
    private val currentPage = 1

    init {
        viewModelScope.launch {
            val pages = dao.ayahs().groupBy { it.page }.map { Page(it.key, it.value) }

            state = QuranState(pages)
        }
    }
}

data class QuranState(val pages: List<Page>) : ScreenState