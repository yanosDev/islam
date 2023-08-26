package de.yanos.islam.ui.topic.list

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.TopicDao
import de.yanos.islam.data.model.Topic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val dao: TopicDao
) : ViewModel() {
    var state = mutableStateListOf<Topic>()

    init {
        viewModelScope.launch {
            dao.loadAllMainTopics().distinctUntilChanged().collect { topics ->
                state.clear()
                state.addAll(topics)
            }
        }
    }

    fun loadSubTopic(id: Int) {
        viewModelScope.launch {
            dao.loadSubTopics(id).distinctUntilChanged().collect { topics ->
                state.clear()
                state.addAll(topics)
            }
        }
    }
}