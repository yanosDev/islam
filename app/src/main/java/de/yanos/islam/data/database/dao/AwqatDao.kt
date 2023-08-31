package de.yanos.islam.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.yanos.islam.data.model.awqat.AwqatCityDetails
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.data.model.awqat.AwqatLocation
import de.yanos.islam.data.model.awqat.CityData
import de.yanos.islam.data.model.awqat.CityDetail
import de.yanos.islam.data.model.awqat.Location
import de.yanos.islam.data.model.awqat.PrayerTime
import kotlinx.coroutines.flow.Flow

@Dao
interface AwqatDao : BaseDao<PrayerTime> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDailyContent(dailyContentData: AwqatDailyContent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCityPrayerTime(time: PrayerTime)

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
                "t.astronomicalSunrise as sunriseLocation " +
                "FROM CityDetail  c, Degree d " +
                "JOIN PrayerTime t ON d.id = t.id " +
                "ORDER BY c.ts " +
                "LIMIT 1"
    )
    fun loadRecentCity(): Flow<CityData>

    @Query("SELECT id FROM Location WHERE (code = :locationName OR name = :locationName) AND type = 'CITY'  LIMIT 1")
    fun loadCityCode(locationName: String): Int?

    @Query("UPDATE Degree SET degree = :degree")
    fun updateDegree(degree: Int)
}