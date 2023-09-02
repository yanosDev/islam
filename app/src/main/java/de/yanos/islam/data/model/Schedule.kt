package de.yanos.islam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Schedule(@PrimaryKey val id: String, val relativeTime: Int = -45, val enabled: Boolean = false, val ordinal: Int)