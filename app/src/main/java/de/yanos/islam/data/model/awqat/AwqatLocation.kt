package de.yanos.islam.data.model.awqat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AwqatLocationResponse(val data: List<AwqatLocation>)

@JsonClass(generateAdapter = true)
@Entity
data class AwqatLocation(@PrimaryKey val id: Long, val code: String, val name: String)