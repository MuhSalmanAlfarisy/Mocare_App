package com.mocare.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mocare.app.data.local.entity.FuelCheckpointEntity
import com.mocare.app.data.local.entity.FuelRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelRecordDao {
    @Insert
    suspend fun insertRecord(record: FuelRecordEntity)

    @Insert
    suspend fun insertCheckpoint(checkpoint: FuelCheckpointEntity)

    @Query("SELECT * FROM fuel_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<FuelRecordEntity>>

    @Query("SELECT * FROM fuel_records ORDER BY timestamp DESC LIMIT 1")
    fun getLatestRecord(): Flow<FuelRecordEntity?>

    @Query("SELECT * FROM fuel_checkpoints ORDER BY timestamp DESC LIMIT 1")
    fun getLatestCheckpoint(): Flow<FuelCheckpointEntity?>
}