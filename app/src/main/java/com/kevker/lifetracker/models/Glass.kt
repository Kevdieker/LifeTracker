package com.kevker.lifetracker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "glass")
data class Glass(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Int,
    val timestamp: Long = System.currentTimeMillis()
)
