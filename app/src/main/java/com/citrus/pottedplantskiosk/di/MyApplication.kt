package com.citrus.pottedplantskiosk.di

import android.app.Application
import com.citrus.pottedplantskiosk.util.Prefs
import com.pax.dal.IDAL
import com.pax.neptunelite.api.NeptuneLiteUser
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp

val prefs: Prefs by lazy {
    MyApplication.prefs!!
}

val mDal: IDAL by lazy {
    MyApplication.mDal!!
}

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Lingver.init(this)
        prefs = Prefs(applicationContext)
        mDal = NeptuneLiteUser.getInstance().getDal(applicationContext)

    }

    companion object {
        var prefs: Prefs? = null
        var mDal: IDAL? = null
    }
}