package com.mocare.app.ui.screen.summary

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mocare.app.ui.viewmodel.FuelStatusType
import com.mocare.app.ui.viewmodel.MaintenanceItem
import com.mocare.app.ui.viewmodel.MaintenanceStatusType
import com.mocare.app.ui.viewmodel.SummaryViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    motorId: Long,
    viewModel: SummaryViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showUpdateKmDialog by remember { mutableStateOf(false) }
    var kmInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(motorId) {
        viewModel.loadSummary(motorId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.motor?.name ?: "Ringkasan Motor") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Card Status Bensin
            val (fuelColor, fuelBg) = when (uiState.fuelStatus) {
                FuelStatusType.SAFE -> Pair(Color(0xFF1B5C3A), Color(0xFFD7F2E8))
                FuelStatusType.WARNING -> Pair(Color(0xFFF57F17), Color(0xFFFFFDE7))
                FuelStatusType.URGENT -> Pair(Color(0xFFC62828), Color(0xFFFFEBEE))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = fuelBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Status Bensin: ${uiState.fuelStatusLabel}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = fuelColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val estKmText = String.format(Locale.getDefault(), "%.0f KM", uiState.estimatedRemainingRangeKm)
                        Text(
                            text = "Estimasi sisa jarak tempuh: ~$estKmText",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }
                    Text(
                        text = uiState.fuelStatusLabel,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(fuelColor, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card Statistik BBM
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Statistik Penggunaan",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Odometer Terbaru", style = MaterialTheme.typography.labelMedium)
                                Text("${uiState.latestKm} KM", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Konsumsi BBM", style = MaterialTheme.typography.labelMedium)
                                val kmL = if (uiState.avgKmPerLiter > 0) String.format(Locale.getDefault(), "%.1f KM/L", uiState.avgKmPerLiter) else "Belum cukup data"
                                Text(kmL, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Update KM Sekarang
            OutlinedButton(
                onClick = {
                    kmInput = if (uiState.motor?.currentOdometer ?: 0 > 0) {
                        uiState.motor?.currentOdometer.toString()
                    } else {
                        uiState.latestKm.toString()
                    }
                    showUpdateKmDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Update KM Sekarang")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section Status Perawatan
            Text(
                text = "Status Perawatan Komponen",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.maintenanceItems.isEmpty()) {
                Text("Belum ada data perawatan yang tercatat.")
            } else {
                uiState.maintenanceItems.forEach { item ->
                    MaintenanceCard(item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Dialog Update KM
    if (showUpdateKmDialog) {
        AlertDialog(
            onDismissRequest = { showUpdateKmDialog = false },
            title = { Text("Update KM Sekarang") },
            text = {
                Column {
                    Text(
                        text = "Masukkan kilometer odometer motor Anda saat ini.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = kmInput,
                        onValueChange = { kmInput = it },
                        label = { Text("KM Sekarang") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newKm = kmInput.trim().replace(".", "").replace(",", "").toIntOrNull()
                        if (newKm != null && newKm > 0) {
                            viewModel.updateCurrentOdometer(motorId, newKm)
                            showUpdateKmDialog = false
                            Toast.makeText(context, "KM berhasil diperbarui ke $newKm KM", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Masukkan angka KM yang valid", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUpdateKmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun MaintenanceCard(item: MaintenanceItem) {
    val (statusLabel, statusColor, backgroundColor) = when (item.status) {
        MaintenanceStatusType.SAFE -> Triple("Aman", Color(0xFF1B5C3A), Color(0xFFD7F2E8))
        MaintenanceStatusType.WARNING -> Triple("Segera diperiksa", Color(0xFFF57F17), Color(0xFFFFFDE7))
        MaintenanceStatusType.URGENT -> Triple("Perlu diperiksa sekarang", Color(0xFFC62828), Color(0xFFFFEBEE))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Interval: Tiap ${item.intervalKm} KM | Servis Terakhir: ${item.lastKm} KM",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(2.dp))
                val sisaText = if (item.remainingKm > 0) "Sisa: ${item.remainingKm} KM lagi" else "Melebihi jadwal sebesar ${-item.remainingKm} KM"
                Text(
                    text = sisaText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = statusLabel,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(statusColor, shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}
