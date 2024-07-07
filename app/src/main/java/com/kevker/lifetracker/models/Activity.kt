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
    val date: Long? = null,  // Store date as timestamp
    val hasReminder: Boolean = false,
    val reminderTime: Long? = null,  // Store time as timestamp
    val reminderDaysOfWeek: List<Int> = emptyList(),  // Store days of the week as a list of integers
    val category: Category? = null
)

fun getActivities(): List<Activity> {
    return listOf(
        Activity(
            title = "Running",
            description = "Go for a run in the park",
        ),
        Activity(
            title = "Reading",
            description = "Read a new book"
        ),
        Activity(
            title = "Cooking",
            description = "Try out a new recipe"
        )
    )
}
