package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.BotReply
import kotlinx.coroutines.flow.Flow

@Dao
interface BotDao : BaseDao<BotReply> {
    @Query("SELECT * FROM BotReply ORDER BY ts DESC LIMIT 100")
    fun loadPreviousQuestions(): Flow<List<BotReply>>

    @Query("UPDATE BOTREPLY SET  replies = :replies WHERE id = :id")
    fun updateReplies(id: Long, replies: List<String>)
    @Query("SELECT id FROM BotReply ORDER BY ts DESC LIMIT 1")
    fun recentRequestId(): Long
}