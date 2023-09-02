package de.yanos.islam.data.repositories

import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.model.awqat.AwqatLocation
import de.yanos.islam.data.model.awqat.CityDetail
import de.yanos.islam.data.model.awqat.CityEid
import de.yanos.islam.data.model.awqat.Location
import de.yanos.islam.data.model.awqat.LocationType
import de.yanos.islam.data.model.awqat.PrayerTime
import de.yanos.islam.data.repositories.source.LocalAwqatSource
import de.yanos.islam.data.repositories.source.RemoteAwqatSource
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.LoadState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


interface AwqatRepository {
    suspend fun fetchAwqatData()
    suspend fun fetchCityData(locationName: String)
}

class AwqatRepositoryImpl @Inject constructor(
    private val appSettings: AppSettings,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val localSource: LocalAwqatSource,
    private val remoteSource: RemoteAwqatSource
) : AwqatRepository {

    override suspend fun fetchAwqatData() {
        withContext(dispatcher) {
            remoteSource.auth()
            if (appSettings.authToken.isNotBlank()) {
                async { fetchDailyContent() }
                async { fetchCountries() }
                async { fetchStates() }
                async { fetchCities() }
            }
        }
    }


    override suspend fun fetchCityData(locationName: String) {
        withContext(dispatcher) {
            if (appSettings.authToken.isNotBlank()) {
                localSource.loadCityId(locationName.uppercase())?.let { cityId ->
                    if (localSource.loadCityTimes(cityId).none {
                            val date = LocalDate.parse(it.gregorianDateShort, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                            date.dayOfYear == LocalDate.now().dayOfYear && date.year == LocalDate.now().year
                        }) {
                        async {
                            getData(remoteSource.loadCityDetails(cityId))?.let {
                                localSource.insertCityDetails(
                                    CityDetail(
                                        id = cityId,
                                        name = it.data.name,
                                        code = it.data.code,
                                        geographicQiblaAngle = it.data.geographicQiblaAngle,
                                        distanceToKaaba = it.data.distanceToKaaba,
                                        qiblaAngle = it.data.qiblaAngle,
                                        city = it.data.city,
                                        cityEn = it.data.cityEn,
                                        country = it.data.country,
                                        countryEn = it.data.countryEn
                                    )
                                )
                            }
                        }
                        async {
                            getData(remoteSource.loadCityPrayerTimes(cityId))?.data?.let { times ->
                                localSource.insertCityPrayerTimes(
                                    times.map { time ->
                                        PrayerTime(
                                            id = cityId,
                                            key = "${cityId}_${time.gregorianDateShort}",
                                            shapeMoonUrl = time.shapeMoonUrl,
                                            fajr = time.fajr,
                                            sunrise = time.sunrise,
                                            dhuhr = time.dhuhr,
                                            asr = time.asr,
                                            maghrib = time.maghrib,
                                            isha = time.isha,
                                            astronomicalSunset = time.astronomicalSunset,
                                            astronomicalSunrise = time.astronomicalSunrise,
                                            hijriDateShort = time.hijriDateShort,
                                            hijriDateLong = time.hijriDateLong,
                                            qiblaTime = time.qiblaTime,
                                            gregorianDateShort = time.gregorianDateShort,
                                            gregorianDateLong = time.gregorianDateLong,
                                        )
                                    }
                                )
                            }
                        }
                        async {
                            getData(remoteSource.loadCityPrayerTimesEid(cityId))?.data?.let { time ->
                                localSource.insertCityEid(
                                    CityEid(
                                        cityId = cityId,
                                        key = "${cityId}_${time.eidAlAdhaHijri}",
                                        eidAlAdhaHijri = time.eidAlAdhaHijri,
                                        eidAlAdhaTime = time.eidAlAdhaTime,
                                        eidAlAdhaDate = time.eidAlAdhaDate,
                                        eidAlFitrHijri = time.eidAlFitrHijri,
                                        eidAlFitrTime = time.eidAlFitrTime,
                                        eidAlFitrDate = time.eidAlFitrDate,
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchDailyContent() {
        withContext(dispatcher) {
            getData(remoteSource.fetchDailyContent())?.let {
                localSource.insertDailyContent(it.data)
            }
        }
    }

    private suspend fun fetchCountries() {
        withContext(dispatcher) {
            getData(remoteSource.loadCountries())?.let {
                localSource.insertLocations(toLocation(it.data, LocationType.COUNTRY))
            }
        }
    }

    private fun toLocation(locations: List<AwqatLocation>, type: LocationType): List<Location> {
        return locations.map { Location(id = it.id, code = it.code, name = it.name, type) }
    }

    private suspend fun fetchStates() {
        withContext(dispatcher) {
            getData(remoteSource.loadStates())?.let {
                localSource.insertLocations(toLocation(it.data, LocationType.STATE))
            }
        }
    }

    private suspend fun fetchCities() {
        withContext(dispatcher) {
            getData(remoteSource.loadCities())?.let {
                localSource.insertLocations(toLocation(it.data, LocationType.CITY))
            }
        }
    }

    private fun <T> getData(response: LoadState<T>): T? {
        return (response as? LoadState.Data)?.let {
            it.data
        } ?: (response as? LoadState.Failure)?.let {
            Timber.e(it.e)
            null
        }
    }
}