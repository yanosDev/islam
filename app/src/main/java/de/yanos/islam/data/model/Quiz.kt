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
    val quizIds: List<Int>,
    val createdAt: Long,
    val quizCount: Int,
    val quizDifficulty: Int,
    val solved: Int
)