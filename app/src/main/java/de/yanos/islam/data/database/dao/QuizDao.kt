package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.Quiz
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao : BaseDao<Quiz> {
    @Query("SELECT * FROM Quiz WHERE topicId IN (:ids)")
    fun loadAllByTopic(vararg ids: Int): Flow<List<Quiz>>

    @Query("SELECT * FROM Quiz WHERE topicId IN (:ids)")
    fun loadAllQuizByTopics(ids: Int): Flow<List<Quiz>>

    @Query("SELECT * FROM Quiz WHERE id IN (:ids) ORDER BY id")
    fun loadAllQuizByIds(vararg ids: Int): List<Quiz>
}