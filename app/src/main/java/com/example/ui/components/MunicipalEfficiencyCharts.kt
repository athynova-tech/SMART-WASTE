package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CharcoalOutline
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenLight
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.ObsidianDarkBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

// Data structures for Recharts-style municipal visualizations
data class CollectionEfficiencyPoint(
    val period: String,
    val collectedTons: Float,
    val targetTons: Float,
    val efficiencyRate: Float, // percentage
    val onTimeDeliveriesPct: Float
)

data class IncidentFrequencyPoint(
    val period: String,
    val missedPickups: Int,
    val illegalDumping: Int,
    val binOverflows: Int,
    val hazardousContaminations: Int,
    val totalIncidents: Int
)

data class WardEfficiencyMetric(
    val wardName: String,
    val efficiencyPct: Float,
    val collectedTons: Float,
    val openIncidents: Int,
    val avgResponseHours: Float,
    val riskLevel: String // "LOW", "MODERATE", "ELEVATED"
)

/**
 * Interactive Municipal Waste Efficiency & Incident Frequency Dashboard
 */
@Composable
fun MunicipalEfficiencyDashboard(
    modifier: Modifier = Modifier,
    onExportReportClick: () -> Unit = {}
) {
    var selectedTimeRange by remember { mutableIntStateOf(0) } // 0: Weekly, 1: Monthly
    var activeChartType by remember { mutableIntStateOf(0) } // 0: Collection Efficiency, 1: Incident Frequency, 2: Ward Performance

    // Simulated data models for weekly & monthly periods
    val weeklyEfficiencyData = remember {
        listOf(
            CollectionEfficiencyPoint("Mon", 34.2f, 38.0f, 90.0f, 94f),
            CollectionEfficiencyPoint("Tue", 41.5f, 40.0f, 103.7f, 96f),
            CollectionEfficiencyPoint("Wed", 38.0f, 38.0f, 100.0f, 92f),
            CollectionEfficiencyPoint("Thu", 45.8f, 42.0f, 109.0f, 97f),
            CollectionEfficiencyPoint("Fri", 52.4f, 48.0f, 109.1f, 95f),
            CollectionEfficiencyPoint("Sat", 48.2f, 45.0f, 107.1f, 91f),
            CollectionEfficiencyPoint("Sun", 28.6f, 30.0f, 95.3f, 98f)
        )
    }

    val monthlyEfficiencyData = remember {
        listOf(
            CollectionEfficiencyPoint("W1", 248.5f, 260.0f, 95.5f, 93f),
            CollectionEfficiencyPoint("W2", 272.0f, 265.0f, 102.6f, 96f),
            CollectionEfficiencyPoint("W3", 290.4f, 270.0f, 107.5f, 95f),
            CollectionEfficiencyPoint("W4", 305.8f, 280.0f, 109.2f, 98f)
        )
    }

    val weeklyIncidents = remember {
        listOf(
            IncidentFrequencyPoint("Mon", missedPickups = 4, illegalDumping = 2, binOverflows = 6, hazardousContaminations = 1, totalIncidents = 13),
            IncidentFrequencyPoint("Tue", missedPickups = 2, illegalDumping = 1, binOverflows = 4, hazardousContaminations = 0, totalIncidents = 7),
            IncidentFrequencyPoint("Wed", missedPickups = 3, illegalDumping = 3, binOverflows = 5, hazardousContaminations = 1, totalIncidents = 12),
            IncidentFrequencyPoint("Thu", missedPickups = 1, illegalDumping = 1, binOverflows = 2, hazardousContaminations = 0, totalIncidents = 4),
            IncidentFrequencyPoint("Fri", missedPickups = 5, illegalDumping = 4, binOverflows = 8, hazardousContaminations = 2, totalIncidents = 19),
            IncidentFrequencyPoint("Sat", missedPickups = 3, illegalDumping = 2, binOverflows = 7, hazardousContaminations = 1, totalIncidents = 13),
            IncidentFrequencyPoint("Sun", missedPickups = 1, illegalDumping = 0, binOverflows = 3, hazardousContaminations = 0, totalIncidents = 4)
        )
    }

    val wardMetrics = remember {
        listOf(
            WardEfficiencyMetric("Kigamboni Ferry", efficiencyPct = 96.4f, collectedTons = 84.2f, openIncidents = 2, avgResponseHours = 1.8f, riskLevel = "LOW"),
            WardEfficiencyMetric("Kibada Ward", efficiencyPct = 94.1f, collectedTons = 68.5f, openIncidents = 3, avgResponseHours = 2.4f, riskLevel = "LOW"),
            WardEfficiencyMetric("Somangila Ward", efficiencyPct = 88.7f, collectedTons = 45.2f, openIncidents = 5, avgResponseHours = 3.6f, riskLevel = "MODERATE"),
            WardEfficiencyMetric("Vijibweni Hub", efficiencyPct = 91.5f, collectedTons = 52.0f, openIncidents = 4, avgResponseHours = 2.9f, riskLevel = "LOW"),
            WardEfficiencyMetric("Tuangoma Suburb", efficiencyPct = 82.0f, collectedTons = 39.8f, openIncidents = 9, avgResponseHours = 4.7f, riskLevel = "ELEVATED"),
            WardEfficiencyMetric("Kisarawe II", efficiencyPct = 78.4f, collectedTons = 29.4f, openIncidents = 11, avgResponseHours = 5.8f, riskLevel = "ELEVATED")
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Efficiency & KPI Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Overall Efficiency", fontSize = 11.sp, color = Slate400)
                        Icon(Icons.Default.Speed, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("94.8%", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Slate50)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(12.dp))
                        Text("+3.2% vs target", fontSize = 10.sp, color = EcoGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Incident Frequency", fontSize = 11.sp, color = Slate400)
                        Icon(Icons.Default.ReportProblem, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("72 Total", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Slate50)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(12.dp))
                        Text("-14% this week", fontSize = 10.sp, color = EcoGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SLA Turnaround", fontSize = 11.sp, color = Slate400)
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("2.6 hrs", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Slate50)
                    Text("Avg dispatch resolution", fontSize = 9.sp, color = Slate500)
                }
            }
        }

        // View Mode Filter Tabs (Recharts-Style Sub-Navigation)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Municipal Intelligence Visualizer",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )

                    // Time Range Selector
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ObsidianDarkBg)
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        FilterPill("7 Days", selectedTimeRange == 0) { selectedTimeRange = 0 }
                        FilterPill("30 Days", selectedTimeRange == 1) { selectedTimeRange = 1 }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Chart View Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ChartTabButton("Efficiency Rate", activeChartType == 0, Modifier.weight(1f)) { activeChartType = 0 }
                    ChartTabButton("Incident Frequency", activeChartType == 1, Modifier.weight(1f)) { activeChartType = 1 }
                    ChartTabButton("Ward Breakdown", activeChartType == 2, Modifier.weight(1f)) { activeChartType = 2 }
                }
            }
        }

        // Main Visualizer Panel
        when (activeChartType) {
            0 -> {
                // Waste Collection Efficiency Chart (Recharts Bar + Line Composable Canvas)
                WasteCollectionEfficiencyChart(
                    data = if (selectedTimeRange == 0) weeklyEfficiencyData else monthlyEfficiencyData,
                    isWeekly = selectedTimeRange == 0
                )
            }
            1 -> {
                // Incident Frequency & Escalation Breakdown Chart
                IncidentFrequencyChart(
                    data = weeklyIncidents
                )
            }
            2 -> {
                // Ward Efficiency & Risk Heatmap
                WardPerformanceMatrix(
                    wardMetrics = wardMetrics
                )
            }
        }

        // Incident Root Cause & Action Escalation Matrix
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🚨 Incident Root Cause Breakdown",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                    Text("Live Feed", fontSize = 10.sp, color = AmberWarning, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                IncidentCategoryProgressBar("Overflowing Commercial Bins", count = 31, percentage = 43f, color = AmberWarning)
                IncidentCategoryProgressBar("Missed Scheduled Residential Pickups", count = 19, percentage = 26f, color = Color(0xFFF97316))
                IncidentCategoryProgressBar("Illegal Dumping in Tuangoma & Kisarawe II", count = 14, percentage = 19f, color = Color(0xFFEF4444))
                IncidentCategoryProgressBar("Hazardous / Unsorted Contamination", count = 8, percentage = 12f, color = Color(0xFFA855F7))
            }
        }
    }
}

