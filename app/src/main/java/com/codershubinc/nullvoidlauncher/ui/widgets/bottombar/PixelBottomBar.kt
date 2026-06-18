package com.codershubinc.nullvoidlauncher.ui.widgets.bottombar

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
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.LazyAppIcon
import com.codershubinc.nullvoidlauncher.data.repository.getInstalledApps
import com.codershubinc.nullvoidlauncher.ui.widgets.music.FusedMusicWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun PixelBottomBar(onOpenDrawer: () -> Unit) {
    val context = LocalContext.current
    var amPmTimeText by remember { mutableStateOf("") }
    var dayText by remember { mutableStateOf("") }
    val userManager = remember { UserManager(context) }
    var favoriteApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }

    LaunchedEffect(Unit) {
        val amPmTimeFormatter = SimpleDateFormat("hh:mm", Locale.getDefault())
        val dayFormatter = SimpleDateFormat("EEEE", Locale.getDefault())

        while (true) {
            val now = Date()
            amPmTimeText = amPmTimeFormatter.format(now).replace(" ", "")
            dayText = dayFormatter.format(now)
            delay(1000.milliseconds)
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Bar Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray, shape = RoundedCornerShape(40.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$amPmTimeText | $dayText",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                favoriteApps.take(3).forEach { app ->
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

                // Drawer Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(18.dp))
                        .clickable { onOpenDrawer() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        repeat(3) {
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                repeat(3) {
                                    Box(modifier = Modifier.size(2.dp).background(Color.White, shape = RoundedCornerShape(1.dp)))
                                }
                            }
                            if (it < 2) Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }
        }
    }
}
