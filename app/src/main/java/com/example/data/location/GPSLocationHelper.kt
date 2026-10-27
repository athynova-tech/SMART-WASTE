package com.example.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class HighPrecisionLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val altitudeMeters: Double = 0.0,
    val speedKmh: Float = 0f,
    val bearing: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val provider: String = "GPS/Fused"
) {
    val latLng: LatLng get() = LatLng(latitude, longitude)
    val formattedCoordinates: String get() = String.format("%.6f, %.6f", latitude, longitude)
    val formattedAccuracy: String get() = String.format("±%.1fm", accuracyMeters)
}

object GPSLocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    @SuppressLint("MissingPermission")
    fun getHighPrecisionLocationUpdates(
        context: Context,
        intervalMillis: Long = 3000L,
        fastestIntervalMillis: Long = 1500L
    ): Flow<HighPrecisionLocation> = callbackFlow {
        if (!hasLocationPermission(context)) {
            // Default fallback centered in Kigamboni, Dar es Salaam with high precision
            trySend(
                HighPrecisionLocation(
                    latitude = -6.852341,
                    longitude = 39.325120,
                    accuracyMeters = 3.2f,
                    provider = "Simulated High Precision"
                )
            )
            close()
            return@callbackFlow
        }

        val fusedClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMillis)
            .setMinUpdateIntervalMillis(fastestIntervalMillis)
            .setWaitForAccurateLocation(true)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (loc in result.locations) {
                    trySend(
                        HighPrecisionLocation(
                            latitude = loc.latitude,
                            longitude = loc.longitude,
                            accuracyMeters = if (loc.hasAccuracy()) loc.accuracy else 4.0f,
                            altitudeMeters = if (loc.hasAltitude()) loc.altitude else 0.0,
                            speedKmh = if (loc.hasSpeed()) loc.speed * 3.6f else 0f,
                            bearing = if (loc.hasBearing()) loc.bearing else 0f,
                            timestamp = loc.time,
                            provider = loc.provider ?: "FusedLocation"
                        )
                    )
                }
            }
        }

        fusedClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())

        awaitClose {
            fusedClient.removeLocationUpdates(callback)
        }
    }

    /**
     * Calculates distance in meters between two high-precision geographic coordinates
     */
    fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Formats distance gracefully in meters or kilometers
     */
    fun formatDistance(meters: Double): String {
        return if (meters < 1000) {
            String.format("%.0f m", meters)
        } else {
            String.format("%.2f km", meters / 1000.0)
        }
    }

    /**
     * Interpolates intermediate GPS points between two coordinates for smooth truck animation
     */
    fun interpolate(start: LatLng, end: LatLng, fraction: Float): LatLng {
        val lat = start.latitude + (end.latitude - start.latitude) * fraction
        val lng = start.longitude + (end.longitude - start.longitude) * fraction
        return LatLng(lat, lng)
    }
}
