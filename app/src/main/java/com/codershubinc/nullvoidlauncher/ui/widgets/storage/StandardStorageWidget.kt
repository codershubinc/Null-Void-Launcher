package com.codershubinc.nullvoidlauncher.ui.widgets.storage

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.utils.StorageUtils

@Composable
fun StandardStorageWidget(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val totalStorage = StorageUtils.getTotalStorage(context)
    val availableStorage = StorageUtils.getAvailableStorage(context)

    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.White)) {
                append(StorageUtils.formatSize(availableStorage))
            }
            withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.5f))) {
                append(" remaining of ")
            }
            withStyle(style = SpanStyle(color = Color.White)) {
                append(StorageUtils.formatSize(totalStorage))
            }
        },
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        modifier = modifier
    )
}
