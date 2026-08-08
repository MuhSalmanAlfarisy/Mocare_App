package com.mocare.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocare.app.data.local.entity.FuelRecordEntity
import com.mocare.app.data.local.entity.MotorEntity
import com.mocare.app.data.repository.FuelRecordRepository
import com.mocare.app.data.repository.MotorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

enum class FuelStatusType {
    SAFE,           // Masih aman
    WARNING,        // Segera isi bensin
    URGENT          // Harus isi sekarang
}

enum class MaintenanceStatusType {
    SAFE,           // Aman
    WARNING,        // Segera diperiksa
    URGENT          // Perlu diperiksa sekarang
}

data class MaintenanceItem(
    val name: String,
    val lastKm: Int,
    val intervalKm: Int,
    val currentKm: Int,
    val remainingKm: Int,
    val status: MaintenanceStatusType
)

data class SummaryUiState(
    val motor: MotorEntity? = null,
    val latestKm: Int = 0,
    val totalRecords: Int = 0,
    val avgKmPerLiter: Double = 0.0,
    val estimatedRemainingRangeKm: Double = 0.0,
    val fuelStatus: FuelStatusType = FuelStatusType.SAFE,
    val fuelStatusLabel: String = "Masih aman",
    val maintenanceItems: List<MaintenanceItem> = emptyList()
)

class SummaryViewModel(
    private val motorRepository: MotorRepository,
    private val fuelRecordRepository: FuelRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    fun loadSummary(motorId: Long) {
        viewModelScope.launch {
            combine(
                motorRepository.getMotorById(motorId),
                fuelRecordRepository.getRecordsForMotor(motorId)
            ) { motor, records ->
                if (motor == null) {
                    SummaryUiState()
                } else {
                    calculateSummary(motor, records)
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun calculateSummary(
        motor: MotorEntity,
        records: List<FuelRecordEntity>
    ): SummaryUiState {
        val latestRecord = records.maxByOrNull { it.kmWhenFilled }
        val latestKm = latestRecord?.kmWhenFilled ?: 0
        val totalRecords = records.size

        // 1. Perhitungan Rata-rata Konsumsi BBM (KM/Liter)
        val avgKmPerLiter = if (records.size >= 2) {
            val sorted = records.sortedBy { it.kmWhenFilled }
            val firstKm = sorted.first().kmWhenFilled
            val lastKm = sorted.last().kmWhenFilled
            val totalDistance = (lastKm - firstKm).toDouble()
            val totalLiters = sorted.drop(1).sumOf { it.amountLiters }
            if (totalLiters > 0) totalDistance / totalLiters else 0.0
        } else {
            0.0
        }

        // 2. Perhitungan Estimasi Status Bensin & Jarak Sisa Tempuh Tangki
        val lastFillAmount = latestRecord?.amountLiters ?: motor.tankCapacity
        val estimatedRemainingRangeKm = if (avgKmPerLiter > 0) {
            lastFillAmount * avgKmPerLiter
        } else {
            lastFillAmount * 35.0 // Fallback konsumsi rata-rata bawaan 35 KM/L
        }

        val fuelPercentage = (lastFillAmount / motor.tankCapacity).coerceIn(0.0, 1.0)
        val (fuelStatus, fuelStatusLabel) = when {
            fuelPercentage <= 0.15 -> Pair(FuelStatusType.URGENT, "Harus isi sekarang")
            fuelPercentage <= 0.35 -> Pair(FuelStatusType.WARNING, "Segera isi bensin")
            else -> Pair(FuelStatusType.SAFE, "Masih aman")
        }

        // 3. Perhitungan Status Perawatan Komponen (Oli Mesin, Gardan, Minyak Rem, Oli Shock)
        val items = mutableListOf<MaintenanceItem>()

        val effectiveCurrentKm = when {
            motor.currentOdometer > 0 -> motor.currentOdometer
            latestKm > 0 -> latestKm
            else -> maxOf(
                motor.lastEngineOilKm, motor.lastGearOilKm, motor.lastBrakeFluidKm, motor.lastShockOilKm
            )
        }

        items.add(
            createMaintenanceItem(
                name = "Oli Mesin (${motor.oilCapacityLiters}L)",
                currentKm = effectiveCurrentKm,
                lastKm = motor.lastEngineOilKm,
                intervalKm = motor.engineOilIntervalKm
            )
        )

        items.add(
            createMaintenanceItem(
                name = "Oli Gardan",
                currentKm = effectiveCurrentKm,
                lastKm = motor.lastGearOilKm,
                intervalKm = motor.gearOilIntervalKm
            )
        )

        items.add(
            createMaintenanceItem(
                name = "Minyak Rem",
                currentKm = effectiveCurrentKm,
                lastKm = motor.lastBrakeFluidKm,
                intervalKm = motor.brakeFluidIntervalKm
            )
        )

        items.add(
            createMaintenanceItem(
                name = "Oli Shock",
                currentKm = effectiveCurrentKm,
                lastKm = motor.lastShockOilKm,
                intervalKm = motor.shockOilIntervalKm
            )
        )

        return SummaryUiState(
            motor = motor,
            latestKm = effectiveCurrentKm,
            totalRecords = totalRecords,
            avgKmPerLiter = avgKmPerLiter,
            estimatedRemainingRangeKm = estimatedRemainingRangeKm,
            fuelStatus = fuelStatus,
            fuelStatusLabel = fuelStatusLabel,
            maintenanceItems = items
        )
    }

    private fun createMaintenanceItem(
        name: String,
        currentKm: Int,
        lastKm: Int,
        intervalKm: Int
    ): MaintenanceItem {
        val kmDriven = currentKm - lastKm
        val remainingKm = intervalKm - kmDriven

        val status = when {
            remainingKm <= 0 -> MaintenanceStatusType.URGENT        // Perlu diperiksa sekarang
            remainingKm <= (intervalKm * 0.2) -> MaintenanceStatusType.WARNING // Segera diperiksa
            else -> MaintenanceStatusType.SAFE                      // Aman
        }

        return MaintenanceItem(
            name = name,
            lastKm = lastKm,
            intervalKm = intervalKm,
            currentKm = currentKm,
            remainingKm = remainingKm,
            status = status
        )
    }

    fun updateCurrentOdometer(motorId: Long, newKm: Int) {
        viewModelScope.launch {
            val motor = _uiState.value.motor ?: return@launch
            motorRepository.update(motor.copy(currentOdometer = newKm))
        }
    }
}
