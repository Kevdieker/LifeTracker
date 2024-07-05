package com.kevker.lifetracker.APIs
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class StepCounter(private val context: Context) {
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    suspend fun steps(): Long = suspendCancellableCoroutine { continuation ->
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                    continuation.resume(event.values[0].toLong())
                    sensorManager.unregisterListener(this)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // No action needed
            }
        }

        stepSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
            continuation.invokeOnCancellation {
                sensorManager.unregisterListener(listener)
            }
        } ?: continuation.resume(0)
    }
}
