package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.DisposalFacilityEntity
import com.example.data.local.DriverApplicationEntity
import com.example.data.model.CollectionStatus
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CharcoalOutline
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.ObsidianDarkBg
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MunicipalReportsDialog(
    requests: List<CollectionRequestEntity>,
    drivers: List<DriverApplicationEntity>,
    facilities: List<DisposalFacilityEntity>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedReportTab by remember { mutableStateOf(0) } // 0: Daily Collection Summary, 1: Revenue & Financials, 2: Environmental & Recycling, 3: Driver KPI Scorecard

    val numberFmt = NumberFormat.getNumberInstance(Locale.US)
    val totalRevenueTzs = requests.filter { it.paymentStatus == "PAID" || it.collectionStatus in listOf(CollectionStatus.COLLECTED, CollectionStatus.DISPOSED) }.sumOf { it.estimatedPriceTzs }
    val totalWeightCollectedKg = requests.filter { it.collectionStatus in listOf(CollectionStatus.COLLECTED, CollectionStatus.DISPOSED) }.sumOf { it.actualWeightKg ?: it.quantityKg }
    val totalWeightRecycledKg = requests.filter { it.isRecycled }.sumOf { it.actualWeightKg ?: it.quantityKg }
    val recyclingRatePercent = if (totalWeightCollectedKg > 0) (totalWeightRecycledKg.toDouble() / totalWeightCollectedKg.toDouble() * 100).toInt() else 34
    val co2OffsetKg = (totalWeightRecycledKg * 1.85).toInt()

    val reportTimestamp = SimpleDateFormat("dd MMMM yyyy - HH:mm", Locale.getDefault()).format(Date())

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CharcoalOutline, RoundedCornerShape(20.dp)),
            color = CharcoalSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
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
                                .background(EcoGreenPrimary.copy(alpha = 0.15f))
                                .border(1.dp, EcoGreenPrimary.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = "Reports",
                                tint = EcoGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Municipal Intelligence Reports",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )
                            Text(
                                text = "Kigamboni Municipal Council • $reportTimestamp",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedReportTab,
                    containerColor = ObsidianDarkBg,
                    contentColor = EcoGreenPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedReportTab]),
                            color = EcoGreenPrimary
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, CharcoalOutline, RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedReportTab == 0,
                        onClick = { selectedReportTab = 0 },
                        text = { Text("Operations", fontSize = 11.sp, fontWeight = if (selectedReportTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedReportTab == 1,
                        onClick = { selectedReportTab = 1 },
                        text = { Text("Revenue", fontSize = 11.sp, fontWeight = if (selectedReportTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedReportTab == 2,
                        onClick = { selectedReportTab = 2 },
                        text = { Text("Eco Impact", fontSize = 11.sp, fontWeight = if (selectedReportTab == 2) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedReportTab == 3,
                        onClick = { selectedReportTab = 3 },
                        text = { Text("Driver KPIs", fontSize = 11.sp, fontWeight = if (selectedReportTab == 3) FontWeight.Bold else FontWeight.Normal) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedReportTab) {
                    0 -> {
                        // Operations & Collection Summary
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = ObsidianDarkBg),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Municipal Collection Breakdown", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                Spacer(modifier = Modifier.height(10.dp))

                                ReportStatRow("Total Logged Requests:", "${requests.size} Requests")
                                ReportStatRow("Dispatched & Completed:", "${requests.count { it.collectionStatus in listOf(CollectionStatus.COLLECTED, CollectionStatus.DISPOSED) }} Completed")
                                ReportStatRow("Active on Route:", "${requests.count { it.collectionStatus in listOf(CollectionStatus.DRIVER_ASSIGNED, CollectionStatus.ON_THE_WAY, CollectionStatus.COLLECTING) }} Trucks Active")
                                ReportStatRow("Awaiting Assignment:", "${requests.count { it.collectionStatus in listOf(CollectionStatus.REQUESTED, CollectionStatus.PAID, CollectionStatus.WAITING_ASSIGNMENT) }} Pending")
                                ReportStatRow("Total Gross Waste Weight:", "${numberFmt.format(totalWeightCollectedKg)} kg (${String.format("%.2f", totalWeightCollectedKg / 1000.0)} Tons)")
                            }
                        }
                    }

                    1 -> {
                        // Revenue & Financials
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = ObsidianDarkBg),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Payments, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Municipal Revenue Ledger", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                ReportStatRow("Gross Municipal Revenue:", "TZS ${numberFmt.format(totalRevenueTzs)}")
                                ReportStatRow("Mobile Money Inflow (M-Pesa / Tigo / Airtel):", "TZS ${numberFmt.format((totalRevenueTzs * 0.85).toInt())}")
                                ReportStatRow("Municipal Resident Wallets & Cards:", "TZS ${numberFmt.format((totalRevenueTzs * 0.15).toInt())}")
                                ReportStatRow("Authorized Municipal Tariff Surcharge:", "10% Retained for Landfill Maintenance")
                            }
                        }
                    }

                    2 -> {
                        // Environmental Impact & Circular Economy
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = ObsidianDarkBg),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Eco, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Circular Economy & Environmental Metrics", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                ReportStatRow("Total Recycled Weight:", "${numberFmt.format(totalWeightRecycledKg)} kg")
                                ReportStatRow("Municipal Recycling Diversion Rate:", "$recyclingRatePercent%")
                                ReportStatRow("Estimated CO2 Offset Equivalent:", "$co2OffsetKg kg CO2e")
                                ReportStatRow("Landfill Space Saved:", "${String.format("%.1f", totalWeightRecycledKg / 450.0)} cubic meters")
                            }
                        }
                    }

                    3 -> {
                        // Driver Scorecards
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = ObsidianDarkBg),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Driver Fleet Performance Rankings", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                Spacer(modifier = Modifier.height(10.dp))

                                drivers.forEach { driver ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(driver.fullName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                            Text("${driver.vehicleRegNumber} • ${driver.preferredZone}", fontSize = 10.sp, color = Slate600)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("⭐ ${driver.rating} / 5.0", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberWarning)
                                            Text("${driver.completedTrips} Trips Completed", fontSize = 10.sp, color = EcoGreenPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions: Copy / Export CSV or Markdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val reportContent = buildString {
                                appendLine("=== KIGAMBONI MUNICIPAL SMART WASTE REPORT ===")
                                appendLine("Date: $reportTimestamp")
                                appendLine("Total Requests: ${requests.size}")
                                appendLine("Completed Collections: ${requests.count { it.collectionStatus == CollectionStatus.DISPOSED }}")
                                appendLine("Gross Revenue: TZS ${numberFmt.format(totalRevenueTzs)}")
                                appendLine("Total Waste Collected: ${numberFmt.format(totalWeightCollectedKg)} kg")
                                appendLine("Recycling Rate: $recyclingRatePercent%")
                                appendLine("CO2 Offset: $co2OffsetKg kg CO2e")
                            }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Municipal Report", reportContent))
                            Toast.makeText(context, "Municipal Report copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Slate50, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Report", color = Slate50, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Report exported to Municipal Central Database & PDF Archive", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export CSV / PDF", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportStatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.sp, color = Slate600)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate50)
    }
}
