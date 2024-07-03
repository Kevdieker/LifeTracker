package com.kevker.lifetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kevker.lifetracker.models.SleepEntity
import com.kevker.lifetracker.repositories.SleepRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SleepViewModel(private val repository: SleepRepository) : ViewModel() {

    private val _sleepState = MutableStateFlow(false) // false for awake, true for asleep
    val sleepState: StateFlow<Boolean> = _sleepState

    private val _sleepTimes = MutableStateFlow<List<Pair<Long, Long>>>(emptyList())
    val sleepTimes: StateFlow<List<Pair<Long, Long>>> = _sleepTimes

    private val _yesterdaySleepDuration = MutableStateFlow(0L)
    val yesterdaySleepDuration: StateFlow<Long> = _yesterdaySleepDuration

    private val _allSleepEntries = MutableStateFlow<List<SleepEntity>>(emptyList())
    val allSleepEntries: StateFlow<List<SleepEntity>> = _allSleepEntries

    private val _todaySleepDuration = MutableStateFlow(0L)
    val todaySleepDuration: StateFlow<Long> = _todaySleepDuration

    private val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        viewModelScope.launch {
            repository.allSleepTimes.collect { sleepEntities ->
                _sleepTimes.value = sleepEntities.map { it.startTime to it.endTime }
                _allSleepEntries.value = sleepEntities
                calculateTodaySleep()
            }
            calculateYesterdaySleep()
        }
    }

    fun toggleSleepState(bufferTime: Int) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            _sleepState.value = !_sleepState.value

            if (_sleepState.value) {
                // Sleep started
                _sleepTimes.value = _sleepTimes.value + Pair(currentTime + bufferTime * 60 * 1000, 0L)
            } else {
                // Sleep ended
                val updatedSleepTimes = ArrayList(_sleepTimes.value)
                val lastIndex = updatedSleepTimes.size - 1
                updatedSleepTimes[lastIndex] =
                    updatedSleepTimes[lastIndex].copy(second = currentTime)
                _sleepTimes.value = updatedSleepTimes

                // Persist sleep time
                val sleepEntity = SleepEntity(startTime = updatedSleepTimes[lastIndex].first, endTime = updatedSleepTimes[lastIndex].second)
                repository.insertSleepTime(sleepEntity)

                // Update today's and yesterday's sleep duration
                calculateTodaySleep()
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

    private fun calculateTodaySleep() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val todayStart = calendar.timeInMillis
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val todayEnd = calendar.timeInMillis

            var totalSleep = 0L
            for (sleepSession in _sleepTimes.value) {
                val (start, end) = sleepSession
                if (start in todayStart..todayEnd && end in todayStart..todayEnd) {
                    totalSleep += (end - start)
                } else if (start < todayStart && end > todayStart) {
                    totalSleep += (end - todayStart)
                } else if (start < todayEnd && end > todayEnd) {
                    totalSleep += (todayEnd - start)
                }
            }
            _todaySleepDuration.value = totalSleep
        }
    }

    fun formatTime(millis: Long): String {
        return timeFormatter.format(Date(millis))
    }
}
