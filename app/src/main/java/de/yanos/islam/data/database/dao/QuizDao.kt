package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.data.model.Search
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao : BaseDao<Quiz> {
    @Query("SELECT * FROM Quiz WHERE topicId IN (:ids)")
    fun loadAllByTopic(ids: List<Int>): Flow<List<Quiz>>

    @Query("SELECT * FROM Quiz WHERE id IN (:ids) ORDER BY id")
    fun loadAllQuizByIds(ids: List<Int>): List<Quiz>

    @Query("SELECT * FROM Quiz WHERE (question LIKE '%' || :query || '%' OR  answer LIKE '%' || :query || '%' )")
    fun findMatches(query: String): List<Quiz>

    @Query("SELECT * FROM Search ORDER BY ts LIMIT 4")
    fun getRecentSearches(): Flow<List<Search>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearch(search: Search)
}