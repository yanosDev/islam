package de.yanos.islam.ui.prayer

import android.location.Geocoder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.IODispatcher
import de.yanos.islam.R
import de.yanos.islam.data.api.QiblaApi
import de.yanos.islam.data.database.dao.AwqatDao
import de.yanos.islam.data.repositories.AwqatRepository
import de.yanos.islam.data.usecase.LocationUseCase
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.LatandLong
import de.yanos.islam.util.correctColor
import de.yanos.islam.util.epochSecondToDateString
import de.yanos.islam.util.errorColor
import de.yanos.islam.util.goldColor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import timber.log.Timber
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask
import kotlin.math.abs

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val geocoder: Geocoder,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val locationUseCase: LocationUseCase,
    private val qiblaApi: QiblaApi,
    private val dao: AwqatDao,
    private val repository: AwqatRepository,
) : ViewModel() {
    private var lastLocation: LatandLong by mutableStateOf(LatandLong(appSettings.lastLatitude, appSettings.lastLongitude))
    private var qiblaDegree = appSettings.lastDirection
    private var phoneDegreeX = 0
    var direction by mutableStateOf(0F)
    var timeRemaining by mutableStateOf("00:00")
    var times = mutableStateListOf<PrayingTime>()
    val funtimer: Timer = Timer()

    val cityDetails = dao.loadRecentCity().distinctUntilChanged()

    init {
        locationUseCase.addCallback(this::onSensorChange)
        if (appSettings.lastLongitude != 0.0 && appSettings.lastLatitude != 0.0)
            onCurrentLocationChanged(LatandLong(appSettings.lastLatitude, appSettings.lastLongitude))

        funtimer.scheduleAtFixedRate(
            timerTask()
            {
                viewModelScope.launch(Dispatchers.Main) {
                    timeRemaining = (System.currentTimeMillis() / 1000).epochSecondToDateString("HH:mm:ss")
                }
            }, 0, 1000
        )
        //Mock Data
        times.add(PrayingTime(id = 0, textId = R.string.praying_imsak_title, "03:30", state = TimeState.Past))
        times.add(PrayingTime(id = 1, textId = R.string.praying_morning_title, "05:30", state = TimeState.Past))
        times.add(PrayingTime(id = 2, textId = R.string.praying_lunch_title, "13:16", state = TimeState.Past))
        times.add(PrayingTime(id = 3, textId = R.string.praying_afternoon_title, "16:58", state = TimeState.Current))
        times.add(PrayingTime(id = 4, textId = R.string.praying_evening_title, "20:11", state = TimeState.Future))
        times.add(PrayingTime(id = 5, textId = R.string.praying_night_title, "22:10", state = TimeState.Future))
    }

    private var fetchInProgress: Boolean = false
    fun onCurrentLocationChanged(location: LatandLong) {
        if (!fetchInProgress
            && (location.latitude != 0.0 || location.longitude != 0.0)
            && abs(location.latitude - lastLocation.latitude) > 1
            && abs(location.longitude - lastLocation.longitude) > 1
        ) {
            fetchInProgress = true
            viewModelScope.launch {
                listOf(
                    refreshQiblaDirectionAsync(location),
                    refreshCityDataAsync(location)
                ).awaitAll()
                fetchInProgress = false
                refreshDegree()
            }
        }
    }

    private fun onSensorChange(phoneX: Int) {
        phoneDegreeX = phoneX
        refreshDegree()
    }

    private suspend fun refreshCityDataAsync(location: LatandLong): Deferred<Unit?> {
        return withContext(dispatcher) {
            async {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()?.let { address ->
                    (address.subAdminArea ?: address.adminArea)?.let {
                        repository.fetchCityData(it)
                    }
                }
            }
        }
    }

    private suspend fun refreshQiblaDirectionAsync(location: LatandLong): Deferred<Unit?> {
        return withContext(dispatcher) {
            async {
                try {
                    val response = qiblaApi.getDirection(location.latitude, location.longitude).awaitResponse()
                    response.body()?.let {
                        qiblaDegree = it.data.direction.toInt()
                        lastLocation = location
                        appSettings.lastLatitude = location.latitude
                        appSettings.lastLongitude = location.longitude
                        appSettings.lastDirection = it.data.direction.toInt()
                    }
                    response.errorBody()?.let {
                        Timber.e(it.toString())
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    private fun refreshDegree() {
        direction = -(qiblaDegree - phoneDegreeX).toFloat()
    }
}

data class PrayingTime(val id: Int, val textId: Int, val timeText: String, val state: TimeState) {
    @Composable
    fun color(): Color {
        return when (state) {
            TimeState.Current -> correctColor()
            TimeState.Past -> errorColor()
            else -> goldColor()
        }
    }
}

enum class TimeState {
    Current, Past, Future
}