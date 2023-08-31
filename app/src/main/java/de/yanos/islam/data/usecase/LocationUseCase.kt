package de.yanos.islam.data.usecase

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import de.yanos.islam.di.Accelerometer
import de.yanos.islam.di.Magnetometer
import javax.inject.Inject

interface LocationUseCase {
    fun addCallback(callback:(Int) -> Unit)
    fun removeCallback(callback:(Int) -> Unit)
}

class LocationUseCaseImpl @Inject constructor(
    @Accelerometer private val accelerometer: Sensor,
    @Magnetometer private val magnetometer: Sensor,
    private val sensorManager: SensorManager,
) : LocationUseCase, SensorEventListener {

    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false
    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    val callbacks = mutableListOf<(Int) -> Unit>()

    init {
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }


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
                val phoneDegreeX = ((((Math.toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360).toInt() / 2) * 2)
                callbacks.forEach { it(phoneDegreeX) }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun addCallback(callback: (Int) -> Unit) {
        callbacks.add(callback)
    }

    override fun removeCallback(callback: (Int) -> Unit) {
      callbacks.remove(callback)
    }
}