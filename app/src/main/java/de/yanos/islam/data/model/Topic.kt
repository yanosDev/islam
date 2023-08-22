package de.yanos.islam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Topic(@PrimaryKey val id: String, val title: String, val ordinal: Int)