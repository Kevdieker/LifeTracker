package com.kevker.lifetracker.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.kevker.lifetracker.models.SleepEntity
import com.kevker.lifetracker.data.SleepRepository
import com.kevker.lifetracker.utils.PreferenceManager
import com.kevker.lifetracker.workers.SleepWorker
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

    private val _bufferTime = MutableStateFlow(0)
    val bufferTime: StateFlow<Int> = _bufferTime

    private val _countdownTime = MutableStateFlow(0)
    val countdownTime: StateFlow<Int> = _countdownTime

    private val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        viewModelScope.launch {
            val context = repository.context
            _bufferTime.value = PreferenceManager.getBufferTime(context)
            _sleepState.value = PreferenceManager.getSleepState(context)
            _countdownTime.value = PreferenceManager.getCountdownTime(context)

            repository.allSleepTimes.collect { sleepEntities ->
                _sleepTimes.value = sleepEntities.map { it.startTime to it.endTime }
                _allSleepEntries.value = sleepEntities
                calculateTodaySleep()
                calculateYesterdaySleep()
            }
        }
    }

    fun toggleSleepState(bufferTime: Int) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            _sleepState.value = !_sleepState.value

            val context = repository.context
            PreferenceManager.saveSleepState(context, _sleepState.value)
            PreferenceManager.saveBufferTime(context, bufferTime)

            if (_sleepState.value) {
                if (bufferTime == 0) {
                    // Start the sleep session immediately
                    val sleepEntity = SleepEntity(startTime = currentTime, endTime = 0L)
                    repository.insertSleepTime(sleepEntity)
                    val updatedSleepTimes = _sleepTimes.value.toMutableList()
                    updatedSleepTimes.add(currentTime to 0L)
                    _sleepTimes.value = updatedSleepTimes
                } else {
                    // Start the countdown worker
                    startCountdownWorker(bufferTime)
                }
            } else {
                // End the current sleep session
                val lastSleepSession = _allSleepEntries.value.lastOrNull()
                if (lastSleepSession != null && lastSleepSession.endTime == 0L) {
                    val updatedSleepTimes = _sleepTimes.value.toMutableList()
                    updatedSleepTimes[updatedSleepTimes.size - 1] = lastSleepSession.startTime to currentTime
                    _sleepTimes.value = updatedSleepTimes

                    // Persist the updated sleep session
                    repository.updateSleepEndTime(lastSleepSession.id, currentTime)

                    // Update today's and yesterday's sleep duration
                    calculateTodaySleep()
                    calculateYesterdaySleep()
                }
            }
        }
    }

    fun startCountdownWorker(bufferTime: Int) {
        val workManager = WorkManager.getInstance(repository.context)
        val sleepWorkRequest = OneTimeWorkRequestBuilder<SleepWorker>()
            .setInputData(workDataOf("bufferTime" to bufferTime))
            .build()
        workManager.enqueue(sleepWorkRequest)
    }

    fun updateCountdownTime(countdownTime: Int) {
        _countdownTime.value = countdownTime

        // Save countdown time
        val context = repository.context
        PreferenceManager.saveCountdownTime(context, countdownTime)
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

    fun updateBufferTime(newBufferTime: Int) {
        _bufferTime.value = newBufferTime

        // Save buffer time
        val context = repository.context
        PreferenceManager.saveBufferTime(context, newBufferTime)
    }

    fun formatTime(millis: Long): String {
        return if (millis == 0L) {
            "00:00:00"
        } else {
            timeFormatter.format(Date(millis))
        }
    }
}
