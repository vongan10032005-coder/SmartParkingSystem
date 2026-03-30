package com.nhom.smartparking.presentation.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToCamera  : () -> Unit,
    onNavigateToHistory : () -> Unit,
    onLogout            : () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title  = { Text("Smart Parking") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor             = MaterialTheme.colorScheme.primary,
                    titleContentColor          = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor     = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Đăng xuất"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "Chào mừng đến bãi đỗ xe",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text     = "Chọn chức năng bên dưới",
                fontSize = 14.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Camera button
            Button(
                onClick  = onNavigateToCamera,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.CameraAlt,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Quét biển số xe", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // History button
            OutlinedButton(
                onClick  = onNavigateToHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.History,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Lịch sử xe ra/vào", fontSize = 18.sp)
            }
        }
    }
}