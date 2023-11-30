package de.yanos.islam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class QuranBookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val page: Int,
    val juz: Int,
    val surahName: String,
    val ayah: Int,
    val ayahId: Int,
)
