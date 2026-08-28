package com.mocare.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocare.app.data.FuelEvent
import com.mocare.app.data.VehicleConfig
import com.mocare.app.data.repository.FuelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed class HistoryItem {
    abstract val timestamp: Long

    data class Refuel(
        override val timestamp: Long,
        val odometerKm: Double,
        val liters: Double,
        val totalCost: Double,
        val isFullTank: Boolean = false,
        val isEmptyTank: Boolean = false
    ) : HistoryItem()

    data class Checkpoint(
        override val timestamp: Long,
        val odometerKm: Double,
        /** Jarak tempuh sejak event sebelumnya. 0 bila ini event pertama. */
        val distanceSinceLastKm: Double
    ) : HistoryItem()
}

data class HistoryUiState(
    val items: List<HistoryItem> = emptyList(),
    val isLoading: Boolean = true
)

class HistoryViewModel(private val fuelRepository: FuelRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                fuelRepository.getAllRecordsAsc(),
                fuelRepository.getAllCheckpointsAsc()
            ) { records, checkpoints ->
                val events: List<FuelEvent> = (
                    records.map {
                        FuelEvent.Refuel(
                            timestamp = it.timestamp,
                            odometerKm = it.odometerKm,
                            liters = it.liters,
                            totalCost = it.totalCost,
                            isFullTank = it.isFullTank,
                            isEmptyTank = it.isEmptyTank
                        )
                    } + checkpoints.map {
                        FuelEvent.Checkpoint(
                            timestamp = it.timestamp,
                            odometerKm = it.odometerKm
                        )
                    }
                    ).sortedBy { it.timestamp }
                    
                android.util.Log.d("MOCARE_DEBUG", "--- EVENT TIMELINE ---")
                var currentLiters = 0.0
                var currentOdometer: Double? = null
                events.forEach { event ->
                    currentOdometer?.let { prev -> 
                        val dist = event.odometerKm - prev
                        if (dist > 0) currentLiters -= (dist / VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER)
                    }
                    currentLiters = currentLiters.coerceAtLeast(0.0)
                    if (event is FuelEvent.Refuel) {
                        val beforeLiters = currentLiters
                        if (event.isFullTank) {
                            currentLiters = VehicleConfig.TANK_CAPACITY_LITERS
                        } else {
                            currentLiters += event.liters
                        }
                        android.util.Log.d("MOCARE_DEBUG", "REFUEL at ${event.odometerKm} km. isFullTank=${event.isFullTank}, litersAdded=${event.liters}. Before: $beforeLiters, After: $currentLiters")
                    } else if (event is FuelEvent.Checkpoint) {
                        android.util.Log.d("MOCARE_DEBUG", "CHECKPOINT at ${event.odometerKm} km. Tank level: $currentLiters")
                    }
                    currentLiters = currentLiters.coerceIn(0.0, VehicleConfig.TANK_CAPACITY_LITERS)
                    currentOdometer = event.odometerKm
                }
                android.util.Log.d("MOCARE_DEBUG", "----------------------")

                // Setiap event dipetakan menjadi tepat satu kartu. Jarak tempuh checkpoint
                // dihitung terhadap event sebelumnya pada timeline (refuel maupun checkpoint).
                val items = events.mapIndexed { index, event ->
                    when (event) {
                        is FuelEvent.Refuel -> HistoryItem.Refuel(
                            timestamp = event.timestamp,
                            odometerKm = event.odometerKm,
                            liters = event.liters,
                            totalCost = event.totalCost,
                            isFullTank = event.isFullTank,
                            isEmptyTank = event.isEmptyTank
                        )
                        is FuelEvent.Checkpoint -> {
                            val previousOdometer = events.getOrNull(index - 1)?.odometerKm
                            HistoryItem.Checkpoint(
                                timestamp = event.timestamp,
                                odometerKm = event.odometerKm,
                                distanceSinceLastKm = previousOdometer
                                    ?.let { (event.odometerKm - it).coerceAtLeast(0.0) }
                                    ?: 0.0
                            )
                        }
                    }
                }.sortedByDescending { it.timestamp }

                HistoryUiState(items = items, isLoading = false)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
