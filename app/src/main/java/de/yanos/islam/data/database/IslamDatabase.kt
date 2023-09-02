package de.yanos.islam.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.yanos.islam.data.database.dao.AwqatDao
import de.yanos.islam.data.database.dao.ChallengeDao
import de.yanos.islam.data.database.dao.QuizDao
import de.yanos.islam.data.database.dao.TopicDao
import de.yanos.islam.data.model.Challenge
import de.yanos.islam.data.model.Degree
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.data.model.Schedule
import de.yanos.islam.data.model.Search
import de.yanos.islam.data.model.Topic
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.data.model.awqat.CityDetail
import de.yanos.islam.data.model.awqat.CityEid
import de.yanos.islam.data.model.awqat.Location
import de.yanos.islam.data.model.awqat.PrayerTime

interface IslamDatabase {
    fun topicDao(): TopicDao
    fun quizDao(): QuizDao
    fun quizFormDao(): ChallengeDao
    fun awqatDao(): AwqatDao
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
        Schedule::class
    ],
    version = 1
)
internal abstract class IslamDatabaseImpl : IslamDatabase, RoomDatabase() {
    abstract override fun topicDao(): TopicDao
    abstract override fun quizDao(): QuizDao
    abstract override fun quizFormDao(): ChallengeDao
    abstract override fun awqatDao(): AwqatDao
}