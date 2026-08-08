package com.mocare.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fuel_record",
    foreignKeys = [
        ForeignKey(
            entity = MotorEntity::class,
            parentColumns = ["id"],
            childColumns = ["motorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["motorId"])]
)
data class FuelRecordEntity(
    @PrimaryKey(autoGenerate = true)
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