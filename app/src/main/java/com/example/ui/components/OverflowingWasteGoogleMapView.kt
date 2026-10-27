package com.example.ui.components

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
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PersonPinCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.ComplaintEntity
import com.example.data.local.DisposalFacilityEntity
import com.example.data.local.DriverApplicationEntity
import com.example.data.location.GPSLocationHelper
import com.example.data.model.CollectionStatus
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
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

sealed class MapInspectorTarget {
    data class Driver(val driver: DriverApplicationEntity, val activeRequest: CollectionRequestEntity?) : MapInspectorTarget()
    data class ResidentRequest(val request: CollectionRequestEntity) : MapInspectorTarget()
    data class OverflowIncident(val pin: OverflowIncidentPin) : MapInspectorTarget()
    data class Facility(val facility: DisposalFacilityEntity) : MapInspectorTarget()
}

data class OverflowIncidentPin(
    val id: String,
    val complaintId: String,
    val ticketNumber: String,
    val ward: String,
    val landmark: String,
    val latitude: Double,
    val longitude: Double,
    val severity: OverflowSeverityLevel,
    val status: String,
    val description: String,
    val citizenName: String,
    val reportedAt: Long
)

enum class OverflowSeverityLevel(
    val title: String,
    val hue: Float,
    val color: Color,
    val priorityScore: Int
) {
    CRITICAL_HAZARD("Critical Hazard", BitmapDescriptorFactory.HUE_RED, Color(0xFFDC2626), 3),
    SEVERE_OVERFLOW("Severe Overflow", BitmapDescriptorFactory.HUE_ORANGE, Color(0xFFEA580C), 2),
    MILD_OVERFILL("Mild Overfill", BitmapDescriptorFactory.HUE_YELLOW, Color(0xFFD97706), 1),
    RESOLVED("Resolved / Cleared", BitmapDescriptorFactory.HUE_GREEN, Color(0xFF16A34A), 0)
}

data class WardPreset(
    val name: String,
    val center: LatLng,
    val zoom: Float = 14f
)

val KIGAMBONI_WARD_PRESETS = listOf(
    WardPreset("All Kigamboni", LatLng(-6.8550, 39.3250), 12.5f),
    WardPreset("Kibada", LatLng(-6.8520, 39.3250), 14.8f),
    WardPreset("Ferry Terminal", LatLng(-6.8280, 39.3020), 15.2f),
    WardPreset("Somangila", LatLng(-6.8780, 39.3600), 14.5f),
    WardPreset("Vijibweni", LatLng(-6.8390, 39.3110), 14.8f),
    WardPreset("Tuangoma", LatLng(-6.9020, 39.3450), 14.2f),
    WardPreset("Kisarawe II", LatLng(-6.9350, 39.3820), 14f)
)

/**
 * High-Precision Google Maps GIS Radar for Municipal Commissioner:
 * Renders ALL Active Drivers, ALL Resident Pickups, Overflow Incidents & Disposal Facilities with high precision.
 */
