package de.yanos.islam.ui.knowledge.topics.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.islam.data.database.dao.QuizDao
import de.yanos.islam.data.database.dao.TopicDao
import javax.inject.Inject

@HiltViewModel
class QuestionListViewModel @Inject constructor(
    private val quizDao: QuizDao,
    private val topicDao: TopicDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val topicId: Int = savedStateHandle["id"]!!
    private val parentId: Int = savedStateHandle["parentId"]!!
    val list = quizDao.loadAllByTopic(listOf(topicId))
    val topicName = topicDao.loadTopicNames(listOf(topicId, parentId))
}