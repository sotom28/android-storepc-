package com.example.storecomponents.features.camera

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val cameraManager = com.example.storecomponents.features.camera.CameraManager(context)
    val preview = cameraManager.getPreview()
    val cameraSelector = cameraManager.getCameraSelector()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) Log.e("CameraScreen", "Permiso de cámara denegado")
    }
    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.CAMERA) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx)
            preview.setSurfaceProvider(previewView.surfaceProvider)
            previewView.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            previewView
        }, modifier = Modifier.fillMaxSize())
    }
}
