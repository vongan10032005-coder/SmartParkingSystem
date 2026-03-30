package com.nhom.smartparking.presentation.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.nhom.smartparking.domain.model.VehicleRecord
import com.nhom.smartparking.presentation.viewmodel.HistoryViewModel
import com.nhom.smartparking.presentation.viewmodel.SyncState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val records   by viewModel.records.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử xe ra/vào") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.sync() },
                        enabled = syncState !is SyncState.Syncing
                    ) {
                        if (syncState is SyncState.Syncing) {
                            CircularProgressIndicator(
                                modifier  = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector        = Icons.Default.Sync,
                                contentDescription = "Đồng bộ"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor             = MaterialTheme.colorScheme.primary,
                    titleContentColor          = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor     = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Sync status bar
            when (val state = syncState) {
                is SyncState.Success ->
                    Text(
                        text     = "✅ Đồng bộ thành công!",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        color    = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                is SyncState.Error ->
                    Text(
                        text     = "❌ ${state.message}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        color    = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                else -> {}
            }

            if (records.isEmpty()) {
                // Empty state
                Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector        = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            modifier           = Modifier.size(64.dp),
                            tint               = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text  = "Chưa có lịch sử xe",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = records,
                        key   = { it.id }
                    ) { record ->
                        VehicleRecordCard(record = record)
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleRecordCard(record: VehicleRecord) {
    val fmt = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // Left — plate + times
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = record.licensePlate,
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text     = "Vào: ${fmt.format(Date(record.entryTime))}",
                    fontSize = 13.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (record.exitTime != null) {
                    Text(
                        text     = "Ra:   ${fmt.format(Date(record.exitTime))}",
                        fontSize = 13.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right — status / fee
            Column(horizontalAlignment = Alignment.End) {
                if (record.isParked()) {
                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                        Text(
                            text     = "Trong bãi",
                            modifier = Modifier.padding(horizontal = 4.dp),
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Text(
                        text       = record.formattedFee(),
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.secondary
                    )
                }

                if (!record.isSynced) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text     = "⏳ Chưa sync",
                        fontSize = 11.sp,
                        color    = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}