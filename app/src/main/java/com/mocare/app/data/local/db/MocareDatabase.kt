package com.mocare.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mocare.app.data.local.dao.FuelRecordDao
import com.mocare.app.data.local.entity.FuelCheckpointEntity
import com.mocare.app.data.local.entity.FuelRecordEntity

@Database(
    entities = [FuelRecordEntity::class, FuelCheckpointEntity::class],
    version = 8,
    exportSchema = false
)
abstract class MocareDatabase : RoomDatabase() {
    abstract fun fuelRecordDao(): FuelRecordDao

    companion object {
        val MIGRATION_6_7 = object : androidx.room.migration.Migration(6, 7) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE fuel_records ADD COLUMN isFullTank INTEGER NOT NULL DEFAULT 0")
            }
        }
        val MIGRATION_7_8 = object : androidx.room.migration.Migration(7, 8) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE fuel_records ADD COLUMN isEmptyTank INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}