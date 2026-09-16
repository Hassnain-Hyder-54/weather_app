package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.SavedCity
import com.example.data.model.GeoLocation
import com.example.ui.util.WeatherFormatters

@Composable
fun QuickCityChips(
    savedCities: List<SavedCity>,
    currentCity: GeoLocation,
    isMetric: Boolean,
    atmosphere: AtmosphereTheme,
    onSelectCity: (SavedCity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (savedCities.isEmpty()) return

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_city_chips"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
    ) {
        items(savedCities) { city ->
            val isSelected = Math.abs(city.latitude - currentCity.latitude) < 0.1 &&
                    Math.abs(city.longitude - currentCity.longitude) < 0.1

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) atmosphere.accentColor else atmosphere.cardBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelectCity(city) }
                    .testTag("chip_${city.name}"),
                color = if (isSelected) atmosphere.accentColor.copy(alpha = 0.25f) else atmosphere.cardBackground,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (city.isFavorite) Icons.Rounded.Bookmark else Icons.Rounded.Public,
                        contentDescription = null,
                        tint = if (isSelected) atmosphere.accentColor else atmosphere.secondaryText,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = city.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) atmosphere.onSurface else atmosphere.onSurface.copy(alpha = 0.85f)
                    )

                    if (city.lastTempC != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = WeatherFormatters.formatTempShort(city.lastTempC, isMetric),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = atmosphere.accentColor
                        )
                    }
                }
            }
        }
    }
}
