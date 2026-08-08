package com.mocare.app.ui.screen.fuelinput

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import com.mocare.app.ui.viewmodel.FuelInputViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelInputScreen(
    motorId: Long,
    viewModel: FuelInputViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var kmWhenFilled by remember { mutableStateOf("") }
    var amountLiters by remember { mutableStateOf("") }
    var pricePerLiter by remember { mutableStateOf("10000") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catat Pengisian BBM") },
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
        ) {
            Text(
                text = "Form Pengisian Bensin",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = kmWhenFilled,
                onValueChange = { kmWhenFilled = it },
                label = { Text("Kilometer (Odometer) Saat Pengisian") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amountLiters,
                onValueChange = { amountLiters = it },
                label = { Text("Jumlah Liter Bensin") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = pricePerLiter,
                onValueChange = { pricePerLiter = it },
                label = { Text("Harga per Liter (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val cleanKmString = kmWhenFilled.trim().replace(".", "").replace(",", "")
                    val cleanLitersString = amountLiters.trim().replace(",", ".")
                    val cleanPriceString = pricePerLiter.trim().replace(".", "").replace(",", ".")

                    val km = cleanKmString.toIntOrNull()
                    val liters = cleanLitersString.toDoubleOrNull()
                    val price = cleanPriceString.toDoubleOrNull() ?: 10000.0

                    if (km != null && liters != null && liters > 0) {
                        viewModel.saveFuelRecord(
                            motorId = motorId,
                            kmWhenFilled = km,
                            amountLiters = liters,
                            pricePerLiter = price,
                            onSuccess = {
                                Toast.makeText(context, "Catatan BBM berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                onNavigateBack()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Format angka belum sesuai. Mohon periksa kembali.", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = kmWhenFilled.isNotBlank() && amountLiters.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Catatan BBM")
            }
        }
    }
}
