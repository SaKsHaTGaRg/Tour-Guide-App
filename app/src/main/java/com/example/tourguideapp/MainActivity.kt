package com.example.tourguideapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

class MainActivity : BaseActivity() {

    private lateinit var cameraPreview: PreviewView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnUploadPhoto: Button
    private lateinit var imgPreview: ImageView

    private var imageCapture: ImageCapture? = null
    private val CAMERA_PERMISSION_CODE = 101

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
            simulateUploadPhoto()
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

    private fun drawableToFile(drawableId: Int): File {
        val bitmap = BitmapFactory.decodeResource(resources, drawableId)
        val file = File(cacheDir, "dummy_${drawableId}.jpg")

        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()

        return file
    }

    private fun simulateUploadPhoto() {
        // Hide camera preview (to make UI cleaner)
        cameraPreview.visibility = View.GONE
        btnTakePhoto.visibility = View.GONE

        // Randomly choose between dummy images
        val dummyImages = listOf(R.drawable.dummy2, R.drawable.dummy3)
        val selectedImage = dummyImages[Random.nextInt(dummyImages.size)]

        // Show dummy image as preview
        imgPreview.setImageResource(selectedImage)
        val dummyFile = drawableToFile(selectedImage)

        // Go to reload activity
        val intent = Intent(this, ReloadActivity::class.java)
        intent.putExtra(EXTRA_PHOTO_PATH, dummyFile.absolutePath)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        // Reset layout to normal when coming back from result
        cameraPreview.visibility = View.VISIBLE
        btnTakePhoto.visibility = View.VISIBLE
        imgPreview.setImageDrawable(null)
    }
}
