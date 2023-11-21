package de.yanos.islam.ui.quran.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.data.database.dao.SearchDao
import de.yanos.islam.data.model.Search
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranSearchViewModel @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val quranDao: QuranDao,
    private val searchDao: SearchDao
) : ViewModel() {
    var query by mutableStateOf("")
    var findings = mutableStateListOf<AyetSearch>()
    var recentSearches = searchDao.getRecentSearches().distinctUntilChanged()

    fun search(query: String, saveToRecent: Boolean = false) {
        viewModelScope.launch(dispatcher) {
            if (saveToRecent && query.isNotBlank())
                searchDao.insert(Search(query = query))
        }

        if (this.query != query) {
            this.query = query
            if (this.query.isNotBlank()) {
                viewModelScope.launch(dispatcher) {
                    findings.clear()
                    val newFindings = quranDao.findMatches(query).groupBy { it.sureId }.map { (sureId, ayahs) ->
                        AyetSearch(
                            id = sureId,
                            sureName = ayahs.firstOrNull()?.sureName ?: "",
                            ayet = ayahs.subList(0, minOf(4, ayahs.size)).joinToString("\n") { "${it.id}. \n${it.translationTr}\n${it.transliterationEn}" },
                        )
                    }
                    findings.addAll(newFindings)
                }
            }
        }
    }

    fun clearSearch() {
        this.query = ""
        findings.clear()
    }
}

data class AyetSearch(val id: Int, val sureName: String, val ayet: String)