package de.yanos.islam.ui.knowledge.topics.sub

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.data.database.dao.TopicDao
import javax.inject.Inject

@HiltViewModel
class SubTopicViewModel @Inject constructor(
    private val dao: TopicDao,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val parentId: Int = savedStateHandle["id"]!!
    val list = dao.loadSubTopics(parentId)
    val topicName = dao.loadTopicNames(listOf(parentId))

}