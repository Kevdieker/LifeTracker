package com.kevker.lifetracker.data

import com.kevker.lifetracker.models.Activity
import kotlinx.coroutines.flow.Flow

class ActivityRepository(private val activityDao: ActivityDao) {
    suspend fun add(activity: Activity) = activityDao.add(activity)
    suspend fun delete(activity: Activity) = activityDao.delete(activity)
    suspend fun update(activity: Activity) = activityDao.update(activity)
    fun getActivityById(activityId: Long): Flow<Activity?> = activityDao.getActivityById(activityId)
    fun getAllActivities(): Flow<List<Activity>> = activityDao.readAll()

    companion object {
        @Volatile
        private var instance: ActivityRepository? = null

        fun getInstance(activityDao: ActivityDao) =
            instance ?: synchronized(this) {
                instance ?: ActivityRepository(activityDao).also { instance = it }
            }
    }
}