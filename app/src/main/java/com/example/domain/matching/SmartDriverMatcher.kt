package com.example.domain.matching

import com.example.data.local.CollectionRequestEntity
import com.example.data.local.DriverApplicationEntity
import com.example.data.model.MatchedDriverRecommendation
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

object SmartDriverMatcher {

    /**
     * Algorithmic Driver Matching Engine
     * Calculates suitability score for each approved driver based on distance,
     * available vehicle capacity, current route/zone, and driver performance rating.
     */
    fun findBestDriverMatches(
        request: CollectionRequestEntity,
        approvedDrivers: List<DriverApplicationEntity>
    ): List<MatchedDriverRecommendation> {
        val scoredList = approvedDrivers.mapNotNull { driver ->
            // Hard constraint: Must be approved and not suspended
            if (driver.status != com.example.data.model.DriverApplicationStatus.APPROVED) {
                return@mapNotNull null
            }

            val remainingCapacityKg = (driver.vehicleCapacityKg - driver.currentLoadKg).coerceAtLeast(0)

            // Hard constraint: Vehicle must have capacity for the requested waste quantity
            if (remainingCapacityKg < request.quantityKg) {
                return@mapNotNull null
            }

            // Calculate Haversine distance in kilometers
            val distanceKm = calculateHaversineDistanceKm(
                driver.currentLat, driver.currentLng,
                request.latitude, request.longitude
            )

            // Distance score (0-40 points): max 40 points if < 1km, degrades over 20km
            val distanceScore = (40.0 - (distanceKm * 2.0)).coerceIn(5.0, 40.0)

            // Capacity score (0-30 points): drivers with high remaining capacity score better
            val capacityRatio = remainingCapacityKg.toDouble() / driver.vehicleCapacityKg.toDouble()
            val capacityScore = (capacityRatio * 30.0).coerceIn(5.0, 30.0)

            // Zone compatibility score (0-20 points)
            val isSameZone = driver.preferredZone.contains(request.ward, ignoreCase = true) ||
                    driver.preferredZone.contains(request.district, ignoreCase = true) ||
                    driver.preferredZone.contains("All", ignoreCase = true)
            val zoneScore = if (isSameZone) 20.0 else 8.0

            // Performance & Rating score (0-10 points)
            val ratingScore = (driver.rating / 5.0) * 10.0

            val totalScorePercent = (distanceScore + capacityScore + zoneScore + ratingScore).roundToInt().coerceIn(10, 99)

            val etaMinutes = ((distanceKm / 25.0) * 60.0 + 5.0).roundToInt().coerceAtLeast(4)

            val reasons = mutableListOf<String>()
            reasons.add(String.format("%.1f km from resident", distanceKm))
            reasons.add(String.format("%d kg capacity remaining (Load: %d/%d kg)", remainingCapacityKg, driver.currentLoadKg, driver.vehicleCapacityKg))
            if (isSameZone) {
                reasons.add("Assigned to ${request.ward} sector")
            } else {
                reasons.add("Available for cross-zone dispatch")
            }
            reasons.add(String.format("⭐ %.1f rating (%d completed trips)", driver.rating, driver.completedTrips))

            MatchedDriverRecommendation(
                driverId = driver.assignedDriverId ?: driver.id,
                driverName = driver.fullName,
                phone = driver.phone,
                vehicleRegNumber = driver.vehicleRegNumber,
                vehicleType = driver.vehicleType,
                capacityKg = driver.vehicleCapacityKg,
                currentLoadKg = driver.currentLoadKg,
                remainingCapacityKg = remainingCapacityKg,
                distanceKm = (distanceKm * 10.0).roundToInt() / 10.0,
                etaMinutes = etaMinutes,
                matchScorePercent = totalScorePercent,
                currentZone = driver.preferredZone,
                ratingScore = driver.rating,
                recommendationReasons = reasons
            )
        }

        return scoredList.sortedByDescending { it.matchScorePercent }
    }

    private fun calculateHaversineDistanceKm(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
