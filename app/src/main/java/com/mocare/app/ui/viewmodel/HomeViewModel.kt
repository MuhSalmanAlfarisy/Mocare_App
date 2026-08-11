package com.mocare.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocare.app.data.VehicleConfig
import com.mocare.app.data.local.entity.FuelCheckpointEntity
import com.mocare.app.data.local.entity.FuelRecordEntity
import com.mocare.app.data.repository.FuelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeUiState(
    val currentOdometerKm: Int = 0,
    val fuelLevelPercent: Int = -1,      // -1 = No Data / Empty
    val estimatedRangeKm: Int = 0,
    val hasRefuelData: Boolean = false,
    val isLoading: Boolean = true
)

class HomeViewModel(private val fuelRepository: FuelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                fuelRepository.getLatestRecord(),
                fuelRepository.getLatestCheckpoint()
            ) { latestRecord, latestCheckpoint ->
                if (latestRecord == null) {
                    // Belum pernah refuel → No Data
                    HomeUiState(
                        currentOdometerKm = 0,
                        fuelLevelPercent = -1,
                        estimatedRangeKm = 0,
                        hasRefuelData = false,
                        isLoading = false
                    )
                } else {
                    // Sudah pernah refuel
                    val odometer = latestRecord.odometerKm
                    val fuelPercent = latestCheckpoint?.fuelLevelPercent ?: 100
                    val estimatedRange = (fuelPercent / 100.0
                        * VehicleConfig.TANK_CAPACITY_LITERS
                        * VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER).toInt()

                    HomeUiState(
                        currentOdometerKm = odometer,
                        fuelLevelPercent = fuelPercent,
                        estimatedRangeKm = estimatedRange,
                        hasRefuelData = true,
                        isLoading = false
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun saveRefuelRecord(odometerKm: Int, nominalRupiah: Double, timestamp: Long) {
        viewModelScope.launch {
            val addedLiters = nominalRupiah / VehicleConfig.FUEL_PRICE_PER_LITER

            val record = FuelRecordEntity(
                timestamp = timestamp,
                odometerKm = odometerKm,
                liters = addedLiters,
                pricePerLiter = VehicleConfig.FUEL_PRICE_PER_LITER,
                totalCost = nominalRupiah
            )
            fuelRepository.insertRecord(record)
            
            // Calculate new fuel percentage
            val currentPercent = _uiState.value.fuelLevelPercent.coerceAtLeast(0)
            val currentLiters = (currentPercent / 100.0) * VehicleConfig.TANK_CAPACITY_LITERS
            val newLiters = currentLiters + addedLiters
            val newPercent = ((newLiters / VehicleConfig.TANK_CAPACITY_LITERS) * 100).toInt().coerceAtMost(100)

            fuelRepository.insertCheckpoint(
                FuelCheckpointEntity(fuelLevelPercent = newPercent, timestamp = timestamp)
            )
        }
    }

    fun saveFuelCheckpoint(percent: Int) {
        viewModelScope.launch {
            fuelRepository.insertCheckpoint(
                FuelCheckpointEntity(fuelLevelPercent = percent.coerceIn(0, 100))
            )
        }
    }
}
