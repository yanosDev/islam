package de.yanos.islam.data.repositories.source

import de.yanos.core.utils.IODispatcher
import de.yanos.islam.data.api.AwqatApi
import de.yanos.islam.data.model.awqat.AwqatDailyContent
import de.yanos.islam.data.model.awqat.Login
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.LoadState
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.awaitResponse
import timber.log.Timber
import javax.inject.Inject

interface RemoteAwqatSource {
    suspend fun auth()
    suspend fun fetchDailyContent(): LoadState<AwqatDailyContent>
}

class RemoteAwqatSourceImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val appSettings: AppSettings,
    private val api: AwqatApi,
) : RemoteAwqatSource {
    override suspend fun auth() {
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
            } else { Timber.e(response.errorBody().toString())
            }
        }
    }

    override suspend fun fetchDailyContent(): LoadState<AwqatDailyContent> {
        if (appSettings.authToken.isBlank())
            return LoadState.Failure(Exception("Auth Token is not available"))
        val response = api.dailyContent(appSettings.authToken).awaitResponse()
        return if (response.isSuccessful) {
            response.body()?.let {
                LoadState.Data(it.data)
            } ?: LoadState.Failure(Exception(response.errorBody().toString()))

        } else {
            LoadState.Failure(Exception(response.errorBody().toString()))
        }
    }
}