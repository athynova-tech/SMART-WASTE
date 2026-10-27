package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BusinessEntity
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.ScheduledCollectionPlanEntity
import com.example.data.local.UserEntity
import com.example.data.model.BusinessVerificationStatus
import com.example.data.model.CollectionStatus
import com.example.data.model.WasteCategory
import com.example.ui.WasteViewModel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.DarkSlate900
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.PendingAmber
import com.example.ui.theme.RecycleGold
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.SuccessGreen

@Composable
fun BusinessPortalScreen(
    viewModel: WasteViewModel,
    onRequestCollection: () -> Unit,
    onScheduleContract: () -> Unit,
    onOpenComplaint: () -> Unit,
    onViewReceipt: (CollectionRequestEntity) -> Unit,
    onViewCustodyProof: (CollectionRequestEntity) -> Unit,
    onOpenNavigationMap: () -> Unit,
    currentTab: com.example.ui.navigation.BusinessNavTab = com.example.ui.navigation.BusinessNavTab.OVERVIEW,
    onNavigateToTab: (com.example.ui.navigation.BusinessNavTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentBizUser by viewModel.currentResidentUser.collectAsState()
    val allRequests by viewModel.allRequests.collectAsState()
    val scheduledPlans by viewModel.allScheduledPlans.collectAsState()
    val allBusinesses by viewModel.allBusinesses.collectAsState()

    val myBusiness = allBusinesses.find { it.userId == currentBizUser.id || it.businessId == currentBizUser.id }
        ?: BusinessEntity(
            businessId = currentBizUser.id,
            userId = currentBizUser.id,
            companyName = currentBizUser.businessName ?: currentBizUser.fullName,
            businessType = currentBizUser.businessType ?: "COMMERCIAL",
            tin = currentBizUser.businessTin ?: "109-882-341",
            registrationNumber = "BRELA-2024-88910",
            businessLicenseNumber = "MUN-LIC-KGB-2026-092",
            email = currentBizUser.email,
            phone = currentBizUser.phone,
            physicalAddress = currentBizUser.address,
            ward = currentBizUser.ward,
            district = currentBizUser.district,
            latitude = currentBizUser.businessLatitude,
            longitude = currentBizUser.businessLongitude,
            verificationStatus = BusinessVerificationStatus.APPROVED,
            contactPerson = currentBizUser.businessContactPerson ?: "Operations Lead",
            operatingHours = currentBizUser.businessOperatingHours ?: "07:00 - 22:00",
            binCount = currentBizUser.businessBinCount,
            binType = currentBizUser.businessBinType
        )

    val companyRequests = allRequests.filter {
        it.residentId == currentBizUser.id ||
        (it.businessName != null && (it.businessName.equals(myBusiness.companyName, ignoreCase = true) || it.businessName.equals(currentBizUser.businessName, ignoreCase = true))) ||
        (it.residentPhone.isNotBlank() && it.residentPhone == currentBizUser.phone)
    }

    val myPlans = scheduledPlans.filter {
        it.businessId == currentBizUser.id ||
        it.businessName.equals(myBusiness.companyName, ignoreCase = true) ||
        it.businessName.equals(currentBizUser.businessName, ignoreCase = true)
    }

    var selectedTabIndex by remember {
        mutableIntStateOf(
            when (currentTab) {
                com.example.ui.navigation.BusinessNavTab.OVERVIEW -> 0
                com.example.ui.navigation.BusinessNavTab.PICKUPS -> 1
                com.example.ui.navigation.BusinessNavTab.CONTRACTS -> 2
                com.example.ui.navigation.BusinessNavTab.PROFILE -> 4
            }
        )
    }

    androidx.compose.runtime.LaunchedEffect(currentTab) {
        selectedTabIndex = when (currentTab) {
            com.example.ui.navigation.BusinessNavTab.OVERVIEW -> 0
            com.example.ui.navigation.BusinessNavTab.PICKUPS -> 1
            com.example.ui.navigation.BusinessNavTab.CONTRACTS -> 2
            com.example.ui.navigation.BusinessNavTab.PROFILE -> 4
        }
    }

    val tabs = listOf("Overview", "Pickups", "Contracts", "Billing", "Profile")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Commercial Company Header Card
        CompanyHeaderBanner(
            business = myBusiness,
            user = currentBizUser,
            onRequestPickup = onRequestCollection,
            onSchedulePlan = onScheduleContract,
            onNavigate = onOpenNavigationMap
        )

        // Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        when (index) {
                            0 -> onNavigateToTab(com.example.ui.navigation.BusinessNavTab.OVERVIEW)
                            1 -> onNavigateToTab(com.example.ui.navigation.BusinessNavTab.PICKUPS)
                            2 -> onNavigateToTab(com.example.ui.navigation.BusinessNavTab.CONTRACTS)
                            4 -> onNavigateToTab(com.example.ui.navigation.BusinessNavTab.PROFILE)
                        }
                    },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when (selectedTabIndex) {
                0 -> BusinessOverviewTab(
                    business = myBusiness,
                    requests = companyRequests,
                    plans = myPlans,
                    onRequestPickup = onRequestCollection,
                    onSchedulePlan = onScheduleContract,
                    onOpenMap = onOpenNavigationMap,
                    onViewReceipt = onViewReceipt,
                    onViewProof = onViewCustodyProof
                )
                1 -> BusinessPickupsTab(
                    requests = companyRequests,
                    onRequestPickup = onRequestCollection,
                    onOpenMap = onOpenNavigationMap,
                    onViewReceipt = onViewReceipt,
                    onViewProof = onViewCustodyProof
                )
                2 -> BusinessContractsTab(
                    plans = myPlans,
                    onNewPlan = onScheduleContract
                )
                3 -> BusinessBillingTab(
                    requests = companyRequests,
                    plans = myPlans,
                    onViewReceipt = onViewReceipt
                )
                4 -> BusinessProfileTab(
                    business = myBusiness,
                    onOpenComplaint = onOpenComplaint
                )
            }
        }
    }
}

