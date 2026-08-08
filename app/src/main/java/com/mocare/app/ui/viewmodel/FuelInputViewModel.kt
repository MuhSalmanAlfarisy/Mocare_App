package com.mocare.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocare.app.data.local.entity.FuelRecordEntity
import com.mocare.app.data.repository.FuelRecordRepository
import kotlinx.coroutines.launch

class FuelInputViewModel(private val fuelRecordRepository: FuelRecordRepository) : ViewModel() {
    fun saveFuelRecord(
        motorId: Long,
        kmWhenFilled: Int,
        amountLiters: Double,
        pricePerLiter: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val record = FuelRecordEntity(
                motorId = motorId,
                kmWhenFilled = kmWhenFilled,
                dateFilled = System.currentTimeMillis(),
                amountLiters = amountLiters,
                pricePerLiter = pricePerLiter
            )
            fuelRecordRepository.insert(record)
            onSuccess()
        }
    }
}
