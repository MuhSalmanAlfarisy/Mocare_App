package com.mocare.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mocare.app.data.local.dao.FuelRecordDao
import com.mocare.app.data.local.dao.MotorDao
import com.mocare.app.data.local.entity.FuelRecordEntity
import com.mocare.app.data.local.entity.MotorEntity

@Database(
    entities = [MotorEntity::class, FuelRecordEntity::class],
    version = 3,
    exportSchema = false
)
abstract class MocareDatabase : RoomDatabase() {
    abstract fun motorDao(): MotorDao
    abstract fun fuelRecordDao(): FuelRecordDao
}