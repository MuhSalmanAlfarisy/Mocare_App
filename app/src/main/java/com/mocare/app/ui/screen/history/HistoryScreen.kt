package com.mocare.app.ui.screen.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mocare.app.ui.screen.home.MocareBottomNavigationBar
import com.mocare.app.ui.theme.ActionIconBlueBg
import com.mocare.app.ui.theme.ActionIconBlueTint
import com.mocare.app.ui.theme.ActionIconGreenBg
import com.mocare.app.ui.theme.CardBorderColor
import com.mocare.app.ui.theme.HeaderLabelGray
import com.mocare.app.ui.theme.MileageGreen
import com.mocare.app.ui.theme.MocareBrandTeal
import com.mocare.app.ui.theme.PageBackground
import com.mocare.app.ui.theme.SubtitleGray
import com.mocare.app.ui.theme.TextDarkNavy
import com.mocare.app.ui.viewmodel.HistoryItem
import com.mocare.app.ui.viewmodel.HistoryViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel(),
    onNavigateHome: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = PageBackground,
        bottomBar = { 
            MocareBottomNavigationBar(
                currentRoute = "history",
                onNavigateHome = onNavigateHome
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 24.dp, end = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HISTORY",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = MocareBrandTeal
                )
            }

            if (uiState.items.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada riwayat aktivitas",
                        fontSize = 14.sp,
                        color = SubtitleGray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
                ) {
                    items(uiState.items) { item ->
                        HistoryCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }
    val priceFormat = remember { NumberFormat.getNumberInstance(Locale("id", "ID")) }
    val kmFormat = remember { NumberFormat.getNumberInstance(Locale("id", "ID")) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
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
            when (item) {
                is HistoryItem.Refuel -> {
                    // Refuel Icon
                    Box(
                        modifier = Modifier
                            .size(46.dp)
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
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Isi Bensin",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkNavy
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dateFormat.format(Date(item.timestamp)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SubtitleGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Rp${priceFormat.format(item.totalCost.toInt())}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MileageGreen
                            )
                            Text(
                                text = " • ${kmFormat.format(item.odometerKm)} KM",
                                fontSize = 12.sp,
                                color = HeaderLabelGray
                            )
                        }
                    }
                }
                is HistoryItem.Checkpoint -> {
                    // Checkpoint Icon
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
                            text = dateFormat.format(Date(item.timestamp)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SubtitleGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tersisa: ${item.fuelLevelPercent}%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ActionIconBlueTint
                        )
                    }
                }
            }
        }
    }
}