@Composable
fun OverflowingWasteGoogleMapView(
    complaints: List<ComplaintEntity>,
    requests: List<CollectionRequestEntity> = emptyList(),
    drivers: List<DriverApplicationEntity> = emptyList(),
    facilities: List<DisposalFacilityEntity> = emptyList(),
    onResolveComplaint: (complaintId: String, notes: String) -> Unit = { _, _ -> },
    onDispatchRapidSweep: (incidentPin: OverflowIncidentPin) -> Unit = {},
    onSelectRequestForDispatch: (CollectionRequestEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val defaultLocation = remember { LatLng(-6.8550, 39.3250) }
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12.8f)
    }

    var selectedMapType by remember { mutableStateOf(MapType.NORMAL) }
    var isVectorRadarMode by remember { mutableStateOf(false) }
    var selectedWardFilter by remember { mutableStateOf("All Kigamboni") }
    var showDriversLayer by remember { mutableStateOf(true) }
    var showResidentsLayer by remember { mutableStateOf(true) }
    var showOverflowBinsLayer by remember { mutableStateOf(true) }
    var showFacilitiesLayer by remember { mutableStateOf(true) }
    var showOnlyCritical by remember { mutableStateOf(false) }

    var selectedInspectorTarget by remember { mutableStateOf<MapInspectorTarget?>(null) }

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

    val allIncidentPins = remember(complaints) {
        parseComplaintToOverflowPins(complaints)
    }

    val filteredPins = remember(allIncidentPins, selectedWardFilter, showOnlyCritical) {
        allIncidentPins.filter { pin ->
            val matchesWard = if (selectedWardFilter == "All Kigamboni") true else pin.ward.contains(selectedWardFilter, ignoreCase = true)
            val matchesCritical = if (showOnlyCritical) pin.severity == OverflowSeverityLevel.CRITICAL_HAZARD else true
            matchesWard && matchesCritical
        }
    }

    val activeRequests = remember(requests) {
        requests.filter { it.collectionStatus != CollectionStatus.CANCELLED }
    }

    val activeDriversList = remember(drivers) {
        drivers.filter { it.assignedDriverId != null }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar
            Surface(
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                                    .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GpsFixed,
                                    contentDescription = "GIS Command Radar",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "MUNICIPAL HIGH-PRECISION GIS RADAR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF38BDF8),
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Live Driver Fleet & Resident Coordinates",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Map Type Toggle Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.clickable {
                                selectedMapType = if (selectedMapType == MapType.NORMAL) MapType.HYBRID else MapType.NORMAL
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (selectedMapType == MapType.NORMAL) "Hybrid" else "Normal",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Multi-Layer Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LayerFilterChip(
                            label = if (!isVectorRadarMode) "🗺️ Google Map (Active)" else "🛰️ GIS Radar",
                            isActive = !isVectorRadarMode,
                            activeColor = MunicipalBlue,
                            onClick = { isVectorRadarMode = !isVectorRadarMode }
                        )

                        LayerFilterChip(
                            label = "🚛 Drivers (${activeDriversList.size})",
                            isActive = showDriversLayer,
                            activeColor = MunicipalBlue,
                            onClick = { showDriversLayer = !showDriversLayer }
                        )

                        LayerFilterChip(
                            label = "📍 Residents (${activeRequests.size})",
                            isActive = showResidentsLayer,
                            activeColor = EcoGreenPrimary,
                            onClick = { showResidentsLayer = !showResidentsLayer }
                        )

                        LayerFilterChip(
                            label = "🚨 Bins (${filteredPins.size})",
                            isActive = showOverflowBinsLayer,
                            activeColor = Color(0xFFEF4444),
                            onClick = { showOverflowBinsLayer = !showOverflowBinsLayer }
                        )

                        LayerFilterChip(
                            label = "♻️ Hubs (${facilities.size})",
                            isActive = showFacilitiesLayer,
                            activeColor = Color(0xFF8B5CF6),
                            onClick = { showFacilitiesLayer = !showFacilitiesLayer }
                        )
                    }
                }
            }

            // Ward Fast Navigation Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate100)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sector:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate700)
                KIGAMBONI_WARD_PRESETS.forEach { preset ->
                    val isSelected = selectedWardFilter == preset.name
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedWardFilter = preset.name
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(preset.center, preset.zoom),
                                    800
                                )
                            }
                        },
                        label = { Text(preset.name, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MunicipalBlue,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Slate800
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            // Google Maps Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(390.dp)
                    .testTag("municipal_commissioner_google_map")
            ) {
                if (isVectorRadarMode) {
                    InteractiveMapCanvas(
                        requests = activeRequests,
                        drivers = drivers,
                        facilities = facilities,
                        selectedRequestId = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = mapProperties,
                        uiSettings = mapUiSettings,
                        onMapClick = {
                            selectedInspectorTarget = null
                        }
                    ) {
                    // 1. DRIVERS FLEET LAYER (Live Moving Trucks)
                    if (showDriversLayer) {
                        activeDriversList.forEach { drv ->
                            val driverLatLng = LatLng(drv.currentLat, drv.currentLng)
                            val matchedReq = activeRequests.find {
                                it.assignedDriverId == drv.assignedDriverId &&
                                it.collectionStatus in listOf(CollectionStatus.ON_THE_WAY, CollectionStatus.ARRIVED, CollectionStatus.COLLECTING)
                            }

                            // Driver Radar Precision Ring
                            Circle(
                                center = driverLatLng,
                                radius = 45.0,
                                fillColor = MunicipalBlue.copy(alpha = 0.18f),
                                strokeColor = MunicipalBlue,
                                strokeWidth = 2f
                            )

                            // Driver Truck Marker
                            val driverMarkerState = remember(drv.id) { MarkerState(position = driverLatLng) }
                            driverMarkerState.position = driverLatLng
                            Marker(
                                state = driverMarkerState,
                                title = "Truck: ${drv.vehicleRegNumber} (${drv.fullName})",
                                snippet = "Load: ${drv.currentLoadKg}/${drv.vehicleCapacityKg} kg • Rating: ${drv.rating}★",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                                onClick = {
                                    selectedInspectorTarget = MapInspectorTarget.Driver(drv, matchedReq)
                                    coroutineScope.launch {
                                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(driverLatLng, 16f), 500)
                                    }
                                    true
                                }
                            )

                            // Connective Route Path to current assigned resident
                            if (matchedReq != null) {
                                val resLatLng = LatLng(matchedReq.latitude, matchedReq.longitude)
                                Polyline(
                                    points = listOf(driverLatLng, resLatLng),
                                    color = Color(0xFF0284C7),
                                    width = 6f
                                )
                            }
                        }
                    }

                    // 2. RESIDENT COLLECTION REQUESTS LAYER
                    if (showResidentsLayer) {
                        activeRequests.forEach { req ->
                            val resLatLng = LatLng(req.latitude, req.longitude)
                            val markerHue = when (req.collectionStatus) {
                                CollectionStatus.DISPOSED -> BitmapDescriptorFactory.HUE_GREEN
                                CollectionStatus.COLLECTED, CollectionStatus.DISPOSAL_PENDING -> BitmapDescriptorFactory.HUE_YELLOW
                                CollectionStatus.ON_THE_WAY, CollectionStatus.ARRIVED, CollectionStatus.COLLECTING -> BitmapDescriptorFactory.HUE_AZURE
                                CollectionStatus.DRIVER_ASSIGNED -> BitmapDescriptorFactory.HUE_CYAN
                                else -> BitmapDescriptorFactory.HUE_ROSE
                            }

                            // High-precision accuracy circle at resident's location
                            Circle(
                                center = resLatLng,
                                radius = 20.0,
                                fillColor = EcoGreenPrimary.copy(alpha = 0.12f),
                                strokeColor = EcoGreenPrimary,
                                strokeWidth = 1.5f
                            )

                            val reqMarkerState = remember(req.id) { MarkerState(position = resLatLng) }
                            reqMarkerState.position = resLatLng
                            Marker(
                                state = reqMarkerState,
                                title = "Resident: ${req.residentName} (${req.trackingId})",
                                snippet = "${req.pickupAddress} • ${req.bagCount} bags (${req.wasteCategory.name})",
                                icon = BitmapDescriptorFactory.defaultMarker(markerHue),
                                onClick = {
                                    selectedInspectorTarget = MapInspectorTarget.ResidentRequest(req)
                                    coroutineScope.launch {
                                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(resLatLng, 16.5f), 500)
                                    }
                                    true
                                }
                            )
                        }
                    }

                    // 3. OVERFLOWING BIN INCIDENTS LAYER
                    if (showOverflowBinsLayer) {
                        filteredPins.forEach { pin ->
                            val pos = LatLng(pin.latitude, pin.longitude)
                            val pinMarkerState = remember(pin.id) { MarkerState(position = pos) }
                            pinMarkerState.position = pos
                            Marker(
                                state = pinMarkerState,
                                title = "${pin.severity.title}: ${pin.ticketNumber}",
                                snippet = "${pin.ward} - ${pin.landmark}",
                                icon = BitmapDescriptorFactory.defaultMarker(pin.severity.hue),
                                onClick = {
                                    selectedInspectorTarget = MapInspectorTarget.OverflowIncident(pin)
                                    coroutineScope.launch {
                                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pos, 16f), 500)
                                    }
                                    true
                                }
                            )
                        }
                    }

                    // 4. DISPOSAL FACILITIES LAYER
                    if (showFacilitiesLayer) {
                        facilities.forEach { fac ->
                            val pos = LatLng(fac.latitude, fac.longitude)
                            val facMarkerState = remember(fac.id) { MarkerState(position = pos) }
                            facMarkerState.position = pos
                            Marker(
                                state = facMarkerState,
                                title = "Disposal Depot: ${fac.name}",
                                snippet = "${fac.zone} • Cap: ${fac.totalCapacityTons.toInt()} Tons",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET),
                                onClick = {
                                    selectedInspectorTarget = MapInspectorTarget.Facility(fac)
                                    coroutineScope.launch {
                                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(pos, 15f), 500)
                                    }
                                    true
                                }
                            )
                        }
                    }
                }
                }

                // Floating Action Control Column (Top Right)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Fit All Assets
                    FloatingOverlayButton(
                        icon = Icons.Default.CenterFocusStrong,
                        contentDescription = "Fit All Municipal Assets",
                        onClick = {
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(defaultLocation, 12.8f),
                                    800
                                )
                            }
                        }
                    )
                }

                // HIGH-PRECISION INSPECTOR CARD (Bottom Overlay)
                androidx.compose.animation.AnimatedVisibility(
                    visible = selectedInspectorTarget != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                ) {
                    when (val target = selectedInspectorTarget) {
                        is MapInspectorTarget.Driver -> {
                            DriverInspectorCard(
                                driver = target.driver,
                                activeRequest = target.activeRequest,
                                onClose = { selectedInspectorTarget = null }
                            )
                        }
                        is MapInspectorTarget.ResidentRequest -> {
                            ResidentInspectorCard(
                                request = target.request,
                                onDispatch = {
                                    onSelectRequestForDispatch(target.request)
                                    selectedInspectorTarget = null
                                },
                                onClose = { selectedInspectorTarget = null }
                            )
                        }
                        is MapInspectorTarget.OverflowIncident -> {
                            IncidentInspectorCard(
                                pin = target.pin,
                                onDispatchRapidSweep = {
                                    onDispatchRapidSweep(target.pin)
                                    selectedInspectorTarget = null
                                },
                                onResolve = {
                                    onResolveComplaint(target.pin.complaintId, "Investigated & cleared by Municipal Rapid Sweep crew.")
                                    selectedInspectorTarget = null
                                },
                                onClose = { selectedInspectorTarget = null }
                            )
                        }
                        is MapInspectorTarget.Facility -> {
                            FacilityInspectorCard(
                                facility = target.facility,
                                onClose = { selectedInspectorTarget = null }
                            )
                        }
                        null -> {}
                    }
                }
            }

            // Legend Footer Strip
            Surface(
                color = Slate100,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MapLegendItem(color = Color(0xFF0284C7), label = "Fleet")
                        MapLegendItem(color = EcoGreenPrimary, label = "Residents")
                        MapLegendItem(color = Color(0xFFDC2626), label = "Critical Bins")
                        MapLegendItem(color = Color(0xFF8B5CF6), label = "Facilities")
                    }

                    Text(
                        text = "GPS: ±1.8m RTK Sync",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenDark
                    )
                }
            }
        }
    }
}

