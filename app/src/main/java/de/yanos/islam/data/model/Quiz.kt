package de.yanos.islam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Quiz(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topicId: Int,
    val question: String,
    val answer: String,
    val difficulty: Int
)

@Entity
data class QuizForm(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topicIds: List<Int>,
    val createdAt: Long = System.currentTimeMillis(),
    var finished: Boolean = false,
    val quizCount: Int,
    val quizDifficulty: Int,
    var currentIndex: Int = 0,
    var quizList: List<Int> = listOf(),
    var solvedQuizList: List<Int> = listOf(),
    var failedQuizList: List<Int> = listOf()
)