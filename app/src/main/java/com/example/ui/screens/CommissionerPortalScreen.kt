package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AuditLogEntity
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.ComplaintEntity
import com.example.data.local.DisposalFacilityEntity
import com.example.data.local.DriverApplicationEntity
import com.example.data.model.CollectionStatus
import com.example.data.model.DriverApplicationStatus
import com.example.data.model.RequestUrgency
import com.example.ui.components.DriverStatusBadge
import com.example.ui.components.InteractiveMapCanvas
import com.example.ui.components.MunicipalEfficiencyDashboard
import com.example.ui.components.OverflowingWasteGoogleMapView
import com.example.ui.components.RechartsWasteDashboard
import com.example.ui.components.StatusBadge
import com.example.ui.components.UrgencyBadge
import com.example.ui.components.WasteCategoryTag
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CharcoalOutline
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenLight
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ErrorRedLight
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommissionerPortalScreen(
    requests: List<CollectionRequestEntity>,
    applications: List<DriverApplicationEntity>,
    approvedDrivers: List<DriverApplicationEntity>,
    complaints: List<ComplaintEntity>,
    facilities: List<DisposalFacilityEntity>,
    auditLogs: List<AuditLogEntity>,
    onOpenDispatchDialog: (CollectionRequestEntity) -> Unit,
    onOpenReviewAppDialog: (DriverApplicationEntity) -> Unit,
    onResolveComplaint: (complaintId: String, notes: String) -> Unit,
    onOpenReportsDialog: () -> Unit = {},
    onSuspendDriver: (driverId: String, reason: String) -> Unit = { _, _ -> },
    onReactivateDriver: (driverId: String) -> Unit = {},
    onQuickApproveDriver: (applicationId: String) -> Unit = {},
    onQuickRejectDriver: (applicationId: String, reason: String) -> Unit = { _, _ -> },
    onQuickAutoAssign: (requestId: String) -> Unit = {},
    onAssignDriverDirect: (requestId: String, driver: DriverApplicationEntity) -> Unit = { _, _ -> },
    onBatchAutoAssign: () -> Unit = {},
    onBatchApproveDrivers: () -> Unit = {},
    currentTab: com.example.ui.navigation.CommissionerNavTab = com.example.ui.navigation.CommissionerNavTab.OVERVIEW,
    onNavigateToTab: (com.example.ui.navigation.CommissionerNavTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember {
        mutableIntStateOf(
            when (currentTab) {
                com.example.ui.navigation.CommissionerNavTab.OVERVIEW -> 3
                com.example.ui.navigation.CommissionerNavTab.FLEET_MAP -> 2
                com.example.ui.navigation.CommissionerNavTab.APPROVALS -> 0
                com.example.ui.navigation.CommissionerNavTab.AUDITS -> 4
            }
        )
    }

    androidx.compose.runtime.LaunchedEffect(currentTab) {
        selectedTab = when (currentTab) {
            com.example.ui.navigation.CommissionerNavTab.OVERVIEW -> 3
            com.example.ui.navigation.CommissionerNavTab.FLEET_MAP -> 2
            com.example.ui.navigation.CommissionerNavTab.APPROVALS -> 0
            com.example.ui.navigation.CommissionerNavTab.AUDITS -> 4
        }
    }

    // Search and filter states for Registrations
    var regSearchQuery by remember { mutableStateOf("") }
    var selectedRegStatusFilter by remember { mutableStateOf<DriverApplicationStatus?>(null) }

    // Search and filter states for Waste Pickups
    var pickupSearchQuery by remember { mutableStateOf("") }
    var selectedPickupUrgencyFilter by remember { mutableStateOf<RequestUrgency?>(null) }
    var selectedPickupStatusFilter by remember { mutableStateOf("PENDING_ALL") }

    // Quick Driver Selection Dropdown State for In-Line Assignment
    var activeDropdownRequestId by remember { mutableStateOf<String?>(null) }

    val pendingRequests = requests.filter {
        it.collectionStatus in listOf(CollectionStatus.REQUESTED, CollectionStatus.PAID, CollectionStatus.WAITING_ASSIGNMENT)
    }
    val urgentRequests = pendingRequests.filter {
        it.urgency == RequestUrgency.EMERGENCY || it.urgency == RequestUrgency.PRIORITY
    }
    val pendingApplications = applications.filter {
        it.status == DriverApplicationStatus.SUBMITTED || it.status == DriverApplicationStatus.UNDER_REVIEW
    }
    val totalRevenue = requests.filter { it.paymentStatus == "PAID" }.sumOf { it.estimatedPriceTzs }
    val formattedRevenue = NumberFormat.getNumberInstance(Locale.US).format(totalRevenue)
    val totalPendingWeightKg = pendingRequests.sumOf { it.quantityKg }

    val tabs = listOf(
        "Pending Registrations (${pendingApplications.size})",
        "Pending Pickups (${pendingRequests.size})",
        "GIS Fleet Radar",
        "Recharts Waste Efficiency",
        "Complaints & Audit (${complaints.count { it.status != "RESOLVED" }})"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Commissioner Official Administrative Authority Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)), // Royal Deep Indigo
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4338CA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .border(1.5.dp, Color(0xFFA5B4FC), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "MUNICIPAL COMMISSIONER EXECUTIVE COMMAND",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = Color(0xFFA5B4FC)
                                )
                                Text(
                                    text = "Hon. Eng. David Malecela",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EcoGreenPrimary)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                "Live Portal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Jurisdiction: Kigamboni Municipal Waste Management Authority (Wards: Kibada, Ferry, Vijibweni, Somangila, Tungi)",
                        fontSize = 13.sp,
                        color = Color(0xFFC7D2FE)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.White.copy(alpha = 0.12f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Commissioner Batch Action Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onBatchAutoAssign,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MunicipalBlue,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(17.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Auto-Dispatch (${pendingRequests.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onBatchApproveDrivers,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EcoGreenPrimary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(17.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Approve Verified (${pendingApplications.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. Executive KPI Metrics Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pending Driver Registrations Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 0 },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedTab == 0) CharcoalSurfaceVariant else CharcoalSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selectedTab == 0) AmberWarning else CharcoalOutline
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Registrations", fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.Person, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${pendingApplications.size} Pending",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pendingApplications.isNotEmpty()) AmberWarning else EcoGreenPrimary
                        )
                        Text(
                            text = "Driver & Contractor review",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }

                // Pending Waste Pickup Requests Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 1 },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedTab == 1) CharcoalSurfaceVariant else CharcoalSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selectedTab == 1) MunicipalBlue else CharcoalOutline
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Waste Pickups", fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${pendingRequests.size} Pending",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (urgentRequests.isNotEmpty()) ErrorRed else MunicipalBlue
                        )
                        Text(
                            text = "${urgentRequests.size} Urgent • ${totalPendingWeightKg} kg",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }

                // Active Trucks & Revenue Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 2 },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedTab == 2) CharcoalSurfaceVariant else CharcoalSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selectedTab == 2) EcoGreenPrimary else CharcoalOutline
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Fleet & Rev", fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.Payment, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${approvedDrivers.size} Active",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )
                        Text(
                            text = "TZS ${formattedRevenue}",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }

                // Recharts Waste Efficiency KPI Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedTab = 3 },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedTab == 3) CharcoalSurfaceVariant else CharcoalSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selectedTab == 3) MunicipalBlue else CharcoalOutline
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Efficiency", fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.Assessment, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "104.2%",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MunicipalBlue
                        )
                        Text(
                            text = "Recharts vs Target",
                            fontSize = 11.sp,
                            color = EcoGreenPrimary
                        )
                    }
                }
            }
        }

        // 3. Tab Navigation Bar
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = CharcoalSurface,
                contentColor = MunicipalBlue,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = when (selectedTab) {
                            0 -> AmberWarning
                            1 -> MunicipalBlue
                            2 -> EcoGreenPrimary
                            else -> MunicipalBlue
                        },
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            when (index) {
                                0 -> onNavigateToTab(com.example.ui.navigation.CommissionerNavTab.APPROVALS)
                                2 -> onNavigateToTab(com.example.ui.navigation.CommissionerNavTab.FLEET_MAP)
                                3 -> onNavigateToTab(com.example.ui.navigation.CommissionerNavTab.OVERVIEW)
                                4 -> onNavigateToTab(com.example.ui.navigation.CommissionerNavTab.AUDITS)
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) Slate50 else Slate400
                            )
                        }
                    )
                }
            }
        }

        // 4. Tab Content Router
        when (selectedTab) {
            0 -> {
                // ==========================================
                // TAB 0: PENDING REGISTRATIONS (APPROVALS)
                // ==========================================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Driver & Contractor Registrations",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate50
                                )
                                Text(
                                    text = "Verify Government NIDA, Driver Licenses, and Vehicle Roadworthiness",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }

                            if (pendingApplications.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AmberLight)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        "${pendingApplications.size} Awaiting Action",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Search Bar
                        OutlinedTextField(
                            value = regSearchQuery,
                            onValueChange = { regSearchQuery = it },
                            placeholder = { Text("Search by name, DL number, plate, or zone...", fontSize = 12.sp, color = Slate500) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                            trailingIcon = {
                                if (regSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { regSearchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Slate400)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CharcoalSurface,
                                unfocusedContainerColor = CharcoalSurface,
                                focusedBorderColor = AmberWarning,
                                unfocusedBorderColor = CharcoalOutline,
                                focusedTextColor = Slate50,
                                unfocusedTextColor = Slate50
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status Filter Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                FilterChip(
                                    selected = selectedRegStatusFilter == null,
                                    onClick = { selectedRegStatusFilter = null },
                                    label = { Text("All (${applications.size})", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AmberWarning,
                                        selectedLabelColor = Color.Black,
                                        containerColor = CharcoalSurface,
                                        labelColor = Slate400
                                    )
                                )
                            }
                            item {
                                FilterChip(
                                    selected = selectedRegStatusFilter == DriverApplicationStatus.SUBMITTED || selectedRegStatusFilter == DriverApplicationStatus.UNDER_REVIEW,
                                    onClick = { selectedRegStatusFilter = DriverApplicationStatus.SUBMITTED },
                                    label = { Text("Pending Review (${pendingApplications.size})", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AmberWarning,
                                        selectedLabelColor = Color.Black,
                                        containerColor = CharcoalSurface,
                                        labelColor = Slate400
                                    )
                                )
                            }
                            item {
                                FilterChip(
                                    selected = selectedRegStatusFilter == DriverApplicationStatus.APPROVED,
                                    onClick = { selectedRegStatusFilter = DriverApplicationStatus.APPROVED },
                                    label = { Text("Approved (${applications.count { it.status == DriverApplicationStatus.APPROVED }})", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EcoGreenPrimary,
                                        selectedLabelColor = Color.Black,
                                        containerColor = CharcoalSurface,
                                        labelColor = Slate400
                                    )
                                )
                            }
                            item {
                                FilterChip(
                                    selected = selectedRegStatusFilter == DriverApplicationStatus.SUSPENDED,
                                    onClick = { selectedRegStatusFilter = DriverApplicationStatus.SUSPENDED },
                                    label = { Text("Suspended (${applications.count { it.status == DriverApplicationStatus.SUSPENDED }})", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ErrorRed,
                                        selectedLabelColor = Color.White,
                                        containerColor = CharcoalSurface,
                                        labelColor = Slate400
                                    )
                                )
                            }
                        }
                    }
                }

                // Filtered Applications List
                val filteredApplications = applications.filter { app ->
                    val matchesQuery = regSearchQuery.isBlank() ||
                            app.fullName.contains(regSearchQuery, ignoreCase = true) ||
                            app.licenseNumber.contains(regSearchQuery, ignoreCase = true) ||
                            app.vehicleRegNumber.contains(regSearchQuery, ignoreCase = true) ||
                            app.preferredZone.contains(regSearchQuery, ignoreCase = true) ||
                            (app.assignedDriverId?.contains(regSearchQuery, ignoreCase = true) == true)

                    val matchesStatus = when (selectedRegStatusFilter) {
                        null -> true
                        DriverApplicationStatus.SUBMITTED -> app.status == DriverApplicationStatus.SUBMITTED || app.status == DriverApplicationStatus.UNDER_REVIEW
                        else -> app.status == selectedRegStatusFilter
                    }

                    matchesQuery && matchesStatus
                }

                if (filteredApplications.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("No Registration Applications Pending", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                Text("All driver registration dossiers have been inspected and processed.", fontSize = 11.sp, color = Slate400)
                            }
                        }
                    }
                } else {
                    items(filteredApplications) { app ->
                        DriverRegistrationCard(
                            app = app,
                            onQuickApprove = { onQuickApproveDriver(app.id) },
                            onQuickReject = { onQuickRejectDriver(app.id, "Rejected by Municipal Commissioner after credential review.") },
                            onOpenFullInspection = { onOpenReviewAppDialog(app) },
                            onSuspend = { if (app.assignedDriverId != null) onSuspendDriver(app.assignedDriverId, "Under compliance review") },
                            onReactivate = { if (app.assignedDriverId != null) onReactivateDriver(app.assignedDriverId) }
                        )
                    }
                }
            }

            1 -> {
                // ==========================================
                // TAB 1: PENDING WASTE PICKUPS (ASSIGNMENT)
                // ==========================================
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Waste Pickup Requests & Assignment",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate50
                                )
                                Text(
                                    text = "Dispatch active trucks to citizen and commercial collection points",
                                    fontSize = 11.sp,
                                    color = Slate400
                                )
                            }

                            if (urgentRequests.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ErrorRedLight)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        "🚨 ${urgentRequests.size} Urgent Pickups",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7F1D1D)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Search Bar
                        OutlinedTextField(
                            value = pickupSearchQuery,
                            onValueChange = { pickupSearchQuery = it },
                            placeholder = { Text("Search by tracking ID, resident, address, ward...", fontSize = 12.sp, color = Slate500) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                            trailingIcon = {
                                if (pickupSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { pickupSearchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Slate400)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CharcoalSurface,
                                unfocusedContainerColor = CharcoalSurface,
                                focusedBorderColor = MunicipalBlue,
                                unfocusedBorderColor = CharcoalOutline,
                                focusedTextColor = Slate50,
                                unfocusedTextColor = Slate50
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Urgency & Status Filter Chips
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                FilterChip(
                                    selected = selectedPickupStatusFilter == "PENDING_ALL" && selectedPickupUrgencyFilter == null,
                                    onClick = {
                                        selectedPickupStatusFilter = "PENDING_ALL"
                                        selectedPickupUrgencyFilter = null
                                    },
                                    label = { Text("Pending Dispatch (${pendingRequests.size})", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MunicipalBlue,
                                        selectedLabelColor = Color.Black,
                                        containerColor = CharcoalSurface,
                                        labelColor = Slate400
                                    )
                                )
                            }
                            item {
                                FilterChip(
                                    selected = selectedPickupUrgencyFilter == RequestUrgency.EMERGENCY || selectedPickupUrgencyFilter == RequestUrgency.PRIORITY,
                                    onClick = {
                                        selectedPickupUrgencyFilter = if (selectedPickupUrgencyFilter == RequestUrgency.PRIORITY) null else RequestUrgency.PRIORITY
                                    },
                                    label = { Text("🚨 High / Urgent (${urgentRequests.size})", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = ErrorRed,
                                        selectedLabelColor = Color.White,
                                        containerColor = CharcoalSurface,
                                        labelColor = Slate400
                                    )
                                )
                            }
                            item {
                                FilterChip(
                                    selected = selectedPickupStatusFilter == "ALL",
                                    onClick = {
                                        selectedPickupStatusFilter = "ALL"
                                        selectedPickupUrgencyFilter = null
                                    },
                                    label = { Text("All Requests (${requests.size})", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MunicipalBlue,
                                        selectedLabelColor = Color.Black,
                                        containerColor = CharcoalSurface,
                                        labelColor = Slate400
                                    )
                                )
                            }
                        }
                    }
                }

                // Filtered Requests List
                val filteredRequests = requests.filter { req ->
                    val matchesQuery = pickupSearchQuery.isBlank() ||
                            req.trackingId.contains(pickupSearchQuery, ignoreCase = true) ||
                            req.residentName.contains(pickupSearchQuery, ignoreCase = true) ||
                            req.pickupAddress.contains(pickupSearchQuery, ignoreCase = true) ||
                            req.ward.contains(pickupSearchQuery, ignoreCase = true) ||
                            req.wasteCategory.title.contains(pickupSearchQuery, ignoreCase = true)

                    val matchesStatus = when (selectedPickupStatusFilter) {
                        "PENDING_ALL" -> req.collectionStatus in listOf(CollectionStatus.REQUESTED, CollectionStatus.PAID, CollectionStatus.WAITING_ASSIGNMENT)
                        else -> true
                    }

                    val matchesUrgency = when (selectedPickupUrgencyFilter) {
                        null -> true
                        RequestUrgency.PRIORITY -> req.urgency == RequestUrgency.PRIORITY || req.urgency == RequestUrgency.EMERGENCY
                        else -> req.urgency == selectedPickupUrgencyFilter
                    }

                    matchesQuery && matchesStatus && matchesUrgency
                }

                if (filteredRequests.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("No Pending Pickup Requests", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                Text("All waste collection orders are assigned or completed.", fontSize = 11.sp, color = Slate400)
                            }
                        }
                    }
                } else {
                    items(filteredRequests) { req ->
                        WastePickupAssignmentCard(
                            request = req,
                            approvedDrivers = approvedDrivers,
                            isDropdownExpanded = activeDropdownRequestId == req.id,
                            onToggleDropdown = {
                                activeDropdownRequestId = if (activeDropdownRequestId == req.id) null else req.id
                            },
                            onQuickAutoAssign = { onQuickAutoAssign(req.id) },
                            onOpenSmartMatchDialog = { onOpenDispatchDialog(req) },
                            onSelectDriver = { driver ->
                                activeDropdownRequestId = null
                                onAssignDriverDirect(req.id, driver)
                            }
                        )
                    }
                }
            }

            2 -> {
                // ==========================================
                // TAB 2: GIS FLEET RADAR & MAP
                // ==========================================
                item {
                    Text(
                        text = "Real-Time Municipal Fleet GIS Radar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                }

                item {
                    OverflowingWasteGoogleMapView(
                        complaints = complaints,
                        requests = requests,
                        drivers = approvedDrivers,
                        facilities = facilities,
                        onResolveComplaint = onResolveComplaint,
                        onDispatchRapidSweep = { pin ->
                            val matchingReq = requests.find {
                                it.ward.equals(pin.ward, ignoreCase = true) &&
                                        it.collectionStatus in listOf(CollectionStatus.REQUESTED, CollectionStatus.PAID, CollectionStatus.WAITING_ASSIGNMENT)
                            }
                            if (matchingReq != null) {
                                onOpenDispatchDialog(matchingReq)
                            } else if (requests.isNotEmpty()) {
                                onOpenDispatchDialog(requests.first())
                            }
                        }
                    )
                }

                item {
                    Text("Approved Fleet on Active Patrol", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate50)
                }

                items(approvedDrivers) { drv ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MunicipalBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "${drv.fullName} • ${drv.assignedDriverId}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate50
                                    )
                                    Text(
                                        "Truck: ${drv.vehicleRegNumber} • Sector: ${drv.preferredZone}",
                                        fontSize = 10.sp,
                                        color = Slate400
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "Payload: ${drv.currentLoadKg}/${drv.vehicleCapacityKg} kg",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EcoGreenPrimary
                                )
                                Text(
                                    "⭐ ${drv.rating} (${drv.completedTrips} trips)",
                                    fontSize = 10.sp,
                                    color = AmberWarning
                                )
                            }
                        }
                    }
                }
            }

            3 -> {
                // ==========================================
                // TAB 3: EFFICIENCY & MUNICIPAL REPORTS (RECHARTS VISUALIZATION DASHBOARD)
                // ==========================================
                item {
                    RechartsWasteDashboard(
                        requests = requests,
                        onExportReport = onOpenReportsDialog
                    )
                }

                item {
                    MunicipalEfficiencyDashboard(
                        onExportReportClick = onOpenReportsDialog
                    )
                }

                item {
                    Button(
                        onClick = onOpenReportsDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export Official Municipal Compliance Report", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            4 -> {
                // ==========================================
                // TAB 4: COMPLAINTS & AUDIT LOGS
                // ==========================================
                item {
                    Text("Citizen Complaints & Overflow Alerts (${complaints.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
                }

                if (complaints.isEmpty()) {
                    item {
                        Text("No complaints lodged by residents.", fontSize = 12.sp, color = Slate400)
                    }
                } else {
                    items(complaints) { cmp ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(cmp.category, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (cmp.status == "RESOLVED") EcoGreenLight else ErrorRedLight)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            cmp.status,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (cmp.status == "RESOLVED") EcoGreenDark else Color(0xFF7F1D1D)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Resident: ${cmp.residentName} • Request: ${cmp.requestId}", fontSize = 11.sp, color = Slate400)
                                Text(cmp.description, fontSize = 11.sp, color = Color(0xFFE2E8F0))

                                if (cmp.status != "RESOLVED") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { onResolveComplaint(cmp.id, "Investigated and resolved by Municipal Health Officer.") },
                                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary, contentColor = Color.Black),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Mark Investigated & Resolved", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Municipal System Audit Trail", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
                }

                items(auditLogs.take(12)) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.action, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                                Text("${log.actorName} (${log.actorRole})", fontSize = 10.sp, color = Slate400)
                            }
                            Text(log.details, fontSize = 10.sp, color = Color(0xFFCBD5E1))
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

/**
 * Driver Registration Card with Quick Action Buttons (Approve / Reject / Inspect)
 */
@Composable
private fun DriverRegistrationCard(
    app: DriverApplicationEntity,
    onQuickApprove: () -> Unit,
    onQuickReject: () -> Unit,
    onOpenFullInspection: () -> Unit,
    onSuspend: () -> Unit,
    onReactivate: () -> Unit
) {
    val isPending = app.status == DriverApplicationStatus.SUBMITTED || app.status == DriverApplicationStatus.UNDER_REVIEW
    val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(app.appliedAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPending) AmberWarning.copy(alpha = 0.5f) else CharcoalOutline
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
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
                            .background(
                                if (isPending) AmberWarning.copy(alpha = 0.15f) else EcoGreenPrimary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isPending) AmberWarning else EcoGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(app.fullName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
                        Text(
                            text = if (app.assignedDriverId != null) "Driver ID: ${app.assignedDriverId}" else "Applied: $formattedDate",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (app.assignedDriverId != null) MunicipalBlue else Slate400
                        )
                    }
                }
                DriverStatusBadge(status = app.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Verified Identifiers Grid
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Government NIDA:", fontSize = 10.sp, color = Slate400)
                        Text(app.nidaMasked, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Slate50)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Driving License:", fontSize = 10.sp, color = Slate400)
                        Text("${app.licenseNumber} (${app.licenseClass})", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MunicipalBlue)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Vehicle & Capacity:", fontSize = 10.sp, color = Slate400)
                        Text("${app.vehicleRegNumber} • ${app.vehicleType} (${app.vehicleCapacityKg} kg)", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Slate50)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Assigned Zone:", fontSize = 10.sp, color = Slate400)
                        Text(app.preferredZone, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = EcoGreenPrimary)
                    }
                }
            }

            if (app.commissionerNotes != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Commissioner Note: ${app.commissionerNotes}", fontSize = 10.sp, color = AmberWarning)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isPending) {
                    // Quick Approve Button
                    Button(
                        onClick = onQuickApprove,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcoGreenPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve & Issue ID", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Inspect Button
                    OutlinedButton(
                        onClick = onOpenFullInspection,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate50),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Inspect", fontSize = 11.sp)
                    }

                    // Quick Reject Button
                    OutlinedButton(
                        onClick = onQuickReject,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        modifier = Modifier
                            .weight(0.8f)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = ErrorRed)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Reject", fontSize = 11.sp, color = ErrorRed)
                    }
                } else if (app.status == DriverApplicationStatus.APPROVED && app.assignedDriverId != null) {
                    OutlinedButton(
                        onClick = onOpenFullInspection,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate50),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.FactCheck, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View Dossier", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onSuspend,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorRed,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Suspend", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (app.status == DriverApplicationStatus.SUSPENDED && app.assignedDriverId != null) {
                    OutlinedButton(
                        onClick = onOpenFullInspection,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate50),
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                    ) {
                        Text("Dossier", fontSize = 11.sp)
                    }

                    Button(
                        onClick = onReactivate,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcoGreenPrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reactivate License", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Waste Pickup Request Assignment Card with Quick Auto-Dispatch & In-line Truck Selector
 */
@Composable
private fun WastePickupAssignmentCard(
    request: CollectionRequestEntity,
    approvedDrivers: List<DriverApplicationEntity>,
    isDropdownExpanded: Boolean,
    onToggleDropdown: () -> Unit,
    onQuickAutoAssign: () -> Unit,
    onOpenSmartMatchDialog: () -> Unit,
    onSelectDriver: (DriverApplicationEntity) -> Unit
) {
    val isPendingAssignment = request.collectionStatus in listOf(
        CollectionStatus.REQUESTED,
        CollectionStatus.PAID,
        CollectionStatus.WAITING_ASSIGNMENT
    )
    val isUrgent = request.urgency == RequestUrgency.EMERGENCY || request.urgency == RequestUrgency.PRIORITY

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUrgent) ErrorRed.copy(alpha = 0.5f) else CharcoalOutline
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(request.trackingId, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    UrgencyBadge(urgency = request.urgency)
                }
                StatusBadge(status = request.collectionStatus)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Resident Details & Waste Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${request.residentName} • ${request.residentPhone}", fontSize = 11.sp, color = Color(0xFFD1D5DB), fontWeight = FontWeight.Medium)
                Text("TZS ${NumberFormat.getNumberInstance(Locale.US).format(request.estimatedPriceTzs)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EcoGreenPrimary)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WasteCategoryTag(category = request.wasteCategory)
                Text("• ${request.quantityKg} kg", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate50)
                Text("• ${request.bagCount} bags", fontSize = 10.sp, color = Slate400)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Slate400, modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${request.pickupAddress} (${request.ward})", fontSize = 11.sp, color = Color(0xFFD1D5DB))
            }

            // Assigned Driver status if already assigned
            if (request.assignedDriverName != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Truck: ${request.assignedDriverName} (${request.vehicleRegNumber ?: "Active"})",
                                fontSize = 11.sp,
                                color = Slate50,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text("On Route", fontSize = 10.sp, color = EcoGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            if (isPendingAssignment) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Smart Matching Dialog Button
                    Button(
                        onClick = onOpenSmartMatchDialog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MunicipalBlue,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Smart Match & Dispatch", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Direct Quick Assign Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = onToggleDropdown,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate50),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pick Truck ▾", fontSize = 11.sp)
                        }

                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = onToggleDropdown,
                            modifier = Modifier.background(CharcoalSurface)
                        ) {
                            if (approvedDrivers.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No approved drivers available", color = Slate400, fontSize = 11.sp) },
                                    onClick = onToggleDropdown
                                )
                            } else {
                                approvedDrivers.forEach { driver ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(driver.fullName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                                Text("Plate: ${driver.vehicleRegNumber} • Zone: ${driver.preferredZone}", fontSize = 10.sp, color = Slate400)
                                            }
                                        },
                                        onClick = { onSelectDriver(driver) }
                                    )
                                }
                            }
                        }
                    }

                    // 1-Tap Auto-Assign
                    IconButton(
                        onClick = onQuickAutoAssign,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(EcoGreenPrimary)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = "Auto-assign", tint = Color.Black, modifier = Modifier.size(18.dp))
                    }
                }
            } else {
                OutlinedButton(
                    onClick = onOpenSmartMatchDialog,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MunicipalBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reassign / Change Dispatch Truck", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
