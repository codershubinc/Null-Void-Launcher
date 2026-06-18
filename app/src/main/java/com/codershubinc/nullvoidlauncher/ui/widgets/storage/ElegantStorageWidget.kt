package com.codershubinc.nullvoidlauncher.ui.widgets.storage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.utils.StorageUtils

@Composable
fun ElegantStorageWidget(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val total = StorageUtils.getTotalStorage(context)
    val available = StorageUtils.getAvailableStorage(context)

    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Text(
            text = "SYSTEM ARCHIVE",
            color = Color(0xFFC5A35E),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.Serif
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Light)) {
                    append(StorageUtils.formatSize(available))
                }
                withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.4f), fontSize = 8.sp)) {
                    append(" AVAILABLE OF ")
                }
                withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                    append(StorageUtils.formatSize(total))
                }
            },
            fontSize = 11.sp,
            fontFamily = FontFamily.Serif
        )
    }
}
