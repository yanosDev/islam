package de.yanos.islam.data.repositories.source

import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.database.dao.AwqatDao
import de.yanos.islam.data.model.Degree
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.data.model.awqat.AwqatLocation
import de.yanos.islam.data.model.awqat.AwqatPrayerTime
import de.yanos.islam.data.model.awqat.CityDetail
import de.yanos.islam.data.model.awqat.Location
import de.yanos.islam.data.model.awqat.PrayerTime
import de.yanos.islam.data.usecase.LocationUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface LocalAwqatSource {
    suspend fun loadCityId(locationName: String): Int?
    suspend fun insertDailyContent(dailyContentData: AwqatDailyContent)
    suspend fun insertLocations(data: List<Location>)
    suspend fun insertCityDetails(cityDetail: CityDetail)
    suspend fun insertCityPrayerTimes(times: PrayerTime)
}

class LocalAwqatSourceImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val locationUseCase: LocationUseCase,
    private val dao: AwqatDao
) : LocalAwqatSource {

    private val localJob = Job()
    private val localScope by lazy { CoroutineScope(dispatcher + localJob) }

    init {
        locationUseCase.addCallback {
            localScope.launch {
                dao.updateDegree(Degree(degree = it))
            }
        }
    }

    override suspend fun insertDailyContent(dailyContentData: AwqatDailyContent) {
        withContext(dispatcher) {
            dao.insertDailyContent(dailyContentData)
        }
    }

    override suspend fun insertLocations(data: List<Location>) {
        withContext(dispatcher) {
            dao.insertLocations(data)
        }
    }

    override suspend fun insertCityDetails(cityDetail: CityDetail) {
        withContext(dispatcher) {
            dao.insertCityDetail(cityDetail)
        }
    }

    override suspend fun insertCityPrayerTimes(time: PrayerTime) {
        withContext(dispatcher) {
            dao.insertCityPrayerTime(time)
        }
    }

    override suspend fun loadCityId(locationName: String): Int? {
        return withContext(dispatcher) {
            dao.loadCityCode(locationName)
        }
    }
}