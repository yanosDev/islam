package de.yanos.islam.ui.topic.content

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.QuizDao
import de.yanos.islam.data.database.dao.TopicDao
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.data.model.Topic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicContentViewModel @Inject constructor(
    private val dao: QuizDao
) : ViewModel() {
    var state = mutableStateListOf<Quiz>()

    fun loadTopicContent(id: Int) {
        viewModelScope.launch {
            dao.loadAllQuizByTopic(id).distinctUntilChanged().collect {
                state.clear()
                state.addAll(it)
            }
        }
    }
}