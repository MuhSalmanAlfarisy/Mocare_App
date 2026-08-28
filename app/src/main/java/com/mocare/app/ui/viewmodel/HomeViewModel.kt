package com.mocare.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocare.app.data.FuelCalculator
import com.mocare.app.data.FuelEvent
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
    val currentOdometerKm: Double = 0.0,
    val fuelLevelPercent: Int = -1,      // -1 = No Data / Empty
    val estimatedRangeKm: Int = 0,
    val efficiencyKmPerLiter: Double = VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER,
    val isEfficiencyMeasured: Boolean = false,
    val hasRefuelData: Boolean = false,
    val isLoading: Boolean = true
)

class HomeViewModel(private val fuelRepository: FuelRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Hapus data dummy Rp10.000 yang ditambahkan karena salah instruksi
            fuelRepository.deleteDummyFullTank()
            // Ubah otomatis record Rp 12.500 menjadi kalibrasi 0% agar akurat
            fuelRepository.fixPastRecordToEmptyTank()
            
            // Seluruh event dibaca (bukan hanya yang terakhir) karena sisa bensin
            // direkonstruksi dari jarak tempuh sepanjang timeline.
            combine(
                fuelRepository.getAllRecordsAsc(),
                fuelRepository.getAllCheckpointsAsc()
            ) { records, checkpoints ->
                val events = buildFuelEvents(records, checkpoints)
                val fuel = FuelCalculator.calculate(events)

                HomeUiState(
                    currentOdometerKm = fuel.currentOdometerKm,
                    fuelLevelPercent = if (fuel.hasData) fuel.remainingPercent else -1,
                    estimatedRangeKm = if (fuel.hasData) fuel.estimatedRangeKm else 0,
                    efficiencyKmPerLiter = fuel.efficiencyKmPerLiter,
                    isEfficiencyMeasured = fuel.isEfficiencyMeasured,
                    hasRefuelData = fuel.hasData,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun saveRefuelRecord(odometerKm: Double, nominalRupiah: Double, isFullTank: Boolean, isEmptyTank: Boolean, timestamp: Long) {
        viewModelScope.launch {
            val addedLiters = nominalRupiah / VehicleConfig.FUEL_PRICE_PER_LITER

            // Hanya satu record yang ditulis. Tidak ada checkpoint bayangan, sehingga
            // satu aksi refuel menghasilkan tepat satu entri di History.
            fuelRepository.insertRecord(
                FuelRecordEntity(
                    timestamp = timestamp,
                    odometerKm = odometerKm,
                    liters = addedLiters,
                    pricePerLiter = VehicleConfig.FUEL_PRICE_PER_LITER,
                    totalCost = nominalRupiah,
                    isFullTank = isFullTank,
                    isEmptyTank = isEmptyTank
                )
            )
        }
    }

    /** Checkpoint hanya mencatat angka odometer terkini. */
    fun saveFuelCheckpoint(odometerKm: Double, timestamp: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            fuelRepository.insertCheckpoint(
                FuelCheckpointEntity(
                    timestamp = timestamp,
                    odometerKm = odometerKm
                )
            )
        }
    }

    private fun buildFuelEvents(
        records: List<FuelRecordEntity>,
        checkpoints: List<FuelCheckpointEntity>
    ): List<FuelEvent> {
        val refuels = records.map {
            FuelEvent.Refuel(
                timestamp = it.timestamp,
                odometerKm = it.odometerKm,
                liters = it.liters,
                totalCost = it.totalCost,
                isFullTank = it.isFullTank,
                isEmptyTank = it.isEmptyTank
            )
        }
        val marks = checkpoints.map {
            FuelEvent.Checkpoint(
                timestamp = it.timestamp,
                odometerKm = it.odometerKm
            )
        }
        return refuels + marks
    }
}
