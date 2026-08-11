package com.mocare.app.ui.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mocare.app.ui.theme.ActionIconBlueBg
import com.mocare.app.ui.theme.ActionIconBlueTint
import com.mocare.app.ui.theme.ActionIconGreenBg
import com.mocare.app.ui.theme.ActionIconMintBg
import com.mocare.app.ui.theme.ActionIconMintTint
import com.mocare.app.ui.theme.BottomNavActiveBg
import com.mocare.app.ui.theme.BottomNavActiveContent
import com.mocare.app.ui.theme.BottomNavInactiveContent
import com.mocare.app.ui.theme.CardBorderColor
import com.mocare.app.ui.theme.ChevronGray
import com.mocare.app.ui.theme.EstLeftGreen
import com.mocare.app.ui.theme.FuelAmber
import com.mocare.app.ui.theme.FuelRed
import com.mocare.app.ui.theme.GaugeBorder
import com.mocare.app.ui.theme.GaugeContainerBg
import com.mocare.app.ui.theme.GaugeFill
import com.mocare.app.ui.theme.HeaderLabelGray
import com.mocare.app.ui.theme.MileageGreen
import com.mocare.app.ui.theme.MocareBrandTeal
import com.mocare.app.ui.theme.PageBackground
import com.mocare.app.ui.theme.SubtitleGray
import com.mocare.app.ui.theme.TextDarkNavy
import com.mocare.app.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Helper: fuel level color based on percentage
private fun fuelColor(percent: Int): Color = when {
    percent >= 80 -> GaugeFill           // Emerald Green
    percent >= 50 -> Color(0xFF4CAF50)   // Green
    percent >= 20 -> FuelAmber           // Amber/Orange
    else -> FuelRed                      // Red / Critical
}

