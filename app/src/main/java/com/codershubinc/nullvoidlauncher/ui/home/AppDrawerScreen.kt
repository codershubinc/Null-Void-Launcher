package com.codershubinc.nullvoidlauncher.ui.home

import android.content.Context
import android.content.pm.LauncherApps
import android.widget.ImageView
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
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun AppDrawerScreen(onClose: () -> Unit = {}) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var allApps by remember { mutableStateOf(emptyList<AppInfo>()) }

    // Keyboard & Focus controls
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Fetch apps in the background
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            allApps = getInstalledApps(context)
        }
    }

    // Automatically focus the text field and pop the keyboard
    LaunchedEffect(Unit) {
        delay(150) // Slight delay waits for the slide-up animation to finish
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    // FILTER LOGIC: Return empty list if no search query!
    val filteredApps = if (searchQuery.isEmpty()) {
        emptyList()
    } else {
        allApps.filter {
            it.label.startsWith(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Keeps the void intact
            .pointerInput(Unit) {
                // Swipe DOWN anywhere on the screen to close
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
        // --- The Drag Handle ---
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
                    .clip(RoundedCornerShape(50)) // Makes it a perfect pill shape
                    .background(Color.DarkGray)
            )
        }

        // --- The Search Bar ---
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
                .fillMaxWidth()
                .focusRequester(focusRequester) // Attach the focus requester
                .padding(bottom = 24.dp),
            decorationBox = { innerTextField ->
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "SEARCH...",
                        color = Color.DarkGray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 22.sp
                    )
                }
                innerTextField()
            }
        )

        // --- The App List ---
        // Will be totally invisible until you type at least one letter
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredApps) { app ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Launch the app
                            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
                            launcherApps.startMainActivity(app.componentName, app.userHandle, null, null)

                            // Close the drawer and hide keyboard after launching
                            keyboardController?.hide()
                            onClose()
                        }
                        .padding(vertical = 12.dp)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            ImageView(ctx).apply {
                                scaleType = ImageView.ScaleType.FIT_CENTER
                            }
                        },
                        update = { imageView ->
                            imageView.setImageDrawable(app.icon)
                        },
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = app.label.uppercase(),
                        color = Color.LightGray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}