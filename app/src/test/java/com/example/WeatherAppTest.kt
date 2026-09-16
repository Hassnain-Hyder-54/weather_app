package com.example

import com.example.data.model.WeatherCategory
import com.example.data.model.WeatherConditionInfo
import com.example.ui.util.WeatherFormatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherAppTest {

    @Test
    fun testWmoCodeMapping() {
        val clear = WeatherConditionInfo.fromWmoCode(0)
        assertEquals(WeatherCategory.CLEAR, clear.category)
        assertEquals("Clear Sky", clear.title)

        val rain = WeatherConditionInfo.fromWmoCode(63)
        assertEquals(WeatherCategory.RAIN, rain.category)

        val snow = WeatherConditionInfo.fromWmoCode(71)
        assertEquals(WeatherCategory.SNOW, snow.category)

        val thunder = WeatherConditionInfo.fromWmoCode(95)
        assertEquals(WeatherCategory.THUNDERSTORM, thunder.category)
    }

    @Test
    fun testUnitConversions() {
        // Metric
        assertEquals("20°C", WeatherFormatters.formatTemp(20.0, isMetric = true))
        assertEquals("20°", WeatherFormatters.formatTempShort(20.0, isMetric = true))

        // Imperial: 20°C = 68°F
        assertEquals("68°F", WeatherFormatters.formatTemp(20.0, isMetric = false))
        assertEquals("68°", WeatherFormatters.formatTempShort(20.0, isMetric = false))

        // Speed
        val metricSpeed = WeatherFormatters.formatSpeed(10.0, isMetric = true)
        assertTrue(metricSpeed.contains("km/h"))

        val imperialSpeed = WeatherFormatters.formatSpeed(10.0, isMetric = false)
        assertTrue(imperialSpeed.contains("mph"))
    }

    @Test
    fun testWindBearing() {
        assertEquals("N", WeatherFormatters.windDegreesToDirection(0))
        assertEquals("E", WeatherFormatters.windDegreesToDirection(90))
        assertEquals("S", WeatherFormatters.windDegreesToDirection(180))
        assertEquals("W", WeatherFormatters.windDegreesToDirection(270))
    }

    @Test
    fun testUvClassification() {
        val (lowLabel, _, _) = WeatherFormatters.getUvClassification(1.5)
        assertEquals("Low", lowLabel)

        val (modLabel, _, _) = WeatherFormatters.getUvClassification(4.2)
        assertEquals("Moderate", modLabel)

        val (highLabel, _, _) = WeatherFormatters.getUvClassification(7.0)
        assertEquals("High", highLabel)
    }
}
