package de.yanos.islam.data.database

import android.content.Context
import androidx.annotation.RawRes
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.yanos.islam.data.database.dao.AwqatDao
import de.yanos.islam.data.database.dao.BookmarkDao
import de.yanos.islam.data.database.dao.BotDao
import de.yanos.islam.data.database.dao.ChallengeDao
import de.yanos.islam.data.database.dao.QuizDao
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.data.database.dao.SearchDao
import de.yanos.islam.data.database.dao.TopicDao
import de.yanos.islam.data.database.dao.VideoDao
import de.yanos.islam.data.model.BotReply
import de.yanos.islam.data.model.Challenge
import de.yanos.islam.data.model.Degree
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.data.model.QuranBookmark
import de.yanos.islam.data.model.Schedule
import de.yanos.islam.data.model.Search
import de.yanos.islam.data.model.Topic
import de.yanos.islam.data.model.TopicResource
import de.yanos.islam.data.model.TopicType
import de.yanos.islam.data.model.VideoLearning
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.data.model.awqat.CityDetail
import de.yanos.islam.data.model.awqat.CityEid
import de.yanos.islam.data.model.awqat.Location
import de.yanos.islam.data.model.awqat.PrayerTime
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Surah
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader

interface IslamDatabase {
    fun topicDao(): TopicDao
    fun quizDao(): QuizDao
    fun quranDao(): QuranDao
    fun quizFormDao(): ChallengeDao
    fun awqatDao(): AwqatDao
    fun searchDao(): SearchDao
    fun videoDao(): VideoDao
    fun botDao(): BotDao
    fun bookmarkDao(): BookmarkDao
    suspend fun create(context: Context)
}

@TypeConverters(Converters::class)
@Database(
    entities = [
        Topic::class,
        Quiz::class,
        Challenge::class,
        AwqatDailyContent::class,
        Location::class,
        PrayerTime::class,
        CityDetail::class,
        Degree::class,
        Search::class,
        CityEid::class,
        Schedule::class,
        Surah::class,
        Ayah::class,
        VideoLearning::class,
        BotReply::class,
        QuranBookmark::class
    ],
    version = 4
)
internal abstract class IslamDatabaseImpl : IslamDatabase, RoomDatabase() {
    abstract override fun topicDao(): TopicDao
    abstract override fun quizDao(): QuizDao
    abstract override fun quizFormDao(): ChallengeDao
    abstract override fun awqatDao(): AwqatDao
    abstract override fun searchDao(): SearchDao
    abstract override fun quranDao(): QuranDao
    abstract override fun videoDao(): VideoDao
    abstract override fun botDao(): BotDao
    abstract override fun bookmarkDao(): BookmarkDao

    override suspend fun create(context: Context) {
        TopicResource.values().forEach { topic ->
            topic.raw?.let { raw ->
                createQuizByTopic(context, raw, topic.id)
            }
            topicDao().insert(
                Topic(
                    id = topic.id, title = topic.title, ordinal = topic.ordinal, parentId = topic.parent, type = when {
                        topic.parent == null && topic.raw != null -> TopicType.MAIN
                        topic.parent == null -> TopicType.GROUP
                        else -> TopicType.SUB
                    }
                )
            )
        }

        awqatDao().insertSchedules(
            listOf(
                Schedule(
                    id = "fajr",
                    ordinal = 0
                ),
                Schedule(
                    id = "sunrise",
                    ordinal = 1,
                    enabled = true,
                    relativeTime = -45
                ),
                Schedule(
                    id = "dhuhr",
                    ordinal = 2
                ),
                Schedule(
                    id = "asr",
                    ordinal = 3
                ),
                Schedule(
                    id = "maghrib",
                    ordinal = 4,
                    enabled = true,
                    relativeTime = -5
                ),
                Schedule(
                    id = "isha",
                    ordinal = 5
                ),
            )
        )
    }

    private fun createQuizByTopic(context: Context, @RawRes topicRaw: Int, topicId: Int) {
        val regex = Regex("""\d+|\D+""")
        val br = BufferedReader(InputStreamReader(context.resources.openRawResource(topicRaw)))
        val lines = br.readLines()
        lines.forEachIndexed { index, line ->
            if (line.trim().startsWith("*")) {
                val actualLine = mergeLines(lines, index)
                    .replace("*", "")
                    .replace("  ", " ")
                    .trim()
                try {
                    actualLine.split("?").let { (question, answer) ->
                        val parts = regex.findAll(answer).map { it.groupValues.first() }.toList()
                        val builder = StringBuilder()
                        parts.forEach {
                            val text = it.trim()
                            if (text.isNotBlank())
                                when {
                                    !it.matches(Regex("""\d+""")) || builder.isEmpty() -> builder.append(" $text")
                                    else -> builder.append("\n$text ")
                                }
                        }
                        quizDao().insert(Quiz(question = question, answer = builder.toString().replace("  ", " ").trim(), topicId = topicId, difficulty = 0))
                    }
                } catch (e: Exception) {
                    Timber.e(actualLine)
                    e.localizedMessage?.let { Timber.e("e", it) }
                }
            }
        }
    }

    private fun mergeLines(lines: List<String>, index: Int): String {
        val currentLine = lines[index]
        return if (index == lines.size - 1 || lines[index + 1].trim().startsWith("*")) {
            currentLine
        } else currentLine.trim() + " " + mergeLines(lines, index + 1).trim()
    }
}