/**
 * Custom Recharts-style Jetpack Compose Waste Efficiency Chart
 * Draws dual metric: Bar series (Collected Tonnage) + Line series (Efficiency % / SLA Target)
 */
@Composable
fun WasteCollectionEfficiencyChart(
    data: List<CollectionEfficiencyPoint>,
    isWeekly: Boolean,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isWeekly) "Weekly Waste Collection Volume (Tons)" else "Monthly Tonnage & Target SLA",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                    Text(
                        text = "Bar: Tonnage Collected • Line: Efficiency Rate %",
                        fontSize = 11.sp,
                        color = Slate400
                    )
                }

                // Legend
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LegendItem("Tons", EcoGreenPrimary)
                    LegendItem("SLA %", MunicipalBlue)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianDarkBg)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val widthPerItem = size.width / data.size
                            val index = (offset.x / widthPerItem).toInt().coerceIn(0, data.size - 1)
                            selectedIndex = if (selectedIndex == index) -1 else index
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxTons = (data.maxOfOrNull { it.collectedTons } ?: 50f) * 1.2f
                    val chartWidth = size.width
                    val chartHeight = size.height - 24.dp.toPx()
                    val itemWidth = chartWidth / data.size
                    val barWidth = itemWidth * 0.45f

                    // Draw Horizontal Gridlines (Recharts grid style)
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = chartHeight * (i.toFloat() / gridLines)
                        drawLine(
                            color = Slate200.copy(alpha = 0.4f),
                            start = Offset(0f, y),
                            end = Offset(chartWidth, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                        )
                    }

                    // Target SLA Reference Line (100% threshold)
                    val targetY = chartHeight * (1f - (38f / maxTons).coerceIn(0f, 1f))
                    drawLine(
                        color = AmberWarning.copy(alpha = 0.7f),
                        start = Offset(0f, targetY),
                        end = Offset(chartWidth, targetY),
                        strokeWidth = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f)
                    )

                    val linePath = Path()
                    val points = mutableListOf<Offset>()

                    // Draw Bars
                    data.forEachIndexed { index, point ->
                        val xCenter = index * itemWidth + itemWidth / 2
                        val barHeight = (point.collectedTons / maxTons) * chartHeight
                        val barTop = chartHeight - barHeight
                        val barLeft = xCenter - barWidth / 2

                        val isHighlighted = selectedIndex == index || selectedIndex == -1

                        // Gradient bar
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    if (isHighlighted) EcoGreenPrimary else EcoGreenPrimary.copy(alpha = 0.4f),
                                    if (isHighlighted) EcoGreenDark else EcoGreenDark.copy(alpha = 0.3f)
                                )
                            ),
                            topLeft = Offset(barLeft, barTop),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )

                        // Calculate Line point for efficiency
                        val lineY = chartHeight * (1f - (point.efficiencyRate / 120f).coerceIn(0f, 1f))
                        val linePoint = Offset(xCenter, lineY)
                        points.add(linePoint)

                        if (index == 0) {
                            linePath.moveTo(linePoint.x, linePoint.y)
                        } else {
                            // Smooth bezier line
                            val prev = points[index - 1]
                            val cx = (prev.x + linePoint.x) / 2
                            linePath.cubicTo(cx, prev.y, cx, linePoint.y, linePoint.x, linePoint.y)
                        }
                    }

                    // Draw Smooth Line for Efficiency Rate %
                    drawPath(
                        path = linePath,
                        color = MunicipalBlue,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw Data Dots on the line
                    points.forEachIndexed { index, pt ->
                        val isSel = selectedIndex == index
                        drawCircle(
                            color = ObsidianDarkBg,
                            radius = if (isSel) 6.dp.toPx() else 4.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = if (isSel) AmberWarning else MunicipalBlue,
                            radius = if (isSel) 4.5.dp.toPx() else 2.5.dp.toPx(),
                            center = pt
                        )
                    }
                }

                // X-Axis Labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    data.forEachIndexed { idx, item ->
                        Text(
                            text = item.period,
                            fontSize = 10.sp,
                            color = if (selectedIndex == idx) EcoGreenPrimary else Slate400,
                            fontWeight = if (selectedIndex == idx) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Interactive Tooltip / Inspection Card
            if (selectedIndex in data.indices) {
                val pt = data[selectedIndex]
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ObsidianDarkBg),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenPrimary.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Period: ${pt.period}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate50)
                            Text("Target: ${pt.targetTons} Tons", fontSize = 10.sp, color = Slate400)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Collected: ${pt.collectedTons} Tons", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EcoGreenPrimary)
                            Text("Efficiency: ${pt.efficiencyRate}% (${pt.onTimeDeliveriesPct}% on-time)", fontSize = 10.sp, color = MunicipalBlue)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Recharts-style Incident Frequency Stacked Multi-Category Canvas
 */
