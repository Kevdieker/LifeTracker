package com.kevker.lifetracker.viewmodels

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val context: Context,
) : ViewModel(), SensorEventListener {

    private val _stepCount = MutableStateFlow(0L)
    val stepCount: StateFlow<Long> = _stepCount

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null

    private var initialStepCount: Long = -1

    init {
        startStepSensor()
    }

    private fun startStepSensor() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val steps = event.values[0].toLong()
            if (initialStepCount < 0) {
                initialStepCount = steps
            }
            val currentSteps = steps - initialStepCount
            viewModelScope.launch {
                _stepCount.value = currentSteps
                Log.d("StepCounter", "Current Steps: $currentSteps")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if necessary
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager?.unregisterListener(this)
    }
}
