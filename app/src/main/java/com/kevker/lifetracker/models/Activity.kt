package com.kevker.lifetracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kevker.lifetracker.data.Converters
import com.kevker.lifetracker.enums.Category

@Entity(tableName = "activity")
@TypeConverters(Converters::class)
data class Activity(
    @PrimaryKey(autoGenerate = true)
    val activityId: Long = 0,
    val title: String,
    val description: String,
    val date: Long? = null,
    val hasReminder: Boolean = false,
    val reminderTime: Long? = null,
    val reminderDaysOfWeek: List<Int> = emptyList(),
    val category: Category? = null
)
