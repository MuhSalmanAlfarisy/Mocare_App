package com.mocare.app.data.repository

import com.mocare.app.data.local.dao.FuelRecordDao
import com.mocare.app.data.local.entity.FuelCheckpointEntity
import com.mocare.app.data.local.entity.FuelRecordEntity
import kotlinx.coroutines.flow.Flow

class FuelRepository(private val dao: FuelRecordDao) {
    fun getAllRecords(): Flow<List<FuelRecordEntity>> = dao.getAllRecords()
    fun getLatestRecord(): Flow<FuelRecordEntity?> = dao.getLatestRecord()
    fun getAllCheckpoints(): Flow<List<FuelCheckpointEntity>> = dao.getAllCheckpoints()
    fun getLatestCheckpoint(): Flow<FuelCheckpointEntity?> = dao.getLatestCheckpoint()

    // Ascending: dipakai untuk merekonstruksi kondisi bensin secara kronologis.
    fun getAllRecordsAsc(): Flow<List<FuelRecordEntity>> = dao.getAllRecordsAsc()
    fun getAllCheckpointsAsc(): Flow<List<FuelCheckpointEntity>> = dao.getAllCheckpointsAsc()

    suspend fun insertRecord(record: FuelRecordEntity) = dao.insertRecord(record)
    suspend fun insertCheckpoint(checkpoint: FuelCheckpointEntity) = dao.insertCheckpoint(checkpoint)
}
