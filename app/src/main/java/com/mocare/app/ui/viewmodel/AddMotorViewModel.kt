package com.mocare.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocare.app.data.local.entity.MotorEntity
import com.mocare.app.data.repository.MotorRepository
import kotlinx.coroutines.launch

class AddMotorViewModel(private val motorRepository: MotorRepository) : ViewModel() {
    fun saveMotor(
        name: String,
        tankCapacity: Double,
        oilCapacityLiters: Double,
        engineOilIntervalKm: Int,
        gearOilIntervalKm: Int,
        brakeFluidIntervalKm: Int,
        shockOilIntervalKm: Int,
        currentOdometer: Int,
        lastEngineOilKm: Int,
        lastGearOilKm: Int,
        lastBrakeFluidKm: Int,
        lastShockOilKm: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val motor = MotorEntity(
                name = name,
                tankCapacity = tankCapacity,
                oilCapacityLiters = oilCapacityLiters,
                engineOilIntervalKm = engineOilIntervalKm,
                gearOilIntervalKm = gearOilIntervalKm,
                brakeFluidIntervalKm = brakeFluidIntervalKm,
                shockOilIntervalKm = shockOilIntervalKm,
                currentOdometer = currentOdometer,
                lastEngineOilKm = lastEngineOilKm,
                lastGearOilKm = lastGearOilKm,
                lastBrakeFluidKm = lastBrakeFluidKm,
                lastShockOilKm = lastShockOilKm
            )
            motorRepository.insert(motor)
            onSuccess()
        }
    }
}
