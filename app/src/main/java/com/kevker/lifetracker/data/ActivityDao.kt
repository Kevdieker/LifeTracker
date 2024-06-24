package com.kevker.lifetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kevker.lifetracker.models.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Insert
    suspend fun add(activity: Activity)

    @Update
    suspend fun update(activity: Activity)

    @Delete
    suspend fun delete(activity: Activity)

    @Transaction
    @Query("SELECT * FROM activity WHERE activityId = :activityId")
    fun getActivityById(activityId: Long): Flow<Activity?>

    @Transaction
    @Query("SELECT * FROM activity")
    fun readAll(): Flow<List<Activity>>
}
