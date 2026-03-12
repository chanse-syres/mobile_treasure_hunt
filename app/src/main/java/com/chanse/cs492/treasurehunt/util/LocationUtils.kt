package com.chanse.cs492.treasurehunt.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// Provides location-related helper calculations.
object LocationUtils {
    // Calculates the distance in meters between two latitude and longitude points.
    fun haversineMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        // Uses the Earth's average radius in meters.
        val earthRadiusMeters = 6_371_000.0

        // Converts latitude difference to radians.
        val dLat = Math.toRadians(lat2 - lat1)
        // Converts longitude difference to radians.
        val dLon = Math.toRadians(lon2 - lon1)

        // Computes the haversine formula intermediate value.
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        // Converts the intermediate value into angular distance.
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusMeters * c
    }
}