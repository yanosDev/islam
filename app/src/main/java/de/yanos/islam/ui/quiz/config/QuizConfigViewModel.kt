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
import de.yanos.islam.data.model.Topic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuizConfigViewModel @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    //  @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val topicDao: TopicDao,
    private val quizFormDao: QuizFormDao,
) : ViewModel() {
    private var selections = mutableListOf<TopicSelection>()
    var difficulty by mutableStateOf<Difficulty>(Difficulty.Low)
    var state = mutableStateListOf<List<TopicSelection>>()
    var recentForms = mutableStateListOf<RecentForm>()

    fun loadData() {
        viewModelScope.launch {
            topicDao.loadAllTopics().distinctUntilChanged().collect { topics ->
                populateRecentQuizList(topics)
                configSelections(topics)
            }
        }
    }

    private suspend fun populateRecentQuizList(topics: List<Topic>) {
        withContext(ioDispatcher) {
            val forms = quizFormDao.loadOpenQuiz().map { form ->
                val topicNames = topics.filter { form.topicIds.contains(it.id) }.map { it.title }
                RecentForm(
                    id = form.id,
                    count = form.quizList.size.toString(),
                    corrects = form.solvedQuizList.count().toString(),
                    failures = form.failedQuizList.count().toString(),
                    topics = "${(if (topicNames.size < 5) topicNames else topicNames.subList(0, 3)).joinToString(", ")} ..."
                )
            }
            withContext(Dispatchers.Main) {
                recentForms.clear()
                recentForms.addAll(forms)
            }
        }
    }

    private fun configSelections(topics: List<Topic>) {
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
            this@QuizConfigViewModel.difficulty = difficulty
        }
    }

    fun generateQuizForm(callback: (Int) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            quizFormDao.insert(
                QuizForm(
                    topicIds = selections.filter { topic -> topic.isSelected && selections.none { it.parentId == topic.id } }.map { it.id },
                    createdAt = System.currentTimeMillis(),
                    quizCount = difficulty.quizCount,
                    quizDifficulty = difficulty.quizMinDifficulty,
                )
            )
            val id = quizFormDao.recentFormId()
            withContext(Dispatchers.Main) {
                callback(id)
            }
        }
    }

    fun deleteForm(id: Int) {
        viewModelScope.launch {
            recentForms.firstOrNull { it.id == id }?.let {
                recentForms.remove(it)
                withContext(ioDispatcher) {
                    quizFormDao.deleteById(it.id)
                }
            }
        }
    }

    fun deleteAllForms() {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                quizFormDao.deleteAllOpenForms()
            }
            recentForms.clear()
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

data class RecentForm(
    val id: Int,
    val count: String,
    val corrects: String,
    val failures: String,
    val topics: String
)