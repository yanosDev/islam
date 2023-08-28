package de.yanos.islam.data.database.dao

import androidx.compose.runtime.collectAsState
import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.Challenge
import de.yanos.islam.ui.challenge.open.OpenChallenge
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface ChallengeDao : BaseDao<Challenge> {

    @Query(
        "SELECT" +
                "    CASE WHEN EXISTS " +
                "    (" +
                "        SELECT * FROM CHALLENGE WHERE finished = 0 " +
                "    )" +
                "    THEN 1 " +
                "    ELSE 0 " +
                "END"
    )
    fun hasOpenChallenges(): Flow<Boolean>

    @Query(
        "SELECT q.id, q.quizCount AS count, q.solvedQuizList AS corrects,  q.failedQuizList AS failures, GROUP_CONCAT(t.title, ', ') AS topics " +
                "FROM Challenge q " +
                "LEFT JOIN TOPIC t " +
                "ON (q.topicIds LIKE '%' || t.id ||'%' AND t.type != 'SUB')  WHERE q.finished = 0 " +
                "GROUP BY q.id"
    )
    fun openChallenges(): Flow<List<OpenChallenge>>

    @Query("SELECT id FROM Challenge ORDER BY lastAction DESC LIMIT 1")
    fun newestChallengeId(): Int

    @Query("DELETE FROM Challenge WHERE finished = 0")
    fun deleteAllOpenChallenges()

    @Query("SELECT * FROM Challenge WHERE id = :id")
    fun loadForm(id: Int): Flow<Challenge>

    @Query("DELETE FROM Challenge WHERE id = :id")
    fun deleteById(id: Int)

    @Query("UPDATE Challenge SET currentIndex = :index, lastAction = :ts WHERE id = :id")
    fun updateIndex(id: Int, index: Int, ts: Long = System.currentTimeMillis())

    @Query("UPDATE Challenge SET solvedQuizList = :solved, failedQuizList = :failed, finished = :finished, lastAction = :ts WHERE id = :id")
    fun updateResults(id: Int, solved: List<Int>, failed: List<Int>, finished: Boolean, ts: Long = System.currentTimeMillis())

    @Query("UPDATE Challenge SET quizList = :quizList, lastAction = :ts WHERE id = :id")
    fun updateQuizList(id: Int, quizList: List<Int>, ts: Long = System.currentTimeMillis())
}