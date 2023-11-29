package de.yanos.islam.util

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `Bookmark` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + " `ts` INTEGER NOT NULL, " + " `nr` INTEGER NOT NULL, `type` TEXT NOT NULL)"
            )
        }
    }
}
