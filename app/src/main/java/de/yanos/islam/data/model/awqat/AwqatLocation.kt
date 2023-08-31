package de.yanos.islam.data.model.awqat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AwqatLocationResponse(val data: List<AwqatLocation>)

@JsonClass(generateAdapter = true)
@Entity
data class AwqatLocation(@PrimaryKey val id: Long, val code: String, val name: String)

@Entity
data class Location(@PrimaryKey val id: Long, val code: String, val name: String, val type: LocationType)

enum class LocationType {
    COUNTRY, CITY, STATE, Province
}