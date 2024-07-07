package com.kevker.lifetracker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step")
data class StepCount(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "steps") val steps: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long
)
