package de.yanos.islam.util

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `Bookmark` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + " `ts` INTEGER NOT NULL, " + " `nr` INTEGER NOT NULL, `type` TEXT NOT NULL)"
            )
        }
    }
    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `Ayah` ADD COLUMN audioAlt TEXT")
        }
    }
    val MIGRATION_3_4: Migration = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE `Bookmark`")
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `QuranBookmark` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `page` INTEGER NOT NULL, `juz` INTEGER NOT NULL, `surahName` TEXT NOT NULL, `ayah` INTEGER NOT NULL)"
            )
        }
    }
    val MIGRATION_4_5: Migration = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `QuranBookmark` ADD COLUMN ayahId INTEGER NOT NULL DEFAULT 0")
        }
    }
}
