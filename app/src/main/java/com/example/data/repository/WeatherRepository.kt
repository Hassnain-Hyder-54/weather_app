package com.example.data.repository

import com.example.data.api.ApiClient
import com.example.data.api.WeatherApiService
import com.example.data.local.SavedCity
import com.example.data.local.SavedCityDao
import com.example.data.model.DailyForecastItem
import com.example.data.model.GeoLocation
import com.example.data.model.HourlyForecastItem
import com.example.data.model.WeatherForecastResponse
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WeatherRepository(
    private val apiService: WeatherApiService = ApiClient.weatherService,
    private val cityDao: SavedCityDao
) {

    val savedCities: Flow<List<SavedCity>> = cityDao.getAllSavedCities()

    suspend fun initializeDefaultCitiesIfNeeded() {
        if (cityDao.count() == 0) {
            val defaults = listOf(
                SavedCity(name = "Tokyo", country = "Japan", admin1 = "Tokyo", latitude = 35.6895, longitude = 139.6917, timezone = "Asia/Tokyo", isFavorite = true),
                SavedCity(name = "London", country = "United Kingdom", admin1 = "England", latitude = 51.5074, longitude = -0.1278, timezone = "Europe/London", isFavorite = true),
                SavedCity(name = "New York", country = "United States", admin1 = "New York", latitude = 40.7128, longitude = -74.0060, timezone = "America/New_York", isFavorite = true),
                SavedCity(name = "Paris", country = "France", admin1 = "Île-de-France", latitude = 48.8566, longitude = 2.3522, timezone = "Europe/Paris", isFavorite = false),
                SavedCity(name = "Dubai", country = "United Arab Emirates", admin1 = "Dubai", latitude = 25.2048, longitude = 55.2708, timezone = "Asia/Dubai", isFavorite = false)
            )
            for (city in defaults) {
                cityDao.insertCity(city)
            }
        }
    }

    suspend fun searchCities(query: String): List<GeoLocation> {
        if (query.isBlank() || query.length < 2) return emptyList()
        return try {
            val response = apiService.searchCities(name = query.trim())
            response.results ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchWeather(lat: Double, lon: Double): Result<WeatherForecastResponse> {
        return try {
            val response = apiService.getForecast(latitude = lat, longitude = lon)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveCity(city: SavedCity): Long {
        val existing = cityDao.findCityNearby(city.latitude, city.longitude)
        return if (existing != null) {
            cityDao.updateCity(city.copy(id = existing.id, isFavorite = existing.isFavorite))
            existing.id
        } else {
            cityDao.insertCity(city)
        }
    }

    suspend fun toggleFavorite(city: SavedCity) {
        cityDao.updateCity(city.copy(isFavorite = !city.isFavorite))
    }

    suspend fun deleteCity(city: SavedCity) {
        cityDao.deleteCity(city)
    }

    suspend fun updateCityTemp(lat: Double, lon: Double, tempC: Double, code: Int) {
        val existing = cityDao.findCityNearby(lat, lon)
        if (existing != null) {
            cityDao.updateCity(
                existing.copy(
                    lastTempC = tempC,
                    lastWeatherCode = code,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    fun parseHourlyForecast(response: WeatherForecastResponse): List<HourlyForecastItem> {
        val hourly = response.hourly ?: return emptyList()
        val times = hourly.time
        val temps = hourly.temperature
        val codes = hourly.weatherCode
        val probs = hourly.precipitationProbability
        val winds = hourly.windSpeed
        val uvs = hourly.uvIndex

        val items = mutableListOf<HourlyForecastItem>()
        val count = times.size.coerceAtMost(temps.size).coerceAtMost(codes.size)

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val hourFmt = SimpleDateFormat("h a", Locale.getDefault())
        val now = System.currentTimeMillis()

        var nowIndex = 0
        var minDiff = Long.MAX_VALUE

        for (i in 0 until count) {
            try {
                val date = sdf.parse(times[i]) ?: continue
                val diff = Math.abs(date.time - now)
                if (diff < minDiff) {
                    minDiff = diff
                    nowIndex = i
                }
            } catch (_: Exception) {}
        }

        // Take 24 hours starting from current hour
        val startIndex = nowIndex.coerceAtLeast(0)
        val endIndex = (startIndex + 24).coerceAtMost(count)

        for (i in startIndex until endIndex) {
            val timeStr = times[i]
            var formattedHour = timeStr.substringAfter("T")
            try {
                val date = sdf.parse(timeStr)
                if (date != null) {
                    formattedHour = if (i == startIndex) "Now" else hourFmt.format(date)
                }
            } catch (_: Exception) {}

            items.add(
                HourlyForecastItem(
                    timeIso = timeStr,
                    hourLabel = formattedHour,
                    tempC = temps.getOrElse(i) { 0.0 },
                    weatherCode = codes.getOrElse(i) { 0 },
                    precipProb = probs.getOrElse(i) { 0 },
                    windSpeedKmh = winds.getOrElse(i) { 0.0 },
                    uvIndex = uvs.getOrElse(i) { 0.0 },
                    isNow = i == startIndex
                )
            )
        }
        return items
    }

    fun parseDailyForecast(response: WeatherForecastResponse): List<DailyForecastItem> {
        val daily = response.daily ?: return emptyList()
        val times = daily.time
        val codes = daily.weatherCode
        val maxTemps = daily.tempMax
        val minTemps = daily.tempMin
        val probs = daily.precipitationProbabilityMax
        val precipSums = daily.precipitationSum
        val sunrises = daily.sunrise
        val sunsets = daily.sunset
        val uvs = daily.uvIndexMax

        val items = mutableListOf<DailyForecastItem>()
        val count = times.size.coerceAtMost(maxTemps.size).coerceAtMost(minTemps.size)

        val dateInFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayOfWeekFmt = SimpleDateFormat("EEE", Locale.getDefault())
        val monthDayFmt = SimpleDateFormat("MMM d", Locale.getDefault())
        val timeIsoFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val timeFmt = SimpleDateFormat("h:mm a", Locale.getDefault())

        val cal = Calendar.getInstance()
        val todayDayOfYear = cal.get(Calendar.DAY_OF_YEAR)

        for (i in 0 until count) {
            val dateStr = times[i]
            var dayOfWeek = "Day ${i + 1}"
            var formattedDate = dateStr

            try {
                val d = dateInFmt.parse(dateStr)
                if (d != null) {
                    val itemCal = Calendar.getInstance().apply { time = d }
                    dayOfWeek = when (itemCal.get(Calendar.DAY_OF_YEAR) - todayDayOfYear) {
                        0 -> "Today"
                        1 -> "Tomorrow"
                        else -> dayOfWeekFmt.format(d)
                    }
                    formattedDate = monthDayFmt.format(d)
                }
            } catch (_: Exception) {}

            var sunriseFormatted = ""
            var sunsetFormatted = ""
            try {
                if (i < sunrises.size) {
                    val sDate = timeIsoFmt.parse(sunrises[i])
                    if (sDate != null) sunriseFormatted = timeFmt.format(sDate)
                }
                if (i < sunsets.size) {
                    val sDate = timeIsoFmt.parse(sunsets[i])
                    if (sDate != null) sunsetFormatted = timeFmt.format(sDate)
                }
            } catch (_: Exception) {}

            items.add(
                DailyForecastItem(
                    dateIso = dateStr,
                    dayOfWeek = dayOfWeek,
                    dateFormatted = formattedDate,
                    weatherCode = codes.getOrElse(i) { 0 },
                    tempMinC = minTemps.getOrElse(i) { 0.0 },
                    tempMaxC = maxTemps.getOrElse(i) { 0.0 },
                    precipProbMax = probs.getOrElse(i) { 0 },
                    precipSumMm = precipSums.getOrElse(i) { 0.0 },
                    sunriseTime = sunriseFormatted,
                    sunsetTime = sunsetFormatted,
                    maxUv = uvs.getOrElse(i) { 0.0 }
                )
            )
        }
        return items
    }
}
