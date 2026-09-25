package com.codershubinc.nullvoidlauncher.ui.settings.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WeatherStyle
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.weather.WeatherInfoState
import com.codershubinc.nullvoidlauncher.ui.widgets.WeatherWidget

@Composable
fun WeatherWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    var showWeather by remember { mutableStateOf(userManager.getShowWeatherWidget()) }
    var weatherStyle by remember { mutableStateOf(userManager.getWeatherStyle()) }
    var weatherFont by remember { mutableStateOf(userManager.getWeatherFont()) }

    val previewWeather = remember {
        WeatherInfoState(
            condition = "Sunny",
            temperature = "22°C",
            highTemp = "25°C",
            lowTemp = "17°C",
            humidity = "45%",
            windSpeed = "12 km/h",
            city = "San Francisco"
        )
    }

    WidgetPageScaffold(
        title = "Weather Widget",
        subtitle = "Forecast conditions, temperatures & styles",
        onBack = onBack
    ) {
        WidgetVisibilityCard(
            title = "Show Weather Widget",
            description = "Display local temperature and weather status badge",
            visible = showWeather,
            onVisibleChange = {
                showWeather = it
                userManager.saveShowWeatherWidget(it)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        ModernCard {
            Text(
                text = "Live Preview",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                WeatherWidget(
                    style = weatherStyle,
                    font = weatherFont,
                    previewInfo = previewWeather
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            WidgetStyleSelector(
                title = "Weather Style",
                styles = WeatherStyle.entries,
                selectedStyle = weatherStyle,
                onStyleSelected = {
                    weatherStyle = it
                    userManager.saveWeatherStyle(it)
                },
                getLabel = { it.name }
            )

            WidgetFontSelector(
                selectedFont = weatherFont,
                onFontSelected = {
                    weatherFont = it
                    userManager.saveWeatherFont(it)
                }
            )
        }
    }
}
