package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.data.model.awqat.PrayerTime

@Dao
interface AwqatDao : BaseDao<PrayerTime> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDailyContent(dailyContentData: AwqatDailyContent)
}