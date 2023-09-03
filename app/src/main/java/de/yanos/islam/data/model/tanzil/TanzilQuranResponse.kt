package de.yanos.islam.data.model.tanzil

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TanzilQuranResponse(val kuran: List<SureDetail>, val links: Map<String, String>)

@JsonClass(generateAdapter = true)
@Entity
data class SureDetail(
    @PrimaryKey val sureaditr: String,
    val sureadiar: String,
    val sureadiaroku: String,
    val sureadien: String,
    val ayetsayisi: Int,
    val cuz: String,
    val sayfa: String,
    val kuransira: String,
    val inissira: String,
    val alfabesira: String,
    val yer: String,
    val sureaciklama: String,
)