package de.yanos.islam.data.model.quran

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Surah(
    @PrimaryKey val id: Int,
    val name: String,
    val ayahCount: Int,
    val engName: String,
    val meaning: String,
    val revelation: String,
    val juz: String
)