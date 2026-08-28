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

    @Query("DELETE FROM fuel_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)
    
    @Query("DELETE FROM fuel_records WHERE isFullTank = 1 AND totalCost = 10000.0")
    suspend fun deleteDummyFullTank()

    @Query("UPDATE fuel_records SET isEmptyTank = 1 WHERE totalCost = 12500.0 AND isFullTank = 0")
    suspend fun fixPastRecordToEmptyTank()

    @Insert
    suspend fun insertCheckpoint(checkpoint: FuelCheckpointEntity)

    @Query("SELECT * FROM fuel_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<FuelRecordEntity>>

    @Query("SELECT * FROM fuel_records ORDER BY timestamp DESC LIMIT 1")
    fun getLatestRecord(): Flow<FuelRecordEntity?>

    @Query("SELECT * FROM fuel_checkpoints ORDER BY timestamp DESC LIMIT 1")
    fun getLatestCheckpoint(): Flow<FuelCheckpointEntity?>

    @Query("SELECT * FROM fuel_checkpoints ORDER BY timestamp DESC")
    fun getAllCheckpoints(): Flow<List<FuelCheckpointEntity>>

    // Urutan ascending dibutuhkan untuk rekonstruksi kronologis oleh FuelCalculator.
    @Query("SELECT * FROM fuel_records ORDER BY timestamp ASC")
    fun getAllRecordsAsc(): Flow<List<FuelRecordEntity>>

    @Query("SELECT * FROM fuel_checkpoints ORDER BY timestamp ASC")
    fun getAllCheckpointsAsc(): Flow<List<FuelCheckpointEntity>>
}