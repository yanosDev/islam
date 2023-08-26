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
    val finished: Boolean = false,
    val quizCount: Int,
    val quizDifficulty: Int,
    val quizList: List<Int> = listOf(),
    val solvedQuizList: List<Int> = listOf(),
    val failedQuizList: List<Int> = listOf()
)