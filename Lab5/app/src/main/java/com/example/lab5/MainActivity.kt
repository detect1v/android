package com.example.lab5

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null

    private lateinit var ivArrow: ImageView
    private lateinit var tvDegrees: TextView
    private lateinit var tvDirection: TextView
    private lateinit var tvAccuracy: TextView
    private lateinit var tvSensorInfo: TextView

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var currentDegree = 0f
    private var hasGravity = false
    private var hasMagnetic = false

    // Фільтр низьких частот для плавності
    private val alpha = 0.15f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ivArrow      = findViewById(R.id.ivArrow)
        tvDegrees    = findViewById(R.id.tvDegrees)
        tvDirection  = findViewById(R.id.tvDirection)
        tvAccuracy   = findViewById(R.id.tvAccuracy)
        tvSensorInfo = findViewById(R.id.tvSensorInfo)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer  = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (accelerometer == null || magnetometer == null) {
            tvAccuracy.text = "Датчики недоступні на цьому пристрої"
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                // Фільтр низьких частот для акселерометра
                gravity[0] = alpha * event.values[0] + (1 - alpha) * gravity[0]
                gravity[1] = alpha * event.values[1] + (1 - alpha) * gravity[1]
                gravity[2] = alpha * event.values[2] + (1 - alpha) * gravity[2]
                hasGravity = true

                tvSensorInfo.text =
                    "Акселерометр: X=%.2f Y=%.2f Z=%.2f\nМагнітометр: X=%.2f Y=%.2f Z=%.2f"
                        .format(
                            gravity[0], gravity[1], gravity[2],
                            geomagnetic[0], geomagnetic[1], geomagnetic[2]
                        )
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                // Фільтр низьких частот для магнітометра
                geomagnetic[0] = alpha * event.values[0] + (1 - alpha) * geomagnetic[0]
                geomagnetic[1] = alpha * event.values[1] + (1 - alpha) * geomagnetic[1]
                geomagnetic[2] = alpha * event.values[2] + (1 - alpha) * geomagnetic[2]
                hasMagnetic = true
            }
        }

        if (hasGravity && hasMagnetic) {
            updateCompass()
        }
    }

    private fun updateCompass() {
        val rotationMatrix = FloatArray(9)
        val inclinationMatrix = FloatArray(9)

        val success = SensorManager.getRotationMatrix(
            rotationMatrix, inclinationMatrix, gravity, geomagnetic
        )

        if (success) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)

            // Азимут в градусах
            val azimuthRad = orientation[0]
            val azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
            val degree = (azimuthDeg + 360) % 360

            // Анімація обертання стрілки
            val rotate = RotateAnimation(
                -currentDegree, -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 250
                fillAfter = true
            }
            ivArrow.startAnimation(rotate)
            currentDegree = degree

            // Відображення градусів
            tvDegrees.text = "${degree.roundToInt()}°"

            // Визначення сторони світу
            tvDirection.text = getDirection(degree)
        }
    }

    private fun getDirection(degree: Float): String {
        return when {
            degree < 22.5 || degree >= 337.5  -> "Північ ↑"
            degree < 67.5                      -> "Північний Схід ↗"
            degree < 112.5                     -> "Схід →"
            degree < 157.5                     -> "Південний Схід ↘"
            degree < 202.5                     -> "Південь ↓"
            degree < 247.5                     -> "Південний Захід ↙"
            degree < 292.5                     -> "Захід ←"
            else                               -> "Північний Захід ↖"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        tvAccuracy.text = "Точність: " + when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH   -> "Висока ✓"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Середня"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW    -> "Низька ⚠"
            else                                         -> "Ненадійна ✗"
        }
    }
}