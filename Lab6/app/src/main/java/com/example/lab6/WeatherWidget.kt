package com.example.lab6

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class WeatherWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        const val API_KEY = "7fc2a9fb16f59c5f2eedaf8a703469c2"

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
            val city = prefs.getString("city", "Kyiv") ?: "Kyiv"

            val views = RemoteViews(context.packageName, R.layout.widget_weather)
            views.setTextViewText(R.id.tvWidgetCity, city)
            views.setTextViewText(R.id.tvWidgetDesc, "Завантаження...")

            appWidgetManager.updateAppWidget(appWidgetId, views)

            CoroutineScope(Dispatchers.IO).launch {
                val result = WeatherRepository.getWeather(city, API_KEY)
                result.onSuccess { weather ->
                    val temp = weather.main.temp.roundToInt()
                    val desc = weather.weather.firstOrNull()?.description ?: ""
                    val humidity = weather.main.humidity

                    views.setTextViewText(R.id.tvWidgetCity, weather.name)
                    views.setTextViewText(R.id.tvWidgetTemp, "$temp°C")
                    views.setTextViewText(R.id.tvWidgetDesc, desc)
                    views.setTextViewText(R.id.tvWidgetHumidity, "Вологість: $humidity%")

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
                result.onFailure {
                    views.setTextViewText(R.id.tvWidgetDesc, "Помилка завантаження")
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}