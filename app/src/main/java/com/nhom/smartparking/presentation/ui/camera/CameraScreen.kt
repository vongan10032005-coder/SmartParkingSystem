package com.nhom.smartparking.presentation.ui.camera

import android.Manifest
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.nhom.smartparking.presentation.viewmodel.CameraUiState
import com.nhom.smartparking.presentation.viewmodel.CameraViewModel
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onNavigateBack: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val detectedPlate  by viewModel.detectedPlate.collectAsState()
    val uiState        by viewModel.uiState.collectAsState()

    // Xin quyền camera
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val recognizer     = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            recognizer.close()
        }
    }

    // Tự động xin quyền khi vào màn hình
    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quét biển số xe") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor             = MaterialTheme.colorScheme.primary,
                    titleContentColor          = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                // Đã có quyền → hiện camera
                cameraPermission.status.isGranted -> {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val future = ProcessCameraProvider.getInstance(ctx)
                            future.addListener({
                                val provider = future.get()
                                val preview  = Preview.Builder().build()
                                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                                val analyzer = ImageAnalysis.Builder()
                                    .setTargetResolution(android.util.Size(1280, 720))
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                    .also {
                                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                                            processImage(recognizer, imageProxy) { plate ->
                                                viewModel.onPlateDetected(plate)
                                            }
                                        }
                                    }

                                try {
                                    provider.unbindAll()
                                    provider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        analyzer
                                    )
                                } catch (e: Exception) {
                                    Log.e("CameraScreen", "Bind failed", e)
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay bottom panel
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Color.Black.copy(alpha = 0.75f),
                                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            )
                            .padding(20.dp)
                    ) {
                        if (detectedPlate != null) {
                            Text(
                                text     = "Biển số phát hiện:",
                                color    = Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                            Text(
                                text       = detectedPlate!!,
                                color      = Color.Yellow,
                                fontSize   = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick  = { viewModel.recordEntry(detectedPlate!!) },
                                    enabled  = uiState !is CameraUiState.Loading,
                                    modifier = Modifier.weight(1f),
                                    colors   = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) { Text("🚗 Xe VÀO", fontSize = 16.sp) }

                                Button(
                                    onClick  = { viewModel.recordExit(detectedPlate!!) },
                                    enabled  = uiState !is CameraUiState.Loading,
                                    modifier = Modifier.weight(1f),
                                    colors   = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) { Text("🏁 Xe RA", fontSize = 16.sp) }
                            }
                        } else {
                            Text(
                                text      = "Hướng camera vào biển số xe...",
                                color     = Color.White,
                                fontSize  = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier  = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        when (val state = uiState) {
                            is CameraUiState.Loading ->
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

                            is CameraUiState.EntrySuccess -> {
                                Text("✅ Xe ${state.plate} đã vào bãi!", color = Color.Green)
                                LaunchedEffect(state) {
                                    kotlinx.coroutines.delay(2500)
                                    viewModel.resetState()
                                    viewModel.clearPlate()
                                }
                            }

                            is CameraUiState.ExitSuccess -> {
                                Text(
                                    "✅ Xe ${state.plate} đã ra — Phí: ${state.fee.toLong()}đ",
                                    color = Color(0xFF90EE90)
                                )
                                LaunchedEffect(state) {
                                    kotlinx.coroutines.delay(2500)
                                    viewModel.resetState()
                                    viewModel.clearPlate()
                                }
                            }

                            is CameraUiState.Error ->
                                Text("❌ ${state.message}", color = Color.Red)

                            else -> {}
                        }
                    }
                }

                // Bị từ chối, có thể giải thích
                cameraPermission.status.shouldShowRationale -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement   = Arrangement.Center,
                        horizontalAlignment   = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "App cần quyền camera để quét biển số xe",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                            Text("Cấp quyền camera")
                        }
                    }
                }

                // Chưa có quyền
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement   = Arrangement.Center,
                        horizontalAlignment   = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Đang yêu cầu quyền camera...",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                            Text("Cấp quyền camera")
                        }
                    }
                }
            }
        }
    }
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun processImage(
    recognizer   : com.google.mlkit.vision.text.TextRecognizer,
    imageProxy   : ImageProxy,
    onPlateFound : (String) -> Unit
) {
    val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    recognizer.process(image)
        .addOnSuccessListener { result ->
            val plateRegex = Regex("""\d{2}[A-Z]\d?[-. ]\d{4,5}""")
            for (block in result.textBlocks) {
                val text = block.text.replace(" ", "").replace(".", "-")
                plateRegex.find(text)?.let { onPlateFound(it.value) }
            }
        }
        .addOnCompleteListener { imageProxy.close() }
}