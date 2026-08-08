package com.mocare.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mocare.app.data.local.entity.MotorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MotorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMotor(motor: MotorEntity): Long

    @Update
    suspend fun updateMotor(motor: MotorEntity)

    @Delete
    suspend fun deleteMotor(motor: MotorEntity)

    @Query("SELECT * FROM motor ORDER BY id DESC")
    fun getAllMotors(): Flow<List<MotorEntity>>

    @Query("SELECT * FROM motor WHERE id = :id")
    fun getMotorById(id: Long): Flow<MotorEntity?>
}