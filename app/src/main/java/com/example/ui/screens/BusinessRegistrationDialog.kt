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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BusinessType
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.Slate400

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessRegistrationDialog(
    initialName: String = "",
    initialEmail: String = "",
    initialPhone: String = "",
    initialWard: String = "Kibada",
    onDismiss: () -> Unit,
    onRegister: (
        companyName: String,
        businessType: BusinessType,
        tinNumber: String,
        contactPerson: String,
        email: String,
        phone: String,
        physicalAddress: String,
        ward: String,
        district: String,
        loadingDockNotes: String,
        containerType: String,
        binCount: Int,
        operatingHours: String,
        latitude: Double,
        longitude: Double
    ) -> Unit
) {
    var companyName by remember { mutableStateOf(initialName.ifBlank { "Bahari Breeze Hotel & Cafe" }) }
    var selectedType by remember { mutableStateOf(BusinessType.HOTEL) }
    var tinNumber by remember { mutableStateOf("142-889-301") }
    var contactPerson by remember { mutableStateOf("David Msigwa (Ops Manager)") }
    var email by remember { mutableStateOf(initialEmail.ifBlank { "ops@baharibreeze.co.tz" }) }
    var phone by remember { mutableStateOf(initialPhone.ifBlank { "+255 754 889 900" }) }
    var physicalAddress by remember { mutableStateOf("Plot 88, Ocean Drive, Kigamboni") }
    var ward by remember { mutableStateOf(initialWard) }
    var district by remember { mutableStateOf("Kigamboni District") }
    var loadingDockNotes by remember { mutableStateOf("Rear kitchen receiving bay, ground floor ramp") }
    var containerType by remember { mutableStateOf("1100L Four-Wheel Industrial Dumpster") }
    var binCount by remember { mutableIntStateOf(2) }
    var operatingHours by remember { mutableStateOf("06:00 AM - 23:30 PM (Daily)") }
    var latitude by remember { mutableStateOf(-6.8550) }
    var longitude by remember { mutableStateOf(39.3180) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MunicipalBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Business, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Commercial Business Registration",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Hotels, Cafes, Restaurants, Supermarkets & Offices",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("1. Business Entity Information", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = { Text("Company / Trade Name", fontSize = 11.sp) },
                            placeholder = { Text("e.g. Serena Hotel, Aroma Cafe, City Bakery") },
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = MunicipalBlue) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("business_reg_name_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Business Category", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))

                        val types = BusinessType.values().toList()
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            types.chunked(3).forEach { chunk ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    chunk.forEach { type ->
                                        FilterChip(
                                            selected = selectedType == type,
                                            onClick = { selectedType = type },
                                            label = { Text(type.title, fontSize = 9.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MunicipalBlue,
                                                selectedLabelColor = Color.Black
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = tinNumber,
                                onValueChange = { tinNumber = it },
                                label = { Text("TIN / Tax Number", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = operatingHours,
                                onValueChange = { operatingHours = it },
                                label = { Text("Operating Hours", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Text("2. Contact & Key Responsible Person", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = contactPerson,
                            onValueChange = { contactPerson = it },
                            label = { Text("Contact Person / Operations Lead", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MunicipalBlue) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Corporate Email", fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Slate400) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Direct Phone Number", fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Slate400) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Text("3. Location & Loading Dock / Service Bay", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = physicalAddress,
                            onValueChange = { physicalAddress = it },
                            label = { Text("Physical Plot & Street Address", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MunicipalBlue) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = ward,
                                onValueChange = { ward = it },
                                label = { Text("Municipal Ward", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = district,
                                onValueChange = { district = it },
                                label = { Text("District", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = loadingDockNotes,
                            onValueChange = { loadingDockNotes = it },
                            label = { Text("Loading Bay / Gate Access Instructions", fontSize = 11.sp) },
                            placeholder = { Text("e.g. Back door entrance, security buzzer, ramp") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text("4. Waste Containers Setup", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                        Spacer(modifier = Modifier.height(6.dp))

                        val containerTypes = listOf(
                            "240L Heavy Duty Wheelie Bin",
                            "660L Commercial Wheelie Bin",
                            "1100L Four-Wheel Industrial Dumpster",
                            "5-Ton Municipal Skip Container"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            containerTypes.forEach { type ->
                                val isSelected = containerType == type
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { containerType = type },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MunicipalBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) MunicipalBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Recycling, contentDescription = null, tint = if (isSelected) MunicipalBlue else Slate400, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = type,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MunicipalBlue else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val finalCompanyName = companyName.ifBlank { "Commercial Entity" }
                        val finalContact = contactPerson.ifBlank { "Operations Manager" }
                        val finalEmail = email.ifBlank { "commercial_${System.currentTimeMillis().toString().takeLast(4)}@kigamboni.co.tz" }
                        val finalPhone = phone.ifBlank { "+255 754 000 111" }
                        val finalAddress = physicalAddress.ifBlank { "Plot 40, Kigamboni Main Street" }

                        onRegister(
                            finalCompanyName,
                            selectedType,
                            tinNumber,
                            finalContact,
                            finalEmail,
                            finalPhone,
                            finalAddress,
                            ward,
                            district,
                            loadingDockNotes,
                            containerType,
                            binCount,
                            operatingHours,
                            latitude,
                            longitude
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_commercial_registration_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Register Commercial Entity & Enter", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
