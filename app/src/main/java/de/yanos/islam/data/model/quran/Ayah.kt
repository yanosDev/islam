package de.yanos.islam.data.model.quran

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ayah(
    @PrimaryKey val id: Int,
    val sureId: Int,
    val sureName: String,
    val number: Int,
    val audio: String,
    val audioMore: String?,
    val text: String,
    val translationTr: String,
    val transliterationEn: String,
    val juz: Int,
    val page: Int
)