@Composable
fun IncidentFrequencyChart(
    data: List<IncidentFrequencyPoint>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Incident Frequency by Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                    Text("Daily volume of logged municipal complaints & infractions", fontSize = 11.sp, color = Slate400)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LegendItem("Bin Overflow", AmberWarning)
                LegendItem("Missed Pickup", Color(0xFFF97316))
                LegendItem("Illegal Dump", Color(0xFFEF4444))
                LegendItem("Hazardous", Color(0xFFA855F7))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stacked Bar Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianDarkBg)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxTotal = (data.maxOfOrNull { it.totalIncidents } ?: 20).toFloat() * 1.15f
                    val chartWidth = size.width
                    val chartHeight = size.height - 24.dp.toPx()
                    val itemWidth = chartWidth / data.size
                    val barWidth = itemWidth * 0.5f

                    // Gridlines
                    for (i in 0..3) {
                        val y = chartHeight * (i.toFloat() / 3)
                        drawLine(
                            color = Slate200.copy(alpha = 0.3f),
                            start = Offset(0f, y),
                            end = Offset(chartWidth, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                        )
                    }

                    data.forEachIndexed { index, point ->
                        val xCenter = index * itemWidth + itemWidth / 2
                        val barLeft = xCenter - barWidth / 2

                        var currentTop = chartHeight

                        // Layer 1: Overflow
                        val h1 = (point.binOverflows / maxTotal) * chartHeight
                        currentTop -= h1
                        drawRoundRect(
                            color = AmberWarning,
                            topLeft = Offset(barLeft, currentTop),
                            size = Size(barWidth, h1),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )

                        // Layer 2: Missed Pickup
                        val h2 = (point.missedPickups / maxTotal) * chartHeight
                        currentTop -= h2
                        drawRoundRect(
                            color = Color(0xFFF97316),
                            topLeft = Offset(barLeft, currentTop),
                            size = Size(barWidth, h2),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )

                        // Layer 3: Illegal Dumping
                        val h3 = (point.illegalDumping / maxTotal) * chartHeight
                        currentTop -= h3
                        drawRoundRect(
                            color = Color(0xFFEF4444),
                            topLeft = Offset(barLeft, currentTop),
                            size = Size(barWidth, h3),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )

                        // Layer 4: Hazardous
                        val h4 = (point.hazardousContaminations / maxTotal) * chartHeight
                        currentTop -= h4
                        drawRoundRect(
                            color = Color(0xFFA855F7),
                            topLeft = Offset(barLeft, currentTop),
                            size = Size(barWidth, h4),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                }

                // X-Axis Labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    data.forEach { item ->
                        Text(
                            text = item.period,
                            fontSize = 10.sp,
                            color = Slate400
                        )
                    }
                }
            }
        }
    }
}

