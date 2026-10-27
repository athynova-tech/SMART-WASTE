package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.local.CollectionRequestEntity
import com.example.data.model.CollectionStatus
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CharcoalContainerHigh
import com.example.ui.theme.CharcoalOutline
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenLight
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.ObsidianDarkBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import java.text.NumberFormat
import java.util.Locale

/**
 * Daily waste collection data model for Recharts-styled charts
 */
data class DailyWasteStat(
    val dayLabel: String,
    val dateStr: String,
    val organicTons: Float,
    val recyclableTons: Float,
    val hazardousTons: Float,
    val generalSolidTons: Float,
    val targetTons: Float,
    val activeTrucks: Int,
    val diversionRatePct: Float
) {
    val totalCollectedTons: Float
        get() = organicTons + recyclableTons + hazardousTons + generalSolidTons

    val capacityUtilizationPct: Float
        get() = if (targetTons > 0) (totalCollectedTons / targetTons) * 100f else 0f

    val varianceTons: Float
        get() = totalCollectedTons - targetTons

    val isTargetMet: Boolean
        get() = totalCollectedTons >= targetTons
}

data class WardVolumeStat(
    val wardName: String,
    val totalVolumeTons: Float,
    val targetTons: Float,
    val organicPct: Float,
    val recyclablePct: Float,
    val backlogKg: Int,
    val efficiencyPct: Float,
    val collectionStatus: String
)

