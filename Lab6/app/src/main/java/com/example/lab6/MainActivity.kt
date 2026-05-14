package com.example.lab6

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var etCity: EditText
    private lateinit var btnCheck: Button
    private lateinit var btnSave: Button
    private lateinit var layoutResult: LinearLayout
    private lateinit var tvCity: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvDesc: TextView
    private lateinit var tvDetails: TextView

    private val apiKey = "7fc2a9fb16f59c5f2eedaf8a703469c2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCity       = findViewById(R.id.etCity)
        btnCheck     = findViewById(R.id.btnCheck)
        btnSave      = findViewById(R.id.btnSave)
        layoutResult = findViewById(R.id.layoutResult)
        tvCity       = findViewById(R.id.tvCity)
        tvTemp       = findViewById(R.id.tvTemp)
        tvDesc       = findViewById(R.id.tvDesc)
        tvDetails    = findViewById(R.id.tvDetails)

        // Завантажуємо збережене місто
        val prefs = getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        val savedCity = prefs.getString("city", "")
        if (!savedCity.isNullOrEmpty()) {
            etCity.setText(savedCity)
        }

        btnCheck.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isEmpty()) {
                Toast.makeText(this, "Введіть назву міста!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            fetchWeather(city)
        }

        btnSave.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isEmpty()) {
                Toast.makeText(this, "Введіть назву міста!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Зберігаємо місто в SharedPreferences
            prefs.edit().putString("city", city).apply()
            Toast.makeText(this, "Місто збережено: $city", Toast.LENGTH_SHORT).show()

            // Оновлюємо всі віджети
            val manager = AppWidgetManager.getInstance(this)
            val ids = manager.getAppWidgetIds(
                ComponentName(this, WeatherWidget::class.java)
            )
            ids.forEach { id ->
                WeatherWidget.updateWidget(this, manager, id)
            }
        }
    }

    private fun fetchWeather(city: String) {
        btnCheck.isEnabled = false
        btnCheck.text = "Завантаження..."

        lifecycleScope.launch {
            val result = WeatherRepository.getWeather(city, apiKey)

            result.onSuccess { weather ->
                val temp = weather.main.temp.roundToInt()
                val feelsLike = weather.main.feels_like.roundToInt()
                val desc = weather.weather.firstOrNull()?.description ?: ""
                val humidity = weather.main.humidity
                val wind = weather.wind.speed

                tvCity.text = "📍 ${weather.name}"
                tvTemp.text = "$temp°C"
                tvDesc.text = desc.replaceFirstChar { it.uppercase() }
                tvDetails.text = "Відчувається як $feelsLike°C\n" +
                        "💧 Вологість: $humidity%\n" +
                        "💨 Вітер: $wind м/с"

                layoutResult.visibility = View.VISIBLE
            }

            result.onFailure {
                Toast.makeText(
                    this@MainActivity,
                    "Помилка: місто не знайдено або немає інтернету",
                    Toast.LENGTH_LONG
                ).show()
            }

            btnCheck.isEnabled = true
            btnCheck.text = "🔍 Перевірити погоду"
        }
    }
}