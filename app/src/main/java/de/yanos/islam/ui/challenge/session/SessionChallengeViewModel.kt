package de.yanos.islam.ui.challenge.session

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.ChallengeDao
import de.yanos.islam.data.database.dao.QuizDao
import de.yanos.islam.data.model.Challenge
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.util.correctColor
import de.yanos.islam.util.errorColor
import de.yanos.islam.util.goldColor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SessionChallengeViewModel @Inject constructor(
    private val challengeDao: ChallengeDao,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val quizDao: QuizDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val challengeId: Int = savedStateHandle["id"]!!
    val challenge = challengeDao.loadForm(challengeId)
    var currentIndex by mutableStateOf(0)
    var challengeQuizList = mutableStateListOf<QuizItem>()

    init {
        viewModelScope.launch {
            challenge.collect {
                it?.let { c ->
                    currentIndex = c.currentIndex
                    if (challengeQuizList.isEmpty())
                        if (c.quizList.isEmpty()) {
                            initChallenge(c)
                        } else reloadChallenge(c)
                }
            }
        }
    }

    private suspend fun initChallenge(item: Challenge) {
        quizDao.loadAllByTopic(item.topicIds).collect { newList ->
            val selectedQuizList = mutableListOf<Quiz>()
            val prioList = newList.filter { it.difficulty >= item.quizDifficulty }
            val selectedIndex = mutableListOf<Int>()
            if (item.quizCount != Int.MAX_VALUE && item.quizCount < newList.size) {
                for (i in 0 until item.quizCount) {
                    var index: Int
                    val useList = if (selectedIndex.size < prioList.size) prioList else newList
                    do {
                        index = Random.nextInt((useList.size - 1) * 1000) / 1000
                    } while (selectedIndex.contains(index) && selectedIndex.size < newList.size)
                    selectedIndex.add(index)
                    selectedQuizList.add(useList[index])
                }
            } else {
                selectedQuizList.addAll(newList)
            }

            withContext(ioDispatcher) {
                challengeDao.update(item.copy(topicIds = selectedQuizList.map { it.id }))
            }
        }
    }

    private suspend fun reloadChallenge(item: Challenge) {
        withContext(ioDispatcher) {
            quizDao.loadAllQuizByIds(item.quizList).map {
                QuizItem(
                    id = it.id,
                    question = it.question,
                    answer = it.answer,
                    showSolution = false,
                    answerResult = when {
                        item.solvedQuizList.contains(it.id) -> AnswerResult.CORRECT
                        item.failedQuizList.contains(it.id) -> AnswerResult.FAILURE
                        else -> AnswerResult.OPEN
                    }
                )
            }.let {
                withContext(Dispatchers.Main) {
                    challengeQuizList.clear()
                    challengeQuizList.addAll(it)
                    currentIndex = item.currentIndex
                }
            }
        }
    }

    fun updateAnswerVisibility(id: Int, showAnswer: Boolean) {
        viewModelScope.launch {
            val quiz = challengeQuizList.find { it.id == id }
            quiz?.let {
                val index = challengeQuizList.indexOf(quiz)
                val newList = challengeQuizList.toMutableList()
                newList.removeAt(index)
                newList.add(index, quiz.copy(showSolution = showAnswer))
                challengeQuizList.clear()
                challengeQuizList.addAll(newList)
            }
        }
    }

    fun updateQuizResult(id: Int, result: AnswerResult) {
        viewModelScope.launch {
            val quiz = challengeQuizList.find { it.id == id }
            quiz?.let {
                //Calculate New List
                val index = challengeQuizList.indexOf(quiz)
                val newList = challengeQuizList.toMutableList()
                newList.removeAt(index)
                newList.add(index, quiz.copy(answerResult = result))

                //Update DB
                val solved = newList
                    .filter { it.answerResult == AnswerResult.CORRECT }
                    .map { it.id }
                val failed = newList
                    .filter { it.answerResult == AnswerResult.FAILURE }
                    .map { it.id }
                withContext(ioDispatcher) {
                    challengeDao.updateResults(
                        solved,
                        failed,
                        solved.size + failed.size == challengeQuizList.size
                    )
                }

                //Update UI
                challengeQuizList.clear()
                challengeQuizList.addAll(newList)
            }
        }
    }

    fun updateIndex(index: Int) {
        viewModelScope.launch(ioDispatcher) {
            challengeDao.updateIndex(challengeId, index)
        }
    }
}


data class QuizItem(
    val id: Int,
    val question: String,
    val answer: String,
    val showSolution: Boolean,
    val answerResult: AnswerResult
) {
    @Composable
    fun resultColor(): Color = when (answerResult) {
        AnswerResult.CORRECT -> correctColor()
        AnswerResult.FAILURE -> errorColor()
        AnswerResult.OPEN -> goldColor()
    }
}

enum class AnswerResult {
    CORRECT, FAILURE, OPEN
}