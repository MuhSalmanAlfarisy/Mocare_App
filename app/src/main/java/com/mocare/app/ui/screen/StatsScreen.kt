package com.mocare.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mocare.app.data.StatsPeriod
import com.mocare.app.ui.screen.home.MocareBottomNavigationBar
import com.mocare.app.ui.theme.*
import com.mocare.app.ui.viewmodel.StatsViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import androidx.compose.ui.res.stringResource
import com.mocare.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = koinViewModel(),
    onNavigateHome: () -> Unit = {},
    onNavigateHistory: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val formatK = remember { DecimalFormat("#,###.#", DecimalFormatSymbols(Locale("id", "ID"))) }

    Scaffold(
        containerColor = PageBackground,
        bottomBar = {
            MocareBottomNavigationBar(
                currentRoute = "stats",
                onNavigateHome = onNavigateHome,
                onNavigateHistory = onNavigateHistory
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Text(
                text = stringResource(R.string.stats),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MocareBrandTeal
            )

            // Segmented Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFE2E8F0))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(
                    StatsPeriod.TODAY to stringResource(R.string.today),
                    StatsPeriod.THIS_WEEK to stringResource(R.string.this_week),
                    StatsPeriod.THIS_MONTH to stringResource(R.string.this_month)
                ).forEach { (period, label) ->
                    val isSelected = uiState.selectedPeriod == period
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { viewModel.onPeriodSelected(period) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isSelected) MocareBrandTeal else HeaderLabelGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!uiState.hasSufficientData) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.not_enough_data), color = SubtitleGray)
                }
            } else {
                // Fuel Analysis Section
                SectionTitle(title = stringResource(R.string.fuel_analysis), icon = Icons.Default.LocalGasStation)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.total_volume),
                        value = formatK.format(uiState.totalVolume),
                        unit = stringResource(R.string.liters)
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.total_cost),
                        value = formatK.format(uiState.totalCost / 1000.0),
                        unit = stringResource(R.string.k_rp),
                        prefix = stringResource(R.string.rp)
                    )
                }
                
                // Insight Card 1
                if (uiState.typicalRefillLevel != null) {
                    InsightCard(
                        text = stringResource(R.string.insight_refill_habit, uiState.typicalRefillLevel.toString()),
                        icon = Icons.Default.Lightbulb,
                        iconTint = MileageGreen,
                        bgColor = GaugeContainerBg,
                        borderColor = GaugeFill
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Riding Patterns Section
                SectionTitle(title = stringResource(R.string.riding_patterns), icon = Icons.Default.DirectionsCar)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = borderStroke()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(stringResource(R.string.distance_traveled), color = HeaderLabelGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(formatK.format(uiState.distanceTraveled), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDarkNavy)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.km), fontSize = 12.sp, color = SubtitleGray, modifier = Modifier.padding(bottom = 4.dp))
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(stringResource(R.string.avg_daily), color = HeaderLabelGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(formatK.format(uiState.avgDailyDistance), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDarkNavy)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.km), fontSize = 10.sp, color = SubtitleGray, modifier = Modifier.padding(bottom = 2.dp))
                                }
                            }
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = CardBorderColor)
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(24.dp).clip(CircleShape).background(ActionIconMintBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LocalGasStation, contentDescription = null, tint = ActionIconMintTint, modifier = Modifier.size(14.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.fuel_economy), color = HeaderLabelGray, fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(formatK.format(uiState.fuelEconomy), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MileageGreen)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.km_per_liter), fontSize = 10.sp, color = SubtitleGray, modifier = Modifier.padding(bottom = 2.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Behavioral Insights Section
                SectionTitle(title = stringResource(R.string.behavioral_insights), icon = Icons.Default.Lightbulb)
                
                // Refill Frequency
                uiState.refillFrequencyDays?.let { frequency ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = borderStroke()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(stringResource(R.string.refill_frequency), color = HeaderLabelGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(if (frequency == 1) stringResource(R.string.every_day) else stringResource(R.string.once_every_n_days, frequency), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDarkNavy)
                            }
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFFE2E8F0), modifier = Modifier.size(32.dp))
                        }
                    }
                }

                // Efficiency Trend
                uiState.efficiencyTrend?.let { trend ->
                    val trendFormatted = formatK.format(kotlin.math.abs(trend))
                    val (trendText, color, bgColor) = if (trend > 0) {
                        Triple(stringResource(R.string.n_percent_more_efficient, trendFormatted), MileageGreen, ActionIconMintBg)
                    } else {
                        Triple(stringResource(R.string.n_percent_less_efficient, trendFormatted), FuelRed, FuelRedContainer)
                    }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PageBackground), // the screenshot has slightly different bg
                        border = borderStroke()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(bgColor as Color),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ShowChart, contentDescription = null, tint = color as Color, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(stringResource(R.string.efficiency_trend), color = TextDarkNavy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(stringResource(R.string.you_are_trend_than_last_period, trendText as String), fontSize = 12.sp, color = HeaderLabelGray)
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MocareBrandTeal, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDarkNavy)
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, value: String, unit: String, prefix: String = "") {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = borderStroke()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = HeaderLabelGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                if (prefix.isNotEmpty()) {
                    Text(prefix, fontSize = 10.sp, color = HeaderLabelGray, modifier = Modifier.padding(bottom = 4.dp, end = 2.dp))
                }
                Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextDarkNavy)
                Spacer(modifier = Modifier.width(2.dp))
                Text(unit, fontSize = 12.sp, color = TextDarkNavy, modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    }
}

@Composable
fun InsightCard(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color, bgColor: Color, borderColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(width = 1.dp, color = borderColor.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
    ) {
        Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(borderColor))
        Row(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, fontSize = 12.sp, color = HeaderLabelGray, lineHeight = 18.sp)
        }
    }
}

@Composable
private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
