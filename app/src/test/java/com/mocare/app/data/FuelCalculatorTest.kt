package com.mocare.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FuelCalculatorTest {

    private val tank = VehicleConfig.TANK_CAPACITY_LITERS
    private val fallbackEfficiency = VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER

    private fun refuel(timestamp: Long, odometerKm: Double, liters: Double, isFullTank: Boolean = false) =
        FuelEvent.Refuel(timestamp, odometerKm, liters, liters * VehicleConfig.FUEL_PRICE_PER_LITER, isFullTank = isFullTank)

    private fun checkpoint(timestamp: Long, odometerKm: Double) =
        FuelEvent.Checkpoint(timestamp, odometerKm)

    @Test
    fun `tanpa event mengembalikan state kosong`() {
        val state = FuelCalculator.calculate(emptyList())

        assertFalse(state.hasData)
        assertEquals(-1, state.remainingPercent)
        assertEquals(0, state.estimatedRangeKm)
        assertEquals(0.0, state.currentOdometerKm, 0.0001)
    }

    @Test
    fun `refuel tunggal memakai efisiensi fallback`() {
        val state = FuelCalculator.calculate(listOf(refuel(1_000L, 380_000.0, 1.25)))

        assertTrue(state.hasData)
        assertFalse(state.isEfficiencyMeasured)
        assertEquals(fallbackEfficiency, state.efficiencyKmPerLiter, 0.0001)
        assertEquals(1.25, state.remainingLiters, 0.0001)
        assertEquals(380_000.0, state.currentOdometerKm, 0.0001)
        // 1.25 / 5.5 * 100 = 22.7 -> 23%
        assertEquals(23, state.remainingPercent)
        assertEquals((1.25 * fallbackEfficiency).toInt(), state.estimatedRangeKm)
    }

    @Test
    fun `checkpoint mengurangi bensin sesuai jarak tempuh`() {
        val events = listOf(
            refuel(1_000L, 380_000.0, 2.0),
            checkpoint(2_000L, 380_048.0)   // 48 KM pada 48 km per liter = 1 liter terpakai
        )

        val state = FuelCalculator.calculate(events)

        assertEquals(1.0, state.remainingLiters, 0.0001)
        assertEquals(380_048.0, state.currentOdometerKm, 0.0001)
    }

    @Test
    fun `checkpoint memperbarui odometer terkini`() {
        val events = listOf(
            refuel(1_000L, 380_000.0, 5.0),
            checkpoint(2_000L, 380_100.0)
        )

        assertEquals(380_100.0, FuelCalculator.calculate(events).currentOdometerKm, 0.0001)
    }

    @Test
    fun `bensin habis di-clamp ke nol tanpa nilai negatif`() {
        val events = listOf(
            refuel(1_000L, 380_000.0, 1.0),
            checkpoint(2_000L, 385_000.0)   // jarak jauh melebihi kapasitas bensin
        )

        val state = FuelCalculator.calculate(events)

        assertEquals(0.0, state.remainingLiters, 0.0001)
        assertEquals(0, state.remainingPercent)
        assertEquals(0, state.estimatedRangeKm)
        assertTrue(state.hasData)
    }

    @Test
    fun `pengisian melebihi kapasitas di-clamp ke kapasitas tangki`() {
        val events = listOf(
            refuel(1_000L, 380_000.0, 4.0),
            refuel(2_000L, 380_000.0, 4.0)
        )

        assertEquals(tank, FuelCalculator.calculate(events).remainingLiters, 0.0001)
    }

    @Test
    fun `efisiensi belum terukur dengan satu refuel`() {
        assertNull(FuelCalculator.measureEfficiency(listOf(refuel(1_000L, 380_000.0, 2.0, true))))
    }

    @Test
    fun `efisiensi terukur dari dua refuel dengan metode tank to tank`() {
        val events = listOf(
            refuel(1_000L, 380_000.0, 2.0, true),
            refuel(2_000L, 380_100.0, 2.0, true)   // 100 KM / 2 L = 50 km per liter
        )

        val measured = FuelCalculator.measureEfficiency(events)
        assertEquals(50.0, measured!!, 0.0001)

        val state = FuelCalculator.calculate(events)
        assertTrue(state.isEfficiencyMeasured)
        assertEquals(50.0, state.efficiencyKmPerLiter, 0.0001)
    }

    @Test
    fun `efisiensi merata-ratakan interval terakhir sesuai sample window`() {
        // Interval: 100/2=50, 120/2=60, 80/2=40, 90/2=45
        val events = listOf(
            refuel(1_000L, 380_000.0, 2.0, true),
            refuel(2_000L, 380_100.0, 2.0, true),
            refuel(3_000L, 380_220.0, 2.0, true),
            refuel(4_000L, 380_300.0, 2.0, true),
            refuel(5_000L, 380_390.0, 2.0, true)
        )

        // Window 3 terakhir: (60 + 40 + 45) / 3 = 48.333...
        assertEquals(
            (60.0 + 40.0 + 45.0) / 3,
            FuelCalculator.measureEfficiency(events)!!,
            0.0001
        )
    }

    @Test
    fun `interval dengan odometer mundur diabaikan saat mengukur efisiensi`() {
        val events = listOf(
            refuel(1_000L, 380_000.0, 2.0, true),
            refuel(2_000L, 379_000.0, 2.0, true),   // odometer mundur, interval tidak valid
            refuel(3_000L, 379_100.0, 2.0, true)    // 100 / 2 = 50
        )

        assertEquals(50.0, FuelCalculator.measureEfficiency(events)!!, 0.0001)
    }

    @Test
    fun `checkpoint tanpa refuel tidak dianggap punya data`() {
        val state = FuelCalculator.calculate(listOf(checkpoint(1_000L, 380_000.0)))

        assertFalse(state.hasData)
        assertEquals(380_000.0, state.currentOdometerKm, 0.0001)
        assertEquals(0.0, state.remainingLiters, 0.0001)
    }

    @Test
    fun `event diurutkan berdasarkan timestamp meski input tidak berurutan`() {
        val outOfOrder = listOf(
            checkpoint(3_000L, 380_048.0),
            refuel(1_000L, 380_000.0, 2.0)
        )

        val state = FuelCalculator.calculate(outOfOrder)

        assertEquals(1.0, state.remainingLiters, 0.0001)
        assertEquals(380_048.0, state.currentOdometerKm, 0.0001)
    }

    @Test
    fun `refuel setelah bensin habis mengisi ulang dari nol`() {
        val events = listOf(
            refuel(1_000L, 380_000.0, 1.0),
            checkpoint(2_000L, 385_000.0),        // bensin habis
            refuel(3_000L, 385_000.0, 3.0)        // isi ulang tanpa jarak tambahan
        )

        assertEquals(3.0, FuelCalculator.calculate(events).remainingLiters, 0.0001)
    }
}
