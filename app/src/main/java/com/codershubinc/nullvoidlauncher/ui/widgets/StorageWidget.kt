package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.codershubinc.nullvoidlauncher.data.StorageStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.storage.ElegantStorageWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.storage.ModernStorageWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.storage.StandardStorageWidget

@Composable
fun StorageWidget(modifier: Modifier = Modifier, style: StorageStyle = StorageStyle.STANDARD) {
    when (style) {
        StorageStyle.STANDARD -> StandardStorageWidget(modifier)
        StorageStyle.MODERN -> ModernStorageWidget(modifier)
        StorageStyle.ELEGANT -> ElegantStorageWidget(modifier)
    }
}
