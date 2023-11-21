package de.yanos.islam.data.model.alquran

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuranTextResponse(val data: DataText)

@JsonClass(generateAdapter = true)
data class DataText(val surahs: List<SurahText>)

@JsonClass(generateAdapter = true)
data class SurahText(val number: Int, val name: String, val englishName: String, val ayahs: List<AyahText>, val revelationType: String)

@JsonClass(generateAdapter = true)
data class AyahText(val number: Int, val text: String, val juz: Int, val page: Int)

@JsonClass(generateAdapter = true)
data class QuranAudioResponse(val data: DataAudio)

@JsonClass(generateAdapter = true)
data class DataAudio(val surahs: List<SurahAudio>)

@JsonClass(generateAdapter = true)
data class SurahAudio(val number: Int, val name: String, val englishName: String, val englishNameTranslation: String, val ayahs: List<AyahAudio>, val revelationType: String)

@JsonClass(generateAdapter = true)
data class AyahAudio(val number: Int, val audio: String, val audioSecondary: List<String>, val text: String, val juz: Int, val page: Int)