/**
 * Recharts-Inspired Municipal Waste Collection Efficiency & Volume Trends Dashboard
 * Tailored specifically for the Municipal Commissioner to compare daily collections vs targets.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RechartsWasteDashboard(
    requests: List<CollectionRequestEntity> = emptyList(),
    onExportReport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Time Horizon: 0 = Current Week (Mon-Sun), 1 = Previous Week, 2 = 4-Week Rolling Trend
    var selectedTimeScope by remember { mutableIntStateOf(0) }

    // Chart Mode: 0 = Daily Collections vs Targets (Primary Bar Chart), 1 = Stacked Category Breakdown, 2 = Area Volume Curve, 3 = Ward Efficiency Radar, 4 = Predictive Forecast
    var activeChartMode by remember { mutableIntStateOf(0) }

    // Target Scenario Multiplier (1.0 = Standard Municipal Quota, 1.15 = Surge Target, 0.9 = Baseline)
    var targetMultiplier by remember { mutableFloatStateOf(1.0f) }

    // Selected Ward Filter: "ALL", "Kibada", "Ferry", "Vijibweni", "Somangila"
    var selectedWardFilter by remember { mutableStateOf("ALL") }

    // Calculate live adjustments from database
    val livePaidWeightKg = requests.filter {
        it.paymentStatus == "PAID" || it.collectionStatus == CollectionStatus.COLLECTED || it.collectionStatus == CollectionStatus.DISPOSED
    }.sumOf { it.quantityKg }
    val liveWeightTons = (livePaidWeightKg / 1000f)

    // Weekly data sets
    val currentWeekData = remember(liveWeightTons, targetMultiplier) {
        listOf(
            DailyWasteStat("Mon", "Aug 17", organicTons = 16.4f, recyclableTons = 8.2f, hazardousTons = 1.8f, generalSolidTons = 12.0f + (liveWeightTons * 0.15f), targetTons = 38.0f * targetMultiplier, activeTrucks = 7, diversionRatePct = 64.7f),
            DailyWasteStat("Tue", "Aug 18", organicTons = 19.8f, recyclableTons = 10.5f, hazardousTons = 1.2f, generalSolidTons = 11.5f + (liveWeightTons * 0.18f), targetTons = 40.0f * targetMultiplier, activeTrucks = 8, diversionRatePct = 70.4f),
            DailyWasteStat("Wed", "Aug 19", organicTons = 17.5f, recyclableTons = 9.8f, hazardousTons = 2.4f, generalSolidTons = 10.2f + (liveWeightTons * 0.12f), targetTons = 38.0f * targetMultiplier, activeTrucks = 7, diversionRatePct = 68.4f),
            DailyWasteStat("Thu", "Aug 20", organicTons = 22.1f, recyclableTons = 12.4f, hazardousTons = 1.9f, generalSolidTons = 13.0f + (liveWeightTons * 0.20f), targetTons = 45.0f * targetMultiplier, activeTrucks = 9, diversionRatePct = 69.8f),
            DailyWasteStat("Fri", "Aug 21", organicTons = 24.8f, recyclableTons = 14.1f, hazardousTons = 3.1f, generalSolidTons = 14.6f + (liveWeightTons * 0.25f), targetTons = 50.0f * targetMultiplier, activeTrucks = 10, diversionRatePct = 68.7f),
            DailyWasteStat("Sat", "Aug 22", organicTons = 21.5f, recyclableTons = 13.6f, hazardousTons = 2.0f, generalSolidTons = 12.8f + (liveWeightTons * 0.10f), targetTons = 46.0f * targetMultiplier, activeTrucks = 8, diversionRatePct = 70.3f),
            DailyWasteStat("Sun", "Aug 23", organicTons = 12.0f, recyclableTons = 7.5f, hazardousTons = 0.8f, generalSolidTons = 8.5f, targetTons = 30.0f * targetMultiplier, activeTrucks = 5, diversionRatePct = 67.7f)
        )
    }

    val previousWeekData = remember(targetMultiplier) {
        listOf(
            DailyWasteStat("Mon", "Aug 10", organicTons = 14.2f, recyclableTons = 7.1f, hazardousTons = 1.5f, generalSolidTons = 13.2f, targetTons = 38.0f * targetMultiplier, activeTrucks = 6, diversionRatePct = 59.1f),
            DailyWasteStat("Tue", "Aug 11", organicTons = 17.0f, recyclableTons = 8.8f, hazardousTons = 1.0f, generalSolidTons = 12.8f, targetTons = 40.0f * targetMultiplier, activeTrucks = 7, diversionRatePct = 65.1f),
            DailyWasteStat("Wed", "Aug 12", organicTons = 15.8f, recyclableTons = 8.2f, hazardousTons = 2.0f, generalSolidTons = 11.6f, targetTons = 38.0f * targetMultiplier, activeTrucks = 6, diversionRatePct = 63.8f),
            DailyWasteStat("Thu", "Aug 13", organicTons = 19.5f, recyclableTons = 10.1f, hazardousTons = 1.7f, generalSolidTons = 14.2f, targetTons = 45.0f * targetMultiplier, activeTrucks = 8, diversionRatePct = 65.0f),
            DailyWasteStat("Fri", "Aug 14", organicTons = 21.0f, recyclableTons = 11.8f, hazardousTons = 2.6f, generalSolidTons = 15.8f, targetTons = 50.0f * targetMultiplier, activeTrucks = 9, diversionRatePct = 64.0f),
            DailyWasteStat("Sat", "Aug 15", organicTons = 18.2f, recyclableTons = 10.9f, hazardousTons = 1.8f, generalSolidTons = 13.5f, targetTons = 46.0f * targetMultiplier, activeTrucks = 7, diversionRatePct = 65.5f),
            DailyWasteStat("Sun", "Aug 16", organicTons = 10.5f, recyclableTons = 6.0f, hazardousTons = 0.5f, generalSolidTons = 9.0f, targetTons = 30.0f * targetMultiplier, activeTrucks = 5, diversionRatePct = 63.4f)
        )
    }

    val fourWeekTrendData = remember(liveWeightTons, targetMultiplier) {
        listOf(
            DailyWasteStat("Wk 31", "Jul 27 - Aug 02", organicTons = 112f, recyclableTons = 58f, hazardousTons = 12f, generalSolidTons = 88f, targetTons = 270f * targetMultiplier, activeTrucks = 8, diversionRatePct = 63.0f),
            DailyWasteStat("Wk 32", "Aug 03 - Aug 09", organicTons = 118f, recyclableTons = 63f, hazardousTons = 11f, generalSolidTons = 84f, targetTons = 275f * targetMultiplier, activeTrucks = 8, diversionRatePct = 65.5f),
            DailyWasteStat("Wk 33", "Aug 10 - Aug 16", organicTons = 116.2f, recyclableTons = 62.9f, hazardousTons = 11.1f, generalSolidTons = 90.1f, targetTons = 287f * targetMultiplier, activeTrucks = 8, diversionRatePct = 63.8f),
            DailyWasteStat("Wk 34", "Aug 17 - Aug 23", organicTons = 134.1f, recyclableTons = 76.1f, hazardousTons = 13.2f, generalSolidTons = 83.1f + liveWeightTons, targetTons = 287f * targetMultiplier, activeTrucks = 9, diversionRatePct = 68.5f)
        )
    }

    val activeData = when (selectedTimeScope) {
        0 -> currentWeekData
        1 -> previousWeekData
        else -> fourWeekTrendData
    }

    val wardVolumeStats = remember {
        listOf(
            WardVolumeStat("Kigamboni Ferry", 84.5f, 80.0f, 48f, 32f, 120, 105.6f, "Optimal"),
            WardVolumeStat("Kibada Ward", 68.2f, 65.0f, 52f, 28f, 340, 104.9f, "On Schedule"),
            WardVolumeStat("Somangila Ward", 49.8f, 55.0f, 45f, 25f, 620, 90.5f, "Heavy Volume"),
            WardVolumeStat("Vijibweni Hub", 54.6f, 50.0f, 44f, 34f, 180, 109.2f, "Optimal"),
            WardVolumeStat("Tuangoma Suburb", 41.2f, 48.0f, 55f, 20f, 890, 85.8f, "Sweep Required"),
            WardVolumeStat("Kisarawe II", 32.4f, 40.0f, 58f, 18f, 1150, 81.0f, "Priority Backlog")
        )
    }

    val totalVolumeTons = activeData.sumOf { it.totalCollectedTons.toDouble() }.toFloat()
    val totalTargetTons = activeData.sumOf { it.targetTons.toDouble() }.toFloat()
    val overallEfficiencyPct = if (totalTargetTons > 0) (totalVolumeTons / totalTargetTons) * 100f else 0f
    val avgDiversionRate = activeData.map { it.diversionRatePct }.average().toFloat()
    val totalOrganicTons = activeData.sumOf { it.organicTons.toDouble() }.toFloat()
    val totalRecyclableTons = activeData.sumOf { it.recyclableTons.toDouble() }.toFloat()
    val totalHazardousTons = activeData.sumOf { it.hazardousTons.toDouble() }.toFloat()
    val totalGeneralTons = activeData.sumOf { it.generalSolidTons.toDouble() }.toFloat()

    val volumeFormatted = String.format(Locale.US, "%.1f", totalVolumeTons)
    val targetFormatted = String.format(Locale.US, "%.1f", totalTargetTons)
    val efficiencyFormatted = String.format(Locale.US, "%.1f", overallEfficiencyPct)
    val diversionFormatted = String.format(Locale.US, "%.1f", avgDiversionRate)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recharts_waste_dashboard"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ==========================================
        // 1. RECHARTS DASHBOARD HEADER & CONTROLS
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                            .background(EcoGreenPrimary.copy(alpha = 0.15f))
                            .border(1.dp, EcoGreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Waste Collection Efficiency Engine",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )
                            Text(
                                text = "Recharts Municipal Comparison Analytics",
                                fontSize = 13.sp,
                                color = Slate400
                            )
                        }
                    }

                    // Export report button
                    Button(
                        onClick = onExportReport,
                        colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue.copy(alpha = 0.2f), contentColor = MunicipalBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Time Scope Switcher Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianDarkBg)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TimeScopeTabItem(
                        label = "Current Week (Mon - Sun)",
                        isSelected = selectedTimeScope == 0,
                        modifier = Modifier.weight(1f)
                    ) { selectedTimeScope = 0 }

                    TimeScopeTabItem(
                        label = "Previous Week",
                        isSelected = selectedTimeScope == 1,
                        modifier = Modifier.weight(0.9f)
                    ) { selectedTimeScope = 1 }

                    TimeScopeTabItem(
                        label = "4-Week Trend",
                        isSelected = selectedTimeScope == 2,
                        modifier = Modifier.weight(0.9f)
                    ) { selectedTimeScope = 2 }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Chart Mode Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ChartModeButton(
                        label = "Daily vs Target",
                        icon = Icons.Default.BarChart,
                        isSelected = activeChartMode == 0,
                        modifier = Modifier.weight(1.1f)
                    ) { activeChartMode = 0 }

                    ChartModeButton(
                        label = "Categories",
                        icon = Icons.Default.Assessment,
                        isSelected = activeChartMode == 1,
                        modifier = Modifier.weight(0.9f)
                    ) { activeChartMode = 1 }

                    ChartModeButton(
                        label = "Volume Curve",
                        icon = Icons.Default.Timeline,
                        isSelected = activeChartMode == 2,
                        modifier = Modifier.weight(0.95f)
                    ) { activeChartMode = 2 }

                    ChartModeButton(
                        label = "Ward Radar",
                        icon = Icons.Default.PieChart,
                        isSelected = activeChartMode == 3,
                        modifier = Modifier.weight(0.9f)
                    ) { activeChartMode = 3 }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Target Scenario Benchmark Adjustment Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = Slate400, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Target Benchmark:", fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Medium)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TargetPresetPill("Standard Quota (1.0x)", isSelected = targetMultiplier == 1.0f) {
                            targetMultiplier = 1.0f
                        }
                        TargetPresetPill("Rainy Surge (1.15x)", isSelected = targetMultiplier == 1.15f) {
                            targetMultiplier = 1.15f
                        }
                        TargetPresetPill("Dry Baseline (0.9x)", isSelected = targetMultiplier == 0.9f) {
                            targetMultiplier = 0.9f
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. EXECUTIVE KPI METRIC STRIP (EFFICIENCY FOCUS)
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KpiMetricBox(
                title = "Total Collected",
                value = "$volumeFormatted T",
                subtext = "Target: $targetFormatted T",
                accentColor = EcoGreenPrimary,
                modifier = Modifier.weight(1f)
            )

            KpiMetricBox(
                title = "Efficiency Rate",
                value = "$efficiencyFormatted%",
                subtext = if (overallEfficiencyPct >= 100f) "✅ Target Exceeded" else "⚠️ Target Gap",
                accentColor = if (overallEfficiencyPct >= 100f) EcoGreenPrimary else AmberWarning,
                modifier = Modifier.weight(1f)
            )

            KpiMetricBox(
                title = "Landfill Diversion",
                value = "$diversionFormatted%",
                subtext = "Composted/MRF",
                accentColor = MunicipalBlue,
                modifier = Modifier.weight(1f)
            )
        }

        // ==========================================
        // 3. MAIN RECHARTS VISUALIZATION CANVAS
        // ==========================================
        when (activeChartMode) {
            0 -> {
                // Recharts-Style Comparative Bar Chart: Daily Collections vs Target
                RechartsDailyCollectionsVsTargetBarChart(
                    data = activeData,
                    overallTargetTons = totalTargetTons / activeData.size
                )
            }
            1 -> {
                // Stacked Multi-Category Breakdown Bar Chart
                RechartsStackedCategoryBarChart(
                    data = activeData
                )
            }
            2 -> {
                // Recharts-Style Gradient Area Volume Trend Chart
                RechartsVolumeTrendAreaChart(
                    data = activeData
                )
            }
            3 -> {
                // Ward-by-Ward Volume Distribution & Efficiency Radar
                RechartsWardVolumeRadar(
                    wardStats = wardVolumeStats
                )
            }
            4 -> {
                // Predictive Volume & Fleet Capacity Forecast Chart
                RechartsCapacityForecastChart(
                    historicalData = activeData
                )
            }
        }

        // ==========================================
        // 4. STREAM COMPOSITION BREAKDOWN CHIP ROW
        // ==========================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Municipal Waste Stream Efficiency & Processing",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                    Text("Total: $volumeFormatted Tons", fontSize = 13.sp, color = EcoGreenPrimary, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Detailed Stream Breakdown Progress Rows
                WasteStreamProgressRow(
                    label = "Organic / Biodegradable Waste",
                    tons = totalOrganicTons,
                    totalTons = totalVolumeTons,
                    color = EcoGreenPrimary,
                    destination = "Kigamboni Municipal Composting Center"
                )

                WasteStreamProgressRow(
                    label = "Recyclable Plastics, Glass & Metals",
                    tons = totalRecyclableTons,
                    totalTons = totalVolumeTons,
                    color = MunicipalBlue,
                    destination = "Vijibweni Materials Recovery Facility (MRF)"
                )

                WasteStreamProgressRow(
                    label = "General Municipal Solid Waste",
                    tons = totalGeneralTons,
                    totalTons = totalVolumeTons,
                    color = AmberWarning,
                    destination = "Kinyamwezi Sanitary Landfill"
                )

                WasteStreamProgressRow(
                    label = "Hazardous, Medical & E-Waste",
                    tons = totalHazardousTons,
                    totalTons = totalVolumeTons,
                    color = Color(0xFFA855F7),
                    destination = "Dar Es Salaam Regulated Incinerator"
                )
            }
        }
    }
}

/**
 * Recharts-Style Comparative Bar Chart: Daily Collections vs Targets
 * Visualizes daily tonnage collected against municipal quota target benchmarks with interactive tooltips and efficiency indicators.
 */
