package de.yanos.islam.data.model.awqat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AwqatCityDetailsResponse(val data: AwqatCityDetails)

@JsonClass(generateAdapter = true)
@Entity
data class AwqatCityDetails(
    @PrimaryKey val id: Int,
    val name: String,
    val code: String?,
    val geographicQiblaAngle: String,
    val distanceToKaaba: String,
    val qiblaAngle: String,
    val city: String,
    val cityEn: String,
    val country: String,
    val countryEn: String,
)