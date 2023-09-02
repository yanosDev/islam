package de.yanos.islam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Search(@PrimaryKey(autoGenerate = true) val id: Int = 0, val query: String, val ts: Long = System.currentTimeMillis())