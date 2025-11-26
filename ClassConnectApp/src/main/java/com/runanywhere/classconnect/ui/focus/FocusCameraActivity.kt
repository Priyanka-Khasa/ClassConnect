package com.runanywhere.classconnect.ui.focus

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import androidx.camera.core.ExperimentalGetImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
class FocusCameraActivity : ComponentActivity() {

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            FocusCameraTheme {
                FocusCameraScreen()
            }
        }
    }

    @Composable
    fun FocusCameraScreen() {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        var previewView by remember { mutableStateOf<PreviewView?>(null) }
        var focusStatus by remember { mutableStateOf("Analyzing...") }
        var isFocused by remember { mutableStateOf(false) }

        // Permission launcher
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted: Boolean ->
            if (granted && previewView != null) {
                startCamera(
                    previewView!!, lifecycleOwner
                ) { s, f ->
                    focusStatus = s
                    isFocused = f
                }
            } else {
                focusStatus = "Camera permission required"
            }
        }

        // Ask permission once
        LaunchedEffect(Unit) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                FocusHeader()

                // Camera preview
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).apply {
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                previewView = this
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        update = { view ->
                            startCamera(
                                view, lifecycleOwner
                            ) { s, f ->
                                focusStatus = s
                                isFocused = f
                            }
                        }
                    )
                }

                FocusStatus(focusStatus, isFocused)
                FocusTips()
            }
        }
    }

    private fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: androidx.lifecycle.LifecycleOwner,
        updateStatus: (String, Boolean) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .apply {
                    setAnalyzer(cameraExecutor, FocusAnalyzer(updateStatus))
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    analyzer
                )
            } catch (e: Exception) {
                updateStatus("Camera failed to start!", false)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    inner class FocusAnalyzer(
        val updateStatus: (String, Boolean) -> Unit
    ) : ImageAnalysis.Analyzer {

        private val detector = FaceDetection.getClient()
        private var lostCount = 0

        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image ?: run {
                imageProxy.close()
                return
            }

            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isEmpty()) {
                        lostCount++
                        if (lostCount > 5) {
                            updateStatus("Looking away from screen", false)
                        }
                    } else {
                        lostCount = 0
                        updateStatus("Maintaining good focus", true)
                    }
                }
                .addOnFailureListener {
                    updateStatus("Analysis error", false)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

@Composable
fun FocusCameraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1976D2),
            primaryContainer = Color(0xFFE3F2FD),
            error = Color(0xFFD32F2F),
            errorContainer = Color(0xFFFFEBEE),
            onSurface = Color(0xFF212121),
            onSurfaceVariant = Color(0xFF757575),
            surfaceVariant = Color(0xFFF5F5F5)
        ),
        content = content
    )
}

@Composable
fun FocusCameraHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text("Focus Tracking", fontWeight = FontWeight.Bold)
        Text("Maintain focus for better productivity")
    }
}

@Composable
fun FocusStatus(status: String, focused: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (focused)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (focused) Icons.Default.CheckCircle else Icons.Default.Warning,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(if (focused) "Focused" else "Distracted", fontWeight = FontWeight.Bold)
                Text(status)
            }
        }
    }
}

@Composable
fun FocusTips() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Focus Tips", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("• Sit upright and maintain good posture")
            Text("• Look directly at the screen")
            Text("• Take breaks every 25–30 minutes")
        }
    }
}
