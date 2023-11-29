package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.yanos.islam.data.model.VideoLearning
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao : BaseDao<VideoLearning> {
    @Query("SELECT * FROM VideoLearning ORDER BY `index`")
    suspend fun loadAll(): List<VideoLearning>


    @Query("SELECT * FROM VideoLearning ORDER BY `index`")
    fun loadVideos(): Flow<List<VideoLearning>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImmediate(videos: List<VideoLearning>)
}