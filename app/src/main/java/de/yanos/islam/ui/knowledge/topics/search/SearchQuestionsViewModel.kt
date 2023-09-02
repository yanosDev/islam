package de.yanos.islam.ui.knowledge.topics.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.QuizDao
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.data.model.Search
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchQuestionsViewModel @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val dao: QuizDao
) : ViewModel() {
    var query by mutableStateOf("")
    var findings = mutableStateListOf<Quiz>()
    var recentSearches = dao.getRecentSearches().distinctUntilChanged()

    fun search(query: String, saveToRecent: Boolean = false) {
        viewModelScope.launch(dispatcher) {
            if (saveToRecent && query.isNotBlank())
                dao.insertSearch(Search(query = query))
        }

        if (this.query != query) {
            this.query = query
            if (this.query.isNotBlank()) {
                viewModelScope.launch(dispatcher) {
                    val newFindings = dao.findMatches(query)
                    findings.clear()
                    findings.addAll(newFindings)
                }
            }
        }
    }

    fun getAnnotatedString(name: String, highlightStyle: SpanStyle): AnnotatedString {
        //Find where searchQuery appears in courseName
        val startIndex = name.indexOf(query, 0, true)
        val builder = AnnotatedString.Builder(name)
        //If the query is in the name, add a style, otherwise do nothing
        if (startIndex >= 0) {
            val endIndex = startIndex + query.length
            builder.addStyle(highlightStyle, startIndex, endIndex)
        }
        return builder.toAnnotatedString()
    }

    fun clearSearch() {
        this.query = ""
        findings.clear()
    }
}