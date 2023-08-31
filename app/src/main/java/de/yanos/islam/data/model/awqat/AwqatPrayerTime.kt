package de.yanos.islam.data.model.awqat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AwqatPrayerTimeResponse(val data: List<AwqatPrayerTime>)

@JsonClass(generateAdapter = true)
data class AwqatPrayerTime(
    val shapeMoonUrl: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val astronomicalSunset: String,
    val astronomicalSunrise: String,
    val hijriDateShort: String,
    val hijriDateLong: String,
    val qiblaTime: String,
    val gregorianDateShort: String,
    val gregorianDateLong: String,
)

@Entity
data class PrayerTime(
    @PrimaryKey val id: Int,
    val ts: Long = System.currentTimeMillis(),
    val shapeMoonUrl: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val astronomicalSunset: String,
    val astronomicalSunrise: String,
    val hijriDateShort: String,
    val hijriDateLong: String,
    val qiblaTime: String,
    val gregorianDateShort: String,
    val gregorianDateLong: String,
)