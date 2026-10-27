package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.MunicipalBlueLight
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun DriverApplicationDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        fullName: String,
        phone: String,
        email: String,
        nidaNumber: String,
        licenseNumber: String,
        licenseClass: String,
        licenseExpiry: String,
        vehicleReg: String,
        vehicleType: String,
        capacityKg: Int,
        insuranceExpiry: String,
        preferredZone: String
    ) -> Unit
) {
    var fullName by remember { mutableStateOf("Rashid Said Kondo") }
    var phone by remember { mutableStateOf("+255 713 554 433") }
    var email by remember { mutableStateOf("rashid.kondo@transwaste.co.tz") }
    var nidaNumber by remember { mutableStateOf("19890412-14101-00003-24") }
    var licenseNumber by remember { mutableStateOf("TZ-DL-55209-E") }
    var licenseClass by remember { mutableStateOf("Class C (Heavy Commercial)") }
    var licenseExpiry by remember { mutableStateOf("2029-05-30") }
    var vehicleReg by remember { mutableStateOf("T 789 GHI") }
    var vehicleType by remember { mutableStateOf("Rear Loader Compactor") }
    var capacityKg by remember { mutableStateOf("3500") }
    var insuranceExpiry by remember { mutableStateOf("2027-10-15") }
    var preferredZone by remember { mutableStateOf("Kigamboni & Kibada") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 680.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Driver & Vehicle Application",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Official Municipal Verification Workflow",
                            fontSize = 11.sp,
                            color = MunicipalBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate600)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MunicipalBlueLight)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "All submissions are reviewed directly by the District Municipal Commissioner before license dispatch.",
                            fontSize = 11.sp,
                            color = MunicipalBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("1. Personal & Identity Verification", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)
                    }
                    item {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Legal Name (as per NIDA)") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Slate600) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Phone Number") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Slate600) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Slate600) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = nidaNumber,
                            onValueChange = { nidaNumber = it },
                            label = { Text("NIDA Number (National Identity)") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Slate600) },
                            placeholder = { Text("19890412-14101-00003-24") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("2. Driving License & Certifications", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = licenseNumber,
                                onValueChange = { licenseNumber = it },
                                label = { Text("Driving License ID") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = licenseExpiry,
                                onValueChange = { licenseExpiry = it },
                                label = { Text("License Expiry") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("3. Vehicle Specifications & Capacity", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate800)
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = vehicleReg,
                                onValueChange = { vehicleReg = it },
                                label = { Text("Vehicle Plate (e.g. T 123 ABC)") },
                                leadingIcon = { Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Slate600) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = capacityKg,
                                onValueChange = { capacityKg = it },
                                label = { Text("Payload Capacity (Kg)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = vehicleType,
                            onValueChange = { vehicleType = it },
                            label = { Text("Vehicle Type (Compactor, Tipper, Hook-lift)") },
                            leadingIcon = { Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Slate600) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        Text("Preferred Operating Zone", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                        val zones = listOf("Kigamboni & Kibada", "Kinondoni & Mikocheni", "Ilala & Kariakoo", "Temeke & Kurasini", "All Municipal Sectors")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(zones) { z ->
                                val isSel = preferredZone == z
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) MunicipalBlue else Slate100)
                                        .clickable { preferredZone = z }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(z, color = if (isSel) Color.White else Slate800, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onSubmit(
                            fullName,
                            phone,
                            email,
                            nidaNumber,
                            licenseNumber,
                            licenseClass,
                            licenseExpiry,
                            vehicleReg,
                            vehicleType,
                            capacityKg.toIntOrNull() ?: 2500,
                            insuranceExpiry,
                            preferredZone
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Application to Commissioner", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
