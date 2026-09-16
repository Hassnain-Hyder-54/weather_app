package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbCloudy
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.model.WeatherCategory
import com.example.data.model.WeatherConditionInfo

data class AtmosphereTheme(
    val backgroundBrush: Brush,
    val cardBackground: Color,
    val cardBorder: Color,
    val accentColor: Color,
    val onSurface: Color,
    val secondaryText: Color,
    val weatherIcon: ImageVector
)

object WeatherAtmosphere {

    fun getAtmosphere(weatherCode: Int, isDay: Boolean): AtmosphereTheme {
        val info = WeatherConditionInfo.fromWmoCode(weatherCode)

        if (!isDay) {
            return AtmosphereTheme(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1B2A),
                        Color(0xFF1B263B),
                        Color(0xFF0F172A)
                    )
                ),
                cardBackground = Color(0xFF1E293B).copy(alpha = 0.75f),
                cardBorder = Color(0xFF38BDF8).copy(alpha = 0.25f),
                accentColor = Color(0xFF38BDF8),
                onSurface = Color(0xFFF8FAFC),
                secondaryText = Color(0xFF94A3B8),
                weatherIcon = when (info.category) {
                    WeatherCategory.RAIN -> Icons.Rounded.WaterDrop
                    WeatherCategory.SNOW -> Icons.Rounded.AcUnit
                    WeatherCategory.THUNDERSTORM -> Icons.Rounded.Thunderstorm
                    WeatherCategory.FOG, WeatherCategory.CLOUDY -> Icons.Rounded.Cloud
                    WeatherCategory.CLEAR -> Icons.Rounded.NightsStay
                }
            )
        }

        return when (info.category) {
            WeatherCategory.CLEAR -> AtmosphereTheme(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0284C7),
                        Color(0xFF0EA5E9),
                        Color(0xFF38BDF8)
                    )
                ),
                cardBackground = Color(0xFF0369A1).copy(alpha = 0.35f),
                cardBorder = Color.White.copy(alpha = 0.35f),
                accentColor = Color(0xFFFBBF24),
                onSurface = Color.White,
                secondaryText = Color.White.copy(alpha = 0.85f),
                weatherIcon = Icons.Rounded.WbSunny
            )
            WeatherCategory.CLOUDY -> AtmosphereTheme(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF334155),
                        Color(0xFF475569),
                        Color(0xFF64748B)
                    )
                ),
                cardBackground = Color(0xFF1E293B).copy(alpha = 0.45f),
                cardBorder = Color.White.copy(alpha = 0.25f),
                accentColor = Color(0xFF93C5FD),
                onSurface = Color.White,
                secondaryText = Color(0xFFE2E8F0),
                weatherIcon = Icons.Rounded.WbCloudy
            )
            WeatherCategory.FOG -> AtmosphereTheme(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF475569),
                        Color(0xFF64748B),
                        Color(0xFF94A3B8)
                    )
                ),
                cardBackground = Color(0xFF334155).copy(alpha = 0.45f),
                cardBorder = Color.White.copy(alpha = 0.25f),
                accentColor = Color(0xFFCBD5E1),
                onSurface = Color.White,
                secondaryText = Color(0xFFE2E8F0),
                weatherIcon = Icons.Rounded.Grain
            )
            WeatherCategory.RAIN -> AtmosphereTheme(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                ),
                cardBackground = Color(0xFF162D38).copy(alpha = 0.5f),
                cardBorder = Color(0xFF38BDF8).copy(alpha = 0.3f),
                accentColor = Color(0xFF38BDF8),
                onSurface = Color.White,
                secondaryText = Color(0xFFBAE6FD),
                weatherIcon = Icons.Rounded.WaterDrop
            )
            WeatherCategory.SNOW -> AtmosphereTheme(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E3A5F),
                        Color(0xFF2E5B88),
                        Color(0xFF4A7C9F)
                    )
                ),
                cardBackground = Color(0xFF1A324E).copy(alpha = 0.5f),
                cardBorder = Color.White.copy(alpha = 0.35f),
                accentColor = Color(0xFFBAE6FD),
                onSurface = Color.White,
                secondaryText = Color(0xFFE0F2FE),
                weatherIcon = Icons.Rounded.AcUnit
            )
            WeatherCategory.THUNDERSTORM -> AtmosphereTheme(
                backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF181824),
                        Color(0xFF2B213A),
                        Color(0xFF3C275A)
                    )
                ),
                cardBackground = Color(0xFF231A33).copy(alpha = 0.6f),
                cardBorder = Color(0xFFA78BFA).copy(alpha = 0.35f),
                accentColor = Color(0xFFFBBF24),
                onSurface = Color.White,
                secondaryText = Color(0xFFDDD6FE),
                weatherIcon = Icons.Rounded.Thunderstorm
            )
        }
    }
}
