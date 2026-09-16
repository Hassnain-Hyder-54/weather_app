package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyForecastItem
import com.example.data.model.WeatherConditionInfo
import com.example.ui.util.WeatherFormatters

@Composable
fun DailyForecastSection(
    dailyItems: List<DailyForecastItem>,
    isMetric: Boolean,
    atmosphere: AtmosphereTheme,
    modifier: Modifier = Modifier
) {
    if (dailyItems.isEmpty()) return

    val globalMin = dailyItems.minOfOrNull { it.tempMinC } ?: 0.0
    val globalMax = dailyItems.maxOfOrNull { it.tempMaxC } ?: 40.0
    val tempRange = (globalMax - globalMin).coerceAtLeast(1.0)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, atmosphere.cardBorder, RoundedCornerShape(24.dp))
            .testTag("daily_forecast_section"),
        color = atmosphere.cardBackground,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "7-Day Climate Outlook",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp
                ),
                color = atmosphere.onSurface
            )

            dailyItems.forEach { item ->
                DailyForecastRow(
                    item = item,
                    globalMin = globalMin,
                    tempRange = tempRange,
                    isMetric = isMetric,
                    atmosphere = atmosphere
                )
            }
        }
    }
}

@Composable
fun DailyForecastRow(
    item: DailyForecastItem,
    globalMin: Double,
    tempRange: Double,
    isMetric: Boolean,
    atmosphere: AtmosphereTheme
) {
    val condition = WeatherConditionInfo.fromWmoCode(item.weatherCode)
    val itemAtmosphere = WeatherAtmosphere.getAtmosphere(item.weatherCode, isDay = true)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Day of week & date
        Column(modifier = Modifier.width(85.dp)) {
            Text(
                text = item.dayOfWeek,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (item.dayOfWeek == "Today") FontWeight.Bold else FontWeight.Medium
                ),
                color = if (item.dayOfWeek == "Today") atmosphere.accentColor else atmosphere.onSurface,
                maxLines = 1
            )
            Text(
                text = item.dateFormatted,
                style = MaterialTheme.typography.labelSmall,
                color = atmosphere.secondaryText,
                maxLines = 1
            )
        }

        // Icon & rain prob
        Row(
            modifier = Modifier.width(65.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = itemAtmosphere.weatherIcon,
                contentDescription = condition.title,
                tint = atmosphere.accentColor,
                modifier = Modifier.size(22.dp)
            )

            if (item.precipProbMax > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Rounded.WaterDrop,
                    contentDescription = "Precipitation",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = "${item.precipProbMax}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color(0xFF38BDF8)
                )
            }
        }

        // Temperature range visualization bar
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = WeatherFormatters.formatTempShort(item.tempMinC, isMetric),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = atmosphere.secondaryText,
                modifier = Modifier.width(36.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Range indicator bar
            val leftFraction = ((item.tempMinC - globalMin) / tempRange).toFloat().coerceIn(0f, 0.8f)
            val rightFraction = ((item.tempMaxC - globalMin) / tempRange).toFloat().coerceIn(0.2f, 1f)
            val widthFraction = (rightFraction - leftFraction).coerceAtLeast(0.15f)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(widthFraction)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8),
                                    Color(0xFFFBBF24),
                                    Color(0xFFF97316)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = WeatherFormatters.formatTempShort(item.tempMaxC, isMetric),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = atmosphere.onSurface,
                modifier = Modifier.width(36.dp)
            )
        }
    }
}
