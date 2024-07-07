package com.kevker.lifetracker.repositories

import com.kevker.lifetracker.data.GlassDao
import com.kevker.lifetracker.models.Glass
import kotlinx.coroutines.flow.Flow

class GlassRepository private constructor(private val glassDao: GlassDao) {
    suspend fun add(glass: Glass) = glassDao.add(glass)
    suspend fun delete(glass: Glass) = glassDao.delete(glass)
    suspend fun update(glass: Glass) = glassDao.update(glass)
    fun getAllGlasses(): Flow<List<Glass>> = glassDao.readAll()

    companion object {
        @Volatile
        private var instance: GlassRepository? = null

        fun getInstance(glassDao: GlassDao): GlassRepository =
            instance ?: synchronized(this) {
                instance ?: GlassRepository(glassDao).also { instance = it }
            }
    }
}
