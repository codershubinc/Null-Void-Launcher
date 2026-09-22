package com.codershubinc.nullvoidlauncher.ui.weather

data class WeatherInfoState(
    val condition: String = "Clear Sky",
    val temperature: String = "24°C",
    val highTemp: String = "27°C",
    val lowTemp: String = "19°C",
    val humidity: String = "48%",
    val windSpeed: String = "14 km/h",
    val city: String = "Atmosphere"
)

object WeatherHelper {
    fun getWeatherInfo(): WeatherInfoState {
        // Lightweight local weather telemetry state (can later connect to open-meteo / system sensor)
        return WeatherInfoState(
            condition = "Clear Sky",
            temperature = "24°C",
            highTemp = "27°C",
            lowTemp = "19°C",
            humidity = "48%",
            windSpeed = "14 km/h",
            city = "Local Atmosphere"
        )
    }
}
