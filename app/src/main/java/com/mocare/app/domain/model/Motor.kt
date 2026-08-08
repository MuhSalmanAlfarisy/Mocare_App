package com.mocare.app.domain.model

data class Motor(
    val id: Long = 0,
    val name: String,
    val tankCapacity: Double,
    val oilCapacityLiters: Double = 0.8,
    val engineOilIntervalKm: Int,
    val gearOilIntervalKm: Int,
    val brakeFluidIntervalKm: Int,
    val shockOilIntervalKm: Int = 15000,
    val lastEngineOilKm: Int = 0,
    val lastGearOilKm: Int = 0,
    val lastBrakeFluidKm: Int = 0,
    val lastShockOilKm: Int = 0,
    
    // Penambahan field sesuai spesifikasi
    val brand: String = "",
    val model: String = "",
    val year: Int = 0,
    val currentOdometer: Int = 0,
    val userId: Long = 0L
)
