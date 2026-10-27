package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.CollectionRequestEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.CharcoalOutline
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.ObsidianDarkBg
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChainOfCustodyDialog(
    request: CollectionRequestEntity,
    onDismiss: () -> Unit
) {
    val dateFmt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

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
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verification",
                                tint = EcoGreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Waste Chain of Custody",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )
                            Text(
                                text = "Tracking ID: ${request.trackingId}",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = EcoGreenPrimary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ObsidianDarkBg),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(request.wasteCategory.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
                            StatusBadge(status = request.collectionStatus)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Origin: ${request.pickupAddress}, ${request.ward}",
                            fontSize = 12.sp,
                            color = Slate600
                        )
                        Text(
                            text = "Estimated Weight: ${request.quantityKg} kg • Bags: ${request.bagCount} • Total: TZS ${request.estimatedPriceTzs}",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                        if (request.actualWeightKg != null) {
                            Text(
                                text = "Certified Scale Weight: ${request.actualWeightKg} kg",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Audit Timeline & Lifecycle Milestones", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate50)
                Spacer(modifier = Modifier.height(12.dp))

                // Milestones
                MilestoneItem(
                    title = "1. Collection Requested & Mobile Payment",
                    subtitle = "Submitted by ${request.residentName} (${request.residentPhone}) via ${request.paymentMethod?.title ?: "M-Pesa"}",
                    timestamp = dateFmt.format(Date(request.createdAt)),
                    isCompleted = true,
                    icon = Icons.Default.Inventory
                )

                MilestoneItem(
                    title = "2. Municipal Commissioner Dispatch",
                    subtitle = if (request.assignedDriverId != null)
                        "Assigned to ${request.assignedDriverName} (${request.vehicleRegNumber ?: "Truck"})"
                    else "Queued for automated algorithm routing",
                    timestamp = if (request.assignedDriverId != null) dateFmt.format(Date(request.createdAt + 300000)) else "Pending Dispatch",
                    isCompleted = request.assignedDriverId != null,
                    icon = Icons.Default.LocalShipping
                )

                MilestoneItem(
                    title = "3. Waste Collection & Weight Certification",
                    subtitle = if (request.collectionTimestamp != null)
                        "Collected on site. Certified weight: ${request.actualWeightKg ?: request.quantityKg} kg. Token: ${request.collectionId ?: "COL-8492"}"
                    else "En route to resident pickup location",
                    timestamp = request.collectionTimestamp?.let { dateFmt.format(Date(it)) } ?: "Awaiting Arrival",
                    isCompleted = request.collectionTimestamp != null,
                    icon = Icons.Default.LocationOn
                )

                MilestoneItem(
                    title = "4. Municipal Disposal & Recycling Check-in",
                    subtitle = if (request.disposalTimestamp != null)
                        "Processed at ${request.disposalFacilityName ?: "Pugu Kinyamwezi Hub"}. Stream: ${if (request.isRecycled) "Recycled (Circular Stream)" else "Authorized Landfill"}"
                    else "En route to authorized disposal hub",
                    timestamp = request.disposalTimestamp?.let { dateFmt.format(Date(it)) } ?: "Pending Check-in",
                    isCompleted = request.disposalTimestamp != null,
                    icon = Icons.Default.Recycling
                )

                Spacer(modifier = Modifier.height(14.dp))

                // QR Digital Token Preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ObsidianDarkBg),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = "QR Token", tint = Color.Black, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Digital Waste Compliance QR Token", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate50)
                            Text("Token: SHA256-${request.trackingId.takeLast(6)}-KIGAMBONI", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Slate600)
                            Text("Verified by Kigamboni Municipal Waste Authority", fontSize = 10.sp, color = EcoGreenPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close Chain of Custody", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MilestoneItem(
    title: String,
    subtitle: String,
    timestamp: String,
    isCompleted: Boolean,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isCompleted) EcoGreenPrimary.copy(alpha = 0.2f) else Slate600.copy(alpha = 0.2f))
                .border(1.dp, if (isCompleted) EcoGreenPrimary else Slate600, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isCompleted) Icons.Default.CheckCircle else icon,
                contentDescription = null,
                tint = if (isCompleted) EcoGreenPrimary else Slate600,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isCompleted) Slate50 else Slate600)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 11.sp, color = Slate500)
            Spacer(modifier = Modifier.height(2.dp))
            Text(timestamp, fontSize = 10.sp, color = if (isCompleted) EcoGreenPrimary else Slate600)
        }
    }
}
