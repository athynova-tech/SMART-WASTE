package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.DriverApplicationEntity
import com.example.data.local.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.MunicipalBlue

@Composable
fun UserProfileDialog(
    currentUser: UserEntity,
    currentRole: UserRole,
    currentDriverEntity: DriverApplicationEntity? = null,
    onDismiss: () -> Unit,
    onSaveResidentProfile: (UserEntity) -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(currentUser.fullName) }
    var editEmail by remember { mutableStateOf(currentUser.email) }
    var editPhone by remember { mutableStateOf(currentUser.phone) }
    var editAddress by remember { mutableStateOf(currentUser.address) }
    var editWard by remember { mutableStateOf(currentUser.ward) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .testTag("user_profile_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog Header
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
                                .background(
                                    when (currentRole) {
                                        UserRole.RESIDENT -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        UserRole.BUSINESS -> MunicipalBlue.copy(alpha = 0.15f)
                                        UserRole.DRIVER -> Color(0xFF10B981).copy(alpha = 0.15f)
                                        UserRole.MUNICIPAL_COMMISSIONER -> AmberWarning.copy(alpha = 0.15f)
                                        UserRole.DISPOSAL_OFFICER -> Color(0xFF8B5CF6).copy(alpha = 0.15f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (currentRole) {
                                    UserRole.RESIDENT -> Icons.Default.Person
                                    UserRole.BUSINESS -> Icons.Default.Business
                                    UserRole.DRIVER -> Icons.Default.LocalShipping
                                    UserRole.MUNICIPAL_COMMISSIONER -> Icons.Default.Shield
                                    UserRole.DISPOSAL_OFFICER -> Icons.Default.CheckCircle
                                },
                                contentDescription = "User Profile",
                                tint = when (currentRole) {
                                    UserRole.RESIDENT -> MaterialTheme.colorScheme.primary
                                    UserRole.BUSINESS -> MunicipalBlue
                                    UserRole.DRIVER -> Color(0xFF10B981)
                                    UserRole.MUNICIPAL_COMMISSIONER -> AmberWarning
                                    UserRole.DISPOSAL_OFFICER -> Color(0xFF8B5CF6)
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = when (currentRole) {
                                    UserRole.RESIDENT -> "Resident Profile"
                                    UserRole.BUSINESS -> "Commercial Entity Profile"
                                    UserRole.DRIVER -> "Driver Profile"
                                    UserRole.MUNICIPAL_COMMISSIONER -> "Municipal Commissioner"
                                    UserRole.DISPOSAL_OFFICER -> "Disposal Officer Profile"
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Kigamboni Smart Waste Portal",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_profile_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ROLE-SPECIFIC SINGLE PROFILE DISPLAY (NO OTHER ROLES OR SWITCHERS)
                when (currentRole) {
                    UserRole.RESIDENT -> {
                        // 1. RESIDENT EXCLUSIVE PROFILE
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = currentUser.fullName,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Verified Citizen • NIDA: ${currentUser.nidaMasked}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Active",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                ProfileInfoRow(icon = Icons.Default.Email, label = "Email", value = currentUser.email)
                                ProfileInfoRow(icon = Icons.Default.Phone, label = "Phone", value = currentUser.phone)
                                ProfileInfoRow(icon = Icons.Default.Home, label = "Address", value = "${currentUser.address}, ${currentUser.ward}")
                                ProfileInfoRow(icon = Icons.Default.LocationOn, label = "Ward", value = "${currentUser.ward} Ward, Kigamboni")
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Edit Resident Profile Toggle / Form
                        AnimatedVisibility(visible = isEditing) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Edit Profile Information",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = editName,
                                        onValueChange = { editName = it },
                                        label = { Text("Full Legal Name", fontSize = 12.sp) },
                                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = profileTextFieldColors()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = editEmail,
                                        onValueChange = { editEmail = it },
                                        label = { Text("Email Address", fontSize = 12.sp) },
                                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = profileTextFieldColors()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = editPhone,
                                        onValueChange = { editPhone = it },
                                        label = { Text("Phone Number", fontSize = 12.sp) },
                                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = profileTextFieldColors()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = editAddress,
                                        onValueChange = { editAddress = it },
                                        label = { Text("Physical Street Address", fontSize = 12.sp) },
                                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = profileTextFieldColors()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = editWard,
                                        onValueChange = { editWard = it },
                                        label = { Text("Ward (e.g. Kibada, Somangila)", fontSize = 12.sp) },
                                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = profileTextFieldColors()
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            val updated = currentUser.copy(
                                                fullName = editName,
                                                email = editEmail,
                                                phone = editPhone,
                                                address = editAddress,
                                                ward = editWard
                                            )
                                            onSaveResidentProfile(updated)
                                            isEditing = false
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Save Profile Changes", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Saved Addresses Section
                        Text(
                            text = "Saved Municipal Pickup Addresses",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val addresses = listOf(
                            "Primary Residence" to "${currentUser.address} - ${currentUser.ward}",
                            "Beach House / Plot" to "Plot 104, South Beach Road, Kigamboni",
                            "Commercial Annex" to "Mjimwema Commercial Market, Block B"
                        )

                        addresses.forEach { (label, addr) ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text(text = addr, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Edit Profile / Done Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { isEditing = !isEditing },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isEditing) "Cancel Edit" else "Edit Profile")
                            }

                            Button(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Done", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    UserRole.DRIVER -> {
                        // 2. DRIVER EXCLUSIVE PROFILE
                        val driver = currentDriverEntity
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = driver?.fullName ?: "Juma Rashid Makame",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Driver ID: ${driver?.assignedDriverId ?: "DRV-8830"} • Rating: 4.9 ★",
                                            fontSize = 12.sp,
                                            color = MunicipalBlue,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MunicipalBlue.copy(alpha = 0.18f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Certified Driver", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                ProfileInfoRow(icon = Icons.Default.Phone, label = "Phone", value = driver?.phone ?: "+255 754 112 233")
                                ProfileInfoRow(icon = Icons.Default.Email, label = "Email", value = driver?.email ?: "juma.rashid@kigamboni.go.tz")
                                ProfileInfoRow(icon = Icons.Default.Badge, label = "Driving License", value = "${driver?.licenseNumber ?: "TZ-DL-88301"} (Class ${driver?.licenseClass ?: "C/E"})")
                                ProfileInfoRow(icon = Icons.Default.DirectionsCar, label = "Vehicle Reg", value = driver?.vehicleRegNumber ?: "T 489 DRT")
                                ProfileInfoRow(icon = Icons.Default.LocalShipping, label = "Truck Type", value = "${driver?.vehicleType ?: "Heavy Compactor Truck"} (${driver?.vehicleCapacityKg ?: 10000} kg)")
                                ProfileInfoRow(icon = Icons.Default.LocationOn, label = "Operational Zone", value = driver?.preferredZone ?: "Kibada & Coastal Corridor")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold)
                        }
                    }

                    UserRole.MUNICIPAL_COMMISSIONER -> {
                        // 3. MUNICIPAL COMMISSIONER EXCLUSIVE PROFILE
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Eng. Athumani Kasele",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Municipal Waste Operations Commissioner",
                                            fontSize = 12.sp,
                                            color = AmberWarning,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AmberWarning.copy(alpha = 0.18f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Directorate", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberWarning)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                ProfileInfoRow(icon = Icons.Default.Business, label = "Council", value = "Kigamboni Municipal Council (KMC)")
                                ProfileInfoRow(icon = Icons.Default.WorkspacePremium, label = "Directorate", value = "Solid Waste, Environmental Health & Fleet Logistics")
                                ProfileInfoRow(icon = Icons.Default.Email, label = "Official Email", value = "commissioner.waste@kigamboni.go.tz")
                                ProfileInfoRow(icon = Icons.Default.Security, label = "Clearance Level", value = "Executive Grade I (Fleet & Enforcement Dispatch)")
                                ProfileInfoRow(icon = Icons.Default.LocationOn, label = "Jurisdiction", value = "All 9 Wards of Kigamboni District, Dar es Salaam")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberWarning),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    UserRole.DISPOSAL_OFFICER -> {
                        // 4. DISPOSAL OFFICER EXCLUSIVE PROFILE
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Grace Temba",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Pugu Municipal Dumpsite & MRF Hub Officer",
                                    fontSize = 12.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                ProfileInfoRow(icon = Icons.Default.Business, label = "Facility", value = "Pugu Municipal Sorting & Recycling Dumpsite")
                                ProfileInfoRow(icon = Icons.Default.CheckCircle, label = "Role", value = "Weighbridge Reception & Disposal Verification")
                                ProfileInfoRow(icon = Icons.Default.Email, label = "Email", value = "grace.temba@kigamboni.go.tz")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold)
                        }
                    }

                    UserRole.BUSINESS -> {
                        // 5. COMMERCIAL BUSINESS EXCLUSIVE PROFILE
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = currentUser.businessName ?: currentUser.fullName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Commercial Waste Account • ${currentUser.ward}",
                                    fontSize = 12.sp,
                                    color = MunicipalBlue,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                ProfileInfoRow(icon = Icons.Default.Business, label = "Business Type", value = currentUser.businessType ?: "Commercial Enterprise")
                                ProfileInfoRow(icon = Icons.Default.Badge, label = "TIN / Tax ID", value = currentUser.businessTin ?: "TIN-109-882-341")
                                ProfileInfoRow(icon = Icons.Default.Person, label = "Contact Person", value = currentUser.businessContactPerson ?: currentUser.fullName)
                                ProfileInfoRow(icon = Icons.Default.Phone, label = "Direct Phone", value = currentUser.businessContactPhone ?: currentUser.phone)
                                ProfileInfoRow(icon = Icons.Default.Email, label = "Billing Email", value = currentUser.businessContactEmail ?: currentUser.email)
                                ProfileInfoRow(icon = Icons.Default.LocationOn, label = "Premises Address", value = currentUser.address)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Unified Session Security & Sign Out Section (Visible for ALL roles)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Session Security",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Account Session",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Signed in as ${when (currentRole) {
                                        UserRole.RESIDENT -> currentUser.fullName
                                        UserRole.BUSINESS -> currentUser.businessName ?: currentUser.fullName
                                        UserRole.DRIVER -> currentDriverEntity?.fullName ?: "Driver"
                                        UserRole.MUNICIPAL_COMMISSIONER -> "Eng. David Malecela"
                                        UserRole.DISPOSAL_OFFICER -> "Grace Temba"
                                    }}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                onDismiss()
                                onSignOut()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("profile_dialog_sign_out_button"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFFEF4444).copy(alpha = 0.08f),
                                contentColor = Color(0xFFEF4444)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sign Out",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun profileTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.primary
)
