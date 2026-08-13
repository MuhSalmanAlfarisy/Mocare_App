package com.mocare.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mocare.app.data.local.dao.FuelRecordDao
import com.mocare.app.data.local.entity.FuelCheckpointEntity
import com.mocare.app.data.local.entity.FuelRecordEntity

@Database(
    entities = [FuelRecordEntity::class, FuelCheckpointEntity::class],
    version = 5,
    exportSchema = false
)
abstract class MocareDatabase : RoomDatabase() {
    abstract fun fuelRecordDao(): FuelRecordDao
}