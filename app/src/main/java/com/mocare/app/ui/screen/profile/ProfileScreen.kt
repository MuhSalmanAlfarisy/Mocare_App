package com.mocare.app.ui.screen.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.mocare.app.R
import com.mocare.app.data.VehicleConfig
import com.mocare.app.ui.screen.home.MocareBottomNavigationBar
import com.mocare.app.ui.theme.ActionIconGreenBg
import com.mocare.app.ui.theme.CardBorderColor
import com.mocare.app.ui.theme.ChevronGray
import com.mocare.app.ui.theme.GaugeContainerBg
import com.mocare.app.ui.theme.HeaderLabelGray
import com.mocare.app.ui.theme.MileageGreen
import com.mocare.app.ui.theme.MocareBrandTeal
import com.mocare.app.ui.theme.PageBackground
import com.mocare.app.ui.theme.SubtitleGray
import com.mocare.app.ui.theme.TextDarkNavy

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit = {}
) {
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
            // Header: Back Arrow di kiri, "MOCARE" di tengah
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 12.dp, end = 24.dp, bottom = 16.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = TextDarkNavy,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.app_name).uppercase(),
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = MocareBrandTeal,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Avatar Section (Lingkaran Motor & Judul Motor)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEBF3FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TwoWheeler,
                        contentDescription = null,
                        tint = MocareBrandTeal,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "${VehicleConfig.BRAND} ${VehicleConfig.MODEL}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkNavy
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFF3F8))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${VehicleConfig.YEAR}",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = HeaderLabelGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Daftar Spesifikasi Motor
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. BRAND
                SpecCardItem(
                    label = stringResource(R.string.brand),
                    value = VehicleConfig.BRAND,
                    icon = Icons.Default.Apartment
                )

                // 2. MODEL
                SpecCardItem(
                    label = stringResource(R.string.model),
                    value = VehicleConfig.MODEL,
                    icon = Icons.Default.TwoWheeler
                )

                // 3. ENGINE CAPACITY
                SpecCardItem(
                    label = stringResource(R.string.engine_capacity),
                    value = "${VehicleConfig.ENGINE_CC}",
                    unit = stringResource(R.string.cc),
                    icon = Icons.Default.Settings
                )

                // 4. TRANSMISSION
                SpecCardItem(
                    label = stringResource(R.string.transmission),
                    value = stringResource(R.string.auto_v_matic),
                    icon = Icons.Default.Speed
                )

                // 5. REF. FUEL ECONOMY
                SpecCardItem(
                    label = stringResource(R.string.ref_fuel_economy),
                    value = "${VehicleConfig.REFERENCE_FUEL_ECONOMY_KM_PER_LITER.toInt()}",
                    unit = stringResource(R.string.km_per_liter),
                    icon = Icons.Default.Eco
                )

                // 6. FUEL TYPE
                SpecCardItem(
                    label = stringResource(R.string.fuel_type),
                    value = "${VehicleConfig.FUEL_TYPE} (RON ${VehicleConfig.FUEL_RON})",
                    icon = Icons.Default.LocalGasStation
                )

                // 7. FUEL PRICE
                SpecCardItem(
                    label = stringResource(R.string.fuel_price),
                    value = "${stringResource(R.string.rp)}${java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).format(VehicleConfig.FUEL_PRICE_PER_LITER.toInt())}",
                    unit = stringResource(R.string.per_liter),
                    icon = Icons.Default.Info
                )

                // 8. MAX FUEL TANK CAPACITY (Highlight Mint Card)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = GaugeContainerBg),
                    border = BorderStroke(1.dp, Color(0xFFC2E6D8))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.max_fuel_tank_capacity),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = MileageGreen
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MileageGreen,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${VehicleConfig.TANK_CAPACITY_LITERS}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkNavy
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.liters),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkNavy,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.used_for_range_calculations),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = SubtitleGray
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ActionIconGreenBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalGasStation,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Language Switcher Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = stringResource(R.string.language),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = HeaderLabelGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // English Button
                        val isEn = AppCompatDelegate.getApplicationLocales().toLanguageTags().contains("en") || 
                                   (AppCompatDelegate.getApplicationLocales().isEmpty && java.util.Locale.getDefault().language == "en")
                        
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isEn) ActionIconGreenBg else Color(0xFFEFF3F8)),
                            onClick = { 
                                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
                            }
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.english), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isEn) Color.White else TextDarkNavy)
                            }
                        }
                        
                        // Indonesian Button
                        val isId = AppCompatDelegate.getApplicationLocales().toLanguageTags().contains("id") || 
                                   (AppCompatDelegate.getApplicationLocales().isEmpty && java.util.Locale.getDefault().language == "id")
                                   
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isId) ActionIconGreenBg else Color(0xFFEFF3F8)),
                            onClick = { 
                                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("id"))
                            }
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                Text(stringResource(R.string.bahasa_indonesia), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (isId) Color.White else TextDarkNavy)
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
fun SpecCardItem(
    label: String,
    value: String,
    unit: String? = null,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = HeaderLabelGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = value,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkNavy
                    )
                    if (unit != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = unit,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HeaderLabelGray,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }
                }
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ChevronGray,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
