package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.model.GeoLocation

@Composable
fun CitySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    searchResults: List<GeoLocation>,
    isSearching: Boolean,
    onCitySelected: (GeoLocation) -> Unit,
    atmosphere: AtmosphereTheme,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("city_search_input"),
            placeholder = {
                Text(
                    text = "Search any city in the world...",
                    color = atmosphere.secondaryText
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search",
                    tint = atmosphere.accentColor
                )
            },
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = atmosphere.accentColor,
                        strokeWidth = 2.dp
                    )
                } else if (query.isNotEmpty()) {
                    IconButton(
                        onClick = onClearQuery,
                        modifier = Modifier.testTag("clear_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Clear search",
                            tint = atmosphere.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = atmosphere.cardBackground.copy(alpha = 0.85f),
                unfocusedContainerColor = atmosphere.cardBackground,
                focusedBorderColor = atmosphere.accentColor,
                unfocusedBorderColor = atmosphere.cardBorder,
                focusedTextColor = atmosphere.onSurface,
                unfocusedTextColor = atmosphere.onSurface,
                cursorColor = atmosphere.accentColor
            )
        )

        // Dropdown Search Results
        AnimatedVisibility(
            visible = query.length >= 2 && (searchResults.isNotEmpty() || isSearching),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, atmosphere.cardBorder, RoundedCornerShape(20.dp)),
                color = atmosphere.cardBackground.copy(alpha = 0.95f),
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 8.dp
            ) {
                if (searchResults.isEmpty() && !isSearching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No cities found for '$query'",
                            style = MaterialTheme.typography.bodyMedium,
                            color = atmosphere.secondaryText
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.height((searchResults.size * 64).coerceAtMost(260).dp)) {
                        items(searchResults) { city ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCitySelected(city) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .testTag("search_result_${city.name}"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(atmosphere.accentColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.LocationCity,
                                        contentDescription = null,
                                        tint = atmosphere.accentColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = city.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = atmosphere.onSurface
                                    )
                                    val region = buildString {
                                        if (!city.admin1.isNullOrBlank()) append(city.admin1).append(", ")
                                        if (!city.country.isNullOrBlank()) append(city.country)
                                    }
                                    if (region.isNotBlank()) {
                                        Text(
                                            text = region,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = atmosphere.secondaryText
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(color = atmosphere.cardBorder.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}
