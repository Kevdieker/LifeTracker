package com.kevker.lifetracker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity")
data class Activity(
    @PrimaryKey(autoGenerate = true)
    val activityId: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String
) {
    // Secondary constructor
    constructor(title: String, description: String) : this(0, title, description)

    // Override equals and hashCode for proper object comparison
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Activity

        if (activityId != other.activityId) return false
        if (title != other.title) return false
        if (description != other.description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = activityId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        return result
    }
}

fun getActivities(): List<Activity> {
    return listOf(
        Activity(
            title = "Running",
            description = "Go for a run in the park"
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
