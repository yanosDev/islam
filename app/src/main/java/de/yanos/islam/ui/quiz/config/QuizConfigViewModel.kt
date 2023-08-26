package de.yanos.islam.ui.quiz.config

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.QuizFormDao
import de.yanos.islam.data.database.dao.TopicDao
import de.yanos.islam.data.model.QuizForm
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuizSelectionViewModel @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
  //  @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val topicDao: TopicDao,
    private val quizFormDao: QuizFormDao,
) : ViewModel() {
    private var selections = mutableListOf<TopicSelection>()
    var difficulty by mutableStateOf<Difficulty>(Difficulty.Low)
    var state = mutableStateListOf<List<TopicSelection>>()

    init {
        viewModelScope.launch {
            topicDao.loadAllTopics().distinctUntilChanged().collect { topics ->
                selections.clear()
                selections.addAll(topics.filter { !it.hasSubTopics && it.parentTopicId == null }.map { TopicSelection(id = it.id, title = it.title, isSelected = true, null) })
                topics.filter { it.hasSubTopics }.forEach { detailedTopic ->
                    selections.add(
                        TopicSelection(
                            id = detailedTopic.id,
                            title = detailedTopic.title,
                            isSelected = true,
                            null
                        )
                    )
                    selections.addAll(topics.filter { it.parentTopicId == detailedTopic.id }
                        .map { TopicSelection(id = it.id, title = it.title, isSelected = true, it.parentTopicId) })
                }
                recreateList()
            }
        }
    }

    fun updateSelection(id: Int, selected: Boolean) {
        viewModelScope.launch {
            val changedSelection = selections.firstOrNull { it.id == id }
            val index = selections.indexOf(changedSelection)
            val newList = selections.toMutableList()
            newList.removeAt(index)
            changedSelection?.let {
                it.isSelected = selected
                newList.add(index, it)
                selections.clear()
                selections.addAll(newList)
            }
            recreateList()
        }
    }

    private fun recreateList() {
        var batch = mutableListOf<TopicSelection>()
        val newList = mutableListOf<List<TopicSelection>>()
        selections.forEachIndexed { index, topicSelection ->
            when {
                index == 0 || index == selections.size - 1 -> batch.add(topicSelection)
                selections[index + 1].parentId == null -> batch.add(topicSelection)
                (topicSelection.parentId != null && (selections[index - 1].parentId == topicSelection.parentId || selections[index + 1].parentId == topicSelection.parentId)) -> batch.add(
                    topicSelection
                )

                else -> {
                    newList.add(batch)
                    batch = mutableListOf()
                    batch.add(topicSelection)
                }
            }
        }
        newList.add(batch)
        state.clear()
        state.addAll(newList)
    }

    fun onDifficultyChange(difficulty: Difficulty) {
        viewModelScope.launch {
            this@QuizSelectionViewModel.difficulty = difficulty
        }
    }

    fun generateQuizForm(callback: (Int) -> Unit) {
        viewModelScope.launch(ioDispatcher) {

            quizFormDao.insert(
                QuizForm(
                    quizIds = selections.filter { topic -> topic.isSelected && selections.none { it.parentId == topic.id } }.map { it.id },
                    createdAt = System.currentTimeMillis(),
                    quizCount = difficulty.quizCount,
                    quizDifficulty = difficulty.quizMinDifficulty,
                    solved = 0
                )
            )
            val id = quizFormDao.recentFormId()
            withContext(Dispatchers.Main){
                callback(id)
            }
        }
    }
}

sealed class Difficulty(
    val quizCount: Int,
    val quizMinDifficulty: Int,
) {
    fun count(): String = (this as? Max)?.let { "∞" } ?: quizCount.toString()
    fun diff(): String = (this as? Max)?.let { "∞" } ?: quizMinDifficulty.toString()

    object Low : Difficulty(10, 0)
    object Medium : Difficulty(20, 2)
    object High : Difficulty(50, 3)
    object Max : Difficulty(Int.MAX_VALUE, Int.MAX_VALUE)
    data class Custom(val count: Int, val difficulty: Int) : Difficulty(count, difficulty)
}

data class TopicSelection(
    val id: Int,
    val title: String,
    var isSelected: Boolean,
    val parentId: Int?
)