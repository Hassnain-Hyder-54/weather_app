package com.example.ui.util

import androidx.compose.ui.graphics.Color
import java.util.Locale

object WeatherFormatters {

    fun formatTemp(celsius: Double, isMetric: Boolean): String {
        return if (isMetric) {
            "${Math.round(celsius)}°C"
        } else {
            val f = (celsius * 9.0 / 5.0) + 32.0
            "${Math.round(f)}°F"
        }
    }

    fun formatTempShort(celsius: Double, isMetric: Boolean): String {
        return if (isMetric) {
            "${Math.round(celsius)}°"
        } else {
            val f = (celsius * 9.0 / 5.0) + 32.0
            "${Math.round(f)}°"
        }
    }

    fun formatSpeed(kmh: Double, isMetric: Boolean): String {
        return if (isMetric) {
            String.format(Locale.getDefault(), "%.1f km/h", kmh)
        } else {
            val mph = kmh * 0.621371
            String.format(Locale.getDefault(), "%.1f mph", mph)
        }
    }

    fun formatPrecip(mm: Double, isMetric: Boolean): String {
        return if (isMetric) {
            String.format(Locale.getDefault(), "%.1f mm", mm)
        } else {
            val inches = mm / 25.4
            String.format(Locale.getDefault(), "%.2f in", inches)
        }
    }

    fun windDegreesToDirection(degrees: Int): String {
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = ((degrees + 11.25) / 22.5).toInt() % 16
        return directions[index]
    }

    fun getUvClassification(uv: Double): Triple<String, String, Color> {
        return when {
            uv < 3.0 -> Triple("Low", "Minimal protection required", Color(0xFF4CAF50))
            uv < 6.0 -> Triple("Moderate", "Wear sunglasses & SPF", Color(0xFFFFB300))
            uv < 8.0 -> Triple("High", "Cover up & seek shade", Color(0xFFFF9800))
            uv < 11.0 -> Triple("Very High", "Extra precautions needed", Color(0xFFE53935))
            else -> Triple("Extreme", "Avoid outdoor sun exposure", Color(0xFF8E24AA))
        }
    }

    fun getHumidityDescription(humidity: Int): String {
        return when {
            humidity < 30 -> "Dry Air"
            humidity in 30..60 -> "Comfortable"
            humidity in 61..75 -> "Humid"
            else -> "Very Muggy"
        }
    }

    fun getPressureEvaluation(hPa: Double): String {
        return when {
            hPa > 1022.0 -> "High (Stable, Clear)"
            hPa < 1005.0 -> "Low (Storm Risk)"
            else -> "Normal Atmospheric"
        }
    }
}
