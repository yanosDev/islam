package de.yanos.islam.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.yanos.islam.data.database.dao.AwqatDao
import de.yanos.islam.data.database.dao.ChallengeDao
import de.yanos.islam.data.database.dao.QuizDao
import de.yanos.islam.data.database.dao.QuranDao
import de.yanos.islam.data.database.dao.SearchDao
import de.yanos.islam.data.database.dao.TopicDao
import de.yanos.islam.data.database.dao.VideoDao
import de.yanos.islam.data.model.Challenge
import de.yanos.islam.data.model.Degree
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.data.model.Schedule
import de.yanos.islam.data.model.Search
import de.yanos.islam.data.model.Topic
import de.yanos.islam.data.model.VideoLearning
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.data.model.awqat.CityDetail
import de.yanos.islam.data.model.awqat.CityEid
import de.yanos.islam.data.model.awqat.Location
import de.yanos.islam.data.model.awqat.PrayerTime
import de.yanos.islam.data.model.quran.Ayah
import de.yanos.islam.data.model.quran.Surah

interface IslamDatabase {
    fun topicDao(): TopicDao
    fun quizDao(): QuizDao
    fun quranDao(): QuranDao
    fun quizFormDao(): ChallengeDao
    fun awqatDao(): AwqatDao
    fun searchDao(): SearchDao
    fun videoDao(): VideoDao
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
        VideoLearning::class
    ],
    version = 1
)
internal abstract class IslamDatabaseImpl : IslamDatabase, RoomDatabase() {
    abstract override fun topicDao(): TopicDao
    abstract override fun quizDao(): QuizDao
    abstract override fun quizFormDao(): ChallengeDao
    abstract override fun awqatDao(): AwqatDao
    abstract override fun searchDao(): SearchDao
    abstract override fun quranDao(): QuranDao
    abstract override fun videoDao(): VideoDao
}