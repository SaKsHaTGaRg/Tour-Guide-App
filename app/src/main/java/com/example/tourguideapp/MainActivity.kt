package com.example.tourguideapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
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
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var cameraPreview: PreviewView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnUploadPhoto: Button
    private lateinit var imgPreview: ImageView

    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraPreview = findViewById(R.id.cameraPreview)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto)
        imgPreview = findViewById(R.id.imgPreview)

        setupNav()
        startCamera()

        btnTakePhoto.setOnClickListener { takePhoto() }


        btnUploadPhoto.setOnClickListener { uploadDummyPhoto() }
    }

    private fun setupNav() {
        findViewById<ImageButton>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {}
        findViewById<ImageButton>(R.id.btnReload).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(cameraPreview.surfaceProvider)

            imageCapture = ImageCapture.Builder().build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val outputFile = File(externalCacheDir, "${System.currentTimeMillis()}.jpg")
        val opts = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        imageCapture?.takePicture(
            opts,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                }

                override fun onImageSaved(result: ImageCapture.OutputFileResults) {

                    val bitmap = BitmapFactory.decodeFile(outputFile.absolutePath)

                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                    val compressedBytes = stream.toByteArray()

                    val base64 = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)

                    imgPreview.setImageBitmap(bitmap)

                    val intent = Intent(this@MainActivity, ReloadActivity::class.java)
                    intent.putExtra("image_base64", base64)
                    startActivity(intent)
                }
            }
        )
    }

    // ⭐ RESTORED UPLOAD FEATURE
    private fun uploadDummyPhoto() {

        val dummyImages = listOf(
            R.drawable.dummy1,
            R.drawable.dummy2
        )

        val selected = dummyImages[Random.nextInt(dummyImages.size)]

        val bitmap = BitmapFactory.decodeResource(resources, selected)

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
        val compressedBytes = stream.toByteArray()

        val base64 = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)

        imgPreview.setImageBitmap(bitmap)

        val intent = Intent(this, ReloadActivity::class.java)
        intent.putExtra("image_base64", base64)
        startActivity(intent)
    }
}
