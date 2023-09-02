package de.yanos.islam.data.model

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
    val hijriDateLong: String,
    val gregorianDateLong: String,
    val gregorianDateShort: String,
)