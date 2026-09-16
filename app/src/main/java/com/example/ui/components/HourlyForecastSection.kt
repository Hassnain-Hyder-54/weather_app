package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HourlyForecastItem
import com.example.data.model.WeatherConditionInfo
import com.example.ui.util.WeatherFormatters

@Composable
fun HourlyForecastSection(
    hourlyItems: List<HourlyForecastItem>,
    isMetric: Boolean,
    atmosphere: AtmosphereTheme,
    modifier: Modifier = Modifier
) {
    if (hourlyItems.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hourly_forecast_section")
    ) {
        Text(
            text = "Hourly Forecast (24h)",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.2.sp
            ),
            color = atmosphere.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp)
        ) {
            items(hourlyItems) { item ->
                HourlyItemCard(
                    item = item,
                    isMetric = isMetric,
                    atmosphere = atmosphere
                )
            }
        }
    }
}

@Composable
fun HourlyItemCard(
    item: HourlyForecastItem,
    isMetric: Boolean,
    atmosphere: AtmosphereTheme,
    modifier: Modifier = Modifier
) {
    val condition = WeatherConditionInfo.fromWmoCode(item.weatherCode)
    val itemAtmosphere = WeatherAtmosphere.getAtmosphere(item.weatherCode, isDay = true)

    Surface(
        modifier = modifier
            .width(74.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (item.isNow) 1.5.dp else 1.dp,
                color = if (item.isNow) atmosphere.accentColor else atmosphere.cardBorder,
                shape = RoundedCornerShape(20.dp)
            ),
        color = if (item.isNow) atmosphere.cardBackground.copy(alpha = 0.9f) else atmosphere.cardBackground,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.hourLabel,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (item.isNow) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (item.isNow) atmosphere.accentColor else atmosphere.secondaryText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = itemAtmosphere.weatherIcon,
                    contentDescription = condition.title,
                    tint = if (item.isNow) atmosphere.accentColor else atmosphere.onSurface,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = WeatherFormatters.formatTempShort(item.tempC, isMetric),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                color = atmosphere.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (item.precipProb > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WaterDrop,
                        contentDescription = "Precipitation",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${item.precipProb}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color(0xFF38BDF8)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(13.dp))
            }
        }
    }
}
