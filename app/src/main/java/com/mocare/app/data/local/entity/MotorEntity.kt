package com.mocare.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motor")
data class MotorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val tankCapacity: Double,
    val oilCapacityLiters: Double = 0.8,
    
    val engineOilIntervalKm: Int = 2000,
    val gearOilIntervalKm: Int = 8000,
    val brakeFluidIntervalKm: Int = 10000,
    val shockOilIntervalKm: Int = 15000,

    val lastEngineOilKm: Int = 0,
    val lastGearOilKm: Int = 0,
    val lastBrakeFluidKm: Int = 0,
    val lastShockOilKm: Int = 0,

    // Penambahan field sesuai spesifikasi (dengan nilai default agar tidak memutus kode yang ada)
    val brand: String = "",
    val model: String = "",
    val year: Int = 0,
    val currentOdometer: Int = 0,
    val userId: Long = 0L
)