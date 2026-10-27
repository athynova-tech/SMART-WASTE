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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.RequestUrgency
import com.example.data.model.WasteCategory
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

enum class PendingFilterOption(val label: String) {
    ALL("All Pending"),
    UNASSIGNED("Open to Claim"),
    ASSIGNED_TO_ME("My Assigned"),
    URGENT("Urgent & Priority"),
    COMMERCIAL("Commercial Bulk")
}

/**
 * A dedicated Google Maps Compose view for Municipal Waste Collection Drivers.
 * Displays all pending waste collection requests across the municipality as interactive markers,
 * color-coded by urgency, status, and request type. Allows drivers to inspect requests, launch GPS
 * navigation, claim open pickups, update status, and track routes.
 */
@Composable
fun DriverPendingCollectionMapView(
    driver: DriverApplicationEntity?,
    assignedRequests: List<CollectionRequestEntity>,
    nearbyUnassignedRequests: List<CollectionRequestEntity>,
    allRequests: List<CollectionRequestEntity> = emptyList(),
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

    // Primary Disposal Facility
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

    // Aggregate all pending requests
    val allPendingCollectionRequests = remember(allRequests, assignedRequests, nearbyUnassignedRequests) {
        val pool = if (allRequests.isNotEmpty()) {
            allRequests
        } else {
            (assignedRequests + nearbyUnassignedRequests).distinctBy { it.id }
        }
        pool.filter {
            it.collectionStatus != CollectionStatus.DISPOSED &&
            it.collectionStatus != CollectionStatus.CANCELLED
        }
    }

    // Filter states
    var selectedFilter by remember { mutableStateOf(PendingFilterOption.ALL) }
    var selectedCategory by remember { mutableStateOf<WasteCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedRequestId by remember { mutableStateOf<String?>(null) }
    var showPendingListDrawer by remember { mutableStateOf(false) }
    var showRoutePolyline by remember { mutableStateOf(true) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var isVectorRadarFallback by remember { mutableStateOf(false) }

    // Filtered requests list
    val filteredPendingRequests = remember(
        allPendingCollectionRequests,
        selectedFilter,
        selectedCategory,
        searchQuery,
        driver?.assignedDriverId,
        driver?.id
    ) {
        allPendingCollectionRequests.filter { req ->
            val matchesFilter = when (selectedFilter) {
                PendingFilterOption.ALL -> true
                PendingFilterOption.UNASSIGNED -> req.assignedDriverId == null
                PendingFilterOption.ASSIGNED_TO_ME -> {
                    req.assignedDriverId != null &&
                    (req.assignedDriverId == driver?.assignedDriverId || req.assignedDriverId == driver?.id)
                }
                PendingFilterOption.URGENT -> {
                    req.urgency == RequestUrgency.EMERGENCY || req.urgency == RequestUrgency.PRIORITY
                }
                PendingFilterOption.COMMERCIAL -> req.isCommercialRequest
            }

            val matchesCategory = selectedCategory == null || req.wasteCategory == selectedCategory

            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                req.trackingId.contains(searchQuery, ignoreCase = true) ||
                req.residentName.contains(searchQuery, ignoreCase = true) ||
                req.pickupAddress.contains(searchQuery, ignoreCase = true) ||
                req.ward.contains(searchQuery, ignoreCase = true) ||
                (req.businessName?.contains(searchQuery, ignoreCase = true) == true)
            }

            matchesFilter && matchesCategory && matchesSearch
        }
    }

    // Calculate distances from driver
    val requestsWithDistance = remember(filteredPendingRequests, driverLat, driverLng) {
        filteredPendingRequests.map { req ->
            val dist = calculateDistanceKm(driverLat, driverLng, req.latitude, req.longitude)
            val etaMinutes = ((dist / 25.0) * 60.0 + 3.0).roundToInt().coerceAtLeast(2)
            PendingRequestItem(req, dist, etaMinutes)
        }.sortedBy { it.distanceKm }
    }

    // Selected item
    val selectedItem = remember(requestsWithDistance, selectedRequestId) {
        if (selectedRequestId != null) {
            requestsWithDistance.find { it.request.id == selectedRequestId }
        } else {
            requestsWithDistance.firstOrNull()
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(driverLatLng, 13.5f)
    }

    // Camera initial center
    LaunchedEffect(driverLat, driverLng) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLngZoom(driverLatLng, 13.5f),
            600
        )
    }

    // Polyline points from driver to selected request or active queue
    val polylinePoints = remember(driverLatLng, selectedItem, showRoutePolyline) {
        if (!showRoutePolyline || selectedItem == null) {
            emptyList()
        } else {
            listOf(
                driverLatLng,
                LatLng(selectedItem.request.latitude, selectedItem.request.longitude)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("driver_pending_map_view")
    ) {
        if (isVectorRadarFallback) {
            // Interactive GIS Vector Radar Fallback
            InteractiveMapCanvas(
                requests = filteredPendingRequests,
                drivers = if (driver != null) listOf(driver) else emptyList(),
                facilities = disposalFacilities,
                selectedRequestId = selectedItem?.request?.id,
                onSelectRequest = { req -> selectedRequestId = req.id },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("driver_vector_gis_fallback_map")
            )
        } else {
            // Google Maps Compose Map
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("driver_pending_google_map"),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    mapType = mapType,
                    isMyLocationEnabled = false
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    compassEnabled = true,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = false
                )
            ) {
                // Driver GPS Radar Circle (e.g. 1.5km immediate radius)
                Circle(
                    center = driverLatLng,
                    radius = 1200.0,
                    fillColor = MunicipalBlue.copy(alpha = 0.10f),
                    strokeColor = MunicipalBlue.copy(alpha = 0.6f),
                    strokeWidth = 2f
                )

                // Driver Truck Location Marker
                val driverMarkerState = remember { MarkerState(position = driverLatLng) }
                driverMarkerState.position = driverLatLng
                Marker(
                    state = driverMarkerState,
                    title = "My Municipal Truck (${driver?.vehicleRegNumber ?: "T 123 ABC"})",
                    snippet = "Driver: ${driver?.fullName ?: "Fleet Driver"} • Capacity: ${driver?.vehicleCapacityKg ?: 2500}kg",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )

                // Pending Collection Request Markers
                requestsWithDistance.forEach { item ->
                    val req = item.request
                    val reqLatLng = LatLng(req.latitude, req.longitude)
                    val markerState = remember(req.id) { MarkerState(position = reqLatLng) }
                    markerState.position = reqLatLng

                    val isSelected = selectedItem?.request?.id == req.id
                    val markerHue = when {
                        req.urgency == RequestUrgency.EMERGENCY -> BitmapDescriptorFactory.HUE_RED
                        req.urgency == RequestUrgency.PRIORITY -> BitmapDescriptorFactory.HUE_ORANGE
                        req.isCommercialRequest -> BitmapDescriptorFactory.HUE_MAGENTA
                        req.assignedDriverId == null -> BitmapDescriptorFactory.HUE_VIOLET
                        req.collectionStatus == CollectionStatus.ARRIVED -> BitmapDescriptorFactory.HUE_YELLOW
                        req.collectionStatus == CollectionStatus.COLLECTING -> BitmapDescriptorFactory.HUE_CYAN
                        req.collectionStatus == CollectionStatus.COLLECTED -> BitmapDescriptorFactory.HUE_GREEN
                        else -> if (isSelected) BitmapDescriptorFactory.HUE_RED else BitmapDescriptorFactory.HUE_ROSE
                    }

                    val titleText = if (req.isCommercialRequest) {
                        "🏢 [Commercial] ${req.businessName ?: req.residentName} (${req.trackingId})"
                    } else {
                        "🏠 ${req.residentName} (${req.trackingId})"
                    }

                    val snippetText = "${req.wasteCategory.title} • ${req.bagCount} bags (${req.quantityKg}kg) • ${item.distanceKm} km away"

                    Marker(
                        state = markerState,
                        title = titleText,
                        snippet = snippetText,
                        icon = BitmapDescriptorFactory.defaultMarker(markerHue),
                        onClick = {
                            selectedRequestId = req.id
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(reqLatLng, 15f),
                                    500
                                )
                            }
                            true
                        }
                    )
                }

                // Municipal Disposal & Recycling Facility Marker
                val facLatLng = LatLng(primaryFacility.latitude, primaryFacility.longitude)
                val facMarkerState = remember(primaryFacility.id) { MarkerState(position = facLatLng) }
                facMarkerState.position = facLatLng
                Marker(
                    state = facMarkerState,
                    title = "Disposal Hub: ${primaryFacility.name}",
                    snippet = "Weighbridge & Recycling Plant • Zone: ${primaryFacility.zone}",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )

                // Direct Route Polyline to selected pending collection
                if (polylinePoints.size >= 2) {
                    Polyline(
                        points = polylinePoints,
                        color = MunicipalBlue,
                        width = 8f
                    )
                }
            }
        }

        // Top Floating HUD: Search, Quick Stats, and Filter Chips
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Top Title & Driver Quick Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MunicipalBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Map, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Pending Collections Map",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(EcoGreenPrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${filteredPendingRequests.size} Active",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoGreenDark
                                    )
                                }
                            }
                            Text(
                                text = "Truck ${driver?.vehicleRegNumber ?: "T 123 ABC"} • Kigamboni Municipality",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }

                    // Map Tools: Recenter, Fit Bounds, Map Type
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Fit All Markers Button
                        IconButton(
                            onClick = {
                                if (requestsWithDistance.isNotEmpty()) {
                                    val builder = LatLngBounds.Builder()
                                    builder.include(driverLatLng)
                                    requestsWithDistance.forEach {
                                        builder.include(LatLng(it.request.latitude, it.request.longitude))
                                    }
                                    builder.include(LatLng(primaryFacility.latitude, primaryFacility.longitude))
                                    coroutineScope.launch {
                                        try {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngBounds(builder.build(), 120),
                                                600
                                            )
                                        } catch (_: Exception) {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngZoom(driverLatLng, 12f)
                                            )
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Slate100)
                                .testTag("btn_fit_all_markers")
                        ) {
                            Icon(Icons.Default.ZoomOutMap, contentDescription = "Fit all markers", tint = Slate700, modifier = Modifier.size(18.dp))
                        }

                        // Recenter on Driver Truck
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(driverLatLng, 14.5f))
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MunicipalBlue.copy(alpha = 0.12f))
                                .testTag("btn_recenter_driver")
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = "Center on Truck", tint = MunicipalBlue, modifier = Modifier.size(18.dp))
                        }

                        // Toggle Satellite / Normal Map Type
                        IconButton(
                            onClick = {
                                mapType = if (mapType == MapType.NORMAL) MapType.HYBRID else MapType.NORMAL
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Slate100)
                                .testTag("btn_toggle_map_type")
                        ) {
                            Icon(Icons.Default.Layers, contentDescription = "Toggle Map Type", tint = Slate700, modifier = Modifier.size(18.dp))
                        }

                        // Toggle Drawer List
                        IconButton(
                            onClick = { showPendingListDrawer = !showPendingListDrawer },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (showPendingListDrawer) MunicipalBlue else Slate100)
                        ) {
                            Icon(
                                Icons.Default.FormatListBulleted,
                                contentDescription = "List View",
                                tint = if (showPendingListDrawer) Color.White else Slate700,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar for Quick Filtering
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("search_pending_input"),
                    placeholder = { Text("Search by Tracking ID, resident, address, or ward...", fontSize = 11.sp, color = Slate500) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate500, modifier = Modifier.size(16.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Slate500, modifier = Modifier.size(14.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MunicipalBlue,
                        unfocusedBorderColor = Slate200,
                        focusedContainerColor = Slate100.copy(alpha = 0.5f),
                        unfocusedContainerColor = Slate100.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Primary Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PendingFilterOption.values().forEach { filterOpt ->
                        val count = when (filterOpt) {
                            PendingFilterOption.ALL -> allPendingCollectionRequests.size
                            PendingFilterOption.UNASSIGNED -> allPendingCollectionRequests.count { it.assignedDriverId == null }
                            PendingFilterOption.ASSIGNED_TO_ME -> allPendingCollectionRequests.count {
                                it.assignedDriverId != null &&
                                (it.assignedDriverId == driver?.assignedDriverId || it.assignedDriverId == driver?.id)
                            }
                            PendingFilterOption.URGENT -> allPendingCollectionRequests.count {
                                it.urgency == RequestUrgency.EMERGENCY || it.urgency == RequestUrgency.PRIORITY
                            }
                            PendingFilterOption.COMMERCIAL -> allPendingCollectionRequests.count { it.isCommercialRequest }
                        }

                        val isSelected = selectedFilter == filterOpt
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filterOpt },
                            label = { Text("${filterOpt.label} ($count)", fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MunicipalBlueLight,
                                selectedLabelColor = MunicipalBlue
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // Fallback to GIS Vector Radar toggle
                    FilterChip(
                        selected = isVectorRadarFallback,
                        onClick = { isVectorRadarFallback = !isVectorRadarFallback },
                        label = { Text(if (isVectorRadarFallback) "GIS Active" else "GIS Radar", fontSize = 10.sp) },
                        leadingIcon = { Icon(Icons.Default.Radio, contentDescription = null, modifier = Modifier.size(12.dp)) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EcoGreenLight),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Waste Category Filter Sub-Row
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedCategory == null) Slate800 else Slate100)
                            .clickable { selectedCategory = null }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("All Types", fontSize = 10.sp, color = if (selectedCategory == null) Color.White else Slate700, fontWeight = FontWeight.Medium)
                    }

                    WasteCategory.values().forEach { cat ->
                        val isCatSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isCatSelected) MunicipalBlue else Slate100)
                                .clickable { selectedCategory = if (isCatSelected) null else cat }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(cat.title, fontSize = 10.sp, color = if (isCatSelected) Color.White else Slate700, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        // Floating Drawer of Matching Pending Pickups
        AnimatedVisibility(
            visible = showPendingListDrawer,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 180.dp, start = 12.dp, end = 12.dp)
                .align(Alignment.TopCenter)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MunicipalBlue)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FormatListBulleted, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pending Collections (${requestsWithDistance.size} locations)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        }
                        IconButton(onClick = { showPendingListDrawer = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate600, modifier = Modifier.size(16.dp))
                        }
                    }

                    Text(
                        text = "Sorted by proximity to your truck. Tap an entry to focus its marker on Google Maps.",
                        fontSize = 11.sp,
                        color = Slate600
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (requestsWithDistance.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No pending collection requests match the selected filters.", fontSize = 12.sp, color = Slate500)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 240.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(requestsWithDistance) { item ->
                                val req = item.request
                                val isSelected = selectedItem?.request?.id == req.id
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedRequestId = req.id
                                            showPendingListDrawer = false
                                            coroutineScope.launch {
                                                cameraPositionState.animate(
                                                    CameraUpdateFactory.newLatLngZoom(
                                                        LatLng(req.latitude, req.longitude),
                                                        15.5f
                                                    ),
                                                    500
                                                )
                                            }
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MunicipalBlueLight else Slate100
                                    )
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
                                                if (req.urgency == RequestUrgency.EMERGENCY) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color(0xFFFEE2E2))
                                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                                    ) {
                                                        Text("EMERGENCY", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                                                    }
                                                } else if (req.isCommercialRequest) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color(0xFFEDE9FE))
                                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                                    ) {
                                                        Text("COMMERCIAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
                                                    }
                                                }
                                            }
                                            Text(
                                                text = if (req.isCommercialRequest) "${req.businessName ?: req.residentName} (${req.wasteCategory.title})" else "${req.residentName} (${req.wasteCategory.title})",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Slate900
                                            )
                                            Text(
                                                text = "${req.pickupAddress} • ${req.bagCount} bags",
                                                fontSize = 10.sp,
                                                color = Slate600,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "${item.distanceKm} km",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EcoGreenPrimary
                                            )
                                            Text(
                                                text = "~${item.etaMinutes} mins",
                                                fontSize = 10.sp,
                                                color = Slate600
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Selected Marker Inspection & Action HUD Card
        if (selectedItem != null) {
            val req = selectedItem.request
            val isAssignedToThisDriver = req.assignedDriverId != null &&
                    (req.assignedDriverId == driver?.assignedDriverId || req.assignedDriverId == driver?.id)
            val isUnassigned = req.assignedDriverId == null

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .align(Alignment.BottomCenter)
                    .testTag("driver_pending_marker_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (req.urgency == RequestUrgency.EMERGENCY) Color(0xFFDC2626) else MunicipalBlue
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header: Tracking ID, Urgency, Distance & ETA
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (req.urgency == RequestUrgency.EMERGENCY) Color(0xFFDC2626) else MunicipalBlue)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = req.trackingId,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))

                            // Urgency chip
                            if (req.urgency != RequestUrgency.NORMAL) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (req.urgency == RequestUrgency.EMERGENCY) Color(0xFFFEE2E2) else Color(0xFFFEF3C7))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = req.urgency.title,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (req.urgency == RequestUrgency.EMERGENCY) Color(0xFFDC2626) else Color(0xFFD97706)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            // Distance from Driver
                            Text(
                                text = "${selectedItem.distanceKm} km away • ~${selectedItem.etaMinutes} mins",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary
                            )
                        }

                        // Close Card / Deselect Marker Button
                        IconButton(
                            onClick = { selectedRequestId = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate600, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Resident / Commercial Entity Information & Category
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (req.isCommercialRequest) {
                                    Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = req.businessName ?: "Commercial Enterprise",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                } else {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = req.residentName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${req.pickupAddress} (${req.ward}, ${req.district})",
                                fontSize = 11.sp,
                                color = Slate700
                            )

                            if (req.description.isNotBlank()) {
                                Text(
                                    text = "Notes: ${req.description}",
                                    fontSize = 10.sp,
                                    color = Slate600,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Category & Bag count badge
                        Column(horizontalAlignment = Alignment.End) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EcoGreenLight)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${req.bagCount} Bags • ${req.quantityKg} kg",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenDark
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = req.wasteCategory.title,
                                fontSize = 10.sp,
                                color = Slate600
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Slate200)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Action Buttons Row: Google Maps Turn-by-Turn, Call/Chat, and Claim or Advance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Launch Google Maps GPS Turn-by-Turn Navigation Intent
                        OutlinedButton(
                            onClick = {
                                val uri = Uri.parse("google.navigation:q=${req.latitude},${req.longitude}&mode=d")
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                if (mapIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(mapIntent)
                                } else {
                                    val geoUri = Uri.parse("geo:${req.latitude},${req.longitude}?q=${req.latitude},${req.longitude}(${Uri.encode(req.residentName)})")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("navigate_google_maps_button"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Navigate", fontSize = 11.sp, color = MunicipalBlue, fontWeight = FontWeight.SemiBold)
                        }

                        // Call Resident / Dispatcher
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${req.residentPhone}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Slate100)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                        }

                        // Chat with Resident
                        IconButton(
                            onClick = { onOpenChat(req) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MunicipalBlue.copy(alpha = 0.12f))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                        }

                        // Primary Action: Claim Unassigned Pickup OR Advance Assigned Workflow
                        if (isUnassigned) {
                            Button(
                                onClick = {
                                    onClaimNearbyRequest(req)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("claim_request_button"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Claim Pickup", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (isAssignedToThisDriver) {
                            // Operational Workflow
                            when (req.collectionStatus) {
                                CollectionStatus.DRIVER_ASSIGNED, CollectionStatus.ON_THE_WAY -> {
                                    Button(
                                        onClick = { onAdvanceStatus(req.id, CollectionStatus.ARRIVED, null) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .testTag("advance_status_button"),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text("Mark Arrived", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                CollectionStatus.ARRIVED -> {
                                    Button(
                                        onClick = { onAdvanceStatus(req.id, CollectionStatus.COLLECTING, null) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .testTag("advance_status_button"),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text("Start Loading", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                CollectionStatus.COLLECTING -> {
                                    Button(
                                        onClick = { onAdvanceStatus(req.id, CollectionStatus.COLLECTED, req.bagCount * 15) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .testTag("advance_status_button"),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Confirm Bags", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                CollectionStatus.COLLECTED -> {
                                    Button(
                                        onClick = { onAdvanceStatus(req.id, CollectionStatus.DISPOSAL_PENDING, null) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .testTag("advance_status_button"),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("To Disposal Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .testTag("advance_status_button"),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.FactCheck, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Disposal Check-in", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .weight(1.3f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Slate100)
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(req.collectionStatus.label, fontSize = 11.sp, color = Slate700, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        } else {
                            // Assigned to another driver
                            Box(
                                modifier = Modifier
                                    .weight(1.3f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Slate100)
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Assigned: ${req.assignedDriverName ?: "Other Driver"}", fontSize = 10.sp, color = Slate700, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class PendingRequestItem(
    val request: CollectionRequestEntity,
    val distanceKm: Double,
    val etaMinutes: Int
)

private fun calculateDistanceKm(
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
    return (r * c * 10.0).roundToInt() / 10.0
}
