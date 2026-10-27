package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.DisposalFacilityEntity
import com.example.data.location.GPSLocationHelper
import com.example.data.location.HighPrecisionLocation
import com.example.ui.theme.EcoGreenLight
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

data class WasteCollectionPoint(
    val id: String,
    val name: String,
    val category: String, // "RECYCLING", "COMPOSTING", "TRANSFER_HUB", "BUY_BACK", "LANDFILL"
    val address: String,
    val zone: String,
    val latitude: Double,
    val longitude: Double,
    val openHours: String,
    val acceptedMaterials: List<String>,
    val contactPhone: String,
    val markerHue: Float
)

/**
 * High-Precision Google Map View for Residents:
 * - Real-time tracking of assigned collection truck with moving marker, accuracy radius, and optimized pickup route polyline.
 * - Interactive waste collection points & recycling stations discovery across Dar es Salaam / Kigamboni.
 * - Tap-to-inspect facility details with directions and doorstep pickup ordering.
 */
@Composable
fun ResidentLiveTrackingGoogleMapView(
    request: CollectionRequestEntity? = null,
    facilities: List<DisposalFacilityEntity> = emptyList(),
    gpsLocation: HighPrecisionLocation? = null,
    onOpenChat: () -> Unit = {},
    onSimulateDriverStep: () -> Unit = {},
    onUpdatePrecisionPin: (lat: Double, lng: Double) -> Unit = { _, _ -> },
    onRequestPickupClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Default Resident LatLng (Kigamboni Center or GPS or Request location)
    val residentLat = request?.latitude ?: gpsLocation?.latitude ?: -6.8235
    val residentLng = request?.longitude ?: gpsLocation?.longitude ?: 39.2695
    val residentLatLng = remember(residentLat, residentLng) {
        LatLng(residentLat, residentLng)
    }

    // Driver LatLng
    val driverLat = request?.driverCurrentLat ?: (residentLat + 0.015)
    val driverLng = request?.driverCurrentLng ?: (residentLng - 0.012)
    val driverLatLng = LatLng(driverLat, driverLng)

    // Calculate real-time distance and ETA from driver to resident
    val distanceMeters = remember(driverLat, driverLng, residentLat, residentLng) {
        GPSLocationHelper.calculateDistanceMeters(driverLat, driverLng, residentLat, residentLng)
    }
    val formattedDistance = remember(distanceMeters) {
        GPSLocationHelper.formatDistance(distanceMeters)
    }
    val dynamicEtaMin = remember(distanceMeters) {
        ((distanceMeters / 1000.0 / 25.0) * 60.0).toInt().coerceAtLeast(1)
    }

    // Map UI and properties configuration
    var selectedMapType by remember { mutableStateOf(MapType.NORMAL) }
    var isVectorRadarMode by remember { mutableStateOf(false) } // Default to real Google Map!
    var selectedPointId by remember { mutableStateOf<String?>(null) }
    var filterCategory by remember { mutableStateOf("ALL") }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(residentLatLng, if (request != null) 15f else 13.5f)
    }

    androidx.compose.runtime.LaunchedEffect(residentLat, residentLng) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(residentLatLng, if (request != null) 15f else 13.5f),
            700
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = true,
            myLocationButtonEnabled = false,
            mapToolbarEnabled = true,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true,
            tiltGesturesEnabled = true,
            zoomGesturesEnabled = true
        )
    }

    val mapProperties by remember(selectedMapType) {
        derivedStateOf {
            MapProperties(
                mapType = selectedMapType,
                isBuildingEnabled = true,
                isTrafficEnabled = true
            )
        }
    }

    // Municipal Waste Collection Points Network
    val collectionPoints = remember(facilities) {
        val points = mutableListOf(
            WasteCollectionPoint(
                id = "cp_kigamboni_hub",
                name = "Kigamboni Transfer & Sorting Hub",
                category = "TRANSFER_HUB",
                address = "Tuangoma Road, Kigamboni Ward 2",
                zone = "Kigamboni Central",
                latitude = -6.8320,
                longitude = 39.3100,
                openHours = "06:00 - 20:00 Daily",
                acceptedMaterials = listOf("Recyclables", "Sorted Household", "Cardboard", "Plastics"),
                contactPhone = "+255 22 289 1001",
                markerHue = BitmapDescriptorFactory.HUE_ORANGE
            ),
            WasteCollectionPoint(
                id = "cp_mjimwema_compost",
                name = "Mjimwema Community Composting Center",
                category = "COMPOSTING",
                address = "Beach Road, Mjimwema Village",
                zone = "Mjimwema",
                latitude = -6.8500,
                longitude = 39.3400,
                openHours = "07:00 - 17:00 (Mon - Sat)",
                acceptedMaterials = listOf("Organic Food Waste", "Market Greens", "Garden & Yard Trimmings"),
                contactPhone = "+255 22 289 1002",
                markerHue = BitmapDescriptorFactory.HUE_GREEN
            ),
            WasteCollectionPoint(
                id = "cp_tuangoma_mrf",
                name = "Tuangoma Material Recovery & Recycling Facility",
                category = "RECYCLING",
                address = "Kilwa Highway Corridor, Tuangoma",
                zone = "Tuangoma",
                latitude = -6.8800,
                longitude = 39.3000,
                openHours = "08:00 - 18:00 (Mon - Sat)",
                acceptedMaterials = listOf("PET Plastics", "HDPE Bottles", "Aluminium Cans", "E-Waste"),
                contactPhone = "+255 22 289 1003",
                markerHue = BitmapDescriptorFactory.HUE_AZURE
            ),
            WasteCollectionPoint(
                id = "cp_ferry_dropoff",
                name = "Ferry Waterfront Public Drop-Off Station",
                category = "TRANSFER_HUB",
                address = "Kigamboni Ferry Terminal Approach",
                zone = "Ferry Waterfront",
                latitude = -6.8210,
                longitude = 39.2980,
                openHours = "24/7 Smart Bins Access",
                acceptedMaterials = listOf("Mixed Sorted Household", "PET Bottles", "Beverage Cartons"),
                contactPhone = "+255 22 289 1004",
                markerHue = BitmapDescriptorFactory.HUE_ORANGE
            ),
            WasteCollectionPoint(
                id = "cp_gezaulole_buyback",
                name = "Gezaulole Community Buy-Back Center",
                category = "BUY_BACK",
                address = "Geza Ulole Main St, near Dispensary",
                zone = "Geza Ulole",
                latitude = -6.8920,
                longitude = 39.3650,
                openHours = "08:30 - 16:30 Daily",
                acceptedMaterials = listOf("Cardboard", "PET", "High-Density Plastics", "Scrap Metal"),
                contactPhone = "+255 22 289 1005",
                markerHue = BitmapDescriptorFactory.HUE_CYAN
            ),
            WasteCollectionPoint(
                id = "fac_pugu_01",
                name = "Pugu Kinyamwezi Integrated Sanitary Landfill",
                category = "LANDFILL",
                address = "Pugu Kinyamwezi Heavy Industrial Zone",
                zone = "Ilala - Pugu",
                latitude = -6.9120,
                longitude = 39.1450,
                openHours = "24/7 Municipal Operations",
                acceptedMaterials = listOf("Heavy Municipal Waste", "Non-Recyclable Residue", "Industrial"),
                contactPhone = "+255 22 284 9900",
                markerHue = BitmapDescriptorFactory.HUE_RED
            )
        )

        // Merge any dynamic facilities from database
        facilities.forEach { fac ->
            if (points.none { it.id == fac.id }) {
                points.add(
                    WasteCollectionPoint(
                        id = fac.id,
                        name = fac.name,
                        category = if (fac.name.contains("Compost", ignoreCase = true)) "COMPOSTING" else "LANDFILL",
                        address = fac.zone,
                        zone = fac.zone,
                        latitude = fac.latitude,
                        longitude = fac.longitude,
                        openHours = "06:00 - 20:00",
                        acceptedMaterials = listOf("Municipal Waste", "Bulk Materials"),
                        contactPhone = fac.contactPhone,
                        markerHue = BitmapDescriptorFactory.HUE_MAGENTA
                    )
                )
            }
        }
        points
    }

    // Filter points by category
    val filteredPoints = remember(collectionPoints, filterCategory) {
        if (filterCategory == "ALL") {
            collectionPoints
        } else {
            collectionPoints.filter { it.category == filterCategory }
        }
    }

    // Selected Collection Point
    val selectedPoint = remember(filteredPoints, selectedPointId) {
        filteredPoints.find { it.id == selectedPointId }
    }

    // Connective Route Polyline:
    // Case 1: When request != null -> Driver to Resident optimized pickup polyline
    // Case 2: When selectedPoint != null -> Resident to Selected Facility directions polyline
    val routePoints: List<LatLng> = remember(request, driverLatLng, residentLatLng, selectedPoint) {
        if (request != null) {
            val mid1 = LatLng(
                driverLatLng.latitude + (residentLatLng.latitude - driverLatLng.latitude) * 0.35 + 0.0012,
                driverLatLng.longitude + (residentLatLng.longitude - driverLatLng.longitude) * 0.35 - 0.0008
            )
            val mid2 = LatLng(
                driverLatLng.latitude + (residentLatLng.latitude - driverLatLng.latitude) * 0.70 - 0.0006,
                driverLatLng.longitude + (residentLatLng.longitude - driverLatLng.longitude) * 0.70 + 0.0005
            )
            listOf(driverLatLng, mid1, mid2, residentLatLng)
        } else if (selectedPoint != null) {
            val target = LatLng(selectedPoint.latitude, selectedPoint.longitude)
            val mid = LatLng(
                residentLatLng.latitude + (target.latitude - residentLatLng.latitude) * 0.5 + 0.0010,
                residentLatLng.longitude + (target.longitude - residentLatLng.longitude) * 0.5 - 0.0010
            )
            listOf(residentLatLng, mid, target)
        } else {
            emptyList()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, EcoGreenPrimary)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Top Header Bar: Active Pickup Telemetry OR Waste Network Header
            Surface(
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (request != null) {
                        // ACTIVE COLLECTION HUD
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(EcoGreenPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GpsFixed,
                                        contentDescription = "GPS Radar",
                                        tint = EcoGreenPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "LIVE TRUCK TELEMETRY & ROUTE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        color = Color(0xFF6EE7B7)
                                    )
                                    Text(
                                        text = "Tracking: ${request.trackingId}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Dynamic ETA Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EcoGreenPrimary)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.NearMe,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "ETA ~$dynamicEtaMin min",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Real-time Precision Readout Strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Your Pin: ${String.format("%.5f, %.5f", residentLat, residentLng)}",
                                    fontSize = 11.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    color = Color(0xFFE2E8F0)
                                )
                            }

                            Text(
                                text = "Truck: $formattedDistance away",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    } else {
                        // EXPLORE COLLECTION POINTS HUD
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(MunicipalBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MunicipalBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "MUNICIPAL WASTE & RECYCLING NETWORK",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp,
                                        color = Color(0xFF93C5FD)
                                    )
                                    Text(
                                        text = "Waste Collection & Drop-Off Points",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${filteredPoints.size} Facilities",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 2. Filter Strip & Map Mode Selector Bar
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Filter Pills
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CategoryPill(
                            label = "All Points (${collectionPoints.size})",
                            isSelected = filterCategory == "ALL",
                            onClick = { filterCategory = "ALL" }
                        )
                        CategoryPill(
                            label = "♻️ Recycling",
                            isSelected = filterCategory == "RECYCLING",
                            onClick = { filterCategory = "RECYCLING" }
                        )
                        CategoryPill(
                            label = "🌿 Composting",
                            isSelected = filterCategory == "COMPOSTING",
                            onClick = { filterCategory = "COMPOSTING" }
                        )
                        CategoryPill(
                            label = "📦 Transfer Hub",
                            isSelected = filterCategory == "TRANSFER_HUB",
                            onClick = { filterCategory = "TRANSFER_HUB" }
                        )
                        CategoryPill(
                            label = "💵 Buy-Back",
                            isSelected = filterCategory == "BUY_BACK",
                            onClick = { filterCategory = "BUY_BACK" }
                        )
                        CategoryPill(
                            label = "🏗️ Landfill",
                            isSelected = filterCategory == "LANDFILL",
                            onClick = { filterCategory = "LANDFILL" }
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Engine Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!isVectorRadarMode) MunicipalBlue.copy(alpha = 0.2f) else Color(0xFF0F172A))
                                .border(1.dp, if (!isVectorRadarMode) MunicipalBlue else Slate600, RoundedCornerShape(6.dp))
                                .clickable { isVectorRadarMode = false }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Google Map (Active)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isVectorRadarMode) Color(0xFF38BDF8) else Slate400
                            )
                        }

                        if (!isVectorRadarMode) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, Slate600, RoundedCornerShape(6.dp))
                                    .clickable {
                                        selectedMapType = if (selectedMapType == MapType.NORMAL) MapType.HYBRID else MapType.NORMAL
                                    }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (selectedMapType == MapType.NORMAL) "Satellite" else "Normal",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFA5B4FC)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isVectorRadarMode) EcoGreenPrimary.copy(alpha = 0.2f) else Color(0xFF0F172A))
                                .border(1.dp, if (isVectorRadarMode) EcoGreenPrimary else Slate600, RoundedCornerShape(6.dp))
                                .clickable { isVectorRadarMode = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "GIS Radar",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVectorRadarMode) EcoGreenPrimary else Color.White
                            )
                        }
                    }
                }
            }

            // 3. Interactive Map Canvas Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
            ) {
                if (isVectorRadarMode) {
                    val activeDriverList = remember(request) {
                        if (request != null) {
                            listOf(
                                com.example.data.local.DriverApplicationEntity(
                                    id = "active_driver",
                                    userId = "active_driver",
                                    fullName = request.assignedDriverName ?: "Municipal Driver",
                                    phone = "+255 714 000 111",
                                    email = "driver@kigamboni.go.tz",
                                    nidaMasked = "19900101-****-01",
                                    licenseNumber = "DL-TZ-8841",
                                    licenseClass = "Class C (Heavy)",
                                    licenseExpiryDate = "2028-12-31",
                                    vehicleRegNumber = request.vehicleRegNumber ?: "T 123 ABC",
                                    vehicleType = "Medium Compactor Truck",
                                    vehicleCapacityKg = 3500,
                                    insuranceExpiryDate = "2027-12-31",
                                    preferredZone = "Kigamboni Ward",
                                    currentLat = driverLat,
                                    currentLng = driverLng
                                )
                            )
                        } else emptyList()
                    }
                    InteractiveMapCanvas(
                        requests = if (request != null) listOf(request) else emptyList(),
                        drivers = activeDriverList,
                        facilities = facilities,
                        selectedRequestId = request?.id,
                        onSelectFacility = { fac ->
                            selectedPointId = fac.id
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = mapProperties,
                        uiSettings = mapUiSettings
                    ) {
                        // User / Resident Precision Location Circle & Marker
                        Circle(
                            center = residentLatLng,
                            radius = 25.0,
                            fillColor = EcoGreenPrimary.copy(alpha = 0.20f),
                            strokeColor = EcoGreenPrimary,
                            strokeWidth = 2f
                        )

                        val residentMarkerState = remember { MarkerState(position = residentLatLng) }
                        residentMarkerState.position = residentLatLng
                        Marker(
                            state = residentMarkerState,
                            title = if (request != null) "Your Pickup Spot" else "Your Location",
                            snippet = request?.pickupAddress ?: "Kigamboni Municipal Ward",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                        )

                        // If there is an active pickup in transit:
                        if (request != null) {
                            // Driver Truck Marker
                            val driverMarkerState = remember { MarkerState(position = driverLatLng) }
                            driverMarkerState.position = driverLatLng
                            Marker(
                                state = driverMarkerState,
                                title = "Driver: ${request.assignedDriverName ?: "Municipal Fleet"}",
                                snippet = "Truck ${request.vehicleRegNumber ?: "T 123 ABC"} • Distance: $formattedDistance",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                            )

                            Circle(
                                center = driverLatLng,
                                radius = 35.0,
                                fillColor = MunicipalBlue.copy(alpha = 0.15f),
                                strokeColor = MunicipalBlue,
                                strokeWidth = 2f
                            )
                        }

                        // Waste Collection Points Markers
                        filteredPoints.forEach { point ->
                            val pointLatLng = LatLng(point.latitude, point.longitude)
                            val ptMarkerState = remember(point.id) { MarkerState(position = pointLatLng) }
                            ptMarkerState.position = pointLatLng
                            Marker(
                                state = ptMarkerState,
                                title = point.name,
                                snippet = "${point.openHours} • ${point.acceptedMaterials.joinToString(", ")}",
                                icon = BitmapDescriptorFactory.defaultMarker(point.markerHue),
                                onClick = {
                                    selectedPointId = point.id
                                    true
                                }
                            )
                        }

                        // Connective Route Polyline (Driver -> Resident OR Resident -> Selected Facility)
                        if (routePoints.size >= 2) {
                            Polyline(
                                points = routePoints,
                                color = if (request != null) Color(0xFF10B981) else Color(0xFF38BDF8),
                                width = 9f
                            )
                        }
                    }

                    // Floating Map Controls Toolbar
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Center My Location
                        FloatingMapControl(
                            icon = Icons.Default.MyLocation,
                            contentDescription = "My Location",
                            onClick = {
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(residentLatLng, 15f),
                                        700
                                    )
                                }
                            }
                        )

                        // Center Driver Truck (if active)
                        if (request != null) {
                            FloatingMapControl(
                                icon = Icons.Default.LocalShipping,
                                contentDescription = "Track Driver Truck",
                                tint = MunicipalBlue,
                                onClick = {
                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(driverLatLng, 16f),
                                            700
                                        )
                                    }
                                }
                            )

                            // Fit Route In View
                            FloatingMapControl(
                                icon = Icons.Default.Directions,
                                contentDescription = "Fit Route Bounds",
                                onClick = {
                                    coroutineScope.launch {
                                        val bounds = LatLngBounds.builder()
                                            .include(residentLatLng)
                                            .include(driverLatLng)
                                            .build()
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngBounds(bounds, 80),
                                            700
                                        )
                                    }
                                }
                            )
                        }

                        // Toggle Satellite / Normal Map
                        FloatingMapControl(
                            icon = Icons.Default.Layers,
                            contentDescription = "Toggle Map Type",
                            onClick = {
                                selectedMapType = if (selectedMapType == MapType.NORMAL) MapType.HYBRID else MapType.NORMAL
                            }
                        )
                    }

                    // Selected Collection Point Quick Card Overlay (Bottom / Floating)
                    if (selectedPoint != null) {
                        val pt = selectedPoint
                        val distMeters = GPSLocationHelper.calculateDistanceMeters(residentLat, residentLng, pt.latitude, pt.longitude)
                        val distText = GPSLocationHelper.formatDistance(distMeters)
                        val driveTimeMin = ((distMeters / 1000.0 / 25.0) * 60.0).toInt().coerceAtLeast(2)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(10.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.96f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenPrimary)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pt.name,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${pt.zone} • $distText (~$driveTimeMin min drive)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF38BDF8)
                                        )
                                    }

                                    IconButton(
                                        onClick = { selectedPointId = null },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "🕒 ${pt.openHours} • Accepts: ${pt.acceptedMaterials.joinToString(", ")}",
                                    fontSize = 10.sp,
                                    color = Color(0xFFCBD5E1)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            // Launch Google Maps Turn-by-Turn Navigation Intent
                                            val uri = Uri.parse("google.navigation:q=${pt.latitude},${pt.longitude}")
                                            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                                setPackage("com.google.android.apps.maps")
                                            }
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${pt.latitude},${pt.longitude}")
                                                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(36.dp)
                                    ) {
                                        Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Directions", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }

                                    Button(
                                        onClick = onRequestPickupClick,
                                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.2f).height(36.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Request Pickup", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Bottom Coordination HUD (When Active Request) OR Quick Info (When Browsing)
            if (request != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MunicipalBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalShipping,
                                    contentDescription = null,
                                    tint = MunicipalBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = request.assignedDriverName ?: "Juma Ali Mussa",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Text(
                                    text = "Truck: ${request.vehicleRegNumber ?: "T 123 ABC"} • Kigamboni Unit",
                                    fontSize = 11.sp,
                                    color = Slate600
                                )
                            }
                        }

                        // Direct Call & Live Chat Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+255714000111"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EcoGreenLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call Driver",
                                    tint = EcoGreenPrimary,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            Button(
                                onClick = onOpenChat,
                                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Slate200)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress & Simulation Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pickup Spot: ${request.pickupAddress}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate800,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${request.wasteCategory.name} • ${request.quantityKg} kg (${request.bagCount} bags)",
                                fontSize = 10.sp,
                                color = Slate600
                            )
                        }

                        OutlinedButton(
                            onClick = onSimulateDriverStep,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MunicipalBlue),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Simulate GPS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Quick Doorstep Pickup Call-to-action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Need Doorstep Waste Collection?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Book on-demand household or organic collection",
                            fontSize = 10.sp,
                            color = Slate600
                        )
                    }

                    Button(
                        onClick = onRequestPickupClick,
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Book Pickup", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) EcoGreenPrimary.copy(alpha = 0.22f) else Color(0xFF0F172A))
            .border(
                1.dp,
                if (isSelected) EcoGreenPrimary else Color(0xFF334155),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) EcoGreenPrimary else Color(0xFFE2E8F0)
        )
    }
}

@Composable
private fun FloatingMapControl(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tint: Color = Color(0xFFE2E8F0),
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = Color(0xFF0F172A).copy(alpha = 0.90f),
        shadowElevation = 3.dp,
        modifier = Modifier.size(36.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
