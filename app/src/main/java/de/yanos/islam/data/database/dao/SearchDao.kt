package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.Search
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao : BaseDao<Search> {

    @Query("SELECT * FROM Search ORDER BY ts LIMIT 4")
    fun getRecentSearches(): Flow<List<Search>>

}