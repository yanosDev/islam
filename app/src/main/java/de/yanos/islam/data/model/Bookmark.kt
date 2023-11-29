package de.yanos.islam.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Bookmark(@PrimaryKey(autoGenerate = true) val id: Int = 0, val nr: Int, val type: BookmarkType, val ts: Long = System.currentTimeMillis())

enum class BookmarkType {
    JuzType, PageType
}