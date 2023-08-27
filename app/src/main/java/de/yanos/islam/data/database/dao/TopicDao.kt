package de.yanos.islam.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.Topic
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao : BaseDao<Topic> {
    @Query("SELECT * FROM Topic")
    fun all(): Flow<List<Topic>>
    @Query("SELECT * FROM Topic WHERE parentId = :id")
    fun loadSubTopics(id: Int): Flow<List<Topic>>
    @Query("SELECT title FROM Topic WHERE id = :parentId")
    fun loadTopicName(parentId: Int): Flow<String?>



    @Query("SELECT * FROM Topic WHERE LOWER(title) LIKE LOWER(:title)")
    suspend fun loadMainTopicByTitle(title: String): Topic?

    @Query("SELECT * FROM Topic WHERE parentId IS NULL")
    fun loadAllMainTopics(): Flow<List<Topic>>

    @Query("SELECT * FROM Topic")
    fun loadAllTopics(): Flow<List<Topic>>
}