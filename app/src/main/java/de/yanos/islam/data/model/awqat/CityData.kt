package de.yanos.islam.data.model.awqat

data class CityData(
    val id: Int,
    val degree: Int,
    val name: String,
    val qibla: Double,
    val url: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val sunsetLocation: String,
    val sunriseLocation: String,
    val gregorianDateShort: String,
)