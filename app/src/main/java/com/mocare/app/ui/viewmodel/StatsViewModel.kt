package com.mocare.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocare.app.data.FuelEvent
import com.mocare.app.data.StatsCalculator
import com.mocare.app.data.StatsPeriod
import com.mocare.app.data.repository.FuelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class StatsUiState(
    val selectedPeriod: StatsPeriod = StatsPeriod.THIS_MONTH,
    val totalVolume: Double = 0.0,
    val totalCost: Double = 0.0,
    val distanceTraveled: Double = 0.0,
    val avgDailyDistance: Double = 0.0,
    val fuelEconomy: Double = 0.0,
    val refillFrequencyDays: Int? = null,
    val typicalRefillLevel: Int? = null,
    val efficiencyTrend: Double? = null,
    val hasSufficientData: Boolean = true
)

class StatsViewModel(private val fuelRepository: FuelRepository) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(StatsPeriod.THIS_MONTH)

    val uiState: StateFlow<StatsUiState> = combine(
        _selectedPeriod,
        fuelRepository.getAllEventsAsc()
    ) { period, allEvents ->
        if (allEvents.isEmpty()) {
            return@combine StatsUiState(period, hasSufficientData = false)
        }

        val periodEvents = StatsCalculator.filterEventsByPeriod(allEvents, period)
        
        if (periodEvents.isEmpty()) {
            return@combine StatsUiState(period, hasSufficientData = false)
        }

        StatsUiState(
            selectedPeriod = period,
            totalVolume = StatsCalculator.calculateTotalVolume(periodEvents),
            totalCost = StatsCalculator.calculateTotalCost(periodEvents),
            distanceTraveled = StatsCalculator.calculateDistanceTraveled(periodEvents),
            avgDailyDistance = StatsCalculator.calculateAverageDailyDistance(periodEvents, period),
            fuelEconomy = StatsCalculator.calculateFuelEconomy(periodEvents),
            refillFrequencyDays = StatsCalculator.calculateRefillFrequency(periodEvents, period),
            typicalRefillLevel = StatsCalculator.calculateTypicalRefillLevel(allEvents),
            efficiencyTrend = StatsCalculator.calculateEfficiencyTrend(allEvents, periodEvents, period),
            hasSufficientData = true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsUiState()
    )

    fun onPeriodSelected(period: StatsPeriod) {
        _selectedPeriod.value = period
    }
}
