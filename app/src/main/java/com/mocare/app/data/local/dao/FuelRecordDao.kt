package com.mocare.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mocare.app.data.local.entity.FuelRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: FuelRecordEntity): Long

    @Update
    suspend fun updateRecord(record: FuelRecordEntity)

    @Delete
    suspend fun deleteRecord(record: FuelRecordEntity)

    @Query("SELECT * FROM fuel_record WHERE motorId = :motorId ORDER BY kmWhenFilled DESC")
    fun getRecordsForMotor(motorId: Long): Flow<List<FuelRecordEntity>>

    @Query("SELECT * FROM fuel_record WHERE motorId = :motorId ORDER BY kmWhenFilled DESC LIMIT 1")
    suspend fun getLatestRecordForMotor(motorId: Long): FuelRecordEntity?
}