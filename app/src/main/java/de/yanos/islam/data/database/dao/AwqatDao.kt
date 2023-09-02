package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.yanos.islam.data.model.Degree
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.data.model.awqat.CityData
import de.yanos.islam.data.model.awqat.CityDetail
import de.yanos.islam.data.model.awqat.CityEid
import de.yanos.islam.data.model.awqat.Location
import de.yanos.islam.data.model.awqat.PrayerTime
import kotlinx.coroutines.flow.Flow

@Dao
interface AwqatDao : BaseDao<PrayerTime> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDailyContent(dailyContentData: AwqatDailyContent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCityPrayerTime(time: List<PrayerTime>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCityDetail(cityDetail: CityDetail)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocations(locations: List<Location>)

    @Query(
        "SELECT  c.id as id, " +
                "c.name as name, " +
                "d.degree as degree, " +
                "c.qiblaAngle as qibla, " +
                "t.shapeMoonUrl as url, " +
                "t.fajr as fajr, " +
                "t.sunrise as sunrise, " +
                "t.dhuhr as dhuhr, " +
                "t.asr as asr, " +
                "t.maghrib as maghrib, " +
                "t.isha as isha, " +
                "t.astronomicalSunset as sunsetLocation, " +
                "t.astronomicalSunrise as sunriseLocation, " +
                "t.gregorianDateShort as gregorianDateShort " +
                "FROM CityDetail  c, Degree d " +
                "JOIN PrayerTime t ON c.id = t.id " +
                "ORDER BY c.ts"
    )
    fun loadCityData(): Flow<List<CityData>>

    @Query("SELECT id FROM Location WHERE (code = :locationName OR name = :locationName) AND type = 'CITY'  LIMIT 1")
    fun loadCityCode(locationName: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateDegree(degree: Degree)

    @Query("SELECT * FROM AwqatDailyContent WHERE dayOfYear = :dayOfYear LIMIT 1")
    fun dailyContent(dayOfYear: Int): AwqatDailyContent?

    @Query("SELECT * FROM PrayerTime WHERE id = :cityId")
    fun loadCityTimes(cityId: Int): List<PrayerTime>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCityEid(cityEid: CityEid)
}