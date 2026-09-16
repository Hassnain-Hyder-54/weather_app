package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrentWeather
import com.example.data.model.DailyForecastItem
import com.example.data.model.GeoLocation
import com.example.data.model.WeatherConditionInfo
import com.example.ui.util.WeatherFormatters

@Composable
fun WeatherHeaderCard(
    city: GeoLocation,
    weather: CurrentWeather?,
    todayForecast: DailyForecastItem?,
    isMetric: Boolean,
    isRefreshing: Boolean,
    isSaved: Boolean,
    atmosphere: AtmosphereTheme,
    onRefresh: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .border(1.dp, atmosphere.cardBorder, RoundedCornerShape(28.dp))
            .testTag("weather_header_card"),
        color = atmosphere.cardBackground,
        shape = RoundedCornerShape(28.dp),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top action row: City badge & refresh / bookmark buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = "Location",
                        tint = atmosphere.accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = city.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.3.sp
                        ),
                        color = atmosphere.onSurface,
                        maxLines = 1
                    )
                }

                // Action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("save_city_button")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                            contentDescription = if (isSaved) "Saved City" else "Save City",
                            tint = if (isSaved) atmosphere.accentColor else atmosphere.onSurface.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onRefresh,
                        enabled = !isRefreshing,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("refresh_weather_button")
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = atmosphere.accentColor,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Refresh weather",
                                tint = atmosphere.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Weather Icon & Condition badge
            val conditionInfo = WeatherConditionInfo.fromWmoCode(weather?.weatherCode ?: 0)

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = atmosphere.weatherIcon,
                    contentDescription = conditionInfo.title,
                    tint = atmosphere.accentColor,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Temperature Display
            AnimatedContent(
                targetState = weather?.temperature ?: 0.0,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "temp_animation"
            ) { temp ->
                Text(
                    text = WeatherFormatters.formatTempShort(temp, isMetric),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 68.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-2).sp
                    ),
                    color = atmosphere.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            // Condition Title
            Text(
                text = conditionInfo.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = atmosphere.onSurface.copy(alpha = 0.95f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Apparent Temperature (Feels like) & High/Low
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Feels like ${WeatherFormatters.formatTemp(weather?.apparentTemperature ?: 0.0, isMetric)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = atmosphere.secondaryText
                )

                if (todayForecast != null) {
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = atmosphere.secondaryText
                    )
                    Text(
                        text = "H: ${WeatherFormatters.formatTempShort(todayForecast.tempMaxC, isMetric)}  L: ${WeatherFormatters.formatTempShort(todayForecast.tempMinC, isMetric)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = atmosphere.onSurface
                    )
                }
            }
        }
    }
}
