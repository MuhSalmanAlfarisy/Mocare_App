package com.mocare.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FuelCalculatorTest {

    private val tank = VehicleConfig.TANK_CAPACITY_LITERS
    private val fallbackEfficiency = VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER

    private fun refuel(timestamp: Long, odometerKm: Int, liters: Double) =
        FuelEvent.Refuel(timestamp, odometerKm, liters, liters * VehicleConfig.FUEL_PRICE_PER_LITER)

    private fun checkpoint(timestamp: Long, odometerKm: Int) =
        FuelEvent.Checkpoint(timestamp, odometerKm)

    @Test
    fun `tanpa event mengembalikan state kosong`() {
        val state = FuelCalculator.calculate(emptyList())

        assertFalse(state.hasData)
        assertEquals(-1, state.remainingPercent)
        assertEquals(0, state.estimatedRangeKm)
        assertEquals(0, state.currentOdometerKm)
    }

    @Test
    fun `refuel tunggal memakai efisiensi fallback`() {
        val state = FuelCalculator.calculate(listOf(refuel(1_000L, 380_000, 1.25)))

        assertTrue(state.hasData)
        assertFalse(state.isEfficiencyMeasured)
        assertEquals(fallbackEfficiency, state.efficiencyKmPerLiter, 0.0001)
        assertEquals(1.25, state.remainingLiters, 0.0001)
        assertEquals(380_000, state.currentOdometerKm)
        // 1.25 / 5.5 * 100 = 22.7 -> 23%
        assertEquals(23, state.remainingPercent)
        assertEquals((1.25 * fallbackEfficiency).toInt(), state.estimatedRangeKm)
    }

    @Test
    fun `checkpoint mengurangi bensin sesuai jarak tempuh`() {
        val events = listOf(
            refuel(1_000L, 380_000, 2.0),
            checkpoint(2_000L, 380_048)   // 48 KM pada 48 km per liter = 1 liter terpakai
        )

        val state = FuelCalculator.calculate(events)

        assertEquals(1.0, state.remainingLiters, 0.0001)
        assertEquals(380_048, state.currentOdometerKm)
    }

    @Test
    fun `checkpoint memperbarui odometer terkini`() {
        val events = listOf(
            refuel(1_000L, 380_000, 5.0),
            checkpoint(2_000L, 380_100)
        )

        assertEquals(380_100, FuelCalculator.calculate(events).currentOdometerKm)
    }

    @Test
    fun `bensin habis di-clamp ke nol tanpa nilai negatif`() {
        val events = listOf(
            refuel(1_000L, 380_000, 1.0),
            checkpoint(2_000L, 385_000)   // jarak jauh melebihi kapasitas bensin
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
            refuel(1_000L, 380_000, 4.0),
            refuel(2_000L, 380_000, 4.0)
        )

        assertEquals(tank, FuelCalculator.calculate(events).remainingLiters, 0.0001)
    }

    @Test
    fun `efisiensi belum terukur dengan satu refuel`() {
        assertNull(FuelCalculator.measureEfficiency(listOf(refuel(1_000L, 380_000, 2.0))))
    }

    @Test
    fun `efisiensi terukur dari dua refuel dengan metode tank to tank`() {
        val events = listOf(
            refuel(1_000L, 380_000, 2.0),
            refuel(2_000L, 380_100, 2.0)   // 100 KM / 2 L = 50 km per liter
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
            refuel(1_000L, 380_000, 2.0),
            refuel(2_000L, 380_100, 2.0),
            refuel(3_000L, 380_220, 2.0),
            refuel(4_000L, 380_300, 2.0),
            refuel(5_000L, 380_390, 2.0)
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
            refuel(1_000L, 380_000, 2.0),
            refuel(2_000L, 379_000, 2.0),   // odometer mundur, interval tidak valid
            refuel(3_000L, 379_100, 2.0)    // 100 / 2 = 50
        )

        assertEquals(50.0, FuelCalculator.measureEfficiency(events)!!, 0.0001)
    }

    @Test
    fun `checkpoint tanpa refuel tidak dianggap punya data`() {
        val state = FuelCalculator.calculate(listOf(checkpoint(1_000L, 380_000)))

        assertFalse(state.hasData)
        assertEquals(380_000, state.currentOdometerKm)
        assertEquals(0.0, state.remainingLiters, 0.0001)
    }

    @Test
    fun `event diurutkan berdasarkan timestamp meski input tidak berurutan`() {
        val outOfOrder = listOf(
            checkpoint(3_000L, 380_048),
            refuel(1_000L, 380_000, 2.0)
        )

        val state = FuelCalculator.calculate(outOfOrder)

        assertEquals(1.0, state.remainingLiters, 0.0001)
        assertEquals(380_048, state.currentOdometerKm)
    }

    @Test
    fun `refuel setelah bensin habis mengisi ulang dari nol`() {
        val events = listOf(
            refuel(1_000L, 380_000, 1.0),
            checkpoint(2_000L, 385_000),        // bensin habis
            refuel(3_000L, 385_000, 3.0)        // isi ulang tanpa jarak tambahan
        )

        assertEquals(3.0, FuelCalculator.calculate(events).remainingLiters, 0.0001)
    }
}
