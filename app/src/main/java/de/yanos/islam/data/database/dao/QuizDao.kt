package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.Quiz
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao : BaseDao<Quiz> {
    @Query("SELECT * FROM Quiz WHERE topicId = :id")
    fun loadAllQuizByTopic(id: Int): Flow<List<Quiz>>
}