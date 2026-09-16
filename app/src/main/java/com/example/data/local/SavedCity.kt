package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_cities")
data class SavedCity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val country: String = "",
    val admin1: String? = null,
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val isFavorite: Boolean = false,
    val lastTempC: Double? = null,
    val lastWeatherCode: Int? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val subtitle: String
        get() = buildString {
            if (!admin1.isNullOrBlank() && admin1 != name) {
                append(admin1).append(", ")
            }
            append(country)
        }
}
