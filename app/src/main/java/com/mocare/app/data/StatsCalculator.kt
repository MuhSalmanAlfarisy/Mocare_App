package com.mocare.app.data

import java.util.Calendar
import kotlin.math.roundToInt

enum class StatsPeriod {
    TODAY,
    THIS_WEEK,
    THIS_MONTH
}

object StatsCalculator {

    fun filterEventsByPeriod(events: List<FuelEvent>, period: StatsPeriod): List<FuelEvent> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        when (period) {
            StatsPeriod.TODAY -> {
                // already at start of today
            }
            StatsPeriod.THIS_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            }
            StatsPeriod.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
            }
        }
        val startTime = calendar.timeInMillis
        return events.filter { it.timestamp >= startTime }.sortedBy { it.timestamp }
    }

    fun calculateTotalVolume(events: List<FuelEvent>): Double {
        return events.filterIsInstance<FuelEvent.Refuel>().sumOf { it.liters }
    }

    fun calculateTotalCost(events: List<FuelEvent>): Double {
        return events.filterIsInstance<FuelEvent.Refuel>().sumOf { it.totalCost }
    }

    fun calculateDistanceTraveled(events: List<FuelEvent>): Double {
        if (events.isEmpty()) return 0.0
        val sorted = events.sortedBy { it.timestamp }
        val maxOdo = sorted.last().odometerKm
        val minOdo = sorted.first().odometerKm
        return (maxOdo - minOdo).coerceAtLeast(0.0)
    }

    fun calculateAverageDailyDistance(events: List<FuelEvent>, period: StatsPeriod): Double {
        val distance = calculateDistanceTraveled(events)
        val days = when (period) {
            StatsPeriod.TODAY -> 1.0
            StatsPeriod.THIS_WEEK -> {
                val cal = Calendar.getInstance()
                val currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                val firstDay = cal.firstDayOfWeek
                var diff = currentDayOfWeek - firstDay
                if (diff < 0) diff += 7
                (diff + 1).toDouble()
            }
            StatsPeriod.THIS_MONTH -> {
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toDouble()
            }
        }
        return if (days > 0) distance / days else 0.0
    }

    fun calculateFuelEconomy(events: List<FuelEvent>): Double {
        val measured = FuelCalculator.measureEfficiency(events)
        return measured ?: 0.0 // 0.0 if not enough data within this period
    }

    fun calculateRefillFrequency(events: List<FuelEvent>, period: StatsPeriod): Int? {
        val refuelsCount = events.filterIsInstance<FuelEvent.Refuel>().size
        if (refuelsCount == 0) return null

        val days = when (period) {
            StatsPeriod.TODAY -> 1
            StatsPeriod.THIS_WEEK -> {
                val cal = Calendar.getInstance()
                val diff = cal.get(Calendar.DAY_OF_WEEK) - cal.firstDayOfWeek
                if (diff < 0) diff + 7 + 1 else diff + 1
            }
            StatsPeriod.THIS_MONTH -> {
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            }
        }
        return Math.max(1, (days.toDouble() / refuelsCount).roundToInt())
    }

    fun calculateTypicalRefillLevel(allEvents: List<FuelEvent>): Int? {
        val sorted = allEvents.sortedBy { it.timestamp }
        val refillLevels = mutableListOf<Int>()

        var liters = 0.0
        var lastOdometer: Double? = null
        val efficiency = FuelCalculator.measureEfficiency(sorted) ?: VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER

        for (event in sorted) {
            lastOdometer?.let { previous ->
                val distance = event.odometerKm - previous
                if (distance > 0) {
                    liters -= distance / efficiency
                }
            }
            liters = liters.coerceIn(0.0, VehicleConfig.TANK_CAPACITY_LITERS)

            if (event is FuelEvent.Refuel) {
                // This is the level just before refueling
                val percent = ((liters / VehicleConfig.TANK_CAPACITY_LITERS) * 100).roundToInt().coerceIn(0, 100)
                refillLevels.add(percent)
                liters += event.liters
                liters = liters.coerceIn(0.0, VehicleConfig.TANK_CAPACITY_LITERS)
            }
            lastOdometer = event.odometerKm
        }

        if (refillLevels.isEmpty()) return null
        return refillLevels.average().roundToInt()
    }

    // Returns percentage difference (e.g., 5.0 for 5% more efficient, -2.0 for 2% less efficient)
    fun calculateEfficiencyTrend(allEvents: List<FuelEvent>, currentPeriodEvents: List<FuelEvent>, period: StatsPeriod): Double? {
        val currentEfficiency = FuelCalculator.measureEfficiency(currentPeriodEvents) ?: return null

        // Need to calculate previous period efficiency
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val endPreviousTime = when (period) {
            StatsPeriod.TODAY -> calendar.timeInMillis
            StatsPeriod.THIS_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.timeInMillis
            }
            StatsPeriod.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.timeInMillis
            }
        }

        val startPreviousTime = when (period) {
            StatsPeriod.TODAY -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.timeInMillis
            }
            StatsPeriod.THIS_WEEK -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                calendar.timeInMillis
            }
            StatsPeriod.THIS_MONTH -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.timeInMillis
            }
        }

        val previousPeriodEvents = allEvents.filter { it.timestamp in startPreviousTime until endPreviousTime }.sortedBy { it.timestamp }
        val previousEfficiency = FuelCalculator.measureEfficiency(previousPeriodEvents) ?: return null

        if (previousEfficiency == 0.0) return null
        
        return ((currentEfficiency - previousEfficiency) / previousEfficiency) * 100.0
    }
}
