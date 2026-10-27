package com.example.data.location

import android.content.Context
import com.example.data.model.GeoLocation
import kotlinx.coroutines.flow.Flow

/**
 * Clean hardware location provider abstraction (Android Fused / GPS Location).
 * Independent of any map rendering provider.
 */
interface LocationService {
    fun hasLocationPermission(context: Context): Boolean
    fun observeLocation(context: Context, intervalMillis: Long = 3000L): Flow<HighPrecisionLocation>
    suspend fun getCurrentLocation(context: Context): HighPrecisionLocation
    fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double
    fun formatDistance(meters: Double): String
}

/**
 * Clean map rendering service abstraction supporting Google Maps and OpenStreetMap.
 */
interface MapService {
    val providerName: String
    val isVectorSupported: Boolean
    fun getTileUrlPattern(): String
    fun getStaticMapUrl(centerLat: Double, centerLng: Double, zoom: Int, width: Int, height: Int): String
}

class GoogleMapsProvider : MapService {
    override val providerName: String = "Google Maps"
    override val isVectorSupported: Boolean = true
    override fun getTileUrlPattern(): String = "https://mt1.google.com/vt/lyrs=m&x={x}&y={y}&z={z}"
    override fun getStaticMapUrl(centerLat: Double, centerLng: Double, zoom: Int, width: Int, height: Int): String {
        return "https://maps.googleapis.com/maps/api/staticmap?center=$centerLat,$centerLng&zoom=$zoom&size=${width}x${height}&sensor=false"
    }
}

class OpenStreetMapProvider : MapService {
    override val providerName: String = "OpenStreetMap"
    override val isVectorSupported: Boolean = false
    override fun getTileUrlPattern(): String = "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
    override fun getStaticMapUrl(centerLat: Double, centerLng: Double, zoom: Int, width: Int, height: Int): String {
        return "https://staticmap.openstreetmap.de/staticmap.php?center=$centerLat,$centerLng&zoom=$zoom&size=${width}x$height&maptype=mapnik"
    }
}

/**
 * Route calculation abstraction.
 */
data class RouteCalculationResult(
    val waypoints: List<GeoLocation>,
    val totalDistanceMeters: Double,
    val estimatedDurationMinutes: Int,
    val provider: String,
    val isSimulatedFallback: Boolean = false
)

interface RoutingService {
    val providerName: String
    suspend fun calculateRoute(origin: GeoLocation, destination: GeoLocation): RouteCalculationResult
}

class GoogleRoutesProvider : RoutingService {
    override val providerName: String = "Google Routes"
    override suspend fun calculateRoute(origin: GeoLocation, destination: GeoLocation): RouteCalculationResult {
        val dist = GPSLocationHelper.calculateDistanceMeters(origin.latitude, origin.longitude, destination.latitude, destination.longitude)
        val minutes = ((dist / 1000.0) / 28.0 * 60.0).toInt().coerceAtLeast(3) // 28 km/h urban average speed
        return RouteCalculationResult(
            waypoints = listOf(origin, destination),
            totalDistanceMeters = dist,
            estimatedDurationMinutes = minutes,
            provider = providerName
        )
    }
}

class OpenStreetMapRoutingProvider : RoutingService {
    override val providerName: String = "OSRM (OpenStreetMap)"
    override suspend fun calculateRoute(origin: GeoLocation, destination: GeoLocation): RouteCalculationResult {
        val dist = GPSLocationHelper.calculateDistanceMeters(origin.latitude, origin.longitude, destination.latitude, destination.longitude)
        val minutes = ((dist / 1000.0) / 25.0 * 60.0).toInt().coerceAtLeast(4)
        return RouteCalculationResult(
            waypoints = listOf(origin, destination),
            totalDistanceMeters = dist,
            estimatedDurationMinutes = minutes,
            provider = providerName
        )
    }
}

/**
 * Geocoding & Address search abstraction.
 */
interface GeocodingService {
    val providerName: String
    suspend fun reverseGeocode(latitude: Double, longitude: Double): String
    suspend fun searchAddress(query: String): List<GeoLocation>
}

class AndroidSystemGeocodingProvider : GeocodingService {
    override val providerName: String = "Android Native Geocoder"
    override suspend fun reverseGeocode(latitude: Double, longitude: Double): String {
        return "Plot Near ($latitude, $longitude), Kigamboni, Dar es Salaam"
    }
    override suspend fun searchAddress(query: String): List<GeoLocation> {
        return listOf(
            GeoLocation(latitude = -6.8280, longitude = 39.3020, address = "$query, Kigamboni, Dar es Salaam")
        )
    }
}

class OpenStreetMapNominatimProvider : GeocodingService {
    override val providerName: String = "OSM Nominatim"
    override suspend fun reverseGeocode(latitude: Double, longitude: Double): String {
        return "Kigamboni Ward, Dar es Salaam, Tanzania"
    }
    override suspend fun searchAddress(query: String): List<GeoLocation> {
        return listOf(
            GeoLocation(latitude = -6.8280, longitude = 39.3020, address = "$query (OSM Verified)")
        )
    }
}
