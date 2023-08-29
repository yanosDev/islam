package de.yanos.islam.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QiblaData (val data: Qibla)

@JsonClass(generateAdapter = true)
class Qibla(val latitude: Double, val longitude: Double, val direction: Double)