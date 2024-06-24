package com.kevker.lifetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kevker.lifetracker.models.Glass
import kotlinx.coroutines.flow.Flow

@Dao
interface GlassDao {

    @Insert
    suspend fun add(glass: Glass)

    @Update
    suspend fun update(glass: Glass)

    @Delete
    suspend fun delete(glass: Glass)

    @Transaction
    @Query("SELECT * FROM glass")
    fun readAll(): Flow<List<Glass>>
}
