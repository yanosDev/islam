package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.Topic
import de.yanos.islam.ui.challenge.create.TopicSelection
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao : BaseDao<Topic> {
    @Query("SELECT * FROM Topic WHERE type != 'SUB' ORDER BY id")
    fun allMain(): Flow<List<Topic>>

    @Query("SELECT id as id, title as title, ordinal as ordinal, parentId as parentId, type as type, 1 as isSelected FROM Topic ORDER BY ordinal")
    fun allAsSelection(): Flow<List<TopicSelection>>

    @Query("SELECT * FROM Topic WHERE parentId = :id ORDER BY id")
    fun loadSubTopics(id: Int): Flow<List<Topic>>

    @Query("SELECT title FROM Topic WHERE id IN (:id)")
    fun loadTopicNames(id: List<Int>): Flow<List<String>>
}