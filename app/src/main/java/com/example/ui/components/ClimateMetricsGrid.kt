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
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrentWeather
import com.example.data.model.DailyForecastItem
import com.example.ui.util.WeatherFormatters

@Composable
fun ClimateMetricsGrid(
    weather: CurrentWeather?,
    todayForecast: DailyForecastItem?,
    isMetric: Boolean,
    atmosphere: AtmosphereTheme,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("climate_metrics_grid"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Real-Time Climate Data",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.2.sp
            ),
            color = atmosphere.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        // Row 1: Humidity & Wind
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val humidity = weather?.humidity ?: 0
            ClimateMetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.WaterDrop,
                label = "Humidity",
                value = "$humidity%",
                subtext = WeatherFormatters.getHumidityDescription(humidity),
                atmosphere = atmosphere
            )

            val windSpeed = weather?.windSpeed ?: 0.0
            val windDir = WeatherFormatters.windDegreesToDirection(weather?.windDirection ?: 0)
            ClimateMetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Air,
                label = "Wind & Gusts",
                value = WeatherFormatters.formatSpeed(windSpeed, isMetric),
                subtext = "$windDir • Gusts ${WeatherFormatters.formatSpeed(weather?.windGusts ?: 0.0, isMetric)}",
                atmosphere = atmosphere
            )
        }

        // Row 2: UV Index & Atmospheric Pressure
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val uv = todayForecast?.maxUv ?: 0.0
            val (uvTitle, _, uvColor) = WeatherFormatters.getUvClassification(uv)
            ClimateMetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.WbSunny,
                label = "UV Index",
                value = String.format("%.1f", uv),
                subtext = uvTitle,
                badgeColor = uvColor,
                atmosphere = atmosphere
            )

            val pressure = weather?.pressureMsl ?: 1013.25
            ClimateMetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Compress,
                label = "Pressure",
                value = "${Math.round(pressure)} hPa",
                subtext = WeatherFormatters.getPressureEvaluation(pressure),
                atmosphere = atmosphere
            )
        }

        // Row 3: Precipitation & Cloud Cover
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val precip = weather?.precipitation ?: 0.0
            ClimateMetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Thermostat,
                label = "Precipitation",
                value = WeatherFormatters.formatPrecip(precip, isMetric),
                subtext = "Chance: ${todayForecast?.precipProbMax ?: 0}%",
                atmosphere = atmosphere
            )

            val cloudCover = weather?.cloudCover ?: 0
            ClimateMetricCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Cloud,
                label = "Cloud Coverage",
                value = "$cloudCover%",
                subtext = if (cloudCover > 60) "Heavy Cloud Shield" else "Clear Skies",
                atmosphere = atmosphere
            )
        }

        // Sun & Sunset card
        if (todayForecast != null && todayForecast.sunriseTime.isNotBlank() && todayForecast.sunsetTime.isNotBlank()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, atmosphere.cardBorder, RoundedCornerShape(20.dp)),
                color = atmosphere.cardBackground,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.WbTwilight,
                                contentDescription = "Sun Cycle",
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Solar Cycle",
                                style = MaterialTheme.typography.labelMedium,
                                color = atmosphere.secondaryText
                            )
                            Text(
                                text = "Sunrise: ${todayForecast.sunriseTime}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = atmosphere.onSurface
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Sunset",
                            style = MaterialTheme.typography.labelMedium,
                            color = atmosphere.secondaryText
                        )
                        Text(
                            text = todayForecast.sunsetTime,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = atmosphere.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClimateMetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    subtext: String,
    atmosphere: AtmosphereTheme,
    modifier: Modifier = Modifier,
    badgeColor: Color? = null
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, atmosphere.cardBorder, RoundedCornerShape(20.dp)),
        color = atmosphere.cardBackground,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = atmosphere.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = atmosphere.secondaryText,
                        maxLines = 1
                    )
                }

                if (badgeColor != null) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(badgeColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = atmosphere.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall,
                color = atmosphere.secondaryText,
                maxLines = 1
            )
        }
    }
}
