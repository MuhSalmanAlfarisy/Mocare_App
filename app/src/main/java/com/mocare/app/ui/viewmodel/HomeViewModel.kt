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
    val fuelLevelPercent: Int = 100,
    val estimatedRangeKm: Int = 0,
    val lastPricePerLiter: Double = 0.0,
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
                val odometer = latestRecord?.odometerKm ?: 0
                val fuelPercent = latestCheckpoint?.fuelLevelPercent ?: 100
                val estimatedRange = (fuelPercent / 100.0 * VehicleConfig.TANK_CAPACITY_LITERS * VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER).toInt()
                val lastPrice = latestRecord?.pricePerLiter ?: 0.0

                HomeUiState(
                    currentOdometerKm = odometer,
                    fuelLevelPercent = fuelPercent,
                    estimatedRangeKm = estimatedRange,
                    lastPricePerLiter = lastPrice,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun saveRefuelRecord(odometerKm: Int, timestamp: Long) {
        viewModelScope.launch {
            val record = FuelRecordEntity(
                timestamp = timestamp,
                odometerKm = odometerKm,
                liters = VehicleConfig.TANK_CAPACITY_LITERS,
                pricePerLiter = _uiState.value.lastPricePerLiter,
                totalCost = VehicleConfig.TANK_CAPACITY_LITERS * _uiState.value.lastPricePerLiter
            )
            fuelRepository.insertRecord(record)
            // Reset fuel to 100% after refuel
            fuelRepository.insertCheckpoint(
                FuelCheckpointEntity(fuelLevelPercent = 100, timestamp = timestamp)
            )
        }
    }

    fun saveFuelUsage(liters: Double, pricePerLiter: Double) {
        viewModelScope.launch {
            val record = FuelRecordEntity(
                odometerKm = _uiState.value.currentOdometerKm,
                liters = liters,
                pricePerLiter = pricePerLiter,
                totalCost = liters * pricePerLiter
            )
            fuelRepository.insertRecord(record)
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
