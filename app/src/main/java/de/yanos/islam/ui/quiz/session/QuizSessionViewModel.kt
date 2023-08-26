package de.yanos.islam.ui.quiz.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.QuizDao
import de.yanos.islam.data.database.dao.QuizFormDao
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.data.model.QuizForm
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class QuizFormViewModel @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    //  @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val quizFormDao: QuizFormDao,
    private val quizDao: QuizDao
) : ViewModel() {
    var currentIndex by mutableStateOf(0)
    var quizList = mutableStateListOf<QuizItem>()
    var form: QuizForm? = null

    fun populateQuizForm(id: Int) {
        if (quizList.isEmpty())
            viewModelScope.launch(ioDispatcher) {
                quizFormDao.loadForm(id)?.let { quizForm ->
                    withContext(Dispatchers.Main) {
                        if (quizForm.quizList.isEmpty())
                            initForm(quizForm)
                        else retrieveForm(quizForm)
                        updateForm(quizForm.copy())
                    }
                }
            }
    }

    private suspend fun initForm(quizForm: QuizForm) {
        quizDao.loadAllQuizByTopics(quizForm.topicIds).collect { newList ->
            val selectedQuizList = mutableListOf<Quiz>()
            val prioList = newList.filter { it.difficulty >= quizForm.quizDifficulty }
            val selectedIndex = mutableListOf<Int>()
            for (i in 0 until quizForm.quizCount) {
                var index: Int
                do {
                    index = Random.nextInt((prioList.size - 1) * 1000) / 1000
                } while (selectedIndex.contains(index))
                selectedIndex.add(index)
                selectedQuizList.add(prioList[index])
            }
            quizList.clear()
            quizList.addAll(selectedQuizList.map {
                QuizItem(
                    id = it.id,
                    question = it.question,
                    answer = it.answer,
                    showSolution = false,
                    answerResult = AnswerResult.OPEN
                )
            })
        }
    }

    private suspend fun retrieveForm(quizForm: QuizForm) {
        quizList.clear()
        quizList.addAll(quizDao.loadAllQuizByIds(quizForm.quizList).map {
            QuizItem(
                id = it.id,
                question = it.question,
                answer = it.answer,
                showSolution = false,
                answerResult = when {
                    quizForm.solvedQuizList.contains(it.id) -> AnswerResult.CORRECT
                    quizForm.failedQuizList.contains(it.id) -> AnswerResult.FAILURE
                    else -> AnswerResult.OPEN
                }
            )
        })
    }

    private suspend fun updateForm(quizForm: QuizForm) {
        form?.let {
            quizFormDao.delete(it)
        }
        withContext(Dispatchers.Main) {
            form = quizForm
        }
        quizFormDao.insert(quizForm)
    }

    fun updateAnswerVisibility(id: Int, showAnswer: Boolean) {
        viewModelScope.launch {
            val quiz = quizList.find { it.id == id }
            quiz?.let {
                val index = quizList.indexOf(quiz)
                val newList = quizList.toMutableList()
                newList.removeAt(index)
                newList.add(index, quiz.copy(showSolution = showAnswer))
                quizList.clear()
                quizList.addAll(newList)
            }
        }
    }

    fun updateQuizResult(id: Int, result: AnswerResult) {
        viewModelScope.launch {
            val quiz = quizList.find { it.id == id }
            quiz?.let {
                val index = quizList.indexOf(quiz)
                val newList = quizList.toMutableList()
                newList.removeAt(index)
                newList.add(index, quiz.copy(answerResult = result))
                quizList.clear()
                quizList.addAll(newList)

                form?.copy(
                    solvedQuizList = quizList
                        .filter { it.answerResult == AnswerResult.CORRECT }
                        .map { it.id },
                    failedQuizList = quizList
                        .filter { it.answerResult == AnswerResult.FAILURE }
                        .map { it.id }
                )?.let {
                    updateForm(it)
                }
            }
        }
    }
}

data class QuizItem(
    val id: Int,
    val question: String,
    val answer: String,
    val showSolution: Boolean,
    val answerResult: AnswerResult
)

enum class AnswerResult {
    CORRECT, FAILURE, OPEN
}