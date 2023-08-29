package de.yanos.islam.data.model.awqat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AwqatDailyContentResponse(val data: AwqatDailyContent)

@Entity
data class AwqatDailyContent(
    @PrimaryKey val id: Long,
    val dayOfYear: Long,
    val verse: String,
    val verseSource: String,
    val hadith: String,
    val hadithSource: String,
    val pray: String,
    val praySource: String?
)

