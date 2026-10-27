package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.InAppNotification
import com.example.ui.theme.AmberWarning
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
fun NotificationCenterDialog(
    notifications: List<InAppNotification>,
    onDismiss: () -> Unit,
    onMarkAllAsRead: () -> Unit,
    onTrackRequest: (requestId: String) -> Unit = {},
    onViewReceipt: (requestId: String) -> Unit = {},
    onRateService: (requestId: String) -> Unit = {}
) {
    val dateFmt = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, CharcoalOutline, RoundedCornerShape(20.dp))
                .testTag("notification_center_dialog"),
            color = CharcoalSurface
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MunicipalBlue.copy(alpha = 0.15f))
                                .border(1.dp, MunicipalBlue.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MunicipalBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Municipal Notification Feed",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )
                            Text(
                                text = "${notifications.size} alerts • ${notifications.count { !it.isRead }} unread",
                                fontSize = 11.sp,
                                color = Slate600
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE RESIDENT ALERTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate600,
                        letterSpacing = 0.5.sp
                    )
                    Row(
                        modifier = Modifier
                            .clickable { onMarkAllAsRead() }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Mark all as read",
                            fontSize = 11.sp,
                            color = EcoGreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No notifications at this time.", color = Slate500, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(390.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications) { notif ->
                            val icon: ImageVector = when (notif.type) {
                                "WASTE_ASSIGNED", "DRIVER_ASSIGNED", "DISPATCH" -> Icons.Default.LocalShipping
                                "DRIVER_NEARBY" -> Icons.Default.NearMe
                                "PICKUP_COMPLETED" -> Icons.Default.CheckCircle
                                "PAYMENT" -> Icons.Default.Payment
                                "COMPLIANCE" -> Icons.Default.Warning
                                "RECYCLING" -> Icons.Default.Recycling
                                else -> Icons.Default.Notifications
                            }
                            val iconColor = when (notif.type) {
                                "WASTE_ASSIGNED", "DRIVER_ASSIGNED" -> EcoGreenPrimary
                                "DRIVER_NEARBY" -> MunicipalBlue
                                "PICKUP_COMPLETED" -> EcoGreenPrimary
                                "DISPATCH" -> MunicipalBlue
                                "PAYMENT" -> EcoGreenPrimary
                                "COMPLIANCE" -> AmberWarning
                                "RECYCLING" -> EcoGreenPrimary
                                else -> Slate50
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (!notif.isRead) ObsidianDarkBg else ObsidianDarkBg.copy(alpha = 0.6f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (!notif.isRead) 1.5.dp else 1.dp,
                                    color = if (!notif.isRead) iconColor.copy(alpha = 0.5f) else CharcoalOutline
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(iconColor.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(notif.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                                    if (!notif.isRead) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .size(6.dp)
                                                                .clip(CircleShape)
                                                                .background(iconColor)
                                                        )
                                                    }
                                                }
                                                Text(dateFmt.format(Date(notif.timestamp)), fontSize = 10.sp, color = Slate600)
                                            }
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(notif.message, fontSize = 11.sp, color = if (!notif.isRead) Slate50.copy(alpha = 0.85f) else Slate500)
                                        }
                                    }

                                    // Contextual Action Buttons in notification item
                                    if (notif.requestId != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (notif.type == "DRIVER_NEARBY" || notif.actionType == "TRACK") {
                                                OutlinedButton(
                                                    onClick = {
                                                        onDismiss()
                                                        onTrackRequest(notif.requestId)
                                                    },
                                                    modifier = Modifier.height(28.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MunicipalBlue),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, MunicipalBlue.copy(alpha = 0.5f)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(12.dp), tint = MunicipalBlue)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Track Live", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            if (notif.type == "PICKUP_COMPLETED" || notif.actionType == "RECEIPT") {
                                                OutlinedButton(
                                                    onClick = {
                                                        onDismiss()
                                                        onViewReceipt(notif.requestId)
                                                    },
                                                    modifier = Modifier.height(28.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EcoGreenPrimary),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenPrimary.copy(alpha = 0.5f)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(12.dp), tint = EcoGreenPrimary)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("View Receipt", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                OutlinedButton(
                                                    onClick = {
                                                        onDismiss()
                                                        onRateService(notif.requestId)
                                                    },
                                                    modifier = Modifier.height(28.dp),
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFBBF24)),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBBF24).copy(alpha = 0.5f)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                                ) {
                                                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFFBBF24))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Rate", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
