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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Recycling
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.DisposalFacilityEntity
import com.example.data.local.DriverApplicationEntity
import com.example.data.model.CollectionStatus
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenLight
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.MunicipalBlueLight
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class RouteStop(
    val stopIndex: Int,
    val request: CollectionRequestEntity,
    val distanceKmFromPrevious: Double,
    val distanceKmFromDriver: Double,
    val etaMinutes: Int
)

@Composable
fun DriverAutoRouteMapView(
    driver: DriverApplicationEntity?,
    assignedRequests: List<CollectionRequestEntity>,
    nearbyUnassignedRequests: List<CollectionRequestEntity> = emptyList(),
    disposalFacilities: List<DisposalFacilityEntity> = emptyList(),
    onAdvanceStatus: (requestId: String, nextStatus: CollectionStatus, weightKg: Int?) -> Unit,
    onCheckInAtDisposal: (requestId: String, facilityId: String, facilityName: String, isRecycled: Boolean) -> Unit,
    onClaimNearbyRequest: (CollectionRequestEntity) -> Unit = {},
    onOpenChat: (CollectionRequestEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val driverLat = driver?.currentLat ?: -6.8235
    val driverLng = driver?.currentLng ?: 39.2695
    val driverLatLng = LatLng(driverLat, driverLng)

    // Map configuration
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var isVectorRadarMode by remember { mutableStateOf(false) }
    var isAutoOptimized by remember { mutableStateOf(true) }
    var selectedStopId by remember { mutableStateOf<String?>(null) }
    var showNearbyDrawer by remember { mutableStateOf(false) }
    var showWaypointList by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(driverLatLng, 13.5f)
    }

    androidx.compose.runtime.LaunchedEffect(driverLat, driverLng) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(driverLatLng, 13.5f),
            700
        )
    }

    // Filter active / pending requests on the route
    val activeRouteRequests = remember(assignedRequests) {
        assignedRequests.filter {
            it.collectionStatus != CollectionStatus.DISPOSED &&
            it.collectionStatus != CollectionStatus.CANCELLED
        }
    }

    // Distance Calculation & Auto-Route Sequencing (Nearest-Neighbor TSP Algorithm)
    val sequencedStops: List<RouteStop> = remember(activeRouteRequests, driverLat, driverLng, isAutoOptimized) {
        if (activeRouteRequests.isEmpty()) return@remember emptyList()

        if (!isAutoOptimized) {
            // Keep original assignment order
            var prevLat = driverLat
            var prevLng = driverLng
            activeRouteRequests.mapIndexed { index, req ->
                val distFromPrev = calculateHaversineKm(prevLat, prevLng, req.latitude, req.longitude)
                val distFromDriver = calculateHaversineKm(driverLat, driverLng, req.latitude, req.longitude)
                val eta = ((distFromPrev / 25.0) * 60.0 + 3.0).roundToInt().coerceAtLeast(2)
                prevLat = req.latitude
                prevLng = req.longitude
                RouteStop(
                    stopIndex = index + 1,
                    request = req,
                    distanceKmFromPrevious = (distFromPrev * 10.0).roundToInt() / 10.0,
                    distanceKmFromDriver = (distFromDriver * 10.0).roundToInt() / 10.0,
                    etaMinutes = eta
                )
            }
        } else {
            // Auto Route Optimization based on shortest distance
            val unvisited = activeRouteRequests.toMutableList()
            val result = mutableListOf<RouteStop>()
            var currentLat = driverLat
            var currentLng = driverLng
            var currentStopIndex = 1

            while (unvisited.isNotEmpty()) {
                // Find nearest unvisited stop from current position
                val nearest = unvisited.minByOrNull { req ->
                    calculateHaversineKm(currentLat, currentLng, req.latitude, req.longitude)
                } ?: break

                val distFromPrev = calculateHaversineKm(currentLat, currentLng, nearest.latitude, nearest.longitude)
                val distFromDriver = calculateHaversineKm(driverLat, driverLng, nearest.latitude, nearest.longitude)
                val eta = ((distFromPrev / 25.0) * 60.0 + 3.0).roundToInt().coerceAtLeast(2)

                result.add(
                    RouteStop(
                        stopIndex = currentStopIndex,
                        request = nearest,
                        distanceKmFromPrevious = (distFromPrev * 10.0).roundToInt() / 10.0,
                        distanceKmFromDriver = (distFromDriver * 10.0).roundToInt() / 10.0,
                        etaMinutes = eta
                    )
                )

                unvisited.remove(nearest)
                currentLat = nearest.latitude
                currentLng = nearest.longitude
                currentStopIndex++
            }
            result
        }
    }

    // Nearby unassigned requests sorted by distance from driver
    val nearbySortedRequests = remember(nearbyUnassignedRequests, driverLat, driverLng) {
        nearbyUnassignedRequests
            .filter { it.assignedDriverId == null && it.collectionStatus in listOf(CollectionStatus.REQUESTED, CollectionStatus.PAID, CollectionStatus.WAITING_ASSIGNMENT) }
            .map { req ->
                val dist = calculateHaversineKm(driverLat, driverLng, req.latitude, req.longitude)
                req to ((dist * 10.0).roundToInt() / 10.0)
            }
            .sortedBy { it.second }
    }

    // Primary Disposal Facility (e.g. Pugu Kinyamwezi or default)
    val primaryFacility = disposalFacilities.firstOrNull() ?: DisposalFacilityEntity(
        id = "fac_pugu_01",
        name = "Pugu Kinyamwezi Integrated Recycling Plant",
        zone = "Ilala / Pugu",
        latitude = -6.8850,
        longitude = 39.1200,
        totalCapacityTons = 500.0,
        currentReceivedKg = 18400,
        totalRecycledKg = 14200,
        contactPhone = "+255 765 443 322"
    )

    // Build polyline points: Driver -> Stop 1 -> Stop 2 -> ... -> Disposal Hub
    val routePolylinePoints: List<LatLng> = remember(driverLatLng, sequencedStops, primaryFacility) {
        val points = mutableListOf<LatLng>()
        points.add(driverLatLng)
        sequencedStops.forEach { stop ->
            points.add(LatLng(stop.request.latitude, stop.request.longitude))
        }
        if (sequencedStops.isNotEmpty()) {
            points.add(LatLng(primaryFacility.latitude, primaryFacility.longitude))
        }
        points
    }

    // Total route distance & estimated time
    val totalRouteDistanceKm = remember(sequencedStops, primaryFacility) {
        var total = 0.0
        sequencedStops.forEach { total += it.distanceKmFromPrevious }
        if (sequencedStops.isNotEmpty()) {
            val lastStop = sequencedStops.last()
            total += calculateHaversineKm(lastStop.request.latitude, lastStop.request.longitude, primaryFacility.latitude, primaryFacility.longitude)
        }
        (total * 10.0).roundToInt() / 10.0
    }

    val totalEstimatedTimeMinutes = remember(totalRouteDistanceKm, sequencedStops) {
        ((totalRouteDistanceKm / 25.0) * 60.0 + (sequencedStops.size * 5)).roundToInt().coerceAtLeast(10)
    }

    // Currently focused / active stop
    val activeStop = remember(sequencedStops, selectedStopId) {
        if (selectedStopId != null) {
            sequencedStops.find { it.request.id == selectedStopId } ?: sequencedStops.firstOrNull()
        } else {
            sequencedStops.firstOrNull {
                it.request.collectionStatus in listOf(
                    CollectionStatus.DRIVER_ASSIGNED,
                    CollectionStatus.ON_THE_WAY,
                    CollectionStatus.ARRIVED,
                    CollectionStatus.COLLECTING,
                    CollectionStatus.COLLECTED
                )
            } ?: sequencedStops.firstOrNull()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Map Canvas (Google Maps or Municipal GIS Vector Canvas)
        if (isVectorRadarMode) {
            InteractiveMapCanvas(
                requests = activeRouteRequests,
                drivers = if (driver != null) listOf(driver) else emptyList(),
                facilities = disposalFacilities,
                selectedRequestId = activeStop?.request?.id,
                onSelectRequest = { req ->
                    selectedStopId = req.id
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("driver_auto_route_vector_map")
            )
        } else {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("driver_auto_route_google_map"),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapType = mapType,
                    isMyLocationEnabled = false
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = true
                )
            ) {
                // Driver GPS Radar Circle
                Circle(
                    center = driverLatLng,
                    radius = 350.0,
                    fillColor = MunicipalBlue.copy(alpha = 0.15f),
                    strokeColor = MunicipalBlue,
                    strokeWidth = 2f
                )

                // Driver Truck Marker
                val driverTruckMarkerState = remember { MarkerState(position = driverLatLng) }
                driverTruckMarkerState.position = driverLatLng
                Marker(
                    state = driverTruckMarkerState,
                    title = "My Truck: ${driver?.vehicleRegNumber ?: "T 123 ABC"}",
                    snippet = "Driver: ${driver?.fullName ?: "Active Driver"} • GPS Online",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )

                // Assigned Route Waypoint Markers
                sequencedStops.forEach { stop ->
                    val isSelected = activeStop?.request?.id == stop.request.id
                    val markerHue = when (stop.request.collectionStatus) {
                        CollectionStatus.COLLECTED -> BitmapDescriptorFactory.HUE_GREEN
                        CollectionStatus.COLLECTING -> BitmapDescriptorFactory.HUE_YELLOW
                        CollectionStatus.ARRIVED -> BitmapDescriptorFactory.HUE_ORANGE
                        else -> if (isSelected) BitmapDescriptorFactory.HUE_RED else BitmapDescriptorFactory.HUE_ROSE
                    }
                    val stopLatLng = LatLng(stop.request.latitude, stop.request.longitude)
                    val stopMarkerState = remember(stop.request.id) { MarkerState(position = stopLatLng) }
                    stopMarkerState.position = stopLatLng

                    Marker(
                        state = stopMarkerState,
                        title = "Stop ${stop.stopIndex}: ${stop.request.residentName}",
                        snippet = "${stop.request.wasteCategory.title} (${stop.request.bagCount} bags) • ${stop.distanceKmFromDriver} km away",
                        icon = BitmapDescriptorFactory.defaultMarker(markerHue),
                        onClick = {
                            selectedStopId = stop.request.id
                            true
                        }
                    )
                }

                // Nearby Unassigned Community Requests (Radar Markers)
                nearbySortedRequests.take(6).forEach { (req, dist) ->
                    val reqLatLng = LatLng(req.latitude, req.longitude)
                    val reqMarkerState = remember(req.id) { MarkerState(position = reqLatLng) }
                    reqMarkerState.position = reqLatLng
                    Marker(
                        state = reqMarkerState,
                        title = "Nearby Pickup: ${req.wasteCategory.title}",
                        snippet = "${dist} km away • ${req.bagCount} bags at ${req.ward} • Tap to claim",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET),
                        onClick = {
                            onClaimNearbyRequest(req)
                            true
                        }
                    )
                }

                // Municipal Disposal & Recycling Facility Marker
                val facilityLatLng = LatLng(primaryFacility.latitude, primaryFacility.longitude)
                val facMarkerState = remember(primaryFacility.id) { MarkerState(position = facilityLatLng) }
                facMarkerState.position = facilityLatLng
                Marker(
                    state = facMarkerState,
                    title = "Disposal Hub: ${primaryFacility.name}",
                    snippet = "Weighbridge & Sorting Facility • Zone: ${primaryFacility.zone}",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )

                // Auto-Route Connected Polyline
                if (routePolylinePoints.size >= 2) {
                    Polyline(
                        points = routePolylinePoints,
                        color = MunicipalBlue,
                        width = 12f
                    )
                }
            }
        }

        // 2. Top Route HUD & Smart Controls Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
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
                                .background(MunicipalBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Auto Waste Route",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "${sequencedStops.size} stops • $totalRouteDistanceKm km • ~$totalEstimatedTimeMinutes mins",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }

                    // Auto-Optimize Toggle Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isAutoOptimized) EcoGreenPrimary.copy(alpha = 0.15f) else Slate100)
                            .border(1.dp, if (isAutoOptimized) EcoGreenPrimary else Slate200, RoundedCornerShape(20.dp))
                            .clickable { isAutoOptimized = !isAutoOptimized }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = if (isAutoOptimized) EcoGreenPrimary else Slate600,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAutoOptimized) "Auto-Distance On" else "Manual Order",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAutoOptimized) EcoGreenPrimary else Slate700
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Slate200)
                Spacer(modifier = Modifier.height(8.dp))

                // Map & Layer Quick Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Google Maps Mode
                        FilterChip(
                            selected = !isVectorRadarMode,
                            onClick = { isVectorRadarMode = false; mapType = MapType.NORMAL },
                            label = { Text("Google Map (Active)", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(12.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MunicipalBlueLight)
                        )
                        // Map Engine Switch Chip
                        FilterChip(
                            selected = isVectorRadarMode,
                            onClick = { isVectorRadarMode = true },
                            label = { Text("GIS Radar", fontSize = 10.sp) },
                            leadingIcon = { Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(12.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EcoGreenLight)
                        )
                        FilterChip(
                            selected = showNearbyDrawer,
                            onClick = { showNearbyDrawer = !showNearbyDrawer },
                            label = { Text("Nearby Radar (${nearbySortedRequests.size})", fontSize = 10.sp) },
                            leadingIcon = { Icon(Icons.Default.Radio, contentDescription = null, modifier = Modifier.size(12.dp)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFEF3C7))
                        )
                        FilterChip(
                            selected = showWaypointList,
                            onClick = { showWaypointList = !showWaypointList },
                            label = { Text("Waypoints", fontSize = 10.sp) },
                            leadingIcon = { Icon(Icons.Default.AltRoute, contentDescription = null, modifier = Modifier.size(12.dp)) }
                        )
                    }

                    // Recenter Camera on Driver
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(driverLatLng, 14.5f))
                            }
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Slate100)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Center on Truck", tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                    }
                }

                if (!isVectorRadarMode) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFF1E1B4B),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFFA5B4FC),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Google Maps API active. If tiles don't load, switch back to GIS Radar.",
                                    fontSize = 10.sp,
                                    color = Color(0xFFE0E7FF)
                                )
                            }
                            Text(
                                text = "Use GIS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary,
                                modifier = Modifier.clickable { isVectorRadarMode = true }
                            )
                        }
                    }
                }
            }
        }

        // 3. Nearby Unassigned Pickups Radar Drawer / Popover
        AnimatedVisibility(
            visible = showNearbyDrawer,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 130.dp, start = 14.dp, end = 14.dp)
                .align(Alignment.TopCenter)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NearMe, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Nearby Open Pickups Radar (< 10km)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }
                        IconButton(onClick = { showNearbyDrawer = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate600, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Sorted by shortest distance from your truck. Tap '+' to add directly into your auto-route sequence.", fontSize = 11.sp, color = Slate600)
                    Spacer(modifier = Modifier.height(10.dp))

                    if (nearbySortedRequests.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No open requests currently in your immediate radius.", fontSize = 12.sp, color = Slate500)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.height(200.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(nearbySortedRequests) { (req, dist) ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Slate100)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(req.trackingId, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(EcoGreenLight)
                                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                                ) {
                                                    Text("${dist} km away", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EcoGreenDark)
                                                }
                                            }
                                            Text("${req.wasteCategory.title} • ${req.bagCount} bags", fontSize = 11.sp, color = Slate800, fontWeight = FontWeight.Medium)
                                            Text("${req.pickupAddress} (${req.ward})", fontSize = 10.sp, color = Slate600, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }

                                        Button(
                                            onClick = {
                                                onClaimNearbyRequest(req)
                                                showNearbyDrawer = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Claim & Route", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Waypoint List Modal / Popover
        AnimatedVisibility(
            visible = showWaypointList,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 130.dp, start = 14.dp, end = 14.dp)
                .align(Alignment.TopCenter)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Optimized Waypoint Sequence", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        IconButton(onClick = { showWaypointList = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate600, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.height(220.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        itemsIndexed(sequencedStops) { index, stop ->
                            val isSelected = activeStop?.request?.id == stop.request.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedStopId = stop.request.id
                                        showWaypointList = false
                                        coroutineScope.launch {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngZoom(
                                                    LatLng(stop.request.latitude, stop.request.longitude),
                                                    15.5f
                                                )
                                            )
                                        }
                                    },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isSelected) MunicipalBlueLight else Slate100)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) MunicipalBlue else Slate700),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("${stop.stopIndex}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(stop.request.residentName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                            Text("${stop.request.pickupAddress} • ${stop.request.bagCount} bags", fontSize = 10.sp, color = Slate600)
                                        }
                                    }
                                    Text("${stop.distanceKmFromDriver} km", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = EcoGreenLight)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Recycling, contentDescription = null, tint = EcoGreenDark, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("FINAL STOP: ${primaryFacility.name}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoGreenDark)
                                        Text("Municipal Recycling & Weighbridge Facility", fontSize = 10.sp, color = Slate700)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Bottom Navigation Action HUD (Active Waypoint Focus Card)
        if (activeStop != null) {
            val req = activeStop.request
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .align(Alignment.BottomCenter)
                    .testTag("driver_active_waypoint_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MunicipalBlue)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header: Stop # and Distance / ETA
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MunicipalBlue)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "STOP ${activeStop.stopIndex} OF ${sequencedStops.size}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${activeStop.distanceKmFromDriver} km away • ETA ${activeStop.etaMinutes} mins",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary
                            )
                        }

                        // Call & Chat Actions
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Chat with Resident Button
                            IconButton(
                                onClick = { onOpenChat(req) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(MunicipalBlue.copy(alpha = 0.12f))
                                    .testTag("driver_map_chat_button")
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = "Chat with Resident", tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                            }

                            // Call Resident Button
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${req.residentPhone}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Slate100)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Call Resident", tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Resident & Waste details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = req.residentName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = "${req.pickupAddress} (${req.ward})",
                                fontSize = 11.sp,
                                color = Slate700
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EcoGreenLight)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${req.bagCount} Bags",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenDark
                            )
                        }
                    }

                    if (req.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Notes: ${req.description}",
                            fontSize = 11.sp,
                            color = Slate600
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Slate200)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Buttons Row: Google Maps Navigation & Workflow Step
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Launch Google Maps GPS Turn-by-Turn Intent
                        OutlinedButton(
                            onClick = {
                                val uri = Uri.parse("google.navigation:q=${req.latitude},${req.longitude}&mode=d")
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                if (mapIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(mapIntent)
                                } else {
                                    // Fallback to geo URI
                                    val geoUri = Uri.parse("geo:${req.latitude},${req.longitude}?q=${req.latitude},${req.longitude}(${Uri.encode(req.residentName)})")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Google Maps", fontSize = 12.sp, color = MunicipalBlue)
                        }

                        // Operational Workflow Advancement
                        when (req.collectionStatus) {
                            CollectionStatus.DRIVER_ASSIGNED, CollectionStatus.ON_THE_WAY -> {
                                Button(
                                    onClick = {
                                        onAdvanceStatus(req.id, CollectionStatus.ARRIVED, null)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    Text("Mark Arrived", fontSize = 12.sp)
                                }
                            }
                            CollectionStatus.ARRIVED -> {
                                Button(
                                    onClick = {
                                        onAdvanceStatus(req.id, CollectionStatus.COLLECTING, null)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    Text("Start Loading Bags", fontSize = 12.sp)
                                }
                            }
                            CollectionStatus.COLLECTING -> {
                                Button(
                                    onClick = {
                                        onAdvanceStatus(req.id, CollectionStatus.COLLECTED, req.bagCount * 15)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Confirm Bags Collected", fontSize = 12.sp)
                                }
                            }
                            CollectionStatus.COLLECTED -> {
                                Button(
                                    onClick = {
                                        onAdvanceStatus(req.id, CollectionStatus.DISPOSAL_PENDING, null)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Proceed to Disposal Hub", fontSize = 11.sp)
                                }
                            }
                            CollectionStatus.DISPOSAL_PENDING -> {
                                Button(
                                    onClick = {
                                        onCheckInAtDisposal(
                                            req.id,
                                            primaryFacility.id,
                                            primaryFacility.name,
                                            req.wasteCategory.isRecyclable
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenDark),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Check-in at Plant", fontSize = 11.sp)
                                }
                            }
                            else -> {
                                Button(
                                    onClick = {
                                        // Focus next stop
                                        val next = sequencedStops.getOrNull(activeStop.stopIndex)
                                        if (next != null) {
                                            selectedStopId = next.request.id
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    Text("Next Route Stop", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else if (activeRouteRequests.isEmpty()) {
            // Empty State overlay when no assigned jobs
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No Active Route Pickups", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Check the Nearby Radar (< 10km) to claim community waste collection requests.", fontSize = 11.sp, color = Slate600)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showNearbyDrawer = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.NearMe, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Explore Nearby Open Pickups (${nearbySortedRequests.size})")
                    }
                }
            }
        }
    }
}

private fun calculateHaversineKm(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}
