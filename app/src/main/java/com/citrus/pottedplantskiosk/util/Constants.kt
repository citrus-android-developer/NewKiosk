package com.citrus.pottedplantskiosk.util


import androidx.constraintlayout.motion.widget.MotionLayout
import com.citrus.pottedplantskiosk.R
import com.skydoves.balloon.Balloon
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

object Constants {
    const val BASE_URL = "http://cms.citrus.tw/soramenLAB/"
    const val IMG_URL = "http://cms.citrus.tw/soramenLAB/images/"
    const val GET_MENU = "POSServer/UploadDataWS/Service1.asmx/getAllKindGoods_KIOSK"
    const val SHARED_PREFERENCES_NAME = "sharedPref"
    const val TWO_MINUTES = 120
    var df = DecimalFormat("#,###,##0.###")


    /**Prefs*/
    const val KEY_LANGUAGE_POS = "KEY_LANGUAGE_POS"
    sealed class LanguageType {
        object SimpleChinese: LanguageType()
        object English: LanguageType()
    }

     fun Balloon.setDuration(sec: Long) {
         MainScope().launch {
             val duration = TimeUnit.SECONDS.toMillis(sec)
             delay(duration)
         }
         this.dismiss()
    }


    fun MotionLayout.setTransitionExecute(transitionId:Int,milliseconds:Int) {
        setTransition(transitionId)
        setTransitionDuration(milliseconds)
        transitionToEnd()
    }

    fun MotionLayout.setTransitionReverse(transitionId:Int,milliseconds:Int) {
        setTransition(transitionId)
        setTransitionDuration(milliseconds)
        transitionToStart()
    }
}