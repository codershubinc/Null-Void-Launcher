package com.codershubinc.nullvoidlauncher.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext
import com.codershubinc.nullvoidlauncher.data.ClockStyle
import com.codershubinc.nullvoidlauncher.data.UserManager

@Composable
fun SettingsScreen(
    userManager: UserManager,
    onUsernameUpdated: (String) -> Unit,
    onClockStyleUpdated: (ClockStyle) -> Unit,
    onClose: () -> Unit
) {
    var inputUsername by remember { mutableStateOf(userManager.getUsername()) }
    var selectedStyle by remember { mutableStateOf(userManager.getClockStyle()) }

    Column(modifier = Modifier.fillMaxSize().background(Color.Black).statusBarsPadding().padding(24.dp)) {
        Text(text = "SETTINGS", color = Color.White, fontSize = 24.sp, fontFamily = FontFamily.Monospace, letterSpacing = 4.sp, modifier = Modifier.padding(bottom = 32.dp))

        Text(text = "GITHUB USERNAME", color = Color.Gray, fontSize = 14.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(bottom = 8.dp))

        BasicTextField(
            value = inputUsername,
            onValueChange = { inputUsername = it },
            textStyle = TextStyle(color = Color.White, fontSize = 18.sp, fontFamily = FontFamily.Monospace),
            cursorBrush = SolidColor(Color.White),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "> ", color = Color.DarkGray, fontFamily = FontFamily.Monospace, fontSize = 18.sp)
                    innerTextField()
                }
            }
        )

        Text(text = "CLOCK STYLE", color = Color.Gray, fontSize = 14.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(bottom = 12.dp))

        ClockStyle.entries.forEach { style ->
            val isSelected = selectedStyle == style
            Text(
                text = if (isSelected) "[ ${style.name} ]" else "  ${style.name}",
                color = if (isSelected) Color.White else Color.DarkGray,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedStyle = style }
                    .padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val context = LocalContext.current
        Text(
            text = "SYSTEM PERMISSIONS",
            color = Color.Gray,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = "  MUSIC SYNC ACCESS",
            color = Color.DarkGray,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "[ SAVE & CLOSE ]",
            color = Color.Gray,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    userManager.saveUsername(inputUsername)
                    userManager.saveClockStyle(selectedStyle)
                    onUsernameUpdated(inputUsername)
                    onClockStyleUpdated(selectedStyle)
                    onClose()
                }
                .padding(16.dp)
        )
    }
}
