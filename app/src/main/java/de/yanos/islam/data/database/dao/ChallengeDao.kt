package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import de.yanos.islam.data.model.Challenge
import de.yanos.islam.ui.challenge.open.OpenChallenge
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao : BaseDao<Challenge> {
    @Query(
        "SELECT " +
                "q.id, " +
                "q.quizCount AS count, " +
                "COUNT(q.solvedQuizList) AS corrects, " +
                "COUNT(q.failedQuizList) AS failures, " +
                "GROUP_CONCAT(t.title) AS topics " +
                "FROM Challenge q " +
                "JOIN Topic t WHERE t.id in (q.topicIds)"
    )
    fun openChallenges(): Flow<List<OpenChallenge>>

    @Query("SELECT id FROM Challenge ORDER BY createdAt DESC LIMIT 1")
    fun newestChallengeId(): Int

    @Query("DELETE FROM Challenge WHERE finished = 0")
    fun deleteAllOpenChallenges()

    @Query("SELECT * FROM Challenge WHERE id = :id")
    fun loadForm(id: Int): Flow<Challenge?>

    @Query("DELETE FROM Challenge WHERE id = :id")
    fun deleteById(id: Int)

    @Query("UPDATE Challenge SET currentIndex = :index WHERE id = :id")
    fun updateIndex(id: Int, index: Int)

    @Query("UPDATE CHALLENGE SET solvedQuizList = :solved AND failedQuizList = :failed AND finished = :finished")
    fun updateResults(solved: List<Int>, failed: List<Int>, finished: Boolean)
}