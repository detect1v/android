package com.example.lab4

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etUrl: EditText
    private var mediaType: String = "video"

    // Лаунчер для вибору файлу зі сховища
    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("mediaUri", it.toString())
                putExtra("mediaType", mediaType)
            }
            startActivity(intent)
        }
    }

    // Лаунчер для запиту дозволів
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            openFilePicker()
        } else {
            Toast.makeText(this, "Дозвіл не надано!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.webkit.WebView(this).loadUrl("https://www.google.com")
        setContentView(R.layout.activity_main)

        etUrl = findViewById(R.id.etUrl)

        findViewById<Button>(R.id.btnPickAudio).setOnClickListener {
            mediaType = "audio"
            checkPermissionsAndPick()
        }

        findViewById<Button>(R.id.btnPickVideo).setOnClickListener {
            mediaType = "video"
            checkPermissionsAndPick()
        }

        findViewById<Button>(R.id.btnPlayUrl).setOnClickListener {
            val url = etUrl.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(this, "Введіть URL!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("mediaUri", url)
                putExtra("mediaType", "url")
            }
            startActivity(intent)
        }
    }

    private fun checkPermissionsAndPick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = if (mediaType == "audio") {
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                arrayOf(Manifest.permission.READ_MEDIA_VIDEO)
            }
            if (permissions.all {
                    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
                }) {
                openFilePicker()
            } else {
                requestPermissionLauncher.launch(permissions)
            }
        } else {
            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                openFilePicker()
            } else {
                requestPermissionLauncher.launch(arrayOf(permission))
            }
        }
    }

    private fun openFilePicker() {
        val mimeType = if (mediaType == "audio") "audio/*" else "video/*"
        pickFileLauncher.launch(mimeType)
    }
}