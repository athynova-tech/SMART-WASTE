package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.DisposalFacilityEntity
import com.example.data.local.DriverApplicationEntity
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun InteractiveMapCanvas(
    requests: List<CollectionRequestEntity>,
    drivers: List<DriverApplicationEntity>,
    facilities: List<DisposalFacilityEntity>,
    selectedRequestId: String? = null,
    onSelectRequest: ((CollectionRequestEntity) -> Unit)? = null,
    onSelectFacility: ((DisposalFacilityEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 4f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var selectedItemInfo by remember { mutableStateOf<String?>(null) }
    var selectedEntityTitle by remember { mutableStateOf<String?>(null) }
    var selectedEntitySubtitle by remember { mutableStateOf<String?>(null) }

    // Bounding Box for Dar es Salaam / Kigamboni Region
    // Lat range: approx -6.80 to -6.95 (North to South)
    // Lng range: approx 39.20 to 39.40 (West to East)
    val minLat = -6.96
    val maxLat = -6.80
    val minLng = 39.20
    val maxLng = 39.40

    fun projectToCanvas(lat: Double, lng: Double, width: Float, height: Float): Offset {
        val normX = ((lng - minLng) / (maxLng - minLng)).coerceIn(0.05, 0.95).toFloat()
        // Inverted Y since latitude increases northward (up)
        val normY = (1.0f - ((lat - minLat) / (maxLat - minLat))).coerceIn(0.05, 0.95).toFloat()
        return Offset(normX * width, normY * height)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(310.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(requests, facilities, drivers) {
                        detectTapGestures { tapOffset ->
                            val w = size.width.toFloat()
                            val h = size.height.toFloat()

                            // Check tap near requests
                            var matched = false
                            for (req in requests) {
                                val pt = projectToCanvas(req.latitude, req.longitude, w, h)
                                val dist = (pt - tapOffset).getDistance()
                                if (dist < 40f) {
                                    selectedEntityTitle = "Stop: ${req.pickupAddress}"
                                    selectedEntitySubtitle = "${req.wasteCategory.name} • ${req.quantityKg} kg • Status: ${req.collectionStatus.name}"
                                    selectedItemInfo = "${req.pickupAddress} (${req.wasteCategory.name})"
                                    onSelectRequest?.invoke(req)
                                    matched = true
                                    break
                                }
                            }

                            // Check tap near facilities
                            if (!matched) {
                                for (fac in facilities) {
                                    val pt = projectToCanvas(fac.latitude, fac.longitude, w, h)
                                    val dist = (pt - tapOffset).getDistance()
                                    if (dist < 40f) {
                                        selectedEntityTitle = fac.name
                                        selectedEntitySubtitle = "${fac.zone} • Capacity: ${fac.totalCapacityTons} tons • ${fac.contactPhone}"
                                        selectedItemInfo = "${fac.name} (${fac.zone})"
                                        onSelectFacility?.invoke(fac)
                                        matched = true
                                        break
                                    }
                                }
                            }

                            // Check tap near drivers
                            if (!matched) {
                                for (drv in drivers) {
                                    val lat = drv.currentLat ?: -6.8235
                                    val lng = drv.currentLng ?: 39.2695
                                    val pt = projectToCanvas(lat, lng, w, h)
                                    val dist = (pt - tapOffset).getDistance()
                                    if (dist < 40f) {
                                        selectedEntityTitle = "Truck: ${drv.vehicleRegNumber}"
                                        selectedEntitySubtitle = "${drv.fullName} • ${drv.vehicleType} • Zone: ${drv.preferredZone}"
                                        selectedItemInfo = "Active Fleet: ${drv.vehicleRegNumber}"
                                        matched = true
                                        break
                                    }
                                }
                            }

                            if (!matched) {
                                selectedEntityTitle = null
                                selectedEntitySubtitle = null
                                selectedItemInfo = "Kigamboni & Ilala Municipal GIS • Tap any pin to inspect"
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // 1. Draw dark high-tech GIS grid lines
                val gridColor = Color(0xFF1E293B)
                val step = (40f * zoomLevel).coerceIn(25f, 80f)
                var curX = 0f
                while (curX <= w) {
                    drawLine(gridColor, Offset(curX, 0f), Offset(curX, h), strokeWidth = 1f)
                    curX += step
                }
                var curY = 0f
                while (curY <= h) {
                    drawLine(gridColor, Offset(0f, curY), Offset(w, curY), strokeWidth = 1f)
                    curY += step
                }

                // 2. Draw stylized Dar es Salaam / Kigamboni coastline & harbor channel
                val coastPath = Path().apply {
                    moveTo(w * 0.40f, 0f)
                    cubicTo(w * 0.38f, h * 0.35f, w * 0.50f, h * 0.6f, w * 0.44f, h)
                    lineTo(w, h)
                    lineTo(w, 0f)
                    close()
                }
                drawPath(
                    coastPath,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF047857).copy(alpha = 0.18f), Color(0xFF0F172A))
                    )
                )

                // Coastline stroke
                drawPath(
                    coastPath,
                    color = Color(0xFF10B981).copy(alpha = 0.40f),
                    style = Stroke(width = 2f)
                )

                // 3. Draw Road Arterials (Kilwa Rd, Kigamboni Bridge corridor, Tuangoma Axis)
                val roadColor = Color(0xFF334155)
                drawLine(roadColor, Offset(w * 0.15f, h * 0.85f), Offset(w * 0.42f, h * 0.18f), strokeWidth = 3f)
                drawLine(roadColor, Offset(w * 0.42f, h * 0.18f), Offset(w * 0.85f, h * 0.45f), strokeWidth = 3f)
                drawLine(roadColor, Offset(w * 0.42f, h * 0.18f), Offset(w * 0.65f, h * 0.90f), strokeWidth = 2.5f)

                // 4. Calculate and Draw Route Polylines (Driver -> Requests -> Facility)
                val routeOffsets = mutableListOf<Offset>()
                val firstDriver = drivers.firstOrNull()
                if (firstDriver != null) {
                    val dLat = firstDriver.currentLat ?: -6.8235
                    val dLng = firstDriver.currentLng ?: 39.2695
                    routeOffsets.add(projectToCanvas(dLat, dLng, w, h))
                }

                requests.take(6).forEach { req ->
                    routeOffsets.add(projectToCanvas(req.latitude, req.longitude, w, h))
                }

                val firstFacility = facilities.firstOrNull()
                if (firstFacility != null && requests.isNotEmpty()) {
                    routeOffsets.add(projectToCanvas(firstFacility.latitude, firstFacility.longitude, w, h))
                }

                // Draw route line
                if (routeOffsets.size >= 2) {
                    for (i in 0 until routeOffsets.size - 1) {
                        val p1 = routeOffsets[i]
                        val p2 = routeOffsets[i + 1]
                        drawLine(
                            color = Color(0xFF10B981).copy(alpha = 0.75f),
                            start = p1,
                            end = p2,
                            strokeWidth = 3.5f
                        )
                    }
                }

                // 5. Draw Disposal Facilities (Cyan / Azure Hexagon / Diamond)
                facilities.forEachIndexed { index, facility ->
                    val pos = projectToCanvas(facility.latitude, facility.longitude, w, h)
                    drawCircle(Color(0xFF0284C7).copy(alpha = 0.35f), radius = 16f, center = pos)
                    drawCircle(Color(0xFF38BDF8), radius = 8f, center = pos)
                    drawCircle(Color.White, radius = 3f, center = pos)
                }

                // 6. Draw Collection Request Pins with Badges
                requests.forEachIndexed { idx, req ->
                    val pos = projectToCanvas(req.latitude, req.longitude, w, h)
                    val isSelected = req.id == selectedRequestId

                    val pinColor = when (req.collectionStatus.name) {
                        "ON_THE_WAY", "COLLECTING" -> Color(0xFF10B981)
                        "WAITING_ASSIGNMENT" -> Color(0xFFF59E0B)
                        "COLLECTED", "DISPOSED" -> Color(0xFF059669)
                        else -> Color(0xFF3B82F6)
                    }

                    if (isSelected) {
                        drawCircle(pinColor.copy(alpha = pulseAlpha), radius = pulseRadius * 1.5f, center = pos)
                    }

                    drawCircle(pinColor.copy(alpha = 0.35f), radius = 12f, center = pos)
                    drawCircle(pinColor, radius = 7f, center = pos)
                    drawCircle(Color.White, radius = 2.5f, center = pos)
                }

                // 7. Draw Live Moving Drivers / Waste Trucks
                drivers.forEachIndexed { idx, drv ->
                    val dLat = drv.currentLat ?: (-6.8235 - idx * 0.015)
                    val dLng = drv.currentLng ?: (39.2695 + idx * 0.02)
                    val pos = projectToCanvas(dLat, dLng, w, h)

                    // Pulse wave
                    drawCircle(
                        color = EcoGreenPrimary.copy(alpha = pulseAlpha),
                        radius = pulseRadius * 1.3f,
                        center = pos
                    )

                    // Truck outer badge
                    drawCircle(color = Color(0xFF0F172A), radius = 11f, center = pos)
                    drawCircle(color = EcoGreenPrimary, radius = 9f, center = pos)
                    drawCircle(color = Color.White, radius = 4f, center = pos)
                }
            }

            // Top Overlay Legend & Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.90f))
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GIS RADAR • ${drivers.size} TRUCKS • ${requests.size} STOPS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.90f))
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Truck", fontSize = 9.sp, color = Color.White)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Stop", fontSize = 9.sp, color = Color.White)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF38BDF8)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Plant", fontSize = 9.sp, color = Color.White)
                    }
                }
            }

            // Floating Zoom & Recenter Controls
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 46.dp, end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { zoomLevel = (zoomLevel * 1.25f).coerceAtMost(2.5f) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B).copy(alpha = 0.90f))
                        .border(1.dp, Color(0xFF334155), CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = { zoomLevel = (zoomLevel / 1.25f).coerceAtLeast(0.8f) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B).copy(alpha = 0.90f))
                        .border(1.dp, Color(0xFF334155), CircleShape)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                IconButton(
                    onClick = {
                        zoomLevel = 1.0f
                        selectedEntityTitle = null
                        selectedEntitySubtitle = null
                        selectedItemInfo = "Map view recentered"
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B).copy(alpha = 0.90f))
                        .border(1.dp, Color(0xFF334155), CircleShape)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Recenter", tint = EcoGreenPrimary, modifier = Modifier.size(20.dp))
                }
            }

            // Selected Marker HUD Card
            if (selectedEntityTitle != null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.95f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenPrimary)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedEntityTitle ?: "",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = selectedEntitySubtitle ?: "",
                                fontSize = 11.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }

                        IconButton(
                            onClick = {
                                selectedEntityTitle = null
                                selectedEntitySubtitle = null
                            },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            } else {
                // Bottom default info caption
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A).copy(alpha = 0.90f))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = selectedItemInfo ?: "Kigamboni & Ilala Sectors • Tap pins for telemetry details",
                            fontSize = 10.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            }
        }
    }
}

