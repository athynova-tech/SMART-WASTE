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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.data.local.UserEntity
import com.example.data.model.BusinessType
import com.example.data.model.ScheduleFrequency
import com.example.data.model.WasteCategory
import com.example.ui.theme.CharcoalOutline
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.EcoGreenLight
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.ObsidianDarkBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleCollectionPlanDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onSubmitPlan: (
        frequency: ScheduleFrequency,
        selectedDays: String,
        timeSlot: String,
        category: WasteCategory,
        binCount: Int,
        containerType: String,
        pickupAddress: String,
        ward: String,
        landmark: String,
        latitude: Double,
        longitude: Double,
        monthlyRateTzs: Int
    ) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

    // Frequency & Timing
    var selectedFrequency by remember { mutableStateOf(ScheduleFrequency.MON_WED_FRI) }
    var selectedDays by remember { mutableStateOf("Monday, Wednesday, Friday") }
    var timeSlot by remember { mutableStateOf("06:00 AM - 08:00 AM (Dawn Shift)") }

    // Waste details & Containers
    var selectedCategory by remember { mutableStateOf(WasteCategory.ORGANIC) }
    var binCount by remember { mutableIntStateOf(user.businessBinCount) }
    var containerType by remember { mutableStateOf(user.businessBinType ?: "240L Heavy Duty Wheelie Bin") }

    // Location & Loading Bay
    var address by remember { mutableStateOf(user.address.ifBlank { "Plot 104, Mjimwema Beach Front" }) }
    var ward by remember { mutableStateOf(user.ward.ifBlank { "Mjimwema" }) }
    var landmark by remember { mutableStateOf(user.businessLoadingDockNotes ?: "Rear service driveway / loading bay behind kitchen") }
    var latitude by remember { mutableStateOf(user.businessLatitude) }
    var longitude by remember { mutableStateOf(user.businessLongitude) }

    // Calculate monthly subscription estimate
    val baseRate = when (selectedFrequency) {
        ScheduleFrequency.DAILY -> 380000
        ScheduleFrequency.MON_WED_FRI -> 220000
        ScheduleFrequency.TUE_THU_SAT -> 220000
        ScheduleFrequency.WEEKLY -> 120000
        ScheduleFrequency.BIWEEKLY -> 80000
        ScheduleFrequency.MONTHLY -> 50000
    }
    val containerMultiplier = when {
        containerType.contains("1100L") -> 2.2f
        containerType.contains("660L") -> 1.6f
        containerType.contains("Skip") -> 3.5f
        else -> 1.0f
    }
    val calculatedMonthlyRate = ((baseRate * containerMultiplier) + (binCount * 15000)).toInt()
    val formattedRate = NumberFormat.getNumberInstance(Locale.US).format(calculatedMonthlyRate)

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
                            Icon(
                                imageVector = Icons.Default.EventRepeat,
                                contentDescription = null,
                                tint = MunicipalBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Schedule Collection Contract",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Step $step of 3 • ${if (step == 1) "Frequency & Timing" else if (step == 2) "Bins & Loading Dock" else "Contract & SLA Confirmation"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Business identification banner
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${user.businessName ?: user.fullName} (${user.businessType ?: "Commercial"})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = user.ward,
                            fontSize = 11.sp,
                            color = MunicipalBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (step) {
                        1 -> {
                            // STEP 1: Frequency & Timing
                            item {
                                Text(
                                    text = "1. Collection Frequency",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val frequencies = listOf(
                                    ScheduleFrequency.DAILY to "Daily Collection (7 days/wk - Hotels & Supermarkets)",
                                    ScheduleFrequency.MON_WED_FRI to "Mon / Wed / Fri (3 times/wk - Cafes & Restaurants)",
                                    ScheduleFrequency.TUE_THU_SAT to "Tue / Thu / Sat (3 times/wk - Retail & Offices)",
                                    ScheduleFrequency.WEEKLY to "Weekly (1 time/wk - Light Commercial)",
                                    ScheduleFrequency.BIWEEKLY to "Bi-Weekly (Every 2 weeks - Low Volume)",
                                    ScheduleFrequency.MONTHLY to "Monthly Scheduled Purge"
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    frequencies.forEach { (freq, desc) ->
                                        val isSelected = selectedFrequency == freq
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    selectedFrequency = freq
                                                    selectedDays = when (freq) {
                                                        ScheduleFrequency.DAILY -> "Every Day (Mon - Sun)"
                                                        ScheduleFrequency.MON_WED_FRI -> "Monday, Wednesday, Friday"
                                                        ScheduleFrequency.TUE_THU_SAT -> "Tuesday, Thursday, Saturday"
                                                        ScheduleFrequency.WEEKLY -> "Every Monday"
                                                        ScheduleFrequency.BIWEEKLY -> "1st & 3rd Wednesday of Month"
                                                        ScheduleFrequency.MONTHLY -> "1st Day of Every Month"
                                                    }
                                                },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) MunicipalBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) MunicipalBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isSelected) MunicipalBlue else Color.Transparent)
                                                        .border(1.5.dp, if (isSelected) MunicipalBlue else Slate400, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (isSelected) {
                                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = freq.title,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) MunicipalBlue else MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = desc,
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "2. Preferred Pickup Time Window",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val timeSlots = listOf(
                                    "06:00 AM - 08:00 AM (Dawn Shift - Before Business Hours)",
                                    "08:30 AM - 11:30 AM (Morning Service Window)",
                                    "13:00 PM - 16:00 PM (Afternoon Fleet Window)",
                                    "20:00 PM - 23:00 PM (Night Shift - Restaurant/Hotel Closing)"
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    timeSlots.forEach { slot ->
                                        val isSelected = timeSlot == slot
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { timeSlot = slot },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) EcoGreenPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            ),
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) EcoGreenPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Schedule,
                                                    contentDescription = null,
                                                    tint = if (isSelected) EcoGreenPrimary else Slate400,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = slot,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) EcoGreenPrimary else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // STEP 2: Bins, Containers & Loading Bay
                            item {
                                Text(
                                    text = "1. Primary Waste Category",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val categories = listOf(
                                    WasteCategory.ORGANIC,
                                    WasteCategory.PLASTIC,
                                    WasteCategory.PAPER,
                                    WasteCategory.GLASS,
                                    WasteCategory.GENERAL_HOUSEHOLD,
                                    WasteCategory.E_WASTE
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    categories.take(3).forEach { cat ->
                                        FilterChip(
                                            selected = selectedCategory == cat,
                                            onClick = { selectedCategory = cat },
                                            label = { Text(cat.title, fontSize = 10.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = EcoGreenPrimary,
                                                selectedLabelColor = Color.Black
                                            )
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    categories.drop(3).forEach { cat ->
                                        FilterChip(
                                            selected = selectedCategory == cat,
                                            onClick = { selectedCategory = cat },
                                            label = { Text(cat.title, fontSize = 10.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = EcoGreenPrimary,
                                                selectedLabelColor = Color.Black
                                            )
                                        )
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "2. Container Type & Number of Bins",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val containerTypes = listOf(
                                    "240L Heavy Duty Wheelie Bin",
                                    "660L Commercial Wheelie Bin",
                                    "1100L Four-Wheel Industrial Dumpster",
                                    "5-Ton Municipal Skip Container"
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                                    .padding(10.dp),
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

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Number of Containers:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { if (binCount > 1) binCount-- },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                        }
                                        Text(
                                            text = "$binCount",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 14.dp),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        IconButton(
                                            onClick = { if (binCount < 20) binCount++ },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = "3. Loading Dock & Service Entrance Location",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = address,
                                    onValueChange = { address = it },
                                    label = { Text("Physical Business Address", fontSize = 11.sp) },
                                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MunicipalBlue) },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = landmark,
                                    onValueChange = { landmark = it },
                                    label = { Text("Loading Bay / Gate / Service Instructions", fontSize = 11.sp) },
                                    placeholder = { Text("e.g. Rear kitchen ramp, security gate #2") },
                                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = MunicipalBlue) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        3 -> {
                            // STEP 3: Contract Summary & Confirmation
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MunicipalBlue.copy(alpha = 0.5f))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "Municipal Service Contract",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MunicipalBlue
                                                )
                                                Text(
                                                    text = "Official Ref: MUN-CORP-2026-${(100..999).random()}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Icon(Icons.Default.Verified, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(22.dp))
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                        Spacer(modifier = Modifier.height(10.dp))

                                        ContractRow("Entity Name", user.businessName ?: user.fullName)
                                        ContractRow("Business Type", user.businessType ?: "Commercial Enterprise")
                                        ContractRow("Frequency", selectedFrequency.title)
                                        ContractRow("Collection Days", selectedDays)
                                        ContractRow("Time Slot", timeSlot)
                                        ContractRow("Waste Type", selectedCategory.title)
                                        ContractRow("Containers", "$binCount x $containerType")
                                        ContractRow("Pickup Address", "$address ($ward)")
                                        ContractRow("Service Entrance", landmark)

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("Estimated Monthly Tariff", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text("Includes Municipal Landfill & Recycling Levy", fontSize = 9.sp, color = Slate400)
                                            }
                                            Text(
                                                text = "TZS $formattedRate / mo",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EcoGreenPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = EcoGreenPrimary.copy(alpha = 0.1f)),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenPrimary.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Guaranteed Municipal Collection SLA: Dedicated commercial compactor route with real-time GPS tracking and monthly audit certificates.",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step-- },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back", fontSize = 12.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (step < 3) {
                        Button(
                            onClick = { step++ },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text("Next: ${if (step == 1) "Bins & Dock" else "Summary"}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = {
                                onSubmitPlan(
                                    selectedFrequency,
                                    selectedDays,
                                    timeSlot,
                                    selectedCategory,
                                    binCount,
                                    containerType,
                                    address,
                                    ward,
                                    landmark,
                                    latitude,
                                    longitude,
                                    calculatedMonthlyRate
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("submit_schedule_plan_btn")
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Activate Scheduled Plan", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContractRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
