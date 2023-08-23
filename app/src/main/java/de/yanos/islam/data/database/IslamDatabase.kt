package de.yanos.islam.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.yanos.islam.data.database.dao.QuizDao
import de.yanos.islam.data.database.dao.TopicDao
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.data.model.Topic

interface IslamDatabase {
    fun topicDao(): TopicDao
    fun quizDao(): QuizDao
}

@Database(
    entities = [Topic::class, Quiz::class],
    version = 1
)
internal abstract class IslamDatabaseImpl : IslamDatabase, RoomDatabase() {
    abstract override fun topicDao(): TopicDao
    abstract override fun quizDao(): QuizDao
}