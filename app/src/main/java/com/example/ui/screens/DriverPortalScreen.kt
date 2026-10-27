package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.DisposalFacilityEntity
import com.example.data.local.DriverApplicationEntity
import com.example.data.model.CollectionStatus
import com.example.data.model.DriverApplicationStatus
import com.example.data.model.UserRole
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Place
import com.example.ui.components.DriverAutoRouteMapView
import com.example.ui.components.DriverPendingCollectionMapView
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.WasteCategoryTag
import com.example.ui.navigation.DriverNavTab
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenLight
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.MunicipalBlueLight
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

enum class DriverMapMode(val label: String) {
    PENDING_MARKERS("Pending Pickups Map"),
    AUTO_ROUTE("Route Optimizer")
}

@Composable
fun DriverPortalScreen(
    driver: DriverApplicationEntity?,
    assignedRequests: List<CollectionRequestEntity>,
    nearbyUnassignedRequests: List<CollectionRequestEntity> = emptyList(),
    allRequests: List<CollectionRequestEntity> = emptyList(),
    disposalFacilities: List<DisposalFacilityEntity> = emptyList(),
    currentTab: DriverNavTab = DriverNavTab.MAP,
    onNavigateToTab: (DriverNavTab) -> Unit = {},
    onAdvanceStatus: (requestId: String, nextStatus: CollectionStatus, weightKg: Int?) -> Unit,
    onCheckInAtDisposal: (requestId: String, facilityId: String, facilityName: String, isRecycled: Boolean) -> Unit,
    onClaimNearbyRequest: (CollectionRequestEntity) -> Unit = {},
    onOpenChat: (CollectionRequestEntity) -> Unit = {},
    onApplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val driverCapacity = driver?.vehicleCapacityKg ?: 2500
    val currentLoad = driver?.currentLoadKg ?: 600
    val loadRatio = (currentLoad.toFloat() / driverCapacity.toFloat()).coerceIn(0f, 1f)
    var mapMode by remember { mutableStateOf(DriverMapMode.PENDING_MARKERS) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (currentTab) {
            DriverNavTab.MAP -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Map Sub-View Switcher Pill
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (mapMode == DriverMapMode.PENDING_MARKERS) MunicipalBlue else Slate100)
                                    .clickable { mapMode = DriverMapMode.PENDING_MARKERS }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Place,
                                        contentDescription = null,
                                        tint = if (mapMode == DriverMapMode.PENDING_MARKERS) Color.White else Slate700,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        "Pending Pickups Map",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (mapMode == DriverMapMode.PENDING_MARKERS) Color.White else Slate700
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (mapMode == DriverMapMode.AUTO_ROUTE) MunicipalBlue else Slate100)
                                    .clickable { mapMode = DriverMapMode.AUTO_ROUTE }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.AltRoute,
                                        contentDescription = null,
                                        tint = if (mapMode == DriverMapMode.AUTO_ROUTE) Color.White else Slate700,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        "Route Optimizer",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (mapMode == DriverMapMode.AUTO_ROUTE) Color.White else Slate700
                                    )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        if (mapMode == DriverMapMode.PENDING_MARKERS) {
                            DriverPendingCollectionMapView(
                                driver = driver,
                                assignedRequests = assignedRequests,
                                nearbyUnassignedRequests = nearbyUnassignedRequests,
                                allRequests = allRequests,
                                disposalFacilities = disposalFacilities,
                                onAdvanceStatus = onAdvanceStatus,
                                onCheckInAtDisposal = onCheckInAtDisposal,
                                onClaimNearbyRequest = onClaimNearbyRequest,
                                onOpenChat = onOpenChat,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            DriverAutoRouteMapView(
                                driver = driver,
                                assignedRequests = assignedRequests,
                                nearbyUnassignedRequests = nearbyUnassignedRequests,
                                disposalFacilities = disposalFacilities,
                                onAdvanceStatus = onAdvanceStatus,
                                onCheckInAtDisposal = onCheckInAtDisposal,
                                onClaimNearbyRequest = onClaimNearbyRequest,
                                onOpenChat = onOpenChat,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
            DriverNavTab.DISPATCHES -> {
                DriverDispatchesTab(
                    driver = driver,
                    assignedRequests = assignedRequests,
                    nearbyUnassignedRequests = nearbyUnassignedRequests,
                    disposalFacilities = disposalFacilities,
                    onNavigateToTab = onNavigateToTab,
                    onAdvanceStatus = onAdvanceStatus,
                    onCheckInAtDisposal = onCheckInAtDisposal,
                    onClaimNearbyRequest = onClaimNearbyRequest,
                    onOpenChat = onOpenChat
                )
            }
            DriverNavTab.VEHICLE -> {
                DriverVehicleTab(
                    driver = driver,
                    currentLoad = currentLoad,
                    driverCapacity = driverCapacity,
                    loadRatio = loadRatio
                )
            }
            DriverNavTab.PROFILE -> {
                DriverProfileTab(
                    driver = driver,
                    assignedCount = assignedRequests.size,
                    onApplyClick = onApplyClick
                )
            }
        }
    }
}