@Composable
private fun CompanyHeaderBanner(
    business: BusinessEntity,
    user: UserEntity,
    onRequestPickup: () -> Unit,
    onSchedulePlan: () -> Unit,
    onNavigate: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("business_header_banner")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MunicipalBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CorporateFare,
                            contentDescription = "Commercial Enterprise",
                            tint = MunicipalBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = business.companyName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "TIN: ${business.tin} • ${business.businessType}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Verification Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(business.verificationStatus.colorHex).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(business.verificationStatus.colorHex).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (business.verificationStatus == BusinessVerificationStatus.APPROVED) Icons.Default.Verified else Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(business.verificationStatus.colorHex),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = business.verificationStatus.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(business.verificationStatus.colorHex)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRequestPickup,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("biz_request_pickup_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Request Pickup", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onSchedulePlan,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("biz_schedule_plan_button")
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Recurring Contract", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                IconButton(
                    onClick = onNavigate,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MunicipalBlue.copy(alpha = 0.12f))
                        .testTag("biz_map_nav_button")
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = "Live Map", tint = MunicipalBlue, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun BusinessOverviewTab(
    business: BusinessEntity,
    requests: List<CollectionRequestEntity>,
    plans: List<ScheduledCollectionPlanEntity>,
    onRequestPickup: () -> Unit,
    onSchedulePlan: () -> Unit,
    onOpenMap: () -> Unit,
    onViewReceipt: (CollectionRequestEntity) -> Unit,
    onViewProof: (CollectionRequestEntity) -> Unit
) {
    val activeRequest = requests.find {
        it.collectionStatus !in listOf(CollectionStatus.DISPOSED, CollectionStatus.CANCELLED)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Active Waste Collection Tracking Card
        if (activeRequest != null) {
            item {
                ActiveBusinessCollectionCard(
                    request = activeRequest,
                    onOpenMap = onOpenMap,
                    onViewReceipt = { onViewReceipt(activeRequest) }
                )
            }
        }

        // Commercial Metrics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Allocated Bins",
                    value = "${business.binCount} Bins",
                    subtitle = business.binType,
                    icon = Icons.Default.CorporateFare,
                    color = MunicipalBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Active Contracts",
                    value = "${plans.size} Routine Plans",
                    subtitle = if (plans.isNotEmpty()) plans.first().frequency else "Standard On-Demand",
                    icon = Icons.Default.Schedule,
                    color = EcoGreenPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recent Dispatches / Pickups
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Commercial Dispatches",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${requests.size} Total",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (requests.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Slate400, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No commercial pickups recorded yet", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("Request a waste pickup or activate a recurring schedule.", fontSize = 12.sp, color = Slate600)
                    }
                }
            }
        } else {
            items(requests.take(4)) { req ->
                CommercialRequestRowItem(
                    request = req,
                    onOpenMap = onOpenMap,
                    onViewReceipt = { onViewReceipt(req) },
                    onViewProof = { onViewProof(req) }
                )
            }
        }
    }
}

