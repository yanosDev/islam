package de.yanos.islam.data.repositories.source

import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.api.AwqatApi
import de.yanos.islam.data.model.awqat.AwqatCityDetailsResponse
import de.yanos.islam.data.model.awqat.AwqatDailyContentResponse
import de.yanos.islam.data.model.awqat.AwqatEidResponse
import de.yanos.islam.data.model.awqat.AwqatLocationResponse
import de.yanos.islam.data.model.awqat.AwqatPrayerTimeResponse
import de.yanos.islam.data.model.awqat.Login
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.LoadState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.awaitResponse
import timber.log.Timber
import javax.inject.Inject

interface RemoteAwqatSource {
    suspend fun auth()
    suspend fun fetchDailyContent(): LoadState<AwqatDailyContentResponse>
    suspend fun loadCountries(): LoadState<AwqatLocationResponse>
    suspend fun loadStates(): LoadState<AwqatLocationResponse>
    suspend fun loadCities(): LoadState<AwqatLocationResponse>
    suspend fun loadCityDetails(cityId: Int): LoadState<AwqatCityDetailsResponse>
    suspend fun loadCityPrayerTimes(cityId: Int): LoadState<AwqatPrayerTimeResponse>
    suspend fun loadCityPrayerTimesEid(cityId: Int): LoadState<AwqatEidResponse>
}

class RemoteAwqatSourceImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val appSettings: AppSettings,
    private val api: AwqatApi,
) : RemoteAwqatSource {
    override suspend fun auth() {
        withContext(dispatcher) {
            val ts = System.currentTimeMillis()
            when {
                (ts - appSettings.tokenLastFetch) > 60000000 -> api.login(Login(appSettings.awqatEmail, appSettings.awqatPwd)).awaitResponse()
                (ts - appSettings.tokenLastFetch) > 30000000 -> api.refreshToken(appSettings.refreshToken).awaitResponse()
                else -> null
            }?.let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        appSettings.authToken = "Bearer ${body.data.accessToken}"
                        appSettings.refreshToken = body.data.refreshToken
                        appSettings.tokenLastFetch = System.currentTimeMillis()
                    }
                } else {
                    Timber.e(response.errorBody().toString())
                }
            }
        }
    }

    override suspend fun fetchDailyContent(): LoadState<AwqatDailyContentResponse> {
        return withContext(dispatcher) {
            try {
                val response = api.dailyContent(appSettings.authToken).awaitResponse()
                localResponse(response)
            } catch (e: Exception) {
                Timber.e(e)
                LoadState.Failure(Exception("Error"))
            }
        }
    }

    override suspend fun loadCountries(): LoadState<AwqatLocationResponse> {
        return withContext(dispatcher) {
            try {
                val response = api.loadCountries(appSettings.authToken).awaitResponse()
                localResponse(response)
            } catch (e: Exception) {
                Timber.e(e)
                LoadState.Failure(Exception("Error"))
            }
        }
    }

    override suspend fun loadStates(): LoadState<AwqatLocationResponse> {
        return withContext(dispatcher) {
            try {
                val response = api.loadStates(appSettings.authToken).awaitResponse()
                localResponse(response)
            } catch (e: Exception) {
                Timber.e(e)
                LoadState.Failure(Exception("Error"))
            }
        }
    }

    override suspend fun loadCities(): LoadState<AwqatLocationResponse> {
        return withContext(dispatcher) {
            try {
                val response = api.loadCities(appSettings.authToken).awaitResponse()
                localResponse(response)
            } catch (e: Exception) {
                Timber.e(e)
                LoadState.Failure(Exception("Error"))
            }
        }
    }

    override suspend fun loadCityDetails(cityId: Int): LoadState<AwqatCityDetailsResponse> {
        return withContext(dispatcher) {
            try {
                val response = api.loadCityDetails(appSettings.authToken, cityId).awaitResponse()
                localResponse(response)
            } catch (e: Exception) {
                Timber.e(e)
                LoadState.Failure(Exception("Error"))
            }
        }
    }

    override suspend fun loadCityPrayerTimes(cityId: Int): LoadState<AwqatPrayerTimeResponse> {
        return withContext(dispatcher) {
            try {
                val response = api.loadCityPrayerTimes(appSettings.authToken, cityId).awaitResponse()
                localResponse(response)
            } catch (e: Exception) {
                Timber.e(e)
                LoadState.Failure(Exception("Error"))
            }
        }
    }

    override suspend fun loadCityPrayerTimesEid(cityId: Int): LoadState<AwqatEidResponse> {
        return withContext(dispatcher) {
            try {
                val response = api.loadCityPrayerTimesEid(appSettings.authToken, cityId).awaitResponse()
                localResponse(response)
            } catch (e: Exception) {
                Timber.e(e)
                LoadState.Failure(Exception("Error"))
            }
        }
    }

    private fun <T> localResponse(response: Response<T>): LoadState<T> {
        return if (response.isSuccessful) {
            response.body()?.let { body ->
                LoadState.Data(body)
            } ?: LoadState.Failure(Exception(response.errorBody().toString()))
        } else {
            LoadState.Failure(Exception(response.errorBody().toString()))
        }
    }
}