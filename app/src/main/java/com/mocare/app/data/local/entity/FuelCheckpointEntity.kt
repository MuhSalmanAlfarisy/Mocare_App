package com.mocare.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Checkpoint hanya mencatat fakta objektif: angka odometer motor saat itu.
 *
 * Sisa bensin TIDAK disimpan di sini. Nilai tersebut adalah derived value yang
 * dihitung oleh [com.mocare.app.data.FuelCalculator] dari jarak tempuh antar event
 * dibagi efisiensi konsumsi bensin.
 */
@Entity(tableName = "fuel_checkpoints")
data class FuelCheckpointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val odometerKm: Double
)
