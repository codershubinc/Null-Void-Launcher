package com.codershubinc.nullvoidlauncher.ui.widgets

import android.content.Context
import android.content.pm.LauncherApps
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.home.AppInfo
import com.codershubinc.nullvoidlauncher.ui.home.LazyAppIcon
import com.codershubinc.nullvoidlauncher.ui.home.getInstalledApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PixelBottomBar() {
    val context = LocalContext.current
    var amPmTimeText by remember { mutableStateOf("") }
    var dayText by remember { mutableStateOf("") }
    val userManager = remember { UserManager(context) }
    var favoriteApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }

    LaunchedEffect(Unit) {
        val amPmTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dayFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
        
        while (true) {
            val now = Date()
            amPmTimeText = amPmTimeFormatter.format(now).replace(" ", "")
            dayText = dayFormatter.format(now)
            delay(1000)
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val all = getInstalledApps(context)
            val savedFavs = userManager.getFavorites()
            
            favoriteApps = if (savedFavs.isNotEmpty()) {
                savedFavs.mapNotNull { pkg ->
                    all.find { it.componentName.flattenToString() == pkg }
                }
            } else {
                all.take(5)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.White, shape = RoundedCornerShape(40.dp))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$amPmTimeText | $dayText",
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            favoriteApps.take(4).forEach { app ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black, shape = RoundedCornerShape(18.dp))
                        .clickable {
                            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                            launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    LazyAppIcon(app, context, size = 20)
                }
            }
        }
    }
}
