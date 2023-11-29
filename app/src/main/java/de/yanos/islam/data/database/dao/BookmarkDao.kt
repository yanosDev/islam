package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BookmarkDao : BaseDao<Bookmark> {
    @Query("SELECT * FROM Bookmark ORDER BY id DESC")
    abstract fun loadBookmarks(): Flow<List<Bookmark>>
}