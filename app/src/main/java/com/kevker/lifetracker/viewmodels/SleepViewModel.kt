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
        fetchAllSleepTimes()
    }

    private fun fetchAllSleepTimes() {
        viewModelScope.launch {
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

            if (_sleepState.value) {
                // Sleep started
                _sleepTimes.value += Pair(currentTime + bufferTime * 60 * 1000, 0L)
            } else {
                // Sleep ended
                val updatedSleepTimes = ArrayList(_sleepTimes.value)
                val lastIndex = updatedSleepTimes.size - 1
                updatedSleepTimes[lastIndex] = updatedSleepTimes[lastIndex].copy(second = currentTime)
                _sleepTimes.value = updatedSleepTimes

                // Persist sleep time
                repository.insertSleepTime(
                    SleepEntity(
                        startTime = updatedSleepTimes[lastIndex].first,
                        endTime = updatedSleepTimes[lastIndex].second
                    )
                )

                // Update today's and yesterday's sleep duration
                calculateTodaySleep()
                calculateYesterdaySleep()
            }
        }
    }


    private fun calculateYesterdaySleep() {
        viewModelScope.launch {
            val (yesterdayStart, yesterdayEnd) = getStartAndEndOfDay(-1)
            _yesterdaySleepDuration.value = calculateSleepDuration(yesterdayStart, yesterdayEnd)
        }
    }

    private fun calculateTodaySleep() {
        viewModelScope.launch {
            val (todayStart, todayEnd) = getStartAndEndOfDay(0)
            _todaySleepDuration.value = calculateSleepDuration(todayStart, todayEnd)
        }
    }

    private fun calculateSleepDuration(start: Long, end: Long): Long {
        return _sleepTimes.value.sumOf { (sessionStart, sessionEnd) ->
            when {
                sessionStart in start..end && sessionEnd in start..end -> sessionEnd - sessionStart
                sessionStart < start && sessionEnd > start -> sessionEnd - start
                sessionStart < end && sessionEnd > end -> end - sessionStart
                else -> 0L
            }
        }
    }

    private fun getStartAndEndOfDay(offsetDays: Int): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DATE, offsetDays)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val end = calendar.timeInMillis
        return start to end
    }

    fun formatTime(millis: Long): String {
        return timeFormatter.format(Date(millis))
    }
}
