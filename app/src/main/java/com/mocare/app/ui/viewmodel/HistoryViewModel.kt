package com.mocare.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocare.app.data.FuelEvent
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
        val odometerKm: Int,
        val liters: Double,
        val totalCost: Double
    ) : HistoryItem()

    data class Checkpoint(
        override val timestamp: Long,
        val odometerKm: Int,
        /** Jarak tempuh sejak event sebelumnya. 0 bila ini event pertama. */
        val distanceSinceLastKm: Int
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
                            totalCost = it.totalCost
                        )
                    } + checkpoints.map {
                        FuelEvent.Checkpoint(
                            timestamp = it.timestamp,
                            odometerKm = it.odometerKm
                        )
                    }
                    ).sortedBy { it.timestamp }

                // Setiap event dipetakan menjadi tepat satu kartu. Jarak tempuh checkpoint
                // dihitung terhadap event sebelumnya pada timeline (refuel maupun checkpoint).
                val items = events.mapIndexed { index, event ->
                    when (event) {
                        is FuelEvent.Refuel -> HistoryItem.Refuel(
                            timestamp = event.timestamp,
                            odometerKm = event.odometerKm,
                            liters = event.liters,
                            totalCost = event.totalCost
                        )
                        is FuelEvent.Checkpoint -> {
                            val previousOdometer = events.getOrNull(index - 1)?.odometerKm
                            HistoryItem.Checkpoint(
                                timestamp = event.timestamp,
                                odometerKm = event.odometerKm,
                                distanceSinceLastKm = previousOdometer
                                    ?.let { (event.odometerKm - it).coerceAtLeast(0) }
                                    ?: 0
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
