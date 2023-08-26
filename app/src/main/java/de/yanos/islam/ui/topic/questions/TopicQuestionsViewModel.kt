package de.yanos.islam.ui.topic.questions

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.data.database.dao.QuizDao
import de.yanos.islam.data.model.Quiz
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicQuestionsViewModel @Inject constructor(
    private val dao: QuizDao
) : ViewModel() {
    var state = mutableStateListOf<Quiz>()

    fun loadTopicContent(id: Int) {
        viewModelScope.launch {
            dao.loadAllQuizByTopics(listOf(id)).distinctUntilChanged().collect {
                state.clear()
                state.addAll(it)
            }
        }
    }
}