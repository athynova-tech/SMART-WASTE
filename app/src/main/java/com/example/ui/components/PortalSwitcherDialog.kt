package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserRole
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800

data class RolePortalInfo(
    val role: UserRole,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val responsibilities: List<String>,
    val badgeCount: Int = 0
)

@Composable
fun PortalSwitcherDialog(
    currentRole: UserRole,
    onSelectRole: (UserRole) -> Unit,
    onDismiss: () -> Unit,
    residentActiveRequestsCount: Int = 0,
    driverActiveDispatchesCount: Int = 0,
    commissionerPendingApprovalsCount: Int = 0,
    businessOrdersCount: Int = 0
) {
    val portals = listOf(
        RolePortalInfo(
            role = UserRole.RESIDENT,
            title = "Resident Portal",
            subtitle = "Household & Community Citizens",
            icon = Icons.Default.Home,
            primaryColor = EcoGreenPrimary,
            responsibilities = listOf(
                "1. Home: Ward collection timetable & Eco-Points",
                "2. Request: On-demand waste pickup & history",
                "3. Map: Live truck GPS tracking & drop-off bins",
                "4. Profile: Resident credentials & account"
            ),
            badgeCount = residentActiveRequestsCount
        ),
        RolePortalInfo(
            role = UserRole.BUSINESS,
            title = "Commercial Enterprise",
            subtitle = "Hotels, Cafes, Restaurants & Corporations",
            icon = Icons.Default.Business,
            primaryColor = MunicipalBlue,
            responsibilities = listOf(
                "1. Overview: Waste audit KPIs & compliance score",
                "2. Pickups: Commercial bulk orders & dock access",
                "3. Contracts: Weekly recurring dumpster plans",
                "4. Profile & Tax: TIN, BRELA & VAT receipts"
            ),
            badgeCount = businessOrdersCount
        ),
        RolePortalInfo(
            role = UserRole.DRIVER,
            title = "Driver Hub",
            subtitle = "Waste Collection Fleet & Operators",
            icon = Icons.Default.LocalShipping,
            primaryColor = Color(0xFF10B981),
            responsibilities = listOf(
                "1. Route Map: Turn-by-turn live navigation",
                "2. Dispatches: Assigned pickup queue & QR scans",
                "3. Vehicle: Load capacity meter & truck inspection",
                "4. Shift Profile: Commercial license & ratings"
            ),
            badgeCount = driverActiveDispatchesCount
        ),
        RolePortalInfo(
            role = UserRole.MUNICIPAL_COMMISSIONER,
            title = "Municipal Commissioner",
            subtitle = "Kigamboni Waste Management Directorate",
            icon = Icons.Default.AccountBalance,
            primaryColor = Color(0xFFF59E0B),
            responsibilities = listOf(
                "1. Overview: Ward collection analytics & heatmaps",
                "2. Fleet & Map: Real-time truck dispatch console",
                "3. Approvals: Driver vetting & business permits",
                "4. Audits & Reports: Tamper-evident ledger & PDFs"
            ),
            badgeCount = commissionerPendingApprovalsCount
        ),
        RolePortalInfo(
            role = UserRole.DISPOSAL_OFFICER,
            title = "Disposal & Weighbridge Hub",
            subtitle = "Pugu Kinyamwezi Landfill & MRF Operators",
            icon = Icons.Default.Recycling,
            primaryColor = Color(0xFF8B5CF6),
            responsibilities = listOf(
                "1. Weighbridge: Gross / Tare automated scale tickets",
                "2. Manifests: Inbound waste validation & QR check",
                "3. Facility & MRF: Landfill cells & recycling lines",
                "4. Officer Profile: Shift records & weigh certificates"
            ),
            badgeCount = 0
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("portal_switcher_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Switch User Portal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Each role has a tailored navigation system",
                            fontSize = 12.sp,
                            color = Slate600
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate600)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(portals) { portal ->
                        val isSelected = currentRole == portal.role

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onSelectRole(portal.role)
                                    onDismiss()
                                }
                                .testTag("portal_card_${portal.role.name.lowercase()}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) portal.primaryColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) portal.primaryColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(portal.primaryColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = portal.icon,
                                            contentDescription = portal.title,
                                            tint = portal.primaryColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = portal.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (portal.badgeCount > 0) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = portal.primaryColor
                                                ) {
                                                    Text(
                                                        text = "${portal.badgeCount}",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.Black,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = portal.subtitle,
                                            fontSize = 11.sp,
                                            color = Slate600
                                        )
                                    }
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(portal.primaryColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Active",
                                                tint = Color.Black,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    portal.responsibilities.forEach { resp ->
                                        Text(
                                            text = resp,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
}
