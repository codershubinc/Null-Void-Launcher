package com.codershubinc.nullvoidlauncher.ui.widgets.day

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BrutalDay(dayText: String  ) {
    Text(
        text = dayText.uppercase(),
        color = Color.Black,
        fontSize = 48.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.SansSerif,
        modifier = Modifier
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}
