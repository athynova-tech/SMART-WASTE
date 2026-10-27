package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CharcoalOutline
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenLight
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.MunicipalBlueLight
import com.example.ui.theme.ObsidianDarkBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

data class OverflowPreset(
    val title: String,
    val ward: String,
    val landmark: String,
    val severity: String,
    val estimatedOverflowKg: Int,
    val detectedIssues: List<String>
)

@Composable
fun OverflowBinReportScreen(
    onDismiss: () -> Unit,
    onSubmitReport: (ward: String, landmark: String, severity: String, notes: String, photoUri: String?, latitude: Double, longitude: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Form states
    var selectedWard by remember { mutableStateOf("Kibada") }
    var locationLandmark by remember { mutableStateOf("Opposite Kibada Central Market near bus stop") }
    var severityLevel by remember { mutableStateOf("Severe Overflow") }
    var residentNotes by remember { mutableStateOf("Bin is full, domestic waste and cardboard spilling onto pedestrian sidewalk.") }
    var gpsCoordinates by remember { mutableStateOf("-6.8654° S, 39.3121° E") }
    var latitude by remember { mutableStateOf(-6.8654) }
    var longitude by remember { mutableStateOf(39.3121) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Camera states
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isTorchOn by remember { mutableStateOf(false) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var cameraInstance by remember { mutableStateOf<Camera?>(null) }
    var imageCaptureInstance by remember { mutableStateOf<ImageCapture?>(null) }
    var showCameraViewfinder by remember { mutableStateOf(true) }
    var simulatedPresetSelected by remember { mutableStateOf<String?>("Kibada Market Dumpster") }

    val wardsList = remember {
        listOf("Kibada", "Kigamboni Ferry", "Somangila", "Vijibweni", "Tuangoma", "Kisarawe II")
    }

    val severityOptions = remember {
        listOf(
            "Mild Overfill" to AmberWarning,
            "Severe Overflow" to Color(0xFFF97316),
            "Critical Hazard / Spillage" to Color(0xFFEF4444)
        )
    }

    val overflowPresets = remember {
        listOf(
            OverflowPreset(
                title = "Kibada Market Dumpster",
                ward = "Kibada",
                landmark = "Kibada Central Market Main Entrance",
                severity = "Severe Overflow",
                estimatedOverflowKg = 150,
                detectedIssues = listOf("Plastic Bottles", "Cardboard", "Pedestrian Obstruction")
            ),
            OverflowPreset(
                title = "Ferry Terminal Bin",
                ward = "Kigamboni Ferry",
                landmark = "Magogoni Ferry Passenger Waiting Bay",
                severity = "Critical Hazard / Spillage",
                estimatedOverflowKg = 220,
                detectedIssues = listOf("Organic Spoilage", "Liquid Runoff", "High Foot Traffic")
            ),
            OverflowPreset(
                title = "Somangila Beachside Point",
                ward = "Somangila",
                landmark = "Somangila Public Beach Access Point",
                severity = "Mild Overfill",
                estimatedOverflowKg = 85,
                detectedIssues = listOf("Beverage Cans", "Food Wrappers")
            ),
            OverflowPreset(
                title = "Tuangoma Junction Bin",
                ward = "Tuangoma",
                landmark = "Tuangoma-Vikindu Road Crossing",
                severity = "Severe Overflow",
                estimatedOverflowKg = 190,
                detectedIssues = listOf("Construction Scraps", "Unsorted Waste")
            )
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDarkBg),
        color = ObsidianDarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CharcoalSurface)
                            .border(1.dp, CharcoalOutline, CircleShape)
                            .testTag("close_overflow_camera_screen")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Slate50,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Report Overflowing Bin",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate50
                        )
                        Text(
                            text = "Kigamboni Rapid Municipal Response",
                            fontSize = 11.sp,
                            color = EcoGreenPrimary
                        )
                    }
                }

                // AI Assist Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EcoGreenPrimary.copy(alpha = 0.15f))
                        .border(1.dp, EcoGreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Vision Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EcoGreenPrimary)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: Live Camera Viewfinder or Captured Photo
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                        border = BorderStroke(1.dp, CharcoalOutline)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (capturedBitmap != null) "📸 Photo Evidence Confirmed" else "📷 Camera Viewfinder & Bin Scanner",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate50
                                )
                                if (capturedBitmap != null) {
                                    Text(
                                        text = "Retake",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoGreenPrimary,
                                        modifier = Modifier.clickable {
                                            capturedBitmap = null
                                            showCameraViewfinder = true
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                if (capturedBitmap != null) {
                                    // Captured image display
                                    Image(
                                        bitmap = capturedBitmap!!.asImageBitmap(),
                                        contentDescription = "Captured Bin Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )

                                    // Watermark / timestamp overlay
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(8.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.Black.copy(alpha = 0.75f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "📍 $selectedWard • $gpsCoordinates",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate50
                                        )
                                    }
                                } else if (!hasCameraPermission) {
                                    // Permission Request Fallback
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoCamera,
                                            contentDescription = null,
                                            tint = EcoGreenPrimary,
                                            modifier = Modifier.size(40.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Camera Permission Required",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate50
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Grant camera access to capture real-time photos of overflowing public waste bins.",
                                            fontSize = 11.sp,
                                            color = Slate400,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                            colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("grant_camera_permission_button")
                                        ) {
                                            Text("Allow Camera Access", color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else {
                                    // Live CameraX Preview View
                                    AndroidView(
                                        modifier = Modifier.fillMaxSize(),
                                        factory = { ctx ->
                                            val previewView = PreviewView(ctx).apply {
                                                layoutParams = ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.MATCH_PARENT
                                                )
                                                scaleType = PreviewView.ScaleType.FILL_CENTER
                                            }

                                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                            cameraProviderFuture.addListener({
                                                val cameraProvider = cameraProviderFuture.get()
                                                val preview = Preview.Builder().build().also {
                                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                                }

                                                val imageCapture = ImageCapture.Builder()
                                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                                    .build()
                                                imageCaptureInstance = imageCapture

                                                val cameraSelector = CameraSelector.Builder()
                                                    .requireLensFacing(lensFacing)
                                                    .build()

                                                try {
                                                    cameraProvider.unbindAll()
                                                    val camera = cameraProvider.bindToLifecycle(
                                                        lifecycleOwner,
                                                        cameraSelector,
                                                        preview,
                                                        imageCapture
                                                    )
                                                    cameraInstance = camera
                                                } catch (exc: Exception) {
                                                    Log.e("CameraPreview", "Camera binding failed", exc)
                                                }
                                            }, ContextCompat.getMainExecutor(ctx))

                                            previewView
                                        }
                                    )

                                    // Viewfinder HUD overlay
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp)
                                    ) {
                                        // Target alignment bounding frame
                                        Box(
                                            modifier = Modifier
                                                .size(160.dp)
                                                .align(Alignment.Center)
                                                .border(2.dp, EcoGreenPrimary.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                        )

                                        // Camera controls overlay (Flash & Lens flip)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.TopCenter),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    isTorchOn = !isTorchOn
                                                    cameraInstance?.cameraControl?.enableTorch(isTorchOn)
                                                },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Black.copy(alpha = 0.6f))
                                            ) {
                                                Icon(
                                                    imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                                    contentDescription = "Flashlight Toggle",
                                                    tint = if (isTorchOn) AmberWarning else Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                                        CameraSelector.LENS_FACING_FRONT
                                                    } else {
                                                        CameraSelector.LENS_FACING_BACK
                                                    }
                                                },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Black.copy(alpha = 0.6f))
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.FlipCameraAndroid,
                                                    contentDescription = "Flip Camera",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        // Shutter Button
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White)
                                                    .border(3.dp, EcoGreenPrimary, CircleShape)
                                                    .clickable {
                                                        val imageCapture = imageCaptureInstance
                                                        if (imageCapture != null) {
                                                            val executor = Executors.newSingleThreadExecutor()
                                                            imageCapture.takePicture(
                                                                executor,
                                                                object : ImageCapture.OnImageCapturedCallback() {
                                                                    override fun onCaptureSuccess(image: ImageProxy) {
                                                                        val buffer = image.planes[0].buffer
                                                                        val bytes = ByteArray(buffer.remaining())
                                                                        buffer.get(bytes)
                                                                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                                                        image.close()

                                                                        ContextCompat.getMainExecutor(context).execute {
                                                                            capturedBitmap = bitmap
                                                                        }
                                                                    }

                                                                    override fun onError(exception: ImageCaptureException) {
                                                                        Log.e("CameraCapture", "Capture failed: ${exception.message}", exception)
                                                                        // Fallback generate simulated snapshot
                                                                        ContextCompat.getMainExecutor(context).execute {
                                                                            capturedBitmap = createSimulatedBitmap(selectedWard)
                                                                        }
                                                                    }
                                                                }
                                                            )
                                                        } else {
                                                            // Fallback simulation
                                                            capturedBitmap = createSimulatedBitmap(selectedWard)
                                                        }
                                                    }
                                                    .testTag("camera_shutter_button"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .clip(CircleShape)
                                                        .background(EcoGreenPrimary)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick Preset Selector for Easy Emulator/Device Testing
                            Text(
                                text = "Or select verified municipal incident sample:",
                                fontSize = 11.sp,
                                color = Slate400
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(overflowPresets) { preset ->
                                    val isSel = simulatedPresetSelected == preset.title
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) EcoGreenPrimary.copy(alpha = 0.2f) else ObsidianDarkBg)
                                            .border(1.dp, if (isSel) EcoGreenPrimary else CharcoalOutline, RoundedCornerShape(8.dp))
                                            .clickable {
                                                simulatedPresetSelected = preset.title
                                                selectedWard = preset.ward
                                                locationLandmark = preset.landmark
                                                severityLevel = preset.severity
                                                residentNotes = "Observed overflowing bin with ${preset.detectedIssues.joinToString(", ")}. Requires immediate cleanup."
                                                capturedBitmap = createSimulatedBitmap(preset.ward)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = preset.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) EcoGreenPrimary else Slate400
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Section 2: AI Vision Waste Diagnostics
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CharcoalOutline)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Waste Fill Analysis", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when (severityLevel) {
                                                "Mild Overfill" -> AmberWarning.copy(alpha = 0.2f)
                                                "Severe Overflow" -> Color(0xFFF97316).copy(alpha = 0.2f)
                                                else -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = severityLevel.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (severityLevel) {
                                            "Mild Overfill" -> AmberWarning
                                            "Severe Overflow" -> Color(0xFFF97316)
                                            else -> Color(0xFFEF4444)
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MetricMiniChip("Estimated Volume", "~120 - 180 kg", EcoGreenPrimary, Modifier.weight(1f))
                                MetricMiniChip("Fill Capacity", "135% (Spill)", Color(0xFFF97316), Modifier.weight(1f))
                                MetricMiniChip("Dispatch Priority", "P1 Urgent", Color(0xFFEF4444), Modifier.weight(1f))
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Detected Waste Types: 🥤 Plastics & Poly • 📦 Corrugated Boxes • 🍏 Decomposing Organics",
                                fontSize = 11.sp,
                                color = Slate400
                            )
                        }
                    }
                }

                // Section 3: Location, Ward & Severity Form
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CharcoalOutline)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Incident Location & Details", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate50)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Ward Selector Chips
                            Text("Municipal Ward", fontSize = 11.sp, color = Slate400)
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(wardsList) { ward ->
                                    val isSel = selectedWard == ward
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) EcoGreenPrimary else ObsidianDarkBg)
                                            .border(1.dp, if (isSel) EcoGreenPrimary else CharcoalOutline, RoundedCornerShape(8.dp))
                                            .clickable { selectedWard = ward }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = ward,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) Color.Black else Slate400
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Landmark / Street Field
                            OutlinedTextField(
                                value = locationLandmark,
                                onValueChange = { locationLandmark = it },
                                label = { Text("Landmark / Street Address", color = Slate400) },
                                leadingIcon = {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(18.dp))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("overflow_landmark_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EcoGreenPrimary,
                                    unfocusedBorderColor = CharcoalOutline,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate50,
                                    focusedContainerColor = ObsidianDarkBg,
                                    unfocusedContainerColor = ObsidianDarkBg
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // GPS Coordinates & Auto-locate
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ObsidianDarkBg)
                                    .border(1.dp, CharcoalOutline, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "GPS: $gpsCoordinates", fontSize = 11.sp, color = Slate400)
                                }
                                Text(
                                    text = "Auto-Located",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MunicipalBlue
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Severity Level Selection
                            Text("Severity & Spillage Level", fontSize = 11.sp, color = Slate400)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                severityOptions.forEach { (option, col) ->
                                    val isSel = severityLevel == option
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) col.copy(alpha = 0.2f) else ObsidianDarkBg)
                                            .border(1.dp, if (isSel) col else CharcoalOutline, RoundedCornerShape(8.dp))
                                            .clickable { severityLevel = option }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = option.split(" ").first(),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSel) col else Slate400
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Resident Notes Field
                            OutlinedTextField(
                                value = residentNotes,
                                onValueChange = { residentNotes = it },
                                label = { Text("Citizen Notes / Observations (Optional)", color = Slate400) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("overflow_notes_input"),
                                minLines = 2,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EcoGreenPrimary,
                                    unfocusedBorderColor = CharcoalOutline,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate50,
                                    focusedContainerColor = ObsidianDarkBg,
                                    unfocusedContainerColor = ObsidianDarkBg
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }

                // Submit Button
                item {
                    Button(
                        onClick = {
                            isSubmitting = true
                            onSubmitReport(
                                selectedWard,
                                locationLandmark,
                                severityLevel,
                                residentNotes,
                                "captured_bin_photo_${System.currentTimeMillis()}.jpg",
                                latitude,
                                longitude
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_overflow_report_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Dispatching Municipal Alert...", color = Color.Black, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Submit Incident & Dispatch Cleanup",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun MetricMiniChip(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = ObsidianDarkBg),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, CharcoalOutline)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, fontSize = 9.sp, color = Slate400)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

/**
 * Creates a synthetic high-resolution preview bitmap representing an overflowing bin scene
 */
private fun createSimulatedBitmap(ward: String): Bitmap {
    val width = 480
    val height = 360
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // Background gradient
    val paint = android.graphics.Paint().apply {
        isAntiAlias = true
    }

    // Sky & street backdrop
    paint.color = android.graphics.Color.rgb(33, 40, 48)
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

    // Sidewalk
    paint.color = android.graphics.Color.rgb(65, 70, 78)
    canvas.drawRect(0f, height * 0.55f, width.toFloat(), height.toFloat(), paint)

    // Large Industrial Dumpster Body
    paint.color = android.graphics.Color.rgb(22, 101, 52) // Dark Green Dumpster
    canvas.drawRoundRect(width * 0.25f, height * 0.35f, width * 0.75f, height * 0.85f, 16f, 16f, paint)

    // Dumpster Rim
    paint.color = android.graphics.Color.rgb(34, 197, 94)
    canvas.drawRect(width * 0.22f, height * 0.32f, width * 0.78f, height * 0.38f, paint)

    // Spilling Trash Pile on top & ground
    paint.color = android.graphics.Color.rgb(245, 158, 11) // Cardboard/Yellow
    canvas.drawCircle(width * 0.45f, height * 0.28f, 32f, paint)
    paint.color = android.graphics.Color.rgb(239, 68, 68) // Plastic red
    canvas.drawCircle(width * 0.55f, height * 0.25f, 28f, paint)
    paint.color = android.graphics.Color.rgb(59, 130, 246) // Blue bottles
    canvas.drawCircle(width * 0.38f, height * 0.30f, 24f, paint)

    // Spilled items on sidewalk
    paint.color = android.graphics.Color.rgb(245, 158, 11)
    canvas.drawCircle(width * 0.18f, height * 0.75f, 16f, paint)
    paint.color = android.graphics.Color.rgb(239, 68, 68)
    canvas.drawCircle(width * 0.82f, height * 0.78f, 18f, paint)

    // Municipal Stamp Text
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 20f
        isFakeBoldText = true
        isAntiAlias = true
    }
    canvas.drawText("MUNICIPAL BIN #KB-${(100..999).random()}", width * 0.30f, height * 0.60f, textPaint)

    val subTextPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.rgb(180, 255, 200)
        textSize = 14f
        isAntiAlias = true
    }
    canvas.drawText("ZONE: $ward (OVERFLOW DETECTED)", width * 0.28f, height * 0.68f, subTextPaint)

    return bitmap
}
