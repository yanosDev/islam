package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.Topic
import de.yanos.islam.ui.challenge.create.TopicSelection
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao : BaseDao<Topic> {
    @Query("SELECT * FROM Topic WHERE type != 'SUB'")
    fun allMain(): Flow<List<Topic>>

    @Query("SELECT id as id, title as title, ordinal as ordinal, parentId as parentId, type as type, 1 as isSelected FROM Topic")
    fun allAsSelection(): Flow<List<TopicSelection>>

    @Query("SELECT * FROM Topic WHERE parentId = :id")
    fun loadSubTopics(id: Int): Flow<List<Topic>>

    @Query("SELECT title FROM Topic WHERE id = :id")
    fun loadTopicNames(vararg id: Int): Flow<List<String>>
}