/**
 * Ward-Level Efficiency Matrix & Risk Scoring
 */
@Composable
fun WardPerformanceMatrix(
    wardMetrics: List<WardEfficiencyMetric>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Ward Performance & Incident Risk Matrix",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Slate50
            )
            Text(
                text = "Operational collection efficiency vs unresolved incident density",
                fontSize = 11.sp,
                color = Slate400
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                wardMetrics.forEach { ward ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ObsidianDarkBg),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ward.wardName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when (ward.riskLevel) {
                                                "LOW" -> EcoGreenPrimary.copy(alpha = 0.2f)
                                                "MODERATE" -> AmberWarning.copy(alpha = 0.2f)
                                                else -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${ward.riskLevel} RISK",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (ward.riskLevel) {
                                            "LOW" -> EcoGreenPrimary
                                            "MODERATE" -> AmberWarning
                                            else -> Color(0xFFEF4444)
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Efficiency: ${ward.efficiencyPct}% • ${ward.collectedTons} Tons", fontSize = 10.sp, color = Slate400)
                                Text("${ward.openIncidents} Incidents • SLA ${ward.avgResponseHours}h", fontSize = 10.sp, color = Slate500)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            LinearProgressIndicator(
                                progress = ward.efficiencyPct / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = if (ward.efficiencyPct >= 90f) EcoGreenPrimary else if (ward.efficiencyPct >= 80f) AmberWarning else Color(0xFFEF4444),
                                trackColor = Slate200
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IncidentCategoryProgressBar(
    title: String,
    count: Int,
    percentage: Float,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontSize = 11.sp, color = Slate400)
            Text("$count cases (${percentage.toInt()}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = percentage / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(2.5.dp)),
            color = color,
            trackColor = Slate200
        )
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 10.sp, color = Slate400)
    }
}

@Composable
private fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) EcoGreenPrimary else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else Slate400
        )
    }
}

@Composable
private fun ChartTabButton(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) EcoGreenPrimary.copy(alpha = 0.2f) else ObsidianDarkBg)
            .border(1.dp, if (isSelected) EcoGreenPrimary else CharcoalOutline, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) EcoGreenPrimary else Slate400
        )
    }
}
