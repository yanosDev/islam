package de.yanos.islam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Quiz(
    @PrimaryKey val id: String,
    val topicId: String,
    val question: String,
    val answer: String,
    val difficulty: Int
)