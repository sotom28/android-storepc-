package com.example.storecomponents.features.camera

import android.graphics.Bitmap
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CameraCaptureScreen(cameraViewModel: CameraViewModel = viewModel(), onPhotoCaptured: (Bitmap) -> Unit = {}) {
    val context = LocalContext.current
    val lifecycleOwner = remember { context as LifecycleOwner }
    val capturedBitmap by cameraViewModel.capturedBitmap.collectAsState()
    val imageCapture = remember { ImageCapture.Builder().build() }
    val preview = remember { androidx.camera.core.Preview.Builder().build() }

    Box(modifier = Modifier.fillMaxSize()) {
        if (capturedBitmap == null) {
            AndroidView(factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (exc: Exception) {
                        // Log error
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            }, modifier = Modifier.fillMaxSize())

            Button(onClick = {
                val executor = ContextCompat.getMainExecutor(context)
                imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val bitmap = cameraViewModel.imageProxyToBitmap(image)
                        cameraViewModel.onPhotoCaptured(bitmap)
                        // Notificar al llamador con la Bitmap (si proporcionó la lambda)
                        onPhotoCaptured(bitmap)
                        image.close()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        // Log error
                    }
                })
            }) {
                Text("Capturar foto")
            }
        } else {
            capturedBitmap?.let { bmp ->
                Image(bitmap = bmp.asImageBitmap(), contentDescription = "Foto capturada", modifier = Modifier.fillMaxSize())
                Button(onClick = { cameraViewModel.clearCapturedPhoto() }) {
                    Text("Volver a tomar")
                }
            }
        }
    }
}
