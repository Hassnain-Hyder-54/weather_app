package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CitySearchBar
import com.example.ui.components.ClimateMetricsGrid
import com.example.ui.components.DailyForecastSection
import com.example.ui.components.HourlyForecastSection
import com.example.ui.components.QuickCityChips
import com.example.ui.components.WeatherAtmosphere
import com.example.ui.components.WeatherHeaderCard
import com.example.ui.components.WeatherSettingsDialog

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val savedCities by viewModel.savedCities.collectAsStateWithLifecycle()

    val atmosphere = WeatherAtmosphere.getAtmosphere(
        weatherCode = uiState.currentWeather?.weatherCode ?: 0,
        isDay = uiState.currentWeather?.isDay == 1
    )

    val todayDaily = uiState.dailyForecast.firstOrNull()

    val isCurrentCitySaved = savedCities.any {
        Math.abs(it.latitude - uiState.selectedCity.latitude) < 0.1 &&
                Math.abs(it.longitude - uiState.selectedCity.longitude) < 0.1
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(atmosphere.backgroundBrush)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Top Branding & Controls Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.WbSunny,
                                contentDescription = "Weather Logo",
                                tint = atmosphere.accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Weather",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.3.sp
                                ),
                                color = atmosphere.onSurface
                            )
                            Text(
                                text = "Live Climate Telemetry",
                                style = MaterialTheme.typography.labelSmall,
                                color = atmosphere.secondaryText
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilterChip(
                            selected = uiState.isMetric,
                            onClick = { viewModel.toggleUnits() },
                            label = {
                                Text(
                                    text = if (uiState.isMetric) "°C" else "°F",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = atmosphere.cardBackground,
                                labelColor = atmosphere.onSurface,
                                selectedContainerColor = atmosphere.accentColor.copy(alpha = 0.3f),
                                selectedLabelColor = atmosphere.onSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = uiState.isMetric,
                                borderColor = atmosphere.cardBorder,
                                selectedBorderColor = atmosphere.accentColor
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("unit_toggle_chip")
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { viewModel.setCustomApiDialogVisible(true) },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = "Settings",
                                tint = atmosphere.onSurface.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                // Search Bar with instant suggestions
                CitySearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    onClearQuery = { viewModel.clearSearch() },
                    searchResults = uiState.searchResults,
                    isSearching = uiState.isSearching,
                    onCitySelected = { city -> viewModel.selectCity(city) },
                    atmosphere = atmosphere
                )

                // Quick City Chips
                QuickCityChips(
                    savedCities = savedCities,
                    currentCity = uiState.selectedCity,
                    isMetric = uiState.isMetric,
                    atmosphere = atmosphere,
                    onSelectCity = { city -> viewModel.selectSavedCity(city) }
                )

                // Error Banner with Retry
                AnimatedVisibility(
                    visible = uiState.errorMessage != null && !uiState.isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFEF4444).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ErrorOutline,
                                contentDescription = "Error",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Connection Notice",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = uiState.errorMessage ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Button(
                                onClick = { viewModel.refreshWeather() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Retry", color = Color.White)
                            }
                        }
                    }
                }

                // Loading Indicator if initial load
                if (uiState.isLoading && uiState.currentWeather == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = atmosphere.accentColor,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Fetching real-time climate data...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = atmosphere.secondaryText
                            )
                        }
                    }
                } else {
                    // Header Hero Card
                    WeatherHeaderCard(
                        city = uiState.selectedCity,
                        weather = uiState.currentWeather,
                        todayForecast = todayDaily,
                        isMetric = uiState.isMetric,
                        isRefreshing = uiState.isRefreshing,
                        isSaved = isCurrentCitySaved,
                        atmosphere = atmosphere,
                        onRefresh = { viewModel.refreshWeather() },
                        onToggleSave = { viewModel.toggleSaveCurrentCity() }
                    )

                    // 24-Hour Forecast Section
                    HourlyForecastSection(
                        hourlyItems = uiState.hourlyForecast,
                        isMetric = uiState.isMetric,
                        atmosphere = atmosphere
                    )

                    // Real-Time Climate Metrics Grid
                    ClimateMetricsGrid(
                        weather = uiState.currentWeather,
                        todayForecast = todayDaily,
                        isMetric = uiState.isMetric,
                        atmosphere = atmosphere
                    )

                    // 7-Day Extended Forecast Section
                    DailyForecastSection(
                        dailyItems = uiState.dailyForecast,
                        isMetric = uiState.isMetric,
                        atmosphere = atmosphere
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Settings & API Key Dialog
        if (uiState.showCustomApiDialog) {
            WeatherSettingsDialog(
                isMetric = uiState.isMetric,
                onToggleUnits = { viewModel.toggleUnits() },
                customApiKey = uiState.customApiKey,
                onSaveApiKey = { viewModel.updateCustomApiKey(it) },
                atmosphere = atmosphere,
                onDismiss = { viewModel.setCustomApiDialogVisible(false) }
            )
        }
    }
}
