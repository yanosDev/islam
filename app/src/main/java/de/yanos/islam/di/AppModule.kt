package de.yanos.islam.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.yanos.islam.data.database.IslamDatabase
import de.yanos.islam.data.database.IslamDatabaseImpl

@Module
@InstallIn(SingletonComponent::class)
internal class AppModule {
    fun provideDB(@ApplicationContext context: Context): IslamDatabase {
        return Room.databaseBuilder(context, IslamDatabaseImpl::class.java, "islam_db")
            .build()
    }
}