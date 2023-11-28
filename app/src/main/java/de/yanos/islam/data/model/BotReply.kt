package de.yanos.islam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BotReply(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val question: String,
    val replies: List<String>,
    val ts: Long
)