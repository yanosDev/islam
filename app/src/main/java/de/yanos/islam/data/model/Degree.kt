package de.yanos.islam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Degree(@PrimaryKey val id: Int = 0, val degree: Int)