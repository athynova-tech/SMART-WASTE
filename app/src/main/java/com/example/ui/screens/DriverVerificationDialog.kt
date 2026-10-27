package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.DriverApplicationEntity
import com.example.ui.components.DriverStatusBadge
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun DriverVerificationDialog(
    application: DriverApplicationEntity,
    onDismiss: () -> Unit,
    onApprove: (notes: String) -> Unit,
    onReject: (notes: String) -> Unit
) {
    var commissionerNotes by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 650.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Driver Document Verification",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Municipal Commissioner Review & Approval",
                            fontSize = 11.sp,
                            color = MunicipalBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate600)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(application.fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            DriverStatusBadge(status = application.status)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Phone: ${application.phone} • Email: ${application.email}", fontSize = 11.sp, color = Slate600)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Document inspection list
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Verified Government Identifiers", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("NIDA Identity Number:", fontSize = 11.sp, color = Slate600)
                        Text(application.nidaMasked, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Driving License:", fontSize = 11.sp, color = Slate600)
                        Text("${application.licenseNumber} (${application.licenseClass})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("License Expiry:", fontSize = 11.sp, color = Slate600)
                        Text(application.licenseExpiryDate, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = EcoGreenPrimary)
                    }

                    Divider(color = Slate200)

                    Text("Vehicle Roadworthiness & Capacity", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Vehicle Registration:", fontSize = 11.sp, color = Slate600)
                        Text(application.vehicleRegNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Type & Payload Capacity:", fontSize = 11.sp, color = Slate600)
                        Text("${application.vehicleType} • ${application.vehicleCapacityKg} kg", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Insurance Valid Until:", fontSize = 11.sp, color = Slate600)
                        Text(application.insuranceExpiryDate, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Operational Sector:", fontSize = 11.sp, color = Slate600)
                        Text(application.preferredZone, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MunicipalBlue)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = commissionerNotes,
                    onValueChange = { commissionerNotes = it },
                    label = { Text("Commissioner Verification Notes / Remarks") },
                    placeholder = { Text("e.g. Identity verified via NIDA database. Road safety certificate approved.") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onReject(commissionerNotes) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reject Application")
                    }

                    Button(
                        onClick = { onApprove(commissionerNotes) },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.4f)
                    ) {
                        Text("Approve & Issue Driver ID", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
