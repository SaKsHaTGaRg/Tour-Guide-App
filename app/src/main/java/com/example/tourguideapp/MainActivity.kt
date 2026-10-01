package com.example.tourguideapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : BaseActivity() {

    private lateinit var cameraPreview: PreviewView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnUploadPhoto: Button
    private lateinit var imgPreview: ImageView

    private var imageCapture: ImageCapture? = null
    private val CAMERA_PERMISSION_CODE = 101
    private val selectPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) return@registerForActivityResult
        lifecycleScope.launch {
            btnUploadPhoto.isEnabled = false
            val photo = withContext(Dispatchers.IO) {
                // Decode a bounded preview before converting gallery formats to JPEG.
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                try {
                    contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, options)
                    }
                    require(options.outWidth > 0 && options.outHeight > 0)
                    options.inJustDecodeBounds = false
                    options.inSampleSize = 1
                    while (maxOf(options.outWidth, options.outHeight) / options.inSampleSize > 2048) {
                        options.inSampleSize *= 2
                    }
                    val bitmap = contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it, null, options)
                    } ?: return@withContext null
                    try {
                        val file = File.createTempFile("selected_", ".jpg", cacheDir)
                        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it) }
                        file
                    } finally {
                        bitmap.recycle()
                    }
                } catch (_: Exception) {
                    null
                }
            }
            btnUploadPhoto.isEnabled = true
            if (photo == null) {
                Toast.makeText(this@MainActivity, "Could not open that image. Choose another photo.", Toast.LENGTH_LONG).show()
            } else {
                startActivity(Intent(this@MainActivity, ReloadActivity::class.java)
                    .putExtra(EXTRA_PHOTO_PATH, photo.absolutePath))
            }
        }
    }

    companion object {
        const val EXTRA_PHOTO_PATH = "photo_path"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraPreview = findViewById(R.id.cameraPreview)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto)
        imgPreview = findViewById(R.id.imgPreview)

        // --- Bottom navigation ---
        findViewById<ImageButton>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
        findViewById<ImageButton>(R.id.btnHome).setOnClickListener { /* stay here */ }
        findViewById<ImageButton>(R.id.btnReload).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }
        findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        // --- Camera setup ---
        if (allPermissionsGranted()) {
            cameraPreview.post { startCamera() }
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }

        // --- Buttons ---
        btnTakePhoto.setOnClickListener {
            takePhoto()
        }

        btnUploadPhoto.setOnClickListener {
            selectPhoto.launch("image/*")
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            cameraPreview.post { startCamera() }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(cameraPreview.surfaceProvider) }

            imageCapture = ImageCapture.Builder()
                .setTargetRotation(cameraPreview.display.rotation)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(externalCacheDir, "photo_${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    imgPreview.setImageBitmap(bitmap)

                    // Go to reload activity
                    val intent = Intent(this@MainActivity, ReloadActivity::class.java)
                    intent.putExtra(EXTRA_PHOTO_PATH, photoFile.absolutePath)
                    startActivity(intent)
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        // Reset layout to normal when coming back from result
        cameraPreview.visibility = View.VISIBLE
        btnTakePhoto.visibility = View.VISIBLE
        imgPreview.setImageDrawable(null)
    }
}
