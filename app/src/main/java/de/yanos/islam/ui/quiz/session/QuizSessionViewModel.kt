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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
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

    fun populateQuizForm(id: Int) {
        viewModelScope.launch {
            quizFormDao.loadForm(id).distinctUntilChanged().collect { form ->
                form?.let { quizForm ->
                    quizDao.loadAllQuizByTopics(quizForm.quizIds).collect { newList ->
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
                                answerCorrect = AnswerResult.OPEN
                            )
                        })
                    }
                }
            }
        }
    }

    fun updateAnswerVisibility(id: Int, showAnswer: Boolean) {
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

data class QuizItem(val id: Int, val question: String, val answer: String, val showSolution: Boolean, val answerCorrect: AnswerResult)

enum class AnswerResult {
    CORRECT, FAILURE, OPEN
}