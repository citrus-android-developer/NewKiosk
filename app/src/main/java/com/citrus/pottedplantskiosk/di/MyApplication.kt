package com.citrus.pottedplantskiosk.di

import android.app.Application
import com.citrus.pottedplantskiosk.util.Prefs
import dagger.hilt.android.HiltAndroidApp
import com.yariksoffice.lingver.Lingver

val prefs: Prefs by lazy {
    MyApplication.prefs!!
}

@HiltAndroidApp
class MyApplication:Application(){
    override fun onCreate() {
        super.onCreate()
        Lingver.init(this)
        prefs = Prefs(applicationContext)

    }

    companion object {
        var prefs: Prefs? = null
    }
}