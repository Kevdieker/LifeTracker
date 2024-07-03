package com.kevker.lifetracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_times")
data class SleepEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: Long,
    val endTime: Long
)
