package de.yanos.islam.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import de.yanos.islam.data.database.dao.QuizDao
import de.yanos.islam.data.database.dao.QuizFormDao
import de.yanos.islam.data.database.dao.TopicDao
import de.yanos.islam.data.model.Quiz
import de.yanos.islam.data.model.QuizForm
import de.yanos.islam.data.model.Topic

interface IslamDatabase {
    fun topicDao(): TopicDao
    fun quizDao(): QuizDao

    fun quizFormDao(): QuizFormDao
}

@TypeConverters(Converters::class)
@Database(
    entities = [Topic::class, Quiz::class, QuizForm::class],
    version = 1
)
internal abstract class IslamDatabaseImpl : IslamDatabase, RoomDatabase() {
    abstract override fun topicDao(): TopicDao
    abstract override fun quizDao(): QuizDao
    abstract override fun quizFormDao(): QuizFormDao
}