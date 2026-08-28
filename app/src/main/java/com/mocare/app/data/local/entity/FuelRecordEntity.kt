package com.mocare.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fuel_records")
data class FuelRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val odometerKm: Double,
    val liters: Double,
    val pricePerLiter: Double,
    val totalCost: Double,
    @ColumnInfo(name = "isFullTank", defaultValue = "0")
    val isFullTank: Boolean = false,
    @ColumnInfo(name = "isEmptyTank", defaultValue = "0")
    val isEmptyTank: Boolean = false
)