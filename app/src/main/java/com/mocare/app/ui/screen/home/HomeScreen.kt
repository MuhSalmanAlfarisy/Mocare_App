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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mocare.app.data.VehicleConfig
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
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext

// Helper: fuel level color based on percentage
private fun fuelColor(percent: Int): Color = when {
    percent < 0 -> Color(0xFFBDBDBD)     // No Data / Gray
    percent >= 80 -> GaugeFill            // Emerald Green
    percent >= 50 -> Color(0xFF4CAF50)    // Green
    percent >= 20 -> FuelAmber            // Amber/Orange
    else -> FuelRed                       // Red / Critical
}

private fun fuelBorderColor(percent: Int): Color = when {
    percent < 0 -> Color(0xFFE0E0E0)     // No Data / Gray
    percent >= 80 -> GaugeBorder
    percent >= 50 -> Color(0xFFA5D6A7)
    percent >= 20 -> Color(0xFFFFCC80)
    else -> Color(0xFFEF9A9A)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onProfileClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var showRefuelSheet by remember { mutableStateOf(false) }
    var showFuelInfoSheet by remember { mutableStateOf(false) }
    var showCheckpointSheet by remember { mutableStateOf(false) }

    val numberFormat = remember { NumberFormat.getNumberInstance(Locale("id", "ID")) }

    val hasData = uiState.hasRefuelData
    val noData = !hasData

    Scaffold(
        containerColor = PageBackground,
        bottomBar = { 
            MocareBottomNavigationBar(
                currentRoute = "home",
                onNavigateHistory = onHistoryClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar - Top Left "MOCARE", Top Right Profile Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 24.dp, end = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
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
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MocareBrandTeal,
                        modifier = Modifier.size(24.dp)
                    )
                }
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
                    if (noData) {
                        // No Data state
                        Text(
                            text = "--",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = SubtitleGray
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = numberFormat.format(uiState.currentOdometerKm),
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

                        if (noData) {
                            // No Data state
                            Text(
                                text = "EMPTY",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = SubtitleGray
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalGasStation,
                                    contentDescription = null,
                                    tint = SubtitleGray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "No Data",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SubtitleGray
                                )
                            }
                        } else {
                            // Has data state
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
                    }

                    // Vertical Fuel Gauge
                    val gaugePercent = if (noData) 0 else uiState.fuelLevelPercent
                    Box(
                        modifier = Modifier
                            .width(34.dp)
                            .height(86.dp)
                            .clip(CircleShape)
                            .background(GaugeContainerBg)
                            .border(1.5.dp, fuelBorderColor(uiState.fuelLevelPercent), CircleShape)
                    ) {
                        if (gaugePercent > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .fillMaxHeight(gaugePercent / 100f)
                                    .padding(3.dp)
                                    .clip(CircleShape)
                                    .background(fuelColor(uiState.fuelLevelPercent))
                            )
                        }
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
                // 1. New Refuel — always enabled
                ActionCardItem(
                    icon = Icons.Default.Add,
                    iconBgColor = ActionIconGreenBg,
                    iconTintColor = Color.White,
                    title = "Apakah Anda Baru Isi Bensin?",
                    subtitle = "+ New Refuel",
                    onClick = { showRefuelSheet = true }
                )

                // 2. Fuel Info — always enabled (info only)
                ActionCardItem(
                    icon = Icons.Default.TrendingUp,
                    iconBgColor = ActionIconMintBg,
                    iconTintColor = ActionIconMintTint,
                    title = "Bensin yang Anda Pakai",
                    subtitle = "${VehicleConfig.FUEL_TYPE} (RON ${VehicleConfig.FUEL_RON})",
                    onClick = { showFuelInfoSheet = true }
                )

                // 3. Checkpoint — disabled jika belum pernah refuel
                if (hasData) {
                    ActionCardItem(
                        icon = Icons.Default.Flag,
                        iconBgColor = ActionIconBlueBg,
                        iconTintColor = ActionIconBlueTint,
                        title = "Bensin Checkpoint",
                        subtitle = "Catat KM Terkini",
                        onClick = { showCheckpointSheet = true }
                    )
                } else {
                    // Disabled state
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.45f),
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
                                    .background(ActionIconBlueBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = ActionIconBlueTint,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Bensin Checkpoint",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkNavy
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Isi bensin dulu untuk mengaktifkan",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SubtitleGray
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // === BOTTOM SHEET MODALS ===

    // 1. Refuel Modal
    if (showRefuelSheet) {
        RefuelBottomSheet(
            onDismiss = { showRefuelSheet = false },
            onSave = { odometerKm, nominalRupiah, timestamp ->
                viewModel.saveRefuelRecord(odometerKm, nominalRupiah, timestamp)
                showRefuelSheet = false
            }
        )
    }

    // 2. Fuel Info Modal (informational only)
    if (showFuelInfoSheet) {
        FuelInfoBottomSheet(
            onDismiss = { showFuelInfoSheet = false }
        )
    }

    // 3. Checkpoint Modal
    if (showCheckpointSheet && hasData) {
        CheckpointBottomSheet(
            lastOdometerKm = uiState.currentOdometerKm,
            currentPercent = uiState.fuelLevelPercent,
            efficiencyKmPerLiter = uiState.efficiencyKmPerLiter,
            onDismiss = { showCheckpointSheet = false },
            onSave = { odometerKm, timestamp ->
                viewModel.saveFuelCheckpoint(odometerKm, timestamp)
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
    onSave: (odometerKm: Int, nominalRupiah: Double, timestamp: Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    
    var odometerInput by remember { mutableStateOf("") }
    var nominalInput by remember { mutableStateOf("") }
    
    // Timestamp State
    var selectedTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    val calendar = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }

    val odometerValid = odometerInput.toIntOrNull() != null && odometerInput.toIntOrNull()!! > 0
    val nominalValid = nominalInput.toDoubleOrNull() != null && nominalInput.toDoubleOrNull()!! > 0
    val isValid = odometerValid && nominalValid

    val nominalValue = nominalInput.toDoubleOrNull() ?: 0.0
    val calculatedLiters = if (nominalValue > 0) nominalValue / VehicleConfig.FUEL_PRICE_PER_LITER else 0.0

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

            // Timestamp Input
            Text(
                text = "WAKTU PENGISIAN",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = HeaderLabelGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = dateFormat.format(Date(selectedTimestamp)),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MocareBrandTeal,
                    unfocusedBorderColor = CardBorderColor
                ),
                trailingIcon = {
                    Text(
                        text = "Ubah",
                        color = MocareBrandTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        TimePickerDialog(
                                            context,
                                            { _, hourOfDay, minute ->
                                                val newCal = Calendar.getInstance()
                                                newCal.set(year, month, dayOfMonth, hourOfDay, minute)
                                                selectedTimestamp = newCal.timeInMillis
                                            },
                                            calendar.get(Calendar.HOUR_OF_DAY),
                                            calendar.get(Calendar.MINUTE),
                                            true
                                        ).show()
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nominal Rupiah input
            Text(
                text = "NOMINAL (RP)",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = HeaderLabelGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = nominalInput,
                onValueChange = { nominalInput = it.filter { c -> c.isDigit() } },
                placeholder = { Text("Contoh: 15000", color = SubtitleGray) },
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
            
            if (calculatedLiters > 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Anda telah mengisi ±${String.format(Locale("id", "ID"), "%.2f", calculatedLiters)} liter Pertamax.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MileageGreen
                )
            }

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
                    val nominal = nominalInput.toDoubleOrNull() ?: return@Button
                    onSave(km, nominal, selectedTimestamp)
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
// BOTTOM SHEET: Fuel Info (Informational)
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelInfoBottomSheet(
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val priceFormat = remember { NumberFormat.getNumberInstance(Locale("id", "ID")) }
    val fullTankCost = VehicleConfig.TANK_CAPACITY_LITERS * VehicleConfig.FUEL_PRICE_PER_LITER
    val estRange = (VehicleConfig.TANK_CAPACITY_LITERS * VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER).toInt()

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

            Spacer(modifier = Modifier.height(20.dp))

            FuelInfoRow("JENIS BENSIN", VehicleConfig.FUEL_TYPE)
            Spacer(modifier = Modifier.height(14.dp))
            FuelInfoRow("RON", "${VehicleConfig.FUEL_RON}")
            Spacer(modifier = Modifier.height(14.dp))
            FuelInfoRow("HARGA PER LITER", "Rp${priceFormat.format(VehicleConfig.FUEL_PRICE_PER_LITER.toInt())}")
            Spacer(modifier = Modifier.height(14.dp))
            FuelInfoRow("KAPASITAS TANGKI", "${VehicleConfig.TANK_CAPACITY_LITERS} L")
            Spacer(modifier = Modifier.height(14.dp))
            FuelInfoRow("ESTIMASI FULL TANK", "Rp${priceFormat.format(fullTankCost.toInt())}")
            Spacer(modifier = Modifier.height(14.dp))
            FuelInfoRow("ESTIMASI JARAK FULL", "$estRange KM")
        }
    }
}

@Composable
private fun FuelInfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = HeaderLabelGray
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextDarkNavy
        )
    }
}

// ============================================================
// BOTTOM SHEET: Fuel Checkpoint
// ============================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckpointBottomSheet(
    lastOdometerKm: Int,
    currentPercent: Int,
    efficiencyKmPerLiter: Double,
    onDismiss: () -> Unit,
    onSave: (odometerKm: Int, timestamp: Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale("id", "ID")) }

    var odometerInput by remember { mutableStateOf("") }

    var selectedTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    val calendar = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }

    val odometerValue = odometerInput.toIntOrNull()
    // Odometer tidak bisa mundur: KM baru harus melebihi KM terakhir yang tercatat.
    val isOdometerBackward = odometerValue != null && odometerValue <= lastOdometerKm
    val isValid = odometerValue != null && !isOdometerBackward

    // Preview sisa bensin: murni derived dari jarak tempuh dibagi efisiensi.
    val distanceKm = if (isValid) odometerValue!! - lastOdometerKm else 0
    val litersUsed = if (efficiencyKmPerLiter > 0) distanceKm / efficiencyKmPerLiter else 0.0
    val currentLiters = (currentPercent.coerceAtLeast(0) / 100.0) * VehicleConfig.TANK_CAPACITY_LITERS
    val projectedLiters = (currentLiters - litersUsed).coerceIn(0.0, VehicleConfig.TANK_CAPACITY_LITERS)
    val projectedPercent = ((projectedLiters / VehicleConfig.TANK_CAPACITY_LITERS) * 100).toInt().coerceIn(0, 100)
    val projectedRangeKm = (projectedLiters * efficiencyKmPerLiter).toInt().coerceAtLeast(0)

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

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Catat angka odometer motor Anda saat ini. Sisa bensin dihitung otomatis dari jarak tempuh.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = SubtitleGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Waktu checkpoint
            Text(
                text = "WAKTU CHECKPOINT",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = HeaderLabelGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = dateFormat.format(Date(selectedTimestamp)),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MocareBrandTeal,
                    unfocusedBorderColor = CardBorderColor
                ),
                trailingIcon = {
                    Text(
                        text = "Ubah",
                        color = MocareBrandTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        TimePickerDialog(
                                            context,
                                            { _, hourOfDay, minute ->
                                                val newCal = Calendar.getInstance()
                                                newCal.set(year, month, dayOfMonth, hourOfDay, minute)
                                                selectedTimestamp = newCal.timeInMillis
                                            },
                                            calendar.get(Calendar.HOUR_OF_DAY),
                                            calendar.get(Calendar.MINUTE),
                                            true
                                        ).show()
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                    )
                }
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
                placeholder = {
                    Text("Lebih dari ${numberFormat.format(lastOdometerKm)}", color = SubtitleGray)
                },
                singleLine = true,
                isError = isOdometerBackward,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MocareBrandTeal,
                    unfocusedBorderColor = CardBorderColor,
                    cursorColor = MocareBrandTeal,
                    errorBorderColor = FuelRed
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            when {
                isOdometerBackward -> {
                    Text(
                        text = "KM tidak boleh lebih kecil atau sama dengan catatan terakhir " +
                            "(${numberFormat.format(lastOdometerKm)} KM).",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = FuelRed
                    )
                }
                isValid -> {
                    Text(
                        text = "Jarak tempuh +${numberFormat.format(distanceKm)} KM sejak catatan terakhir.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MileageGreen
                    )
                }
                else -> {
                    Text(
                        text = "Catatan terakhir: ${numberFormat.format(lastOdometerKm)} KM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SubtitleGray
                    )
                }
            }

            // Preview hasil kalkulasi
            if (isValid) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PageBackground),
                    border = BorderStroke(1.dp, CardBorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ESTIMASI SISA BENSIN",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = HeaderLabelGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$projectedPercent%",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = fuelColor(projectedPercent)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Est. $projectedRangeKm KM left",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = fuelColor(projectedPercent)
                            )
                            if (projectedPercent == 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Segera isi bensin!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FuelRed
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val km = odometerInput.toIntOrNull() ?: return@Button
                    onSave(km, selectedTimestamp)
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
// REUSED COMPONENTS
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
fun MocareBottomNavigationBar(
    currentRoute: String = "home",
    onNavigateHome: () -> Unit = {},
    onNavigateHistory: () -> Unit = {}
) {
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
            // Home / Refuel Menu
            val isHome = currentRoute == "home"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isHome) BottomNavActiveBg else Color.Transparent)
                    .clickable(enabled = !isHome) { onNavigateHome() }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalGasStation,
                        contentDescription = "Refuel",
                        tint = if (isHome) BottomNavActiveContent else BottomNavInactiveContent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Refuel",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isHome) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isHome) BottomNavActiveContent else BottomNavInactiveContent
                    )
                }
            }

            // Stats Menu (Disabled placeholder)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).alpha(0.5f)
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

            // History Menu
            val isHistory = currentRoute == "history"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isHistory) BottomNavActiveBg else Color.Transparent)
                    .clickable(enabled = !isHistory) { onNavigateHistory() }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = if (isHistory) BottomNavActiveContent else BottomNavInactiveContent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "History",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isHistory) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isHistory) BottomNavActiveContent else BottomNavInactiveContent
                    )
                }
            }
        }
    }
}
