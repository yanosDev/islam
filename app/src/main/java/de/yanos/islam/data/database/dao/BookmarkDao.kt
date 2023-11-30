package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.QuranBookmark
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BookmarkDao : BaseDao<QuranBookmark> {
    @Query("SELECT * FROM QuranBookmark ORDER BY id DESC")
    abstract fun loadBookmarks(): Flow<List<QuranBookmark>>
}