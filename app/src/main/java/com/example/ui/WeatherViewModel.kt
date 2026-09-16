package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.SavedCity
import com.example.data.model.CurrentWeather
import com.example.data.model.DailyForecastItem
import com.example.data.model.GeoLocation
import com.example.data.model.HourlyForecastItem
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeatherUiState(
    val selectedCity: GeoLocation = GeoLocation(
        name = "Tokyo",
        latitude = 35.6895,
        longitude = 139.6917,
        country = "Japan",
        admin1 = "Tokyo",
        timezone = "Asia/Tokyo"
    ),
    val currentWeather: CurrentWeather? = null,
    val hourlyForecast: List<HourlyForecastItem> = emptyList(),
    val dailyForecast: List<DailyForecastItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isMetric: Boolean = true,
    val searchQuery: String = "",
    val searchResults: List<GeoLocation> = emptyList(),
    val isSearching: Boolean = false,
    val isCurrentCitySaved: Boolean = false,
    val lastUpdatedTimestamp: Long = 0L,
    val showCustomApiDialog: Boolean = false,
    val customApiKey: String = ""
)

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    val savedCities: StateFlow<List<SavedCity>> = repository.savedCities
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeDefaultCitiesIfNeeded()
            loadWeatherForCity(_uiState.value.selectedCity)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        if (query.length < 2) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            delay(350) // debounce typing
            val results = repository.searchCities(query)
            _uiState.update { it.copy(searchResults = results, isSearching = false) }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _uiState.update { it.copy(searchQuery = "", searchResults = emptyList(), isSearching = false) }
    }

    fun selectCity(city: GeoLocation) {
        clearSearch()
        _uiState.update { it.copy(selectedCity = city) }
        loadWeatherForCity(city)
    }

    fun selectSavedCity(city: SavedCity) {
        clearSearch()
        val geo = GeoLocation(
            id = city.id,
            name = city.name,
            latitude = city.latitude,
            longitude = city.longitude,
            country = city.country,
            admin1 = city.admin1,
            timezone = city.timezone
        )
        _uiState.update { it.copy(selectedCity = geo) }
        loadWeatherForCity(geo)
    }

    fun refreshWeather() {
        val city = _uiState.value.selectedCity
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            fetchData(city, isRefresh = true)
        }
    }

    private fun loadWeatherForCity(city: GeoLocation) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            fetchData(city, isRefresh = false)
        }
    }

    private suspend fun fetchData(city: GeoLocation, isRefresh: Boolean) {
        val result = repository.fetchWeather(city.latitude, city.longitude)
        result.onSuccess { response ->
            val hourly = repository.parseHourlyForecast(response)
            val daily = repository.parseDailyForecast(response)
            val current = response.current

            if (current != null) {
                repository.updateCityTemp(
                    city.latitude,
                    city.longitude,
                    current.temperature,
                    current.weatherCode
                )
            }

            _uiState.update {
                it.copy(
                    currentWeather = current,
                    hourlyForecast = hourly,
                    dailyForecast = daily,
                    isLoading = false,
                    isRefreshing = false,
                    lastUpdatedTimestamp = System.currentTimeMillis(),
                    errorMessage = null
                )
            }
        }.onFailure { err ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = err.message ?: "Failed to connect to weather service. Please check your internet."
                )
            }
        }
    }

    fun toggleUnits() {
        _uiState.update { it.copy(isMetric = !it.isMetric) }
    }

    fun toggleSaveCurrentCity() {
        val currentCity = _uiState.value.selectedCity
        val currentWeather = _uiState.value.currentWeather
        viewModelScope.launch {
            val savedCity = SavedCity(
                name = currentCity.name,
                country = currentCity.country ?: "",
                admin1 = currentCity.admin1,
                latitude = currentCity.latitude,
                longitude = currentCity.longitude,
                timezone = currentCity.timezone,
                isFavorite = true,
                lastTempC = currentWeather?.temperature,
                lastWeatherCode = currentWeather?.weatherCode,
                lastUpdated = System.currentTimeMillis()
            )
            repository.saveCity(savedCity)
        }
    }

    fun deleteSavedCity(city: SavedCity) {
        viewModelScope.launch {
            repository.deleteCity(city)
        }
    }

    fun toggleCityFavorite(city: SavedCity) {
        viewModelScope.launch {
            repository.toggleFavorite(city)
        }
    }

    fun setCustomApiDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showCustomApiDialog = visible) }
    }

    fun updateCustomApiKey(key: String) {
        _uiState.update { it.copy(customApiKey = key) }
    }

    class Factory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                return WeatherViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
