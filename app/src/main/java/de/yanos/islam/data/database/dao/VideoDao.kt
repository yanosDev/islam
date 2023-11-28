package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.VideoLearning

@Dao
interface VideoDao : BaseDao<VideoLearning> {
    @Query("SELECT * FROM VideoLearning ORDER BY `index`")
    suspend fun loadAll(): List<VideoLearning>
}