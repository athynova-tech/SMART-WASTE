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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.CollectionRequestEntity
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ReceiptDialog(
    request: CollectionRequestEntity,
    onDismiss: () -> Unit
) {
    val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(request.estimatedPriceTzs)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 620.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Municipal Waste Receipt",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate600)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("DAR ES SALAAM CITY COUNCIL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                                Text("Kigamboni Waste Management Service", fontSize = 10.sp, color = Slate600)
                            }
                            Icon(Icons.Default.Verified, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(22.dp))
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Slate200)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tracking ID:", fontSize = 11.sp, color = Slate600)
                            Text(request.trackingId, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }

                        if (request.collectionId != null) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Collection ID:", fontSize = 11.sp, color = Slate600)
                                Text(request.collectionId, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Resident Name:", fontSize = 11.sp, color = Slate600)
                            Text(request.residentName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Address / Ward:", fontSize = 11.sp, color = Slate600)
                            Text("${request.pickupAddress} (${request.ward})", fontSize = 11.sp, color = Slate800)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Waste Category:", fontSize = 11.sp, color = Slate600)
                            Text(request.wasteCategory.title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Waste Volume:", fontSize = 11.sp, color = Slate600)
                            Text("${request.bagCount} Standard Bag${if (request.bagCount > 1) "s" else ""} / Containers", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Assigned Driver:", fontSize = 11.sp, color = Slate600)
                            Text(request.assignedDriverName ?: "Juma Ali Mussa", fontSize = 11.sp, color = Slate800)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Payment Status:", fontSize = 11.sp, color = Slate600)
                            Text("${request.paymentStatus} (${request.paymentMethod?.title ?: "Vodacom M-Pesa"})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoGreenPrimary)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Slate200)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TOTAL PAID", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            Text("TZS $formattedPrice", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EcoGreenPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // QR Code Representation & Digital Stamp
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(EcoGreenLight)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Official Municipal Digital Stamp • Verified",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close Receipt")
                }
            }
        }
    }
}
