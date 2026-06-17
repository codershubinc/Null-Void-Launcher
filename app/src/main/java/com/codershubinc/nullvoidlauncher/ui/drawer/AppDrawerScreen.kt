package com.codershubinc.nullvoidlauncher.ui.drawer

import android.content.Context
import android.content.pm.LauncherApps
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.LazyAppIcon
import com.codershubinc.nullvoidlauncher.data.repository.getInstalledApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun AppDrawerScreen(onClose: () -> Unit = {}) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var allApps by remember { mutableStateOf(emptyList<AppInfo>()) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            allApps = getInstalledApps(context)
        }
    }

    LaunchedEffect(Unit) {
        delay(150)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    // Filtered list is now derived and remembered
    val filteredApps = remember(searchQuery, allApps) {
        if (searchQuery.isEmpty()) {
            emptyList()
        } else {
            allApps.filter { it.label.startsWith(searchQuery, ignoreCase = true) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 20) {
                        keyboardController?.hide()
                        onClose()
                    }
                }
            }
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.DarkGray)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Monospace
                ),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search...",
                            color = Color.DarkGray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 22.sp
                        )
                    }
                    innerTextField()
                }
            )
            Text(
                text = "[X]",
                color = Color.DarkGray,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .clickable {
                        keyboardController?.hide()
                        onClose()
                    }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = filteredApps,
                key = { it.componentName.flattenToString() + it.userHandle.hashCode() }
            ) { app ->
                AppRow(app, context, keyboardController, onClose)
            }
        }
    }
}

@Composable
fun AppRow(
    app: AppInfo,
    context: Context,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
    onClose: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)
                keyboardController?.hide()
                onClose()
            }
            .padding(vertical = 12.dp)
    ) {
        LazyAppIcon(app, context)

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = app.label.uppercase(),
            color = Color.LightGray,
            fontFamily = FontFamily.Monospace,
            fontSize = 18.sp
        )
    }
}
