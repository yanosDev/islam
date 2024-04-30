package de.yanos.islam.util.helper

sealed class ChallengeDifficulty(
    val quizCount: Int,
    val quizMinDifficulty: Int,
) {
    fun count(): String = (this as? Max)?.let { "∞" } ?: quizCount.toString()
    fun diff(): String = (this as? Max)?.let { "∞" } ?: quizMinDifficulty.toString()

    object Low : ChallengeDifficulty(10, 0)
    object Medium : ChallengeDifficulty(20, 2)
    object High : ChallengeDifficulty(50, 3)
    object Max : ChallengeDifficulty(Int.MAX_VALUE, Int.MAX_VALUE)
    data class Custom(val count: Int, val difficulty: Int) : ChallengeDifficulty(count, difficulty)
}