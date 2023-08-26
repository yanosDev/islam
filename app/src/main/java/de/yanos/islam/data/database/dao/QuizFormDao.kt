package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.QuizForm
import de.yanos.islam.ui.quiz.config.RecentForm
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizFormDao : BaseDao<QuizForm> {
    @Query("SELECT id FROM QuizForm ORDER BY createdAt DESC LIMIT 1")
    fun recentFormId(): Int

    @Query("SELECT * FROM QuizForm WHERE id = :id")
    fun loadForm(id: Int): QuizForm?

    @Query("SELECT * FROM QuizForm WHERE finished = 0 ORDER BY createdAt")
    fun loadOpenQuiz(): List<QuizForm>

    @Query("DELETE FROM QuizForm WHERE id = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM QuizForm WHERE finished = 0")
    fun deleteAllOpenForms()
}