package de.yanos.islam.data.model.tanzil

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TanzilSureResponse(val sureaditr: String, val sure: List<TanzilAyet>)

@JsonClass(generateAdapter = true)
data class TanzilAyet(
    val ayetID: Int,
    val surear: String,
    val suretrans: String,
    val suretur: String,
    val sureen: String,
)

