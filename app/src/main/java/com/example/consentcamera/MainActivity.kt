package com.example.consentcamera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private var imageCapture: ImageCapture? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera() else {/* show explanation to user */}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        previewView = findViewById(R.id.previewView)
        val captureButton = findViewById<android.widget.Button>(R.id.captureButton)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        captureButton.setOnClickListener { takePhotoAndUpload() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
            } catch (exc: Exception) {
                // bind failed
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhotoAndUpload() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(cacheDir, "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())}.jpg")

        val outputOptions = OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // Upload in background
                CoroutineScope(Dispatchers.IO).launch { uploadFile(photoFile) }
            }

            override fun onError(exception: ImageCaptureException) {
                // handle error
            }
        })
    }

    private fun uploadFile(file: File) {
        val client = OkHttpClient()
        // TODO: change to your real endpoint
        val url = "https://example.com/upload"

        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, RequestBody.create("image/jpeg".toMediaTypeOrNull(), file))
            .build()

        val request = Request.Builder().url(url).post(requestBody).build()

        client.newCall(request).execute().use { response ->
            // You can inspect response.isSuccessful and act accordingly
        }
    }
}
