package de.yanos.islam.data.model.awqat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AwqatEidResponse(val data: AwqatEid)

@JsonClass(generateAdapter = true)
data class AwqatEid(
    val eidAlAdhaHijri: String,
    val eidAlAdhaTime: String,
    val eidAlAdhaDate: String,
    val eidAlFitrHijri: String,
    val eidAlFitrTime: String,
    val eidAlFitrDate: String,
)

@Entity
data class CityEid(
    val cityId: Int,
    @PrimaryKey val key: String,
    val eidAlAdhaHijri: String,
    val eidAlAdhaTime: String,
    val eidAlAdhaDate: String,
    val eidAlFitrHijri: String,
    val eidAlFitrTime: String,
    val eidAlFitrDate: String,
)