@Composable
fun RechartsDailyCollectionsVsTargetBarChart(
    data: List<DailyWasteStat>,
    overallTargetTons: Float,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recharts_daily_vs_target_chart"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📊 Daily Collections vs. Targets",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                    Text(
                        text = "Bar chart comparing actual tonnage collected against municipal target quota",
                        fontSize = 13.sp,
                        color = Slate400
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EcoGreenPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Tap bars for details",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recharts Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RechartsLegendPill("Actual Collected (Tons)", EcoGreenPrimary)
                RechartsLegendPill("Daily Target Quota (Tons)", MunicipalBlue)
                RechartsDashedLegendPill("Average Target Line", AmberWarning)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Bar Chart Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianDarkBg)
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .pointerInput(data) {
                        detectTapGestures { offset ->
                            val chartWidth = size.width
                            val itemWidth = chartWidth / data.size
                            val tappedIndex = (offset.x / itemWidth).toInt().coerceIn(0, data.lastIndex)
                            selectedIndex = if (selectedIndex == tappedIndex) -1 else tappedIndex
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxCollected = data.maxOfOrNull { maxOf(it.totalCollectedTons, it.targetTons) } ?: 50f
                    val yMax = maxCollected * 1.2f
                    val chartWidth = size.width
                    val chartHeight = size.height - 30.dp.toPx()
                    val itemWidth = chartWidth / data.size
                    val singleBarWidth = itemWidth * 0.28f

                    // 1. Draw Horizontal Grid Lines & Y-Axis Scale
                    for (i in 0..4) {
                        val frac = i.toFloat() / 4f
                        val y = chartHeight * (1f - frac)
                        drawLine(
                            color = Slate200.copy(alpha = 0.25f),
                            start = Offset(0f, y),
                            end = Offset(chartWidth, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                        )
                    }

                    // 2. Draw Overall Average Target Reference Line (<ReferenceLine y={overallTargetTons} />)
                    if (yMax > 0 && overallTargetTons > 0) {
                        val refY = chartHeight * (1f - (overallTargetTons / yMax).coerceIn(0f, 1f))
                        drawLine(
                            color = AmberWarning.copy(alpha = 0.85f),
                            start = Offset(0f, refY),
                            end = Offset(chartWidth, refY),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        )
                    }

                    // 3. Draw Comparative Bars for each day
                    data.forEachIndexed { index, stat ->
                        val isSelected = selectedIndex == index
                        val xCenter = index * itemWidth + itemWidth / 2

                        val actualLeft = xCenter - singleBarWidth - 2.dp.toPx()
                        val targetLeft = xCenter + 2.dp.toPx()

                        val actualHeight = (stat.totalCollectedTons / yMax) * chartHeight
                        val targetHeight = (stat.targetTons / yMax) * chartHeight

                        val actualTop = chartHeight - actualHeight
                        val targetTop = chartHeight - targetHeight

                        // Selection Highlight Column
                        if (isSelected) {
                            drawRoundRect(
                                color = Slate50.copy(alpha = 0.08f),
                                topLeft = Offset(index * itemWidth + 2.dp.toPx(), 0f),
                                size = Size(itemWidth - 4.dp.toPx(), chartHeight),
                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                            )
                        }

                        // Bar 1: Actual Collected (Eco-Green with gradient)
                        val actualColor = if (stat.isTargetMet) EcoGreenPrimary else AmberWarning
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(actualColor, actualColor.copy(alpha = 0.7f)),
                                startY = actualTop,
                                endY = chartHeight
                            ),
                            topLeft = Offset(actualLeft, actualTop),
                            size = Size(singleBarWidth, actualHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )

                        // Bar 2: Target Quota (Municipal Blue)
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(MunicipalBlue.copy(alpha = 0.9f), MunicipalBlue.copy(alpha = 0.45f)),
                                startY = targetTop,
                                endY = chartHeight
                            ),
                            topLeft = Offset(targetLeft, targetTop),
                            size = Size(singleBarWidth, targetHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )

                        // Highlight Border on Selected Bar
                        if (isSelected) {
                            drawRoundRect(
                                color = Slate50,
                                topLeft = Offset(actualLeft - 1.dp.toPx(), actualTop - 1.dp.toPx()),
                                size = Size(singleBarWidth + 2.dp.toPx(), actualHeight + 2.dp.toPx()),
                                cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx()),
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }
                    }
                }

                // X-Axis Labels & Efficiency Tags
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    data.forEachIndexed { idx, item ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedIndex = if (selectedIndex == idx) -1 else idx }
                        ) {
                            Text(
                                text = item.dayLabel,
                                fontSize = 12.sp,
                                color = if (selectedIndex == idx) EcoGreenPrimary else Slate400,
                                fontWeight = if (selectedIndex == idx) FontWeight.Bold else FontWeight.Medium
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.0f", item.capacityUtilizationPct)}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isTargetMet) EcoGreenPrimary else AmberWarning
                            )
                        }
                    }
                }
            }

            // Interactive Tooltip Card (Recharts signature interactive tooltip)
            AnimatedVisibility(
                visible = selectedIndex in data.indices,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (selectedIndex in data.indices) {
                    val stat = data[selectedIndex]
                    val varianceSign = if (stat.varianceTons >= 0) "+" else ""
                    val varianceFormatted = String.format(Locale.US, "%s%.1f Tons", varianceSign, stat.varianceTons)

                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianDarkBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (stat.isTargetMet) EcoGreenPrimary else AmberWarning)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (stat.isTargetMet) EcoGreenPrimary else AmberWarning)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "${stat.dayLabel} (${stat.dateStr})",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate50
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (stat.isTargetMet) EcoGreenPrimary.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (stat.isTargetMet) "Target Exceeded (${String.format(Locale.US, "%.1f", stat.capacityUtilizationPct)}%)" else "Under Quota (${String.format(Locale.US, "%.1f", stat.capacityUtilizationPct)}%)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (stat.isTargetMet) EcoGreenPrimary else AmberWarning
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Metric Comparisons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Actual Collected:", fontSize = 12.sp, color = Slate400)
                                    Text(
                                        "${String.format(Locale.US, "%.1f", stat.totalCollectedTons)} Tons",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoGreenPrimary
                                    )
                                }

                                Column {
                                    Text("Municipal Target:", fontSize = 12.sp, color = Slate400)
                                    Text(
                                        "${String.format(Locale.US, "%.1f", stat.targetTons)} Tons",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MunicipalBlue
                                    )
                                }

                                Column {
                                    Text("Variance / Net:", fontSize = 12.sp, color = Slate400)
                                    Text(
                                        varianceFormatted,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (stat.varianceTons >= 0) EcoGreenPrimary else AmberWarning
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Slate200.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Mini breakdown chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("🌱 Organic: ${stat.organicTons} T", fontSize = 12.sp, color = EcoGreenPrimary, fontWeight = FontWeight.Medium)
                                Text("♻️ Recyclables: ${stat.recyclableTons} T", fontSize = 12.sp, color = MunicipalBlue, fontWeight = FontWeight.Medium)
                                Text("📦 General: ${String.format(Locale.US, "%.1f", stat.generalSolidTons)} T", fontSize = 12.sp, color = AmberWarning, fontWeight = FontWeight.Medium)
                                Text("🚛 Trucks: ${stat.activeTrucks} Active", fontSize = 12.sp, color = Slate50, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Recharts-Style Stacked Category Bar Chart
 */
@Composable
fun RechartsStackedCategoryBarChart(
    data: List<DailyWasteStat>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Waste Stream Volume Breakdown",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                    Text("Stacked composition across municipal categories", fontSize = 13.sp, color = Slate400)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RechartsLegendPill("Organic", EcoGreenPrimary)
                RechartsLegendPill("Recyclable", MunicipalBlue)
                RechartsLegendPill("General", AmberWarning)
                RechartsLegendPill("Hazardous", Color(0xFFA855F7))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Stacked Bars
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianDarkBg)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxTotal = (data.maxOfOrNull { it.totalCollectedTons } ?: 50f) * 1.15f
                    val chartWidth = size.width
                    val chartHeight = size.height - 28.dp.toPx()
                    val itemWidth = chartWidth / data.size
                    val barWidth = itemWidth * 0.48f

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

                    data.forEachIndexed { index, stat ->
                        val xCenter = index * itemWidth + itemWidth / 2
                        val barLeft = xCenter - barWidth / 2

                        var currentTop = chartHeight

                        // 1. Organic Layer
                        val hOrganic = (stat.organicTons / maxTotal) * chartHeight
                        currentTop -= hOrganic
                        drawRoundRect(
                            color = EcoGreenPrimary,
                            topLeft = Offset(barLeft, currentTop),
                            size = Size(barWidth, hOrganic),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )

                        // 2. Recyclables Layer
                        val hRecyclable = (stat.recyclableTons / maxTotal) * chartHeight
                        currentTop -= hRecyclable
                        drawRoundRect(
                            color = MunicipalBlue,
                            topLeft = Offset(barLeft, currentTop),
                            size = Size(barWidth, hRecyclable),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )

                        // 3. General Solid Layer
                        val hGeneral = (stat.generalSolidTons / maxTotal) * chartHeight
                        currentTop -= hGeneral
                        drawRoundRect(
                            color = AmberWarning,
                            topLeft = Offset(barLeft, currentTop),
                            size = Size(barWidth, hGeneral),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )

                        // 4. Hazardous Layer
                        val hHazardous = (stat.hazardousTons / maxTotal) * chartHeight
                        currentTop -= hHazardous
                        drawRoundRect(
                            color = Color(0xFFA855F7),
                            topLeft = Offset(barLeft, currentTop),
                            size = Size(barWidth, hHazardous),
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
                            text = item.dayLabel,
                            fontSize = 12.sp,
                            color = Slate400,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Recharts-Style Smooth Area Chart with Gradient Fills, Gridlines, and Interactive Tooltips
 */
@Composable
fun RechartsVolumeTrendAreaChart(
    data: List<DailyWasteStat>,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daily Volume Dynamics & Curve",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                    Text("Continuous municipal waste generation curve", fontSize = 13.sp, color = Slate400)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Canvas Area Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianDarkBg)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .pointerInput(data) {
                        detectTapGestures { offset ->
                            val chartWidth = size.width
                            val itemWidth = chartWidth / data.size
                            val tappedIndex = (offset.x / itemWidth).toInt().coerceIn(0, data.lastIndex)
                            selectedIndex = if (selectedIndex == tappedIndex) -1 else tappedIndex
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxCollected = (data.maxOfOrNull { it.totalCollectedTons } ?: 50f) * 1.2f
                    val chartWidth = size.width
                    val chartHeight = size.height - 28.dp.toPx()
                    val itemWidth = chartWidth / data.size

                    // 1. Draw Gridlines
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

                    // 2. Build Points
                    val points = data.mapIndexed { idx, stat ->
                        val x = idx * itemWidth + itemWidth / 2
                        val y = chartHeight * (1f - (stat.totalCollectedTons / maxCollected))
                        Offset(x, y)
                    }

                    if (points.isNotEmpty()) {
                        // Draw Smooth Gradient Fill Path
                        val fillPath = Path()
                        fillPath.moveTo(points.first().x, chartHeight)
                        fillPath.lineTo(points.first().x, points.first().y)

                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val curr = points[i]
                            val cx = (prev.x + curr.x) / 2
                            fillPath.cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                        }

                        fillPath.lineTo(points.last().x, chartHeight)
                        fillPath.close()

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(EcoGreenPrimary.copy(alpha = 0.4f), EcoGreenPrimary.copy(alpha = 0.0f)),
                                startY = 0f,
                                endY = chartHeight
                            )
                        )

                        // Draw Smooth Line Stroke
                        val linePath = Path()
                        linePath.moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val curr = points[i]
                            val cx = (prev.x + curr.x) / 2
                            linePath.cubicTo(cx, prev.y, cx, curr.y, curr.x, curr.y)
                        }

                        drawPath(
                            path = linePath,
                            color = EcoGreenPrimary,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // 3. Draw Data Points & Selection Highlight
                        points.forEachIndexed { index, pt ->
                            val isSelected = selectedIndex == index
                            if (isSelected) {
                                drawCircle(
                                    color = EcoGreenPrimary.copy(alpha = 0.3f),
                                    radius = 12.dp.toPx(),
                                    center = pt
                                )
                                drawLine(
                                    color = Slate50.copy(alpha = 0.3f),
                                    start = Offset(pt.x, 0f),
                                    end = Offset(pt.x, chartHeight),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                                )
                            }

                            drawCircle(
                                color = ObsidianDarkBg,
                                radius = if (isSelected) 6.dp.toPx() else 4.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = if (isSelected) AmberWarning else EcoGreenPrimary,
                                radius = if (isSelected) 4.5.dp.toPx() else 2.5.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }

                // X-Axis Day Labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    data.forEachIndexed { idx, item ->
                        Text(
                            text = item.dayLabel,
                            fontSize = 12.sp,
                            color = if (selectedIndex == idx) EcoGreenPrimary else Slate400,
                            fontWeight = if (selectedIndex == idx) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Ward-by-Ward Waste Volume Distribution & Efficiency
 */
@Composable
fun RechartsWardVolumeRadar(
    wardStats: List<WardVolumeStat>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ward Volume Distribution & Collection Efficiency",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Slate50
            )
            Text(
                text = "Target quota vs actual volume comparison across Kigamboni administrative zones",
                fontSize = 13.sp,
                color = Slate400
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                wardStats.forEach { ward ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = ObsidianDarkBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ward.wardName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when (ward.collectionStatus) {
                                                "Optimal" -> EcoGreenPrimary.copy(alpha = 0.2f)
                                                "On Schedule" -> MunicipalBlue.copy(alpha = 0.2f)
                                                "Heavy Volume" -> AmberWarning.copy(alpha = 0.2f)
                                                else -> ErrorRed.copy(alpha = 0.2f)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${ward.collectionStatus} (${String.format(Locale.US, "%.0f", ward.efficiencyPct)}%)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (ward.collectionStatus) {
                                            "Optimal" -> EcoGreenPrimary
                                            "On Schedule" -> MunicipalBlue
                                            "Heavy Volume" -> AmberWarning
                                            else -> ErrorRed
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Collected: ${ward.totalVolumeTons} T / Target: ${ward.targetTons} T", fontSize = 13.sp, color = EcoGreenPrimary, fontWeight = FontWeight.SemiBold)
                                Text("Backlog: ${ward.backlogKg} kg", fontSize = 13.sp, color = if (ward.backlogKg > 500) AmberWarning else Slate400, fontWeight = FontWeight.Medium)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = (ward.efficiencyPct / 120f).coerceIn(0f, 1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (ward.efficiencyPct >= 100f) EcoGreenPrimary else MunicipalBlue,
                                trackColor = Slate200
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Recharts Predictive Capacity & Volume Forecast Engine
 */
@Composable
fun RechartsCapacityForecastChart(
    historicalData: List<DailyWasteStat>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📈 7-Day Forward Waste Volume Forecast",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                    Text("Predictive model based on historical demographic volume generation", fontSize = 13.sp, color = Slate400)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Forecast Canvas
            val forecastDays = listOf("Mon+", "Tue+", "Wed+", "Thu+", "Fri+", "Sat+", "Sun+")
            val forecastTons = listOf(36.5f, 43.0f, 39.5f, 48.0f, 54.5f, 50.0f, 29.0f)
            val fleetCapacities = listOf(45f, 45f, 45f, 50f, 52f, 50f, 35f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ObsidianDarkBg)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxVal = 60f
                    val chartWidth = size.width
                    val chartHeight = size.height - 28.dp.toPx()
                    val itemWidth = chartWidth / forecastDays.size

                    // Grid
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

                    // Fleet Capacity Line (Dashed Orange)
                    val capPath = Path()
                    forecastDays.indices.forEach { idx ->
                        val x = idx * itemWidth + itemWidth / 2
                        val y = chartHeight * (1f - (fleetCapacities[idx] / maxVal))
                        if (idx == 0) capPath.moveTo(x, y) else capPath.lineTo(x, y)
                    }
                    drawPath(
                        path = capPath,
                        color = AmberWarning.copy(alpha = 0.8f),
                        style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f), 0f))
                    )

                    // Forecast Volume Line (Cyan Solid)
                    val volPath = Path()
                    val points = mutableListOf<Offset>()
                    forecastDays.indices.forEach { idx ->
                        val x = idx * itemWidth + itemWidth / 2
                        val y = chartHeight * (1f - (forecastTons[idx] / maxVal))
                        val pt = Offset(x, y)
                        points.add(pt)
                        if (idx == 0) volPath.moveTo(x, y) else volPath.lineTo(x, y)
                    }
                    drawPath(
                        path = volPath,
                        color = MunicipalBlue,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    points.forEachIndexed { idx, pt ->
                        val isOverCapacity = forecastTons[idx] > fleetCapacities[idx]
                        drawCircle(
                            color = if (isOverCapacity) ErrorRed else MunicipalBlue,
                            radius = 4.dp.toPx(),
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
                    forecastDays.forEach { item ->
                        Text(
                            text = item,
                            fontSize = 12.sp,
                            color = Slate400,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Advisory Note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AmberLight.copy(alpha = 0.15f))
                    .border(1.dp, AmberWarning.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Friday forecast (54.5 T) approaches max fleet ceiling (52 T). Recommend pre-scheduling 2 backup contractor compactor trucks.",
                    fontSize = 12.sp,
                    color = AmberLight,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

// ----------------------------------------------------
// UI HELPERS & COMPOSABLE SUB-COMPONENTS
// ----------------------------------------------------

@Composable
private fun KpiMetricBox(
    title: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = accentColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtext, fontSize = 11.sp, color = Slate500, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun WasteStreamProgressRow(
    label: String,
    tons: Float,
    totalTons: Float,
    color: Color,
    destination: String
) {
    val pct = if (totalTons > 0) (tons / totalTons) * 100f else 0f
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 13.sp, color = Slate50, fontWeight = FontWeight.Medium)
            Text("${String.format(Locale.US, "%.1f", tons)} T (${String.format(Locale.US, "%.0f", pct)}%)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = (pct / 100f).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Slate200
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text("Facility: $destination", fontSize = 11.sp, color = Slate400)
    }
}

@Composable
private fun TimeScopeTabItem(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) EcoGreenPrimary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.Black else Slate400
        )
    }
}

@Composable
private fun ChartModeButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) EcoGreenPrimary else Slate400, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) EcoGreenPrimary else Slate400
            )
        }
    }
}

@Composable
private fun TargetPresetPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) MunicipalBlue.copy(alpha = 0.25f) else CharcoalSurfaceVariant)
            .border(1.dp, if (isSelected) MunicipalBlue else CharcoalOutline, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MunicipalBlue else Slate400
        )
    }
}

@Composable
private fun RechartsLegendPill(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(label, fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun RechartsDashedLegendPill(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(14.dp)
                .height(3.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(label, fontSize = 12.sp, color = Slate400, fontWeight = FontWeight.Medium)
    }
}
