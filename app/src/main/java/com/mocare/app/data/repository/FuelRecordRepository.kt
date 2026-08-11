package com.mocare.app.data.repository

import com.mocare.app.data.local.dao.FuelRecordDao
import com.mocare.app.data.local.entity.FuelCheckpointEntity
import com.mocare.app.data.local.entity.FuelRecordEntity
import kotlinx.coroutines.flow.Flow

class FuelRepository(private val dao: FuelRecordDao) {
    fun getAllRecords(): Flow<List<FuelRecordEntity>> = dao.getAllRecords()
    fun getLatestRecord(): Flow<FuelRecordEntity?> = dao.getLatestRecord()
    fun getLatestCheckpoint(): Flow<FuelCheckpointEntity?> = dao.getLatestCheckpoint()
    suspend fun insertRecord(record: FuelRecordEntity) = dao.insertRecord(record)
    suspend fun insertCheckpoint(checkpoint: FuelCheckpointEntity) = dao.insertCheckpoint(checkpoint)
}
