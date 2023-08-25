package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.QuizForm

@Dao
interface QuizFormDao : BaseDao<QuizForm> {
    @Query("SELECT id FROM QuizForm ORDER BY createdAt DESC LIMIT 1")
    fun recentFormId(): Int
}