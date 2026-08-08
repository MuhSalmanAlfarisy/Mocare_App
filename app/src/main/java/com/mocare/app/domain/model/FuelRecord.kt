package com.mocare.app.domain.model

data class FuelRecord(
    val id: Long = 0,
    val motorId: Long,
    val kmWhenFilled: Int,
    val dateFilled: Long,
    val amountLiters: Double,
    val pricePerLiter: Double,
    
    // Penambahan field sesuai spesifikasi
    val fuelType: String = "",
    val amount: Double = 0.0
)
