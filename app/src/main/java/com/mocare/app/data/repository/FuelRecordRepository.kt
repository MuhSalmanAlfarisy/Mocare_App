package com.mocare.app.data.repository

import com.mocare.app.data.local.dao.FuelRecordDao
import com.mocare.app.data.local.entity.FuelRecordEntity
import kotlinx.coroutines.flow.Flow

class FuelRecordRepository(private val dao: FuelRecordDao) {
    fun getRecordsForMotor(motorId: Long): Flow<List<FuelRecordEntity>> =
        dao.getRecordsForMotor(motorId)

    suspend fun getLatestRecord(motorId: Long): FuelRecordEntity? =
        dao.getLatestRecordForMotor(motorId)

    suspend fun insert(record: FuelRecordEntity): Long = dao.insertRecord(record)
    suspend fun update(record: FuelRecordEntity) = dao.updateRecord(record)
    suspend fun delete(record: FuelRecordEntity) = dao.deleteRecord(record)
}
