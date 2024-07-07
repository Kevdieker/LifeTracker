package com.kevker.lifetracker.viewmodels

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevker.lifetracker.models.StepCount
import com.kevker.lifetracker.repositories.StepRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeScreenViewModel(
    private val context: Context,
    private val repository: StepRepository
) : ViewModel(), SensorEventListener {

    private val _stepCount = MutableStateFlow(0L)
    val stepCount: StateFlow<Long> = _stepCount

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null

    private var initialStepCount: Long = -1

    init {
        initialStepCount = getSavedInitialStepCount()
        _stepCount.value = getLastStoredSteps()
        startStepSensor()
        fetchTodaySteps()
    }

    private fun startStepSensor() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun fetchTodaySteps() {
        val startOfDay = getStartOfDayInMillis()
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000

        viewModelScope.launch {
            repository.loadTodaySteps(startOfDay, endOfDay).collect { steps ->
                val totalSteps = steps.sumOf { it.steps }
                _stepCount.value = totalSteps
                Log.d("StepCounter", "Today's Steps: $totalSteps")
            }
        }
    }

    private fun getSavedInitialStepCount(): Long {
        val sharedPref = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
        return sharedPref.getLong("initial_step_count", -1)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val steps = event.values[0].toLong()
            if (initialStepCount < 0) {
                initialStepCount = steps
                saveInitialStepCount(initialStepCount)
            }
            val currentSteps = steps - initialStepCount
            Log.d("StepCounter", "Current Steps: $currentSteps")
            if (currentSteps != _stepCount.value) {
                viewModelScope.launch {
                    storeSteps(currentSteps - _stepCount.value)  // Store only incremental steps
                    saveLastStoredSteps(currentSteps)  // Save the last stored steps
                    fetchTodaySteps()  // Refresh today's steps after storing
                }
            }
        }
    }

    private fun saveInitialStepCount(initialStepCount: Long) {
        val sharedPref = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong("initial_step_count", initialStepCount)
            apply()
        }
    }

    private suspend fun storeSteps(incrementalSteps: Long) {
        val stepCount = StepCount(
            steps = incrementalSteps,
            createdAt = System.currentTimeMillis()
        )
        repository.storeSteps(stepCount)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if necessary
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager?.unregisterListener(this)
    }

    private fun getStartOfDayInMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun saveLastStoredSteps(lastStoredSteps: Long) {
        val sharedPref = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putLong("last_stored_steps", lastStoredSteps)
            apply()
        }
    }

    private fun getLastStoredSteps(): Long {
        val sharedPref = context.getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
        return sharedPref.getLong("last_stored_steps", 0)
    }
}