@Composable
private fun ActiveBusinessCollectionCard(
    request: CollectionRequestEntity,
    onOpenMap: () -> Unit,
    onViewReceipt: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MunicipalBlue.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MunicipalBlue.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_business_collection_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(EcoGreenPrimary)
                    )
                    Text("ACTIVE COMMERCIAL DISPATCH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                }
                Text(request.trackingId, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${request.wasteCategory.title} (${request.quantityKg} kg / ${request.bagCount} Bins)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Status: ${request.collectionStatus.label}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(request.collectionStatus.colorHex)
            )

            if (request.assignedDriverName != null) {
                Text(
                    text = "Assigned Crew: ${request.assignedDriverName} • Truck: ${request.vehicleRegNumber ?: "TZ-COMPACTOR"}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onOpenMap,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Track Truck on Map", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onViewReceipt,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Invoice", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 11.sp, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun CommercialRequestRowItem(
    request: CollectionRequestEntity,
    onOpenMap: () -> Unit,
    onViewReceipt: () -> Unit,
    onViewProof: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = request.wasteCategory.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(request.collectionStatus.colorHex).copy(alpha = 0.12f)
                ) {
                    Text(
                        text = request.collectionStatus.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(request.collectionStatus.colorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tracking: ${request.trackingId} • ${request.preferredDate} (${request.preferredTimeSlot})",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (request.actualWeightKg != null) {
                Text(
                    text = "Certified Net Weight: ${request.actualWeightKg} kg • Price: TZS ${String.format("%,d", request.estimatedPriceTzs)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EcoGreenPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                if (request.collectionStatus == CollectionStatus.COLLECTED || request.collectionStatus == com.example.data.model.CollectionStatus.DISPOSED) {
                    OutlinedButton(
                        onClick = onViewProof,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Proof Certificate", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                OutlinedButton(
                    onClick = onViewReceipt,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tax Invoice", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun BusinessPickupsTab(
    requests: List<CollectionRequestEntity>,
    onRequestPickup: () -> Unit,
    onOpenMap: () -> Unit,
    onViewReceipt: (CollectionRequestEntity) -> Unit,
    onViewProof: (CollectionRequestEntity) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Commercial Waste Collection History", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Button(
                    onClick = onRequestPickup,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Pickup", fontSize = 11.sp)
                }
            }
        }

        if (requests.isEmpty()) {
            item {
                Text("No collection requests submitted.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(requests) { req ->
                CommercialRequestRowItem(
                    request = req,
                    onOpenMap = onOpenMap,
                    onViewReceipt = { onViewReceipt(req) },
                    onViewProof = { onViewProof(req) }
                )
            }
        }
    }
}

@Composable
private fun BusinessContractsTab(
    plans: List<ScheduledCollectionPlanEntity>,
    onNewPlan: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Commercial Waste Service Contracts", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Button(
                    onClick = onNewPlan,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Contract", fontSize = 11.sp)
                }
            }
        }

        if (plans.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No active recurring contract", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Establish a weekly or monthly municipal contract for guaranteed pickup routines.", fontSize = 12.sp, color = Slate600)
                    }
                }
            }
        } else {
            items(plans) { plan ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(plan.frequency, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MunicipalBlue)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EcoGreenPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "ACTIVE CONTRACT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Ref: ${plan.contractRef} • Stream: ${plan.wasteCategory.title}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Pickup Schedule: ${plan.selectedDays} (${plan.timeSlot})", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("Containers: ${plan.binCount}x ${plan.containerType}", fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Monthly Tariff: TZS ${String.format("%,d", plan.monthlyRateTzs)}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EcoGreenPrimary)
                            Text("Next: ${plan.nextPickupDate}", fontSize = 11.sp, color = MunicipalBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BusinessBillingTab(
    requests: List<CollectionRequestEntity>,
    plans: List<ScheduledCollectionPlanEntity>,
    onViewReceipt: (CollectionRequestEntity) -> Unit
) {
    val totalSpent = requests.filter { it.paymentStatus == "PAID" }.sumOf { it.estimatedPriceTzs }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Municipal Commercial Statement", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("TZS ${String.format("%,d", totalSpent)}", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = EcoGreenPrimary)
                    Text("Total Waste Disposal & Fleet Levies Paid", fontSize = 11.sp, color = Slate600)
                }
            }
        }

        item {
            Text("Invoices & Payment Records", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        if (requests.isEmpty()) {
            item {
                Text("No billing statements available.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(requests) { req ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(req.trackingId, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("TZS ${String.format("%,d", req.estimatedPriceTzs)} • Status: ${req.paymentStatus}", fontSize = 12.sp, color = if (req.paymentStatus == "PAID") SuccessGreen else PendingAmber)
                            Text("Method: ${req.paymentMethod?.title ?: "M-Pesa Business"}", fontSize = 11.sp, color = Slate600)
                        }

                        Button(
                            onClick = { onViewReceipt(req) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View Tax Invoice", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BusinessProfileTab(
    business: BusinessEntity,
    onOpenComplaint: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Company Verification & Tax Record", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    ProfileDetailRow("Company Legal Name", business.companyName)
                    ProfileDetailRow("Business Type", business.businessType)
                    ProfileDetailRow("Tax Identification No (TIN)", business.tin)
                    ProfileDetailRow("BRELA Reg Number", business.registrationNumber)
                    ProfileDetailRow("Municipal Waste License", business.businessLicenseNumber)
                    ProfileDetailRow("Physical Location", "${business.physicalAddress}, ${business.ward}")
                    ProfileDetailRow("Contact Lead", business.contactPerson)
                    ProfileDetailRow("Phone", business.phone)
                    ProfileDetailRow("Email", business.email)
                    ProfileDetailRow("Operating Hours", business.operatingHours)
                    ProfileDetailRow("Loading Dock Info", business.loadingDockNotes ?: "Standard Main Entrance")
                }
            }
        }

        item {
            Button(
                onClick = onOpenComplaint,
                colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ReportProblem, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lodge Commercial Complaint / Missed Route", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
