package com.codershubinc.nullvoidlauncher.ui.widgets.storage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.utils.StorageUtils

@Composable
fun ModernStorageWidget(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val total = StorageUtils.getTotalStorage(context)
    val available = StorageUtils.getAvailableStorage(context)
    val usedPercent = StorageUtils.getUsedStoragePercentage(context)

    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "STORAGE",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${usedPercent}% USED",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // Progress bar
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(usedPercent / 100f)
                    .background(Color.White)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "${StorageUtils.formatSize(available)} FREE / ${StorageUtils.formatSize(total)}",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 9.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}