@Composable
private fun DriverDispatchesTab(
    driver: DriverApplicationEntity?,
    assignedRequests: List<CollectionRequestEntity>,
    nearbyUnassignedRequests: List<CollectionRequestEntity>,
    disposalFacilities: List<DisposalFacilityEntity>,
    onNavigateToTab: (DriverNavTab) -> Unit = {},
    onAdvanceStatus: (String, CollectionStatus, Int?) -> Unit,
    onCheckInAtDisposal: (String, String, String, Boolean) -> Unit,
    onClaimNearbyRequest: (CollectionRequestEntity) -> Unit,
    onOpenChat: (CollectionRequestEntity) -> Unit
) {
    val isDriverApproved = driver?.status == DriverApplicationStatus.APPROVED

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Active Route & Dispatch Queue (${assignedRequests.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Complete collection stops and advance progress steps", fontSize = 11.sp, color = Slate600)
                }
                Button(
                    onClick = { onNavigateToTab(DriverNavTab.MAP) },
                    colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View on Map", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (!isDriverApproved) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (driver?.status) {
                            DriverApplicationStatus.REJECTED, DriverApplicationStatus.SUSPENDED -> Color(0xFFEF4444).copy(alpha = 0.12f)
                            else -> Color(0xFFF59E0B).copy(alpha = 0.12f)
                        }
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (driver?.status) {
                            DriverApplicationStatus.REJECTED, DriverApplicationStatus.SUSPENDED -> Color(0xFFEF4444)
                            else -> Color(0xFFF59E0B)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (driver?.status) {
                                    DriverApplicationStatus.REJECTED, DriverApplicationStatus.SUSPENDED -> Icons.Default.Lock
                                    else -> Icons.Default.Badge
                                },
                                contentDescription = null,
                                tint = when (driver?.status) {
                                    DriverApplicationStatus.REJECTED, DriverApplicationStatus.SUSPENDED -> Color(0xFFEF4444)
                                    else -> Color(0xFFD97706)
                                },
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Application Status: ${driver?.status?.label ?: "Under Review"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (driver?.status) {
                                    DriverApplicationStatus.REJECTED, DriverApplicationStatus.SUSPENDED -> Color(0xFFEF4444)
                                    else -> Color(0xFFD97706)
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = when (driver?.status) {
                                DriverApplicationStatus.REJECTED -> "Your driver registration was rejected: ${driver.commissionerNotes ?: "Documents did not meet municipal requirements"}. Please contact the Municipal Directorate."
                                DriverApplicationStatus.SUSPENDED -> "Driver license/account suspended. Please visit Kigamboni Municipal Office."
                                DriverApplicationStatus.DOCUMENT_VERIFIED -> "Your documents have been verified. Awaiting final operational authorization from Municipal Commissioner."
                                else -> "Your application is currently under municipal review. Collection dispatch actions will be unlocked once approved."
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        if (assignedRequests.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Assignment, contentDescription = null, tint = Slate600, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Assigned Pickups in Queue", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Municipal Commissioner will assign incoming ward requests.", fontSize = 12.sp, color = Slate600)
                    }
                }
            }
        } else {
            items(assignedRequests) { req ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(req.trackingId, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                                Text(req.residentName, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                            }
                            StatusBadge(status = req.collectionStatus)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            WasteCategoryTag(category = req.wasteCategory)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${req.quantityKg} kg • ${req.bagCount} bags", fontSize = 11.sp, color = Slate600)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${req.pickupAddress} (${req.ward})", fontSize = 11.sp, color = Slate600)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Slate200)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons based on status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { onOpenChat(req) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat Resident", fontSize = 11.sp, color = MunicipalBlue)
                            }

                            if (!isDriverApproved) {
                                Surface(
                                    color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        "Approval Required",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }
                            } else {
                                when (req.collectionStatus) {
                                    CollectionStatus.DRIVER_ASSIGNED -> {
                                        Button(
                                            onClick = { onAdvanceStatus(req.id, CollectionStatus.ON_THE_WAY, null) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Start Route 🚚", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    CollectionStatus.ON_THE_WAY -> {
                                        Button(
                                            onClick = { onAdvanceStatus(req.id, CollectionStatus.ARRIVED, null) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Mark Arrived 📍", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }
                                    CollectionStatus.ARRIVED -> {
                                        Button(
                                            onClick = { onAdvanceStatus(req.id, CollectionStatus.COLLECTING, null) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Start Loading 📦", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    CollectionStatus.COLLECTING -> {
                                        Button(
                                            onClick = { onAdvanceStatus(req.id, CollectionStatus.COLLECTED, req.quantityKg) },
                                            colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Collected ✅", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }
                                    CollectionStatus.COLLECTED -> {
                                        val fac = disposalFacilities.firstOrNull()
                                        Button(
                                            onClick = {
                                                if (fac != null) {
                                                    onCheckInAtDisposal(req.id, fac.id, fac.name, true)
                                                } else {
                                                    onAdvanceStatus(req.id, CollectionStatus.DISPOSED, req.quantityKg)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Deliver Landfill 🏭", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                    else -> {
                                        Text("Status: ${req.collectionStatus.name}", fontSize = 11.sp, color = Slate600)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DriverVehicleTab(
    driver: DriverApplicationEntity?,
    currentLoad: Int,
    driverCapacity: Int,
    loadRatio: Float
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        item {
            Text("Vehicle Status & Inspection", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Payload scale, pre-trip checklist and municipal registration", fontSize = 11.sp, color = Slate600)
        }

        // Vehicle Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MunicipalBlue),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(driver?.vehicleRegNumber ?: "T 123 ABC", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${driver?.vehicleType ?: "Compactor Truck"} • Municipal Permit Active", fontSize = 11.sp, color = MunicipalBlueLight)
                        }
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Current Payload", fontSize = 11.sp, color = Color.White)
                        Text("$currentLoad / $driverCapacity kg (${(loadRatio * 100).toInt()}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { loadRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (loadRatio > 0.85f) Color(0xFFEF4444) else EcoGreenPrimary,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }

        // Pre-trip inspection checklist items
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Daily Municipal Inspection Items", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    InspectionItem("Hydraulic Compactor Press", "Normal / Operational")
                    InspectionItem("Hydraulic Lift Bin Arm", "Certified")
                    InspectionItem("Tire Pressure & Tread", "32 PSI (Good)")
                    InspectionItem("GPS Transponder / Beacon", "Connected (Signal 99%)")
                    InspectionItem("Disposal Weighbridge Card", "Active (RFID Tag OK)")
                }
            }
        }
    }
}

@Composable
private fun InspectionItem(title: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
        }
        Text(status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = EcoGreenDark)
    }
}

@Composable
private fun DriverProfileTab(
    driver: DriverApplicationEntity?,
    assignedCount: Int,
    onApplyClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(38.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(driver?.fullName ?: "Juma Ali Mussa", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Official Driver ID: ${driver?.assignedDriverId ?: "DRV-KGB-001"}", fontSize = 12.sp, color = MunicipalBlue, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⭐ ${driver?.rating ?: 4.9}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Rating", fontSize = 10.sp, color = Slate600)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${driver?.completedTrips ?: 142}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Completed", fontSize = 10.sp, color = Slate600)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$assignedCount", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Queue", fontSize = 10.sp, color = Slate600)
                        }
                    }
                }
            }
        }

        // Driver Official Municipal Fleet Credentials
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Vehicle & Fleet Assignment", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Vehicle Registration", fontSize = 12.sp, color = Slate600)
                        Text(driver?.vehicleRegNumber ?: "T 123 ABC", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Vehicle Model & Type", fontSize = 12.sp, color = Slate600)
                        Text(driver?.vehicleType ?: "Compactor Truck 5T", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Payload Max Capacity", fontSize = 12.sp, color = Slate600)
                        Text("${driver?.vehicleCapacityKg ?: 5000} kg", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Operating Ward Zone", fontSize = 12.sp, color = Slate600)
                        Text(driver?.preferredZone ?: "Kigamboni Ward 1 & 2", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}
