package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    @Json(name = "results") val results: List<GeoLocation>? = null
)

@JsonClass(generateAdapter = true)
data class GeoLocation(
    @Json(name = "id") val id: Long = 0L,
    @Json(name = "name") val name: String = "",
    @Json(name = "latitude") val latitude: Double = 0.0,
    @Json(name = "longitude") val longitude: Double = 0.0,
    @Json(name = "country") val country: String? = null,
    @Json(name = "country_code") val countryCode: String? = null,
    @Json(name = "admin1") val admin1: String? = null,
    @Json(name = "timezone") val timezone: String? = null
) {
    val displayName: String
        get() = buildString {
            append(name)
            if (!admin1.isNullOrBlank() && admin1 != name) {
                append(", ").append(admin1)
            }
            if (!country.isNullOrBlank()) {
                append(", ").append(country)
            }
        }
}

@JsonClass(generateAdapter = true)
data class WeatherForecastResponse(
    @Json(name = "latitude") val latitude: Double = 0.0,
    @Json(name = "longitude") val longitude: Double = 0.0,
    @Json(name = "timezone") val timezone: String = "UTC",
    @Json(name = "current") val current: CurrentWeather? = null,
    @Json(name = "hourly") val hourly: HourlyWeather? = null,
    @Json(name = "daily") val daily: DailyWeather? = null
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    @Json(name = "time") val time: String = "",
    @Json(name = "temperature_2m") val temperature: Double = 0.0,
    @Json(name = "relative_humidity_2m") val humidity: Int = 0,
    @Json(name = "apparent_temperature") val apparentTemperature: Double = 0.0,
    @Json(name = "is_day") val isDay: Int = 1,
    @Json(name = "precipitation") val precipitation: Double = 0.0,
    @Json(name = "rain") val rain: Double = 0.0,
    @Json(name = "showers") val showers: Double = 0.0,
    @Json(name = "snowfall") val snowfall: Double = 0.0,
    @Json(name = "weather_code") val weatherCode: Int = 0,
    @Json(name = "cloud_cover") val cloudCover: Int = 0,
    @Json(name = "pressure_msl") val pressureMsl: Double = 1013.25,
    @Json(name = "surface_pressure") val surfacePressure: Double = 1013.25,
    @Json(name = "wind_speed_10m") val windSpeed: Double = 0.0,
    @Json(name = "wind_direction_10m") val windDirection: Int = 0,
    @Json(name = "wind_gusts_10m") val windGusts: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class HourlyWeather(
    @Json(name = "time") val time: List<String> = emptyList(),
    @Json(name = "temperature_2m") val temperature: List<Double> = emptyList(),
    @Json(name = "relative_humidity_2m") val humidity: List<Int> = emptyList(),
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int> = emptyList(),
    @Json(name = "weather_code") val weatherCode: List<Int> = emptyList(),
    @Json(name = "wind_speed_10m") val windSpeed: List<Double> = emptyList(),
    @Json(name = "uv_index") val uvIndex: List<Double> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DailyWeather(
    @Json(name = "time") val time: List<String> = emptyList(),
    @Json(name = "weather_code") val weatherCode: List<Int> = emptyList(),
    @Json(name = "temperature_2m_max") val tempMax: List<Double> = emptyList(),
    @Json(name = "temperature_2m_min") val tempMin: List<Double> = emptyList(),
    @Json(name = "apparent_temperature_max") val apparentTempMax: List<Double> = emptyList(),
    @Json(name = "apparent_temperature_min") val apparentTempMin: List<Double> = emptyList(),
    @Json(name = "sunrise") val sunrise: List<String> = emptyList(),
    @Json(name = "sunset") val sunset: List<String> = emptyList(),
    @Json(name = "uv_index_max") val uvIndexMax: List<Double> = emptyList(),
    @Json(name = "precipitation_sum") val precipitationSum: List<Double> = emptyList(),
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int> = emptyList(),
    @Json(name = "wind_speed_10m_max") val windSpeedMax: List<Double> = emptyList()
)

enum class WeatherCategory {
    CLEAR,
    CLOUDY,
    FOG,
    RAIN,
    SNOW,
    THUNDERSTORM
}

data class WeatherConditionInfo(
    val title: String,
    val description: String,
    val category: WeatherCategory
) {
    companion object {
        fun fromWmoCode(code: Int): WeatherConditionInfo {
            return when (code) {
                0 -> WeatherConditionInfo("Clear Sky", "Sunny, cloudless atmospheric conditions", WeatherCategory.CLEAR)
                1 -> WeatherConditionInfo("Mainly Clear", "Mostly sunny with subtle high clouds", WeatherCategory.CLEAR)
                2 -> WeatherConditionInfo("Partly Cloudy", "Scattered clouds with periodic sunshine", WeatherCategory.CLOUDY)
                3 -> WeatherConditionInfo("Overcast", "Uniform dense cloud cover", WeatherCategory.CLOUDY)
                45 -> WeatherConditionInfo("Foggy", "Dense ground-level condensation fog", WeatherCategory.FOG)
                48 -> WeatherConditionInfo("Depositing Rime Fog", "Freezing fog creating rime ice crystals", WeatherCategory.FOG)
                51 -> WeatherConditionInfo("Light Drizzle", "Fine, gentle atmospheric drizzle", WeatherCategory.RAIN)
                53 -> WeatherConditionInfo("Moderate Drizzle", "Steady light drizzle precipitation", WeatherCategory.RAIN)
                55 -> WeatherConditionInfo("Dense Drizzle", "Heavy drizzle with reduced visibility", WeatherCategory.RAIN)
                56, 57 -> WeatherConditionInfo("Freezing Drizzle", "Sub-freezing drizzle coating surfaces", WeatherCategory.RAIN)
                61 -> WeatherConditionInfo("Slight Rain", "Light steady rainfall", WeatherCategory.RAIN)
                63 -> WeatherConditionInfo("Moderate Rain", "Continuous steady rain", WeatherCategory.RAIN)
                65 -> WeatherConditionInfo("Heavy Rain", "Intense downpour with high volume", WeatherCategory.RAIN)
                66, 67 -> WeatherConditionInfo("Freezing Rain", "Hazardous freezing rainfall", WeatherCategory.RAIN)
                71 -> WeatherConditionInfo("Slight Snow", "Light drifting snow flurries", WeatherCategory.SNOW)
                73 -> WeatherConditionInfo("Moderate Snow", "Steady snowfall accumulating", WeatherCategory.SNOW)
                75 -> WeatherConditionInfo("Heavy Snow", "Dense heavy snow blanket", WeatherCategory.SNOW)
                77 -> WeatherConditionInfo("Snow Grains", "Tiny frozen precipitation grains", WeatherCategory.SNOW)
                80 -> WeatherConditionInfo("Light Showers", "Passing brief rain showers", WeatherCategory.RAIN)
                81 -> WeatherConditionInfo("Moderate Showers", "Periodic rain bursts", WeatherCategory.RAIN)
                82 -> WeatherConditionInfo("Violent Showers", "Torrential cloudburst showers", WeatherCategory.RAIN)
                85, 86 -> WeatherConditionInfo("Snow Showers", "Brief convective snow squalls", WeatherCategory.SNOW)
                95 -> WeatherConditionInfo("Thunderstorm", "Active convective thunderstorm with lightning", WeatherCategory.THUNDERSTORM)
                96, 99 -> WeatherConditionInfo("Severe Thunderstorm", "Violent thunderstorm with hail potential", WeatherCategory.THUNDERSTORM)
                else -> WeatherConditionInfo("Fair", "Moderate atmospheric conditions", WeatherCategory.CLOUDY)
            }
        }
    }
}

data class HourlyForecastItem(
    val timeIso: String,
    val hourLabel: String,
    val tempC: Double,
    val weatherCode: Int,
    val precipProb: Int,
    val windSpeedKmh: Double,
    val uvIndex: Double,
    val isNow: Boolean = false
)

data class DailyForecastItem(
    val dateIso: String,
    val dayOfWeek: String,
    val dateFormatted: String,
    val weatherCode: Int,
    val tempMinC: Double,
    val tempMaxC: Double,
    val precipProbMax: Int,
    val precipSumMm: Double,
    val sunriseTime: String,
    val sunsetTime: String,
    val maxUv: Double
)
