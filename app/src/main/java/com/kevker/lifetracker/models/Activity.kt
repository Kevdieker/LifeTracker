package com.kevker.lifetracker.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kevker.lifetracker.enums.Category

@Entity(tableName = "activity")
data class Activity(
    @PrimaryKey(autoGenerate = true)
    val activityId: Long = 0,
    val title: String,
    val description: String,
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
