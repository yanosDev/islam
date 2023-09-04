package de.yanos.islam.data.model.quran

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Ayet(
    @PrimaryKey val id: String,
    val sureaditr: String,
    val sureOrdinal: Int,
    val ayetNr: Int,
    val surear: String,
    val suretrans: String,
    val suretur: String,
    val sureen: String,
)