package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.DisposalFacilityEntity
import com.example.data.local.ScheduledCollectionPlanEntity
import com.example.data.local.UserEntity
import com.example.data.location.HighPrecisionLocation
import com.example.data.model.CollectionStatus
import com.example.data.model.UserRole
import com.example.data.model.WasteCategory
import com.example.ui.components.ResidentLiveTrackingGoogleMapView
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.UrgencyBadge
import com.example.ui.components.WasteCategoryTag
import com.example.ui.navigation.ResidentNavTab
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenLight
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.MunicipalBlueLight
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ResidentPortalScreen(
    user: UserEntity,
    requests: List<CollectionRequestEntity>,
    facilities: List<DisposalFacilityEntity> = emptyList(),
    scheduledPlans: List<ScheduledCollectionPlanEntity> = emptyList(),
    gpsLocation: HighPrecisionLocation? = null,
    currentTab: ResidentNavTab = ResidentNavTab.HOME,
    onNavigateToTab: (ResidentNavTab) -> Unit = {},
    onRequestCollectionClick: () -> Unit,
    onCommercialRequestClick: () -> Unit = onRequestCollectionClick,
    onScheduleCollectionPlanClick: () -> Unit = {},
    onRegisterCommercialClick: () -> Unit = {},
    onTogglePlanActive: (planId: String, isActive: Boolean) -> Unit = { _, _ -> },
    onDeletePlan: (planId: String) -> Unit = {},
    onReportOverflowingBinClick: () -> Unit = {},
    onViewReceiptClick: (CollectionRequestEntity) -> Unit,
    onRateDriverClick: (CollectionRequestEntity) -> Unit,
    onLodgeComplaintClick: (CollectionRequestEntity) -> Unit,
    onViewChainOfCustodyClick: (CollectionRequestEntity) -> Unit = {},
    onOpenChatClick: (CollectionRequestEntity) -> Unit = {},
    onSimulateDriverStep: (requestId: String) -> Unit = {},
    onUpdatePrecisionPin: (requestId: String, lat: Double, lng: Double) -> Unit = { _, _, _ -> },
    onOpenNotifications: () -> Unit = {},
    onOpenThemeSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isCommercial = user.isBusinessAccount
    val effectivePlans = scheduledPlans.filter { it.businessId == user.id || it.businessName == (user.businessName ?: user.fullName) || it.businessName == user.fullName }

    val userRequests = remember(requests, user) {
        requests.filter {
            it.residentId == user.id ||
            (it.residentPhone.isNotBlank() && it.residentPhone == user.phone) ||
            (it.residentName.isNotBlank() && it.residentName.equals(user.fullName, ignoreCase = true) && !it.isCommercialRequest)
        }
    }

    val pendingCount = userRequests.count { it.collectionStatus != CollectionStatus.DISPOSED && it.collectionStatus != CollectionStatus.CANCELLED }
    val completedCount = userRequests.count { it.collectionStatus == CollectionStatus.DISPOSED }
    val totalSpent = userRequests.filter { it.paymentStatus == "PAID" }.sumOf { it.estimatedPriceTzs }
    val formattedSpent = NumberFormat.getNumberInstance(Locale.US).format(totalSpent)

    val activeRequest = userRequests.firstOrNull {
        it.collectionStatus in listOf(
            CollectionStatus.DRIVER_ASSIGNED,
            CollectionStatus.ON_THE_WAY,
            CollectionStatus.ARRIVED,
            CollectionStatus.COLLECTING
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (currentTab) {
            ResidentNavTab.HOME -> {
                ResidentHomeTab(
                    user = user,
                    requests = userRequests,
                    activeRequest = activeRequest,
                    pendingCount = pendingCount,
                    completedCount = completedCount,
                    formattedSpent = formattedSpent,
                    effectivePlans = effectivePlans,
                    onRequestCollectionClick = onRequestCollectionClick,
                    onScheduleCollectionPlanClick = onScheduleCollectionPlanClick,
                    onReportOverflowingBinClick = onReportOverflowingBinClick,
                    onRegisterCommercialClick = onRegisterCommercialClick,
                    onNavigateToMap = { onNavigateToTab(ResidentNavTab.MAP) },
                    onNavigateToRequests = { onNavigateToTab(ResidentNavTab.REQUEST) },
                    onViewReceiptClick = onViewReceiptClick,
                    onOpenChatClick = onOpenChatClick
                )
            }
            ResidentNavTab.REQUEST -> {
                ResidentRequestsTab(
                    requests = userRequests,
                    scheduledPlans = effectivePlans,
                    isCommercial = isCommercial,
                    onRequestCollectionClick = onRequestCollectionClick,
                    onScheduleCollectionPlanClick = onScheduleCollectionPlanClick,
                    onTogglePlanActive = onTogglePlanActive,
                    onDeletePlan = onDeletePlan,
                    onViewReceiptClick = onViewReceiptClick,
                    onRateDriverClick = onRateDriverClick,
                    onLodgeComplaintClick = onLodgeComplaintClick,
                    onViewChainOfCustodyClick = onViewChainOfCustodyClick,
                    onOpenChatClick = onOpenChatClick
                )
            }
            ResidentNavTab.MAP -> {
                ResidentMapTab(
                    activeRequest = activeRequest,
                    facilities = facilities,
                    gpsLocation = gpsLocation,
                    onOpenChatClick = onOpenChatClick,
                    onSimulateDriverStep = onSimulateDriverStep,
                    onUpdatePrecisionPin = onUpdatePrecisionPin,
                    onReportOverflowingBinClick = onReportOverflowingBinClick,
                    onRequestCollectionClick = onRequestCollectionClick
                )
            }
            ResidentNavTab.PROFILE -> {
                ResidentProfileTab(
                    user = user,
                    completedCount = completedCount,
                    totalSpent = formattedSpent,
                    onOpenThemeSettings = onOpenThemeSettings,
                    onOpenNotifications = onOpenNotifications
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// 1. RESIDENT HOME TAB
// -----------------------------------------------------------------------------------------
@Composable
private fun ResidentHomeTab(
    user: UserEntity,
    requests: List<CollectionRequestEntity>,
    activeRequest: CollectionRequestEntity?,
    pendingCount: Int,
    completedCount: Int,
    formattedSpent: String,
    effectivePlans: List<ScheduledCollectionPlanEntity>,
    onRequestCollectionClick: () -> Unit,
    onScheduleCollectionPlanClick: () -> Unit,
    onReportOverflowingBinClick: () -> Unit,
    onRegisterCommercialClick: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToRequests: () -> Unit,
    onViewReceiptClick: (CollectionRequestEntity) -> Unit,
    onOpenChatClick: (CollectionRequestEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Welcome Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = EcoGreenDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_smart_city_waste_1787216374203),
                        contentDescription = "Smart City",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(155.dp),
                        contentScale = ContentScale.Crop,
                        alpha = 0.28f
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Habari, ${user.fullName.split(" ").first()}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Kigamboni Municipal Household Service • NIDA: ${user.nidaMasked}",
                                    fontSize = 11.sp,
                                    color = EcoGreenLight
                                )
                                Text(
                                    text = "Ward: ${user.ward} • Street: ${user.address}",
                                    fontSize = 10.sp,
                                    color = Slate200
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EcoGreenPrimary)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Verified Resident",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onRequestCollectionClick,
                                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .testTag("request_collection_hero_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Request", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                            }

                            Button(
                                onClick = onScheduleCollectionPlanClick,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("schedule_collection_plan_hero_button")
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Schedule Plan", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                            }

                            Button(
                                onClick = onReportOverflowingBinClick,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("report_overflow_bin_button")
                            ) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Overflow", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Active Pickup Real-time Live Status Card
        if (activeRequest != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigateToMap() },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(EcoGreenPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Active Waste Collection", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text(activeRequest.trackingId, fontSize = 11.sp, color = MunicipalBlue, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            StatusBadge(status = activeRequest.collectionStatus)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Driver: ${activeRequest.assignedDriverName ?: "Municipal Fleet Unit"} (${activeRequest.vehicleRegNumber ?: "T-782-DFK"})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = onNavigateToMap,
                                colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("📍 Track on Live Map", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                            if (activeRequest.assignedDriverId != null) {
                                OutlinedButton(
                                    onClick = { onOpenChatClick(activeRequest) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Chat Driver", fontSize = 11.sp, color = MunicipalBlue)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Eco-Reward Points & Civic Cleanliness Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenPrimary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EcoGreenPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Recycling, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    val ecoPoints = remember(completedCount, requests) {
                        (completedCount * 50) + (requests.count { it.wasteCategory == WasteCategory.PLASTIC } * 25)
                    }
                    val streakWeeks = if (completedCount > 0) (completedCount / 2).coerceAtLeast(1) else 0
                    val streakLabel = when {
                        completedCount >= 10 -> "Tier: Kigamboni Green Champion • $streakWeeks-Week Segregation Streak"
                        completedCount >= 3 -> "Tier: Active Eco Champion • $streakWeeks-Week Streak"
                        completedCount >= 1 -> "Tier: Green Neighbor • Getting Started"
                        else -> "Tier: New Resident • Schedule your first pickup to earn eco-points"
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Civic Eco-Points", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EcoGreenPrimary
                            ) {
                                Text("$ecoPoints PTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                            }
                        }
                        Text(streakLabel, fontSize = 11.sp, color = Slate600)
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Slate400, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Metrics Summary Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Pending",
                    value = pendingCount.toString(),
                    subtitle = "Active trips",
                    icon = Icons.Default.LocalShipping,
                    iconColor = MunicipalBlue,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Completed",
                    value = completedCount.toString(),
                    subtitle = "Past pickups",
                    icon = Icons.Default.CheckCircle,
                    iconColor = EcoGreenPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Total Spent",
                    value = "TZS $formattedSpent",
                    subtitle = "All collections",
                    icon = Icons.Default.Payment,
                    iconColor = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1.3f)
                )
            }
        }

        // Ward Weekly Waste Timetable Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kigamboni Ward Collection Schedule", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ScheduleDayPill(day = "TUE / THU", type = "🍏 Organic", color = EcoGreenPrimary, modifier = Modifier.weight(1f))
                        ScheduleDayPill(day = "SATURDAY", type = "♻️ Recyclables", color = MunicipalBlue, modifier = Modifier.weight(1f))
                        ScheduleDayPill(day = "MON / FRI", type = "🗑️ General", color = Slate600, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Recent Requests Header & Quick Link
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Activity", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                if (requests.isNotEmpty()) {
                    Text(
                        text = "View All (${requests.size}) →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EcoGreenPrimary,
                        modifier = Modifier.clickable { onNavigateToRequests() }
                    )
                }
            }
        }

        if (requests.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Slate400, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("No Waste Requests Yet", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Tap 'Request' above to schedule your first garbage collection.", fontSize = 11.sp, color = Slate600)
                    }
                }
            }
        } else {
            // Top 2 recent requests
            items(requests.take(2)) { req ->
                val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(req.estimatedPriceTzs)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(req.trackingId, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                                Spacer(modifier = Modifier.width(6.dp))
                                WasteCategoryTag(category = req.wasteCategory)
                            }
                            Text(req.pickupAddress, fontSize = 11.sp, color = Slate600)
                            Text("TZS $formattedPrice • ${req.preferredDate}", fontSize = 10.sp, color = Slate400)
                        }
                        StatusBadge(status = req.collectionStatus)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleDayPill(day: String, type: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(day, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
            Text(type, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// -----------------------------------------------------------------------------------------
// 2. RESIDENT REQUEST TAB
// -----------------------------------------------------------------------------------------
@Composable
private fun ResidentRequestsTab(
    requests: List<CollectionRequestEntity>,
    scheduledPlans: List<ScheduledCollectionPlanEntity>,
    isCommercial: Boolean,
    onRequestCollectionClick: () -> Unit,
    onScheduleCollectionPlanClick: () -> Unit,
    onTogglePlanActive: (String, Boolean) -> Unit,
    onDeletePlan: (String) -> Unit,
    onViewReceiptClick: (CollectionRequestEntity) -> Unit,
    onRateDriverClick: (CollectionRequestEntity) -> Unit,
    onLodgeComplaintClick: (CollectionRequestEntity) -> Unit,
    onViewChainOfCustodyClick: (CollectionRequestEntity) -> Unit,
    onOpenChatClick: (CollectionRequestEntity) -> Unit
) {
    var subTab by remember { mutableIntStateOf(0) } // 0: On-demand Pickups, 1: Scheduled Plans

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Quick Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRequestCollectionClick,
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Pickup Request", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                }

                Button(
                    onClick = onScheduleCollectionPlanClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.EventRepeat, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Schedule Contract", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                }
            }
        }

        // Sub Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = subTab == 0,
                    onClick = { subTab = 0 },
                    label = { Text("📦 Pickups & History (${requests.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EcoGreenPrimary,
                        selectedLabelColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    label = { Text("📅 Scheduled Contracts (${scheduledPlans.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFF59E0B),
                        selectedLabelColor = Color.Black
                    ),
                    modifier = Modifier.weight(1.1f)
                )
            }
        }

        if (subTab == 0) {
            // Pickups List
            if (requests.isEmpty()) {
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
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Slate400, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No Pickup Requests Yet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Request household waste, recyclables or bulky pickup above.", fontSize = 12.sp, color = Slate600)
                        }
                    }
                }
            } else {
                items(requests) { req ->
                    val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(req.estimatedPriceTzs)

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
                                    if (req.collectionId != null) {
                                        Text("Proof ID: ${req.collectionId}", fontSize = 10.sp, color = Slate600)
                                    }
                                }
                                StatusBadge(status = req.collectionStatus)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                WasteCategoryTag(category = req.wasteCategory)
                                UrgencyBadge(urgency = req.urgency)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(req.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${req.pickupAddress} (${req.ward})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Amount Paid", fontSize = 10.sp, color = Slate600)
                                    Text("TZS $formattedPrice", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EcoGreenPrimary)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (req.assignedDriverId != null) {
                                        OutlinedButton(
                                            onClick = { onOpenChatClick(req) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Chat, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Chat", fontSize = 11.sp, color = MunicipalBlue)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { onViewChainOfCustodyClick(req) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Lifecycle", fontSize = 11.sp, color = EcoGreenPrimary)
                                    }

                                    OutlinedButton(
                                        onClick = { onViewReceiptClick(req) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Receipt", fontSize = 11.sp)
                                    }

                                    if (req.collectionStatus == CollectionStatus.DISPOSED) {
                                        OutlinedButton(
                                            onClick = { onRateDriverClick(req) },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Rate", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Scheduled Plans List
            if (scheduledPlans.isEmpty()) {
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
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No Scheduled Contracts Active", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Set up weekly or recurring pickups for household or compound.", fontSize = 12.sp, color = Slate600)
                        }
                    }
                }
            } else {
                items(scheduledPlans) { plan ->
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
                                Text(plan.contractRef, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (plan.isActive) EcoGreenPrimary.copy(alpha = 0.15f) else Slate200)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (plan.isActive) "ACTIVE" else "PAUSED",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (plan.isActive) EcoGreenDark else Slate600
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text("🔄 ${plan.frequency} • ${plan.selectedDays} (${plan.timeSlot})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MunicipalBlue)
                            Text("Waste: ${plan.wasteCategory.title} • Container: ${plan.binCount}x ${plan.containerType}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("TZS ${NumberFormat.getNumberInstance(Locale.US).format(plan.monthlyRateTzs)} / mo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EcoGreenPrimary)

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = { onTogglePlanActive(plan.id, !plan.isActive) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(if (plan.isActive) "Pause" else "Resume", fontSize = 11.sp)
                                    }

                                    IconButton(onClick = { onDeletePlan(plan.id) }, modifier = Modifier.size(32.dp)) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
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

// -----------------------------------------------------------------------------------------
// 3. RESIDENT MAP TAB
// -----------------------------------------------------------------------------------------
@Composable
private fun ResidentMapTab(
    activeRequest: CollectionRequestEntity?,
    facilities: List<DisposalFacilityEntity>,
    gpsLocation: HighPrecisionLocation?,
    onOpenChatClick: (CollectionRequestEntity) -> Unit,
    onSimulateDriverStep: (String) -> Unit,
    onUpdatePrecisionPin: (String, Double, Double) -> Unit,
    onReportOverflowingBinClick: () -> Unit,
    onRequestCollectionClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Live GPS Navigation & Drop-Off Points",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Track collection trucks in real-time or locate nearby municipal recycling hubs",
                fontSize = 11.sp,
                color = Slate600
            )
        }

        // Live Google Map Canvas (Real-time Truck Tracking & Waste Collection Points Discovery)
        item {
            ResidentLiveTrackingGoogleMapView(
                request = activeRequest,
                facilities = facilities,
                gpsLocation = gpsLocation,
                onOpenChat = { activeRequest?.let { onOpenChatClick(it) } },
                onSimulateDriverStep = { activeRequest?.let { onSimulateDriverStep(it.id) } },
                onUpdatePrecisionPin = { lat, lng -> activeRequest?.let { onUpdatePrecisionPin(it.id, lat, lng) } },
                onRequestPickupClick = onRequestCollectionClick,
                modifier = Modifier.testTag("resident_google_map_live_view")
            )
        }

        // Nearby Municipal Drop-Off Points & Recycling Stations
        item {
            Text(
                text = "Nearby Municipal Waste Facilities",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        val nearbySites = listOf(
            Triple("Kigamboni Transfer & Sorting Hub", "1.2 km away • Tuangoma Rd", "Open 06:00 - 20:00 • Recyclables & Organic"),
            Triple("Mjimwema Community Composting Center", "2.1 km away • Beach Road", "Open 07:00 - 17:00 • Organic Waste"),
            Triple("Tuangoma Material Recovery Facility", "3.5 km away • Main Kilwa Hwy", "Open 08:00 - 18:00 • Plastics, Paper & Metals"),
            Triple("Pugu Kinyamwezi Integrated Landfill", "28.4 km away • Ilala Pugu", "Open 24/7 • Municipal Heavy Landfill")
        )

        items(nearbySites) { site ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MunicipalBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(site.first, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(site.second, fontSize = 11.sp, color = MunicipalBlue, fontWeight = FontWeight.Medium)
                        Text(site.third, fontSize = 10.sp, color = Slate600)
                    }
                }
            }
        }

        // Quick Overflow Report Button on Map
        item {
            OutlinedButton(
                onClick = onReportOverflowingBinClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ReportProblem, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Report Overflowing Bin / Litter at Location", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// -----------------------------------------------------------------------------------------
// 4. RESIDENT PROFILE TAB
// -----------------------------------------------------------------------------------------
@Composable
private fun ResidentProfileTab(
    user: UserEntity,
    completedCount: Int,
    totalSpent: String,
    onOpenThemeSettings: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
    ) {
        // Identity Card
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
                            .background(EcoGreenPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(38.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(user.fullName, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Resident Account • Kigamboni Ward", fontSize = 12.sp, color = Slate600)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileMiniStat("NIDA", user.nidaMasked)
                        ProfileMiniStat("Ward", user.ward)
                        ProfileMiniStat("Pickups", "$completedCount")
                    }
                }
            }
        }

        // Contact & Details
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Resident Account Details", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(10.dp))
                    ProfileDetailRow(icon = Icons.Default.Phone, label = "Phone", value = user.phone)
                    ProfileDetailRow(icon = Icons.Default.Email, label = "Email", value = user.email)
                    ProfileDetailRow(icon = Icons.Default.LocationOn, label = "Address", value = user.address)
                    ProfileDetailRow(icon = Icons.Default.Badge, label = "National NIDA ID", value = user.nidaMasked)
                }
            }
        }

        // Municipal Ward Program Details
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Kigamboni Clean City Initiative", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "You are enrolled in the municipal scheduled waste & community composting program under the Kigamboni Municipal Waste Directorate.",
                        fontSize = 11.sp,
                        color = Slate600,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Quick Settings & Preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("App Preferences", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(10.dp))

                    ProfileOptionButton(
                        icon = Icons.Default.Palette,
                        title = "Theme & Display Settings",
                        subtitle = "Dark Mode, Font Scale & Palette",
                        onClick = onOpenThemeSettings
                    )

                    ProfileOptionButton(
                        icon = Icons.Default.Notifications,
                        title = "Notification Center",
                        subtitle = "Live proximity alerts & SMS settings",
                        onClick = onOpenNotifications
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMiniStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, fontSize = 10.sp, color = Slate600)
    }
}

@Composable
private fun ProfileDetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(label, fontSize = 11.sp, color = Slate600, modifier = Modifier.width(90.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun ProfileOptionButton(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 10.sp, color = Slate600)
        }
        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Slate400, modifier = Modifier.size(14.dp))
    }
}
