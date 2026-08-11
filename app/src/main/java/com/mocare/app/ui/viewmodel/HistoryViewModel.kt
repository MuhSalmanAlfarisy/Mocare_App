package com.mocare.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        val fuelLevelPercent: Int
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
                fuelRepository.getAllRecords(),
                fuelRepository.getAllCheckpoints()
            ) { records, checkpoints ->
                val refuels = records.map {
                    HistoryItem.Refuel(
                        timestamp = it.timestamp,
                        odometerKm = it.odometerKm,
                        liters = it.liters,
                        totalCost = it.totalCost
                    )
                }
                
                val marks = checkpoints.map {
                    HistoryItem.Checkpoint(
                        timestamp = it.timestamp,
                        fuelLevelPercent = it.fuelLevelPercent
                    )
                }
                
                val allItems = (refuels + marks).sortedByDescending { it.timestamp }
                
                HistoryUiState(
                    items = allItems,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
