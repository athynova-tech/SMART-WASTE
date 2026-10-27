package com.example.data.location

import android.content.Context
import com.example.data.model.GeoLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class LocationRepository(
    val locationService: LocationService = AndroidFusedLocationService(),
    val mapService: MapService = GoogleMapsProvider(),
    val alternativeMapService: MapService = OpenStreetMapProvider(),
    val routingService: RoutingService = GoogleRoutesProvider(),
    val geocodingService: GeocodingService = AndroidSystemGeocodingProvider()
) {
    fun hasPermission(context: Context): Boolean {
        return locationService.hasLocationPermission(context)
    }

    fun observeLocation(context: Context): Flow<HighPrecisionLocation> {
        return locationService.observeLocation(context)
    }

    suspend fun getCurrentLocation(context: Context): HighPrecisionLocation {
        return locationService.getCurrentLocation(context)
    }

    fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        return locationService.calculateDistanceMeters(lat1, lon1, lat2, lon2)
    }

    fun formatDistance(meters: Double): String {
        return locationService.formatDistance(meters)
    }

    suspend fun calculateRoute(origin: GeoLocation, destination: GeoLocation): RouteCalculationResult {
        return routingService.calculateRoute(origin, destination)
    }

    suspend fun reverseGeocode(lat: Double, lng: Double): String {
        return geocodingService.reverseGeocode(lat, lng)
    }
}

class AndroidFusedLocationService : LocationService {
    override fun hasLocationPermission(context: Context): Boolean {
        return GPSLocationHelper.hasLocationPermission(context)
    }

    override fun observeLocation(context: Context, intervalMillis: Long): Flow<HighPrecisionLocation> {
        return GPSLocationHelper.getHighPrecisionLocationUpdates(context, intervalMillis)
    }

    override suspend fun getCurrentLocation(context: Context): HighPrecisionLocation {
        return GPSLocationHelper.getHighPrecisionLocationUpdates(context, 1000L).first()
    }

    override fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        return GPSLocationHelper.calculateDistanceMeters(lat1, lon1, lat2, lon2)
    }

    override fun formatDistance(meters: Double): String {
        return GPSLocationHelper.formatDistance(meters)
    }
}
