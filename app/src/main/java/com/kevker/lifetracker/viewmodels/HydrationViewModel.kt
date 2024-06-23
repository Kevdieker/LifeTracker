package com.kevker.lifetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HydrationViewModel : ViewModel() {

    private val _sleepState = MutableStateFlow(false) // false for awake, true for asleep
    val sleepState: StateFlow<Boolean> = _sleepState

    private val _sleepTimes = MutableStateFlow<List<Pair<Long, Long>>>(emptyList())
    val sleepTimes: StateFlow<List<Pair<Long, Long>>> = _sleepTimes

    private val _yesterdaySleepDuration = MutableStateFlow(0L)
    val yesterdaySleepDuration: StateFlow<Long> = _yesterdaySleepDuration

    private val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun toggleSleepState() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            _sleepState.value = !_sleepState.value

            if (_sleepState.value) {
                // Sleep started
                _sleepTimes.value = _sleepTimes.value + Pair(currentTime, 0L)
            } else {
                // Sleep ended
                val updatedSleepTimes = ArrayList(_sleepTimes.value)
                val lastIndex = updatedSleepTimes.size - 1
                updatedSleepTimes[lastIndex] =
                    updatedSleepTimes[lastIndex].copy(second = currentTime)
                _sleepTimes.value = updatedSleepTimes

                // Update yesterday's sleep duration
                calculateYesterdaySleep()
            }
        }
    }

    private fun calculateYesterdaySleep() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val yesterdayStart = calendar.timeInMillis
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val yesterdayEnd = calendar.timeInMillis

            var totalSleep = 0L
            for (sleepSession in _sleepTimes.value) {
                val (start, end) = sleepSession
                if (start in yesterdayStart..yesterdayEnd && end in yesterdayStart..yesterdayEnd) {
                    totalSleep += (end - start)
                } else if (start < yesterdayStart && end > yesterdayStart) {
                    totalSleep += (end - yesterdayStart)
                } else if (start < yesterdayEnd && end > yesterdayEnd) {
                    totalSleep += (yesterdayEnd - start)
                }
            }
            _yesterdaySleepDuration.value = totalSleep
        }
    }

    init {
        // Calculate yesterday's sleep duration on initialization
        calculateYesterdaySleep()
    }

    fun formatTime(millis: Long): String {
        return timeFormatter.format(Date(millis))
    }
}