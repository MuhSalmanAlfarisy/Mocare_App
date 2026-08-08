package com.mocare.app.ui.screen.addmotor

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mocare.app.ui.viewmodel.AddMotorViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMotorScreen(
    viewModel: AddMotorViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var tankCapacity by remember { mutableStateOf("") }
    var oilCapacityLiters by remember { mutableStateOf("0.8") }
    var engineOilInterval by remember { mutableStateOf("2000") }
    var gearOilInterval by remember { mutableStateOf("8000") }
    var brakeFluidInterval by remember { mutableStateOf("10000") }
    var shockOilInterval by remember { mutableStateOf("15000") }
    var lastEngineOilKm by remember { mutableStateOf("0") }
    var lastGearOilKm by remember { mutableStateOf("0") }
    var lastBrakeFluidKm by remember { mutableStateOf("0") }
    var lastShockOilKm by remember { mutableStateOf("0") }
    var currentOdometer by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Motor Baru") },
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
            Text(
                text = "Informasi Utama Motor",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama / Model Motor (contoh: Vario 150)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tankCapacity,
                onValueChange = { tankCapacity = it },
                label = { Text("Kapasitas Tangki Bensin (Liter)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = oilCapacityLiters,
                onValueChange = { oilCapacityLiters = it },
                label = { Text("Kapasitas Oli Mesin (Liter)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = currentOdometer,
                onValueChange = { currentOdometer = it },
                label = { Text("KM Sekarang (Odometer Motor Saat Ini)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Interval Perawatan (KM)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = engineOilInterval,
                onValueChange = { engineOilInterval = it },
                label = { Text("Interval Oli Mesin (KM)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = gearOilInterval,
                onValueChange = { gearOilInterval = it },
                label = { Text("Interval Oli Gardan (KM)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = brakeFluidInterval,
                onValueChange = { brakeFluidInterval = it },
                label = { Text("Interval Minyak Rem (KM)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = shockOilInterval,
                onValueChange = { shockOilInterval = it },
                label = { Text("Interval Oli Shock (KM)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Servis Terakhir (KM Saat Ini)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastEngineOilKm,
                onValueChange = { lastEngineOilKm = it },
                label = { Text("KM Terakhir Ganti Oli Mesin") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastGearOilKm,
                onValueChange = { lastGearOilKm = it },
                label = { Text("KM Terakhir Ganti Oli Gardan") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastBrakeFluidKm,
                onValueChange = { lastBrakeFluidKm = it },
                label = { Text("KM Terakhir Ganti Minyak Rem") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastShockOilKm,
                onValueChange = { lastShockOilKm = it },
                label = { Text("KM Terakhir Ganti Oli Shock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val cleanName = name.trim()
                    val parsedTank = tankCapacity.trim().replace(",", ".").toDoubleOrNull() ?: 4.5
                    val parsedOilCap = oilCapacityLiters.trim().replace(",", ".").toDoubleOrNull() ?: 0.8
                    val parsedEngineInterval = engineOilInterval.trim().replace(".", "").toIntOrNull() ?: 2000
                    val parsedGearInterval = gearOilInterval.trim().replace(".", "").toIntOrNull() ?: 8000
                    val parsedBrakeInterval = brakeFluidInterval.trim().replace(".", "").toIntOrNull() ?: 10000
                    val parsedShockInterval = shockOilInterval.trim().replace(".", "").toIntOrNull() ?: 15000
                    val parsedLastEngine = lastEngineOilKm.trim().replace(".", "").toIntOrNull() ?: 0
                    val parsedLastGear = lastGearOilKm.trim().replace(".", "").toIntOrNull() ?: 0
                    val parsedLastBrake = lastBrakeFluidKm.trim().replace(".", "").toIntOrNull() ?: 0
                    val parsedLastShock = lastShockOilKm.trim().replace(".", "").toIntOrNull() ?: 0
                    val parsedCurrentOdometer = currentOdometer.trim().replace(".", "").toIntOrNull() ?: 0

                    if (cleanName.isNotBlank()) {
                        viewModel.saveMotor(
                            name = cleanName,
                            tankCapacity = parsedTank,
                            oilCapacityLiters = parsedOilCap,
                            engineOilIntervalKm = parsedEngineInterval,
                            gearOilIntervalKm = parsedGearInterval,
                            brakeFluidIntervalKm = parsedBrakeInterval,
                            shockOilIntervalKm = parsedShockInterval,
                            currentOdometer = parsedCurrentOdometer,
                            lastEngineOilKm = parsedLastEngine,
                            lastGearOilKm = parsedLastGear,
                            lastBrakeFluidKm = parsedLastBrake,
                            lastShockOilKm = parsedLastShock,
                            onSuccess = {
                                Toast.makeText(context, "Data motor berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            }
                        )
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Motor")
            }
        }
    }
}
