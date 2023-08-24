package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.Topic
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao : BaseDao<Topic> {
    @Query("SELECT * FROM Topic WHERE LOWER(title) LIKE LOWER(:title)")
    suspend fun loadMainTopicByTitle(title: String): Topic?

    @Query("SELECT * FROM Topic WHERE parentTopicId IS NULL")
    fun loadAllMainTopics(): Flow<List<Topic>>

    @Query("SELECT * FROM Topic WHERE parentTopicId LIKE :id")
    fun loadSubTopics(id: Int): Flow<List<Topic>>
}