@Composable
private fun LayerFilterChip(
    label: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isActive) activeColor else Color.White.copy(alpha = 0.12f),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) Color.White else Color(0xFFCBD5E1),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun DriverInspectorCard(
    driver: DriverApplicationEntity,
    activeRequest: CollectionRequestEntity?,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, MunicipalBlue)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MunicipalBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "TRUCK FLEET INSPECTOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                        Text(text = "${driver.fullName} (${driver.vehicleRegNumber})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Precision Coordinate Strip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Slate100,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "GPS: ${String.format("%.6f, %.6f", driver.currentLat, driver.currentLng)}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Slate800
                    )
                    Text(
                        text = "Zone: ${driver.preferredZone}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MunicipalBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Payload: ${driver.currentLoadKg} / ${driver.vehicleCapacityKg} kg • Rating: ${driver.rating}★ • Trips: ${driver.completedTrips}",
                fontSize = 11.sp,
                color = Slate700
            )

            if (activeRequest != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "En Route To: ${activeRequest.residentName} (${activeRequest.pickupAddress})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EcoGreenDark
                )
            }
        }
    }
}

@Composable
private fun ResidentInspectorCard(
    request: CollectionRequestEntity,
    onDispatch: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, EcoGreenPrimary)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(EcoGreenPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = EcoGreenDark, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "RESIDENT PICKUP PINPOINT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EcoGreenDark)
                        Text(text = "${request.residentName} • ${request.trackingId}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Coordinates
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Slate100,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "GPS: ${String.format("%.6f, %.6f", request.latitude, request.longitude)}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Slate800
                    )
                    Text(
                        text = "Status: ${request.collectionStatus.label}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MunicipalBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${request.pickupAddress} • ${request.quantityKg} kg (${request.bagCount} bags - ${request.wasteCategory.name})",
                fontSize = 11.sp,
                color = Slate700
            )

            if (request.assignedDriverName != null) {
                Text(
                    text = "Assigned Driver: ${request.assignedDriverName} (${request.vehicleRegNumber ?: "Truck"})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MunicipalBlue
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDispatch,
                    colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dispatch Approved Driver Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun IncidentInspectorCard(
    pin: OverflowIncidentPin,
    onDispatchRapidSweep: () -> Unit,
    onResolve: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, pin.severity.color)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(pin.severity.color)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = pin.severity.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = pin.ticketNumber, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Slate900)
                }
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "${pin.ward} • ${pin.landmark}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
            Text(text = pin.description, fontSize = 11.sp, color = Slate700, maxLines = 2, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "GPS: ${String.format("%.6f, %.6f", pin.latitude, pin.longitude)} (±1.5m accuracy)",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = MunicipalBlue,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onDispatchRapidSweep,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.2f).height(36.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dispatch Rapid Sweep", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                if (pin.status != "RESOLVED") {
                    OutlinedButton(
                        onClick = onResolve,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("Resolve", fontSize = 11.sp, color = EcoGreenDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FacilityInspectorCard(
    facility: DisposalFacilityEntity,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF8B5CF6))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8B5CF6).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Recycling, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "DISPOSAL / RECYCLING PLANT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
                        Text(text = facility.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Zone: ${facility.zone} • Capacity: ${facility.totalCapacityTons.toInt()} Tons", fontSize = 11.sp, color = Slate700)
            Text(
                text = "GPS: ${String.format("%.6f, %.6f", facility.latitude, facility.longitude)}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = Slate800
            )
        }
    }
}

@Composable
private fun FloatingOverlayButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
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
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun MapLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 10.sp, color = Slate700, fontWeight = FontWeight.Medium)
    }
}

private fun parseComplaintToOverflowPins(complaints: List<ComplaintEntity>): List<OverflowIncidentPin> {
    return complaints.mapIndexed { index, complaint ->
        val desc = complaint.description

        val severity = when {
            desc.contains("Critical", ignoreCase = true) || desc.contains("Hazard", ignoreCase = true) || desc.contains("Spillage", ignoreCase = true) ->
                OverflowSeverityLevel.CRITICAL_HAZARD
            desc.contains("Severe", ignoreCase = true) || desc.contains("100%", ignoreCase = true) || desc.contains("120%", ignoreCase = true) ->
                OverflowSeverityLevel.SEVERE_OVERFLOW
            complaint.status == "RESOLVED" ->
                OverflowSeverityLevel.RESOLVED
            else ->
                OverflowSeverityLevel.MILD_OVERFILL
        }

        val ward = when {
            desc.contains("Kibada", ignoreCase = true) -> "Kibada"
            desc.contains("Ferry", ignoreCase = true) || desc.contains("Kigamboni", ignoreCase = true) -> "Kigamboni Ferry"
            desc.contains("Somangila", ignoreCase = true) -> "Somangila"
            desc.contains("Vijibweni", ignoreCase = true) -> "Vijibweni"
            desc.contains("Tuangoma", ignoreCase = true) -> "Tuangoma"
            desc.contains("Kisarawe", ignoreCase = true) -> "Kisarawe II"
            else -> "Kigamboni Central"
        }

        val landmark = when {
            desc.contains("Landmark:", ignoreCase = true) -> {
                desc.substringAfter("Landmark:").substringBefore("•").substringBefore("(").trim()
            }
            desc.contains("Location:", ignoreCase = true) -> {
                desc.substringAfter("Location:").substringBefore(".").trim()
            }
            else -> "$ward Municipal Bin Hub"
        }

        val (lat, lng) = extractOrAssignCoordinates(desc, ward, index)

        OverflowIncidentPin(
            id = complaint.id,
            complaintId = complaint.id,
            ticketNumber = complaint.requestId,
            ward = ward,
            landmark = landmark,
            latitude = lat,
            longitude = lng,
            severity = severity,
            status = complaint.status,
            description = desc,
            citizenName = complaint.residentName,
            reportedAt = complaint.createdAt
        )
    }
}

private fun extractOrAssignCoordinates(desc: String, ward: String, index: Int): Pair<Double, Double> {
    if (desc.contains("Lat:") && desc.contains("Lng:")) {
        try {
            val latPart = desc.substringAfter("Lat:").substringBefore(",").trim().toDoubleOrNull()
            val lngPart = desc.substringAfter("Lng:").substringBefore(")").trim().toDoubleOrNull()
            if (latPart != null && lngPart != null) {
                return Pair(latPart, lngPart)
            }
        } catch (_: Exception) {}
    }

    val offset = (index % 5) * 0.0025
    return when (ward) {
        "Kibada" -> Pair(-6.8520 + offset, 39.3250 - offset)
        "Kigamboni Ferry" -> Pair(-6.8280 - offset, 39.3020 + offset)
        "Somangila" -> Pair(-6.8780 + offset, 39.3600 + offset)
        "Vijibweni" -> Pair(-6.8390 - offset, 39.3110 - offset)
        "Tuangoma" -> Pair(-6.9020 + offset, 39.3450 - offset)
        "Kisarawe II" -> Pair(-6.9350 - offset, 39.3820 + offset)
        else -> Pair(-6.8650 + offset, 39.3300 - offset)
    }
}