private fun fuelBorderColor(percent: Int): Color = when {
    percent >= 80 -> GaugeBorder
    percent >= 50 -> Color(0xFFA5D6A7)
    percent >= 20 -> Color(0xFFFFCC80)
    else -> Color(0xFFEF9A9A)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showRefuelSheet by remember { mutableStateOf(false) }
    var showFuelUsageSheet by remember { mutableStateOf(false) }
    var showCheckpointSheet by remember { mutableStateOf(false) }

    val numberFormat = remember { NumberFormat.getNumberInstance(Locale("id", "ID")) }
    val displayKm = remember(uiState.currentOdometerKm) { numberFormat.format(uiState.currentOdometerKm) }

    Scaffold(
        containerColor = PageBackground,
        bottomBar = { MocareBottomNavigationBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar - Top Right "MOCARE"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 24.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MOCARE",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = MocareBrandTeal
                )
            }

            // Card 1: CURRENT MILEAGE
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 22.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CURRENT MILEAGE",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = HeaderLabelGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = displayKm,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = MileageGreen
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "KM",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MileageGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: FUEL LEVEL & GAUGE
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "FUEL LEVEL",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = HeaderLabelGray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${uiState.fuelLevelPercent}",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkNavy
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "%",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkNavy,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalGasStation,
                                contentDescription = null,
                                tint = fuelColor(uiState.fuelLevelPercent),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Est. ${uiState.estimatedRangeKm} KM left",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = fuelColor(uiState.fuelLevelPercent)
                            )
                        }
                    }

                    // Vertical Fuel Gauge
                    Box(
                        modifier = Modifier
                            .width(34.dp)
                            .height(86.dp)
                            .clip(CircleShape)
                            .background(GaugeContainerBg)
                            .border(1.5.dp, fuelBorderColor(uiState.fuelLevelPercent), CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .fillMaxHeight(uiState.fuelLevelPercent / 100f)
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(fuelColor(uiState.fuelLevelPercent))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCardItem(
                    icon = Icons.Default.Add,
                    iconBgColor = ActionIconGreenBg,
                    iconTintColor = Color.White,
                    title = "Apakah Anda Baru Isi Bensin?",
                    subtitle = "+ New Refuel",
                    onClick = { showRefuelSheet = true }
                )
                ActionCardItem(
                    icon = Icons.Default.TrendingUp,
                    iconBgColor = ActionIconMintBg,
                    iconTintColor = ActionIconMintTint,
                    title = "Bensin yang Anda Pakai",
                    subtitle = "Fuel Usage Statistics",
                    onClick = { showFuelUsageSheet = true }
                )
                ActionCardItem(
                    icon = Icons.Default.Flag,
                    iconBgColor = ActionIconBlueBg,
                    iconTintColor = ActionIconBlueTint,
                    title = "Bensin Checkpoint",
                    subtitle = "Mark Fuel Level",
                    onClick = { showCheckpointSheet = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // === BOTTOM SHEET MODALS ===

    // 1. Refuel Modal
    if (showRefuelSheet) {
        RefuelBottomSheet(
            onDismiss = { showRefuelSheet = false },
            onSave = { odometerKm, timestamp ->
                viewModel.saveRefuelRecord(odometerKm, timestamp)
                showRefuelSheet = false
            }
        )
    }

    // 2. Fuel Usage Modal
    if (showFuelUsageSheet) {
        FuelUsageBottomSheet(
            currentPrice = uiState.lastPricePerLiter,
            onDismiss = { showFuelUsageSheet = false },
            onSave = { liters, price ->
                viewModel.saveFuelUsage(liters, price)
                showFuelUsageSheet = false
            }
        )
    }

    // 3. Checkpoint Modal
    if (showCheckpointSheet) {
        CheckpointBottomSheet(
            currentPercent = uiState.fuelLevelPercent,
            onDismiss = { showCheckpointSheet = false },
            onSave = { percent ->
                viewModel.saveFuelCheckpoint(percent)
                showCheckpointSheet = false
            }
        )
    }
}

// ============================================================
// BOTTOM SHEET: Refuel
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefuelBottomSheet(
    onDismiss: () -> Unit,
    onSave: (odometerKm: Int, timestamp: Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var odometerInput by remember { mutableStateOf("") }
    val currentTimestamp = remember { System.currentTimeMillis() }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }
    val displayDate = remember { dateFormat.format(Date(currentTimestamp)) }

    val isValid = odometerInput.toIntOrNull() != null && odometerInput.toIntOrNull()!! > 0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Isi Bensin Baru",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkNavy
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SubtitleGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timestamp display
            Text(
                text = "WAKTU PENGISIAN",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = HeaderLabelGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayDate,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDarkNavy
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Odometer input
            Text(
                text = "KM MOTOR SAAT INI",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = HeaderLabelGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = odometerInput,
                onValueChange = { odometerInput = it.filter { c -> c.isDigit() } },
                placeholder = { Text("Contoh: 12500", color = SubtitleGray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MocareBrandTeal,
                    unfocusedBorderColor = CardBorderColor,
                    cursorColor = MocareBrandTeal
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Save button
            Button(
                onClick = {
                    val km = odometerInput.toIntOrNull() ?: return@Button
                    onSave(km, currentTimestamp)
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MocareBrandTeal,
                    disabledContainerColor = CardBorderColor
                )
            ) {
                Text(
                    text = "Simpan",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ============================================================
// BOTTOM SHEET: Fuel Usage
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelUsageBottomSheet(
    currentPrice: Double,
    onDismiss: () -> Unit,
    onSave: (liters: Double, pricePerLiter: Double) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var priceInput by remember { mutableStateOf(if (currentPrice > 0) currentPrice.toInt().toString() else "") }
    var litersInput by remember { mutableStateOf("") }

    val priceValid = priceInput.toDoubleOrNull() != null && priceInput.toDoubleOrNull()!! > 0
    val litersValid = litersInput.toDoubleOrNull() != null && litersInput.toDoubleOrNull()!! > 0
    val isValid = priceValid && litersValid

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bensin yang Anda Pakai",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkNavy
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SubtitleGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "HARGA PER LITER (RP)",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = HeaderLabelGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = priceInput,
                onValueChange = { priceInput = it.filter { c -> c.isDigit() } },
                placeholder = { Text("Contoh: 10000", color = SubtitleGray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MocareBrandTeal,
                    unfocusedBorderColor = CardBorderColor,
                    cursorColor = MocareBrandTeal
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "JUMLAH LITER",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = HeaderLabelGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = litersInput,
                onValueChange = { litersInput = it.filter { c -> c.isDigit() || c == '.' } },
                placeholder = { Text("Contoh: 3.5", color = SubtitleGray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MocareBrandTeal,
                    unfocusedBorderColor = CardBorderColor,
                    cursorColor = MocareBrandTeal
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val liters = litersInput.toDoubleOrNull() ?: return@Button
                    val price = priceInput.toDoubleOrNull() ?: return@Button
                    onSave(liters, price)
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MocareBrandTeal,
                    disabledContainerColor = CardBorderColor
                )
            ) {
                Text(
                    text = "Simpan",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ============================================================
// BOTTOM SHEET: Fuel Checkpoint
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckpointBottomSheet(
    currentPercent: Int,
    onDismiss: () -> Unit,
    onSave: (percent: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var sliderValue by remember { mutableFloatStateOf(currentPercent.toFloat()) }
    val displayPercent = sliderValue.toInt()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bensin Checkpoint",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkNavy
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SubtitleGray)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Large percentage display
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$displayPercent",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = fuelColor(displayPercent)
                    )
                    Text(
                        text = "%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = fuelColor(displayPercent),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Slider
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 0f..100f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = fuelColor(displayPercent),
                    activeTrackColor = fuelColor(displayPercent),
                    inactiveTrackColor = CardBorderColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "0%", fontSize = 11.sp, color = SubtitleGray)
                Text(text = "100%", fontSize = 11.sp, color = SubtitleGray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onSave(displayPercent) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MocareBrandTeal
                )
            ) {
                Text(
                    text = "Simpan",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ============================================================
// REUSED COMPONENTS (unchanged from original design)
// ============================================================
@Composable
fun ActionCardItem(
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkNavy
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SubtitleGray
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = ChevronGray,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun MocareBottomNavigationBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color.White,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(BottomNavActiveBg)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalGasStation,
                        contentDescription = "Refuel",
                        tint = BottomNavActiveContent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Refuel",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = BottomNavActiveContent
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Stats",
                    tint = BottomNavInactiveContent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Stats",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = BottomNavInactiveContent
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    tint = BottomNavInactiveContent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "History",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = BottomNavInactiveContent
                )
            }
        }
    }
}
