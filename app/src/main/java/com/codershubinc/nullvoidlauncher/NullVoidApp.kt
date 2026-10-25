package com.codershubinc.nullvoidlauncher

import android.app.Application
import com.codershubinc.nullvoidlauncher.data.fonts.FontManager

class NullVoidApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FontManager.init(this)
    }
}
