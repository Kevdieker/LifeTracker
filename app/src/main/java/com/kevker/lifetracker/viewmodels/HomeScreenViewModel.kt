package com.kevker.lifetracker.viewmodels

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeScreenViewModel(
    private val context: Context,
) : ViewModel() {

    private val _stepCount = MutableStateFlow(0L)
    val stepCount: StateFlow<Long> = _stepCount

    private val _isPermissionGranted = MutableLiveData<Boolean>()
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null


    fun startStepSensor() {
        if (_isPermissionGranted.value == true) {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            stepSensor?.let {
                sensorManager?.registerListener(object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent) {
                        _stepCount.value = event.values[0].toLong()
                    }

                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                        // Handle accuracy changes if necessary
                    }
                }, it, SensorManager.SENSOR_DELAY_UI)
            }

            // Load initial step count from the repository or other source
            viewModelScope.launch {
                val startOfToday = getStartOfToday()

            }
        }
    }

    fun stopStepSensor() {
        sensorManager?.unregisterListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {}
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        })
    }

    private fun getStartOfToday(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
