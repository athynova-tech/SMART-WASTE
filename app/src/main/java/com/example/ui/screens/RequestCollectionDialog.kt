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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PaymentMethod
import com.example.data.model.RequestUrgency
import com.example.data.model.WasteCategory
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestCollectionDialog(
    onDismiss: () -> Unit,
    onSubmit: (
        category: WasteCategory,
        quantityKg: Int,
        bagCount: Int,
        description: String,
        pickupAddress: String,
        ward: String,
        district: String,
        latitude: Double,
        longitude: Double,
        preferredDate: String,
        preferredTimeSlot: String,
        urgency: RequestUrgency,
        paymentMethod: PaymentMethod,
        isRecurring: Boolean,
        recurringInterval: String
    ) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }

    // Form state - using intuitive standard bags / containers without requiring residents to weigh trash
    var selectedCategory by remember { mutableStateOf(WasteCategory.GENERAL_HOUSEHOLD) }
    var bagCount by remember { mutableIntStateOf(3) }
    var description by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf(RequestUrgency.NORMAL) }

    var address by remember { mutableStateOf("Plot 42, Kibada Main Road, Kigamboni") }
    var ward by remember { mutableStateOf("Kibada") }
    var district by remember { mutableStateOf("Kigamboni District") }
    var preferredDate by remember { mutableStateOf("Today") }
    var timeSlot by remember { mutableStateOf("08:00 AM - 11:00 AM") }
    var isRecurring by remember { mutableStateOf(false) }
    var recurringInterval by remember { mutableStateOf("WEEKLY") }

    var paymentMethod by remember { mutableStateOf(PaymentMethod.MPESA) }

    // Price calculation based on standard bag / container count
    val basePrice = (selectedCategory.basePricePerBag * bagCount)
    val finalPrice = (basePrice * urgency.priceMultiplier).toInt().coerceAtLeast(2500)
    val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(finalPrice)

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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Request Waste Collection",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Step $step of 3 • ${if (step == 1) "Waste Details" else if (step == 2) "Pickup Location" else "Payment & Checkout"}",
                            fontSize = 12.sp,
                            color = Slate600
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate600)
                    }
                }

                // Step progress bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (s in 1..3) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (s <= step) EcoGreenPrimary else Slate200)
                        )
                    }
                }

                // Scrollable Content per step
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (step) {
                        1 -> {
                            // Step 1: Waste Type, Bag Count & Urgency
                            item {
                                Text("Select Waste Category", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate800)
                                Spacer(modifier = Modifier.height(6.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    WasteCategory.values().forEach { cat ->
                                        val isSelected = selectedCategory == cat
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { selectedCategory = cat },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) EcoGreenLight else Slate100
                                            ),
                                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, EcoGreenPrimary) else null
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(cat.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Slate900)
                                                        if (cat.isRecyclable) {
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Box(
                                                                modifier = Modifier
                                                                    .clip(RoundedCornerShape(4.dp))
                                                                    .background(EcoGreenPrimary.copy(alpha = 0.2f))
                                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                                            ) {
                                                                Text("Recyclable", fontSize = 9.sp, color = EcoGreenPrimary, fontWeight = FontWeight.Bold)
                                                            }
                                                        }
                                                    }
                                                    Text(cat.description, fontSize = 11.sp, color = Slate600)
                                                }
                                                Text(
                                                    text = "${cat.basePricePerBag} TZS/bag",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) EcoGreenPrimary else Slate700
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Text("Waste Quantity (Number of Bags / Bins)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate800)
                                Spacer(modifier = Modifier.height(6.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Slate100)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "$bagCount Standard Bag${if (bagCount > 1) "s" else ""} / Bin${if (bagCount > 1) "s" else ""}",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Slate900
                                            )
                                            Text(
                                                text = when {
                                                    bagCount <= 2 -> "Small Household Volume (~1-2 standard bags)"
                                                    bagCount <= 5 -> "Standard Family Weekly Volume (~3-5 bags)"
                                                    bagCount <= 8 -> "Medium Cleanout / Yard Refuse (~6-8 bags)"
                                                    else -> "Large Bulk Cleanout / High Volume"
                                                },
                                                fontSize = 11.sp,
                                                color = Slate600
                                            )
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { if (bagCount > 1) bagCount-- },
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White)
                                                    .border(1.dp, Slate200, CircleShape)
                                            ) {
                                                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Slate800, modifier = Modifier.size(16.dp))
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text("$bagCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MunicipalBlue)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            IconButton(
                                                onClick = { if (bagCount < 25) bagCount++ },
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .background(EcoGreenPrimary)
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(1 to "1 Bag", 3 to "3 Bags", 5 to "5 Bags", 8 to "8 Bags", 12 to "12+ Bulk").forEach { (count, label) ->
                                        val isSel = bagCount == count
                                        OutlinedButton(
                                            onClick = { bagCount = count },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = if (isSel) EcoGreenLight else Color.Transparent
                                            ),
                                            border = if (isSel) androidx.compose.foundation.BorderStroke(1.5.dp, EcoGreenPrimary) else androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp, horizontal = 2.dp)
                                        ) {
                                            Text(label, fontSize = 10.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = if (isSel) EcoGreenPrimary else Slate800)
                                        }
                                    }
                                }
                            }

                            item {
                                Text("Collection Urgency", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate800)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RequestUrgency.values().forEach { u ->
                                        val isSelected = urgency == u
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { urgency = u },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) Color(u.badgeHex).copy(alpha = 0.15f) else Slate100
                                            ),
                                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(u.badgeHex)) else null
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text(u.title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(u.badgeHex))
                                                Text(if (u.priceMultiplier == 1.0) "Standard" else "+${((u.priceMultiplier - 1.0) * 100).toInt()}% fee", fontSize = 9.sp, color = Slate600)
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                OutlinedTextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    label = { Text("Waste Description / Gate Notes (Optional)") },
                                    placeholder = { Text("e.g. 3 tied bags outside the gate") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        2 -> {
                            // Step 2: Location, Date & Recurring
                            item {
                                Text("Pickup Location Details", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate800)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = address,
                                    onValueChange = { address = it },
                                    label = { Text("Street Address / Landmark") },
                                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = EcoGreenPrimary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            item {
                                Text("Municipal Ward (Dar es Salaam)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                                val wards = listOf("Kibada", "Kigamboni", "Mjimwema", "Kurasini", "Mikocheni", "Ilala", "Kariakoo", "Mbezi")
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(wards) { w ->
                                        val isSel = ward == w
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) MunicipalBlue else Slate100)
                                                .clickable {
                                                    ward = w
                                                    district = if (w in listOf("Kibada", "Kigamboni", "Mjimwema")) "Kigamboni District" else if (w in listOf("Mikocheni", "Mbezi")) "Kinondoni District" else "Ilala District"
                                                }
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(w, color = if (isSel) Color.White else Slate800, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }

                            item {
                                Text("Preferred Pickup Schedule", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate800)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("Today", "Tomorrow", "Saturday").forEach { d ->
                                        val isSel = preferredDate == d
                                        OutlinedButton(
                                            onClick = { preferredDate = d },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = if (isSel) EcoGreenLight else Color.Transparent
                                            )
                                        ) {
                                            Text(d, fontSize = 11.sp, color = if (isSel) EcoGreenPrimary else Slate800)
                                        }
                                    }
                                }
                            }

                            item {
                                Text("Preferred Time Window", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                                val slots = listOf("08:00 AM - 11:00 AM", "12:00 PM - 03:00 PM", "04:00 PM - 07:00 PM")
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    slots.forEach { slot ->
                                        val isSel = timeSlot == slot
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { timeSlot = slot },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(containerColor = if (isSel) MunicipalBlueLight else Slate100)
                                        ) {
                                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Schedule, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(slot, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Slate900)
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Slate100)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isRecurring,
                                            onCheckedChange = { isRecurring = it }
                                        )
                                        Column {
                                            Text("Set as Recurring Collection", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                            Text("Auto-schedules weekly or monthly truck pickups", fontSize = 10.sp, color = Slate600)
                                        }
                                    }
                                }
                            }
                        }

                        3 -> {
                            // Step 3: Payment & Summary
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = EcoGreenLight)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Total Estimated Fee", fontSize = 13.sp, color = Slate700)
                                            Text("TZS $formattedPrice", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EcoGreenPrimary)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Divider(color = EcoGreenPrimary.copy(alpha = 0.2f))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("• ${selectedCategory.title} ($bagCount bag${if (bagCount > 1) "s" else ""} @ ${selectedCategory.basePricePerBag} TZS/bag)", fontSize = 11.sp, color = Slate800)
                                        Text("• Urgency Multiplier: ${urgency.title} (${urgency.priceMultiplier}x)", fontSize = 11.sp, color = Slate800)
                                        Text("• Destination: $ward, $district", fontSize = 11.sp, color = Slate800)
                                        Text("• Schedule: $preferredDate ($timeSlot)", fontSize = 11.sp, color = Slate800)
                                    }
                                }
                            }

                            item {
                                Text("Select Mobile Money / Payment Method", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate800)
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    PaymentMethod.values().forEach { method ->
                                        val isSel = paymentMethod == method
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { paymentMethod = method },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSel) MunicipalBlueLight else Slate100
                                            ),
                                            border = if (isSel) androidx.compose.foundation.BorderStroke(1.5.dp, MunicipalBlue) else null
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Payments, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(method.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
                                                }
                                                if (isSel) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer Actions
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step-- },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    if (step < 3) {
                        Button(
                            onClick = { step++ },
                            colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    } else {
                        Button(
                            onClick = {
                                onSubmit(
                                    selectedCategory,
                                    bagCount * 15, // automated background capacity estimate (15kg per bag)
                                    bagCount,
                                    description,
                                    address,
                                    ward,
                                    district,
                                    -6.8400,
                                    39.3100,
                                    preferredDate,
                                    timeSlot,
                                    urgency,
                                    paymentMethod,
                                    isRecurring,
                                    recurringInterval
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Pay TZS $formattedPrice & Request")
                        }
                    }
                }
            }
        }
    }
}
