package com.mocare.app.data.repository

import com.mocare.app.data.local.dao.FuelRecordDao
import com.mocare.app.data.local.entity.FuelCheckpointEntity
import com.mocare.app.data.local.entity.FuelRecordEntity
import com.mocare.app.data.FuelEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

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

    fun getAllEventsAsc(): Flow<List<FuelEvent>> {
        return combine(getAllRecordsAsc(), getAllCheckpointsAsc()) { records, checkpoints ->
            val refuels = records.map {
                FuelEvent.Refuel(
                    timestamp = it.timestamp,
                    odometerKm = it.odometerKm,
                    liters = it.liters,
                    totalCost = it.totalCost
                )
            }
            val checkpts = checkpoints.map {
                FuelEvent.Checkpoint(
                    timestamp = it.timestamp,
                    odometerKm = it.odometerKm
                )
            }
            (refuels + checkpts).sortedBy { it.timestamp }
        }
    }
}
