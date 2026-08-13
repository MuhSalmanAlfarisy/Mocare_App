package com.mocare.app.data

import kotlin.math.roundToInt

/**
 * Satu kejadian pada timeline bahan bakar.
 *
 * Keduanya membawa angka odometer, sehingga jarak tempuh dapat dihitung dari
 * event apa pun ke event berikutnya tanpa membedakan jenisnya.
 */
sealed class FuelEvent {
    abstract val timestamp: Long
    abstract val odometerKm: Double

    data class Refuel(
        override val timestamp: Long,
        override val odometerKm: Double,
        val liters: Double,
        val totalCost: Double
    ) : FuelEvent()

    data class Checkpoint(
        override val timestamp: Long,
        override val odometerKm: Double
    ) : FuelEvent()
}

/**
 * Hasil kalkulasi kondisi bahan bakar terkini.
 *
 * @param remainingPercent 0..100. Tidak pernah negatif.
 * @param estimatedRangeKm 0 atau lebih. Tidak pernah negatif.
 * @param efficiencyKmPerLiter efisiensi yang dipakai untuk kalkulasi.
 * @param isEfficiencyMeasured true jika efisiensi dihitung dari histori refuel nyata,
 *   false jika masih memakai nilai fallback [VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER].
 */
data class FuelState(
    val currentOdometerKm: Double = 0.0,
    val remainingLiters: Double = 0.0,
    val remainingPercent: Int = -1,
    val estimatedRangeKm: Int = 0,
    val efficiencyKmPerLiter: Double = VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER,
    val isEfficiencyMeasured: Boolean = false,
    val hasData: Boolean = false
)

/**
 * Kalkulator bahan bakar murni (tanpa dependensi Android) agar dapat diuji sebagai unit test JVM.
 *
 * Prinsip: persentase bensin adalah derived value, bukan input. Yang disimpan hanya
 * fakta objektif (liter masuk saat refuel dan angka odometer), lalu sisa bensin
 * direkonstruksi dengan menelusuri seluruh event secara kronologis.
 */
object FuelCalculator {

    /** Jumlah interval refuel terakhir yang dirata-ratakan agar efisiensi tidak fluktuatif per pengisian. */
    const val EFFICIENCY_SAMPLE_WINDOW = 3

    /**
     * Menghitung efisiensi konsumsi (km/liter) dari histori refuel dengan metode tank-to-tank:
     * liter yang diisi pada refuel ke-N dianggap menempuh jarak dari refuel ke-(N-1) sampai ke-N.
     *
     * Butuh minimal 2 refuel. Jika belum tersedia, mengembalikan null sehingga pemanggil
     * memakai [VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER] sebagai fallback.
     */
    fun measureEfficiency(events: List<FuelEvent>): Double? {
        val refuels = events
            .filterIsInstance<FuelEvent.Refuel>()
            .sortedBy { it.timestamp }

        if (refuels.size < 2) return null

        val samples = mutableListOf<Double>()
        for (i in 1 until refuels.size) {
            val distance = refuels[i].odometerKm - refuels[i - 1].odometerKm
            val liters = refuels[i].liters
            // Abaikan interval tidak masuk akal (odometer mundur / tanpa jarak / liter nol).
            if (distance > 0 && liters > 0.0) {
                samples += distance / liters
            }
        }

        if (samples.isEmpty()) return null

        return samples
            .takeLast(EFFICIENCY_SAMPLE_WINDOW)
            .average()
    }

    /**
     * Merekonstruksi kondisi bahan bakar terkini dari seluruh event.
     *
     * Algoritma: mulai dari 0 liter, lalu untuk setiap event secara kronologis kurangi
     * bensin sebesar (jarak sejak event sebelumnya / efisiensi), kemudian tambahkan
     * liter yang diisi bila event tersebut adalah refuel. Nilai selalu di-clamp pada
     * rentang 0..kapasitas tangki sehingga tidak mungkin negatif maupun melebihi tangki.
     */
    fun calculate(events: List<FuelEvent>): FuelState {
        val sorted = events.sortedBy { it.timestamp }
        if (sorted.isEmpty()) return FuelState()

        val measured = measureEfficiency(sorted)
        val efficiency = measured ?: VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER

        var liters = 0.0
        var lastOdometer: Double? = null

        for (event in sorted) {
            lastOdometer?.let { previous ->
                val distance = event.odometerKm - previous
                if (distance > 0) {
                    liters -= distance / efficiency
                }
            }
            if (event is FuelEvent.Refuel) {
                liters += event.liters
            }
            liters = liters.coerceIn(0.0, VehicleConfig.TANK_CAPACITY_LITERS)
            lastOdometer = event.odometerKm
        }

        val percent = ((liters / VehicleConfig.TANK_CAPACITY_LITERS) * 100)
            .roundToInt()
            .coerceIn(0, 100)

        return FuelState(
            currentOdometerKm = lastOdometer ?: 0.0,
            remainingLiters = liters,
            remainingPercent = percent,
            estimatedRangeKm = (liters * efficiency).roundToInt().coerceAtLeast(0),
            efficiencyKmPerLiter = efficiency,
            isEfficiencyMeasured = measured != null,
            hasData = sorted.any { it is FuelEvent.Refuel }
        )
    }
}
