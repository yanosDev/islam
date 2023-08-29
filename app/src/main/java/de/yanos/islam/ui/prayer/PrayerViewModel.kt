package de.yanos.islam.ui.prayer

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import de.yanos.islam.di.Accelerometer
import de.yanos.islam.di.Magnetometer
import de.yanos.islam.util.AppSettings
import de.yanos.islam.util.LatandLong
import de.yanos.islam.util.correctColor
import de.yanos.islam.util.epochSecondToDateString
import de.yanos.islam.util.errorColor
import de.yanos.islam.util.goldColor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import timber.log.Timber
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask
import kotlin.math.abs

@HiltViewModel
class PrayerViewModel @Inject constructor(
    private val appSettings: AppSettings,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val qiblaApi: QiblaApi,
    private val sensorManager: SensorManager,
    @Magnetometer private val magnetometer: Sensor,
    @Accelerometer private val accelerometer: Sensor,
) : ViewModel(), SensorEventListener {
    private var lastLocation: LatandLong by mutableStateOf(LatandLong(0.0, 0.0))
    private var qiblaDegree = appSettings.lastDirection
    private var phoneDegreeX = 0
    var direction by mutableStateOf(0F)
    var timeRemaining by mutableStateOf("00:00")
    var times = mutableStateListOf<PrayingTime>()
    val funtimer: Timer = Timer()

    init {
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        if (appSettings.lastLongitude != 0.0 && appSettings.lastLatitude != 0.0)
            getLocation(LatandLong(appSettings.lastLatitude, appSettings.lastLongitude))

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
    fun getLocation(location: LatandLong) {
        if (!fetchInProgress
            && (location.latitude != 0.0 || location.longitude != 0.0)
            && abs(location.latitude - lastLocation.latitude) > 1
            && abs(location.longitude - lastLocation.longitude) > 1
        ) {
            fetchInProgress = true
            viewModelScope.launch(dispatcher) {
                try {
                    val response = qiblaApi.getDirection(location.latitude, location.longitude).awaitResponse()
                    response.body()?.let {
                        qiblaDegree = it.data.direction.toInt()
                        lastLocation = location
                        appSettings.lastLatitude = location.latitude
                        appSettings.lastLongitude = location.longitude
                        appSettings.lastDirection = it.data.direction.toInt()
                        fetchInProgress = false
                        refreshDegree()
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

    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    override fun onSensorChanged(e: SensorEvent?) {
        e?.let { event ->
            if (event.sensor === magnetometer) {
                System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
                lastMagnetometerSet = true
            } else if (event.sensor === accelerometer) {
                System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
                lastAccelerometerSet = true
            }
            if (lastAccelerometerSet && lastMagnetometerSet) {
                SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
                SensorManager.getOrientation(rotationMatrix, orientation)
                phoneDegreeX = ((((Math.toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360).toInt() / 2) * 2)
                refreshDegree()
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onCleared() {
        sensorManager.unregisterListener(this, magnetometer)
        sensorManager.unregisterListener(this, accelerometer)
        super.onCleared()
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