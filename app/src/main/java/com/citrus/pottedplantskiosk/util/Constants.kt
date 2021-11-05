package com.citrus.pottedplantskiosk.util


import android.annotation.SuppressLint
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.citrus.pottedplantskiosk.di.prefs
import com.skydoves.balloon.Balloon
import com.skydoves.elasticviews.ElasticAnimation
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Constants {
    //const val BASE_URL = "http://cms.citrus.tw/soramenLAB"
    const val IMG_URL = "http://cms.citrus.tw/soramenLAB/images/"
    const val GET_MENU = "/POSServer/UploadDataWS/Service1.asmx/getAllKindGoods_KIOSK"
    const val GET_BANNER = "/POSServer/UploadDataWS/Service1.asmx/GetAdvertise_KIOSK"
    const val SET_ORDERS = "/POSServer/UploadDataWS/Service1.asmx/SetOrdersDeliveryData_KIOSK"

    const val USB_NO_PERMISSION = "USB_NO_PERMISSION"
    const val SHARED_PREFERENCES_NAME = "sharedPref"
    const val TWO_MINUTES = 120
    var df = DecimalFormat("#,###,##0.###")
    var dfShow = DecimalFormat("###,###,###,##0.##")
    var dateTimeFormatSql = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    var timeFormat = SimpleDateFormat("HH:mm")

    const val ACTION_USB_PERMISSION = "com.citrus.kiosk.USB_PERMISSION"

    /**Prefs*/
    const val KEY_TAX = "KEY_TAX"
    const val KEY_METHOD_OF_OPERATION = "KEY_METHOD_OF_OPERATION"
    const val KEY_DECIMAL_PLACES = "KEY_DECIMAL_PLACES"
    const val KEY_TAX_FUNCTION = "KEY_TAX_FUNCTION"
    const val KEY_IDLE_TIME = "KEY_IDLE_TIME"
    const val KEY_SEVER_IP = "KEY_SEVER_IP"
    const val KEY_STORE_NAME = "KEY_STORE_NAME"
    const val KEY_STORE_ID = "KEY_STORE_ID"
    const val KEY_PRINTER_IS80MM = "KEY_PRINTER_IS80MM"
    const val KEY_LANGUAGE_POS = "KEY_LANGUAGE_POS"
    const val KEY_KIOSK_ID = "KEY_KIOSK_ID"
    const val KEY_STORE_ADDRESS = "KEY_STORE_ADDRESS"
    const val KEY_HEADER = "KEY_HEADER"
    const val KEY_FOOTER = "KEY_FOOTER"
    sealed class LanguageType {
        object SimpleChinese: LanguageType()
        object English: LanguageType()
    }



    fun String.trimSpace():String {
        return this.replace("\\s".toRegex(), "")
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


    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val currentDate = Calendar.getInstance().time
        val sdf = dateTimeFormatSql
        return sdf.format(currentDate)
    }

    inline fun <T> List<T>.forEachReversedByIndex(action: (T) -> Unit) {
        val initialSize = size
        for (i in lastIndex downTo 0) {
            if (size != initialSize) throw ConcurrentModificationException()
            action(get(i))
        }
    }

    inline fun <T> List<T>.forEachReversedWithIndex(
        allowSafeModifications: Boolean = false,
        action: (Int, T) -> Unit
    ) {
        val initialSize = size
        for (i in lastIndex downTo 0) {
            when {
                allowSafeModifications && i > lastIndex -> {
                    throw ConcurrentModificationException()
                }
                allowSafeModifications.not() && size != initialSize -> {
                    throw ConcurrentModificationException()
                }
            }
            action(i, get(i))
        }
    }


    inline fun View.clickAnimation(crossinline block: suspend () -> Unit) {
        ElasticAnimation(this)
            .setScaleX(0.85f)
            .setScaleY(0.85f)
            .setDuration(50)
            .setOnFinishListener {
                this.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    block()
                }
            }.doAction()
    }

    fun getValByMathWay(orgValue: Double): Double {
        return when (prefs.methodOfOperation) {
            0 -> BigDecimal.valueOf(orgValue).setScale(prefs.decimalPlace, BigDecimal.ROUND_HALF_UP).toDouble()
            1 -> BigDecimal.valueOf(orgValue).setScale(prefs.decimalPlace, BigDecimal.ROUND_UP).toDouble()
            2 -> BigDecimal.valueOf(orgValue).setScale(prefs.decimalPlace, BigDecimal.ROUND_DOWN).toDouble()
            3 -> {
                val priceString: String = df.format(orgValue)
                if (priceString.contains(".")) {
                    val s = priceString.split("\\.".toRegex()).toTypedArray()
                    if (s[1].length == 2) {
                        var a = s[1].substring(1, 2)
                        when (a) {
                            "0", "1", "2" -> {
                                a = "0"
                                (s[0] + "." + s[1].substring(0, 1) + a).toDouble()
                            }
                            "3", "4", "5", "6", "7" -> {
                                a = "5"
                                (s[0] + "." + s[1].substring(0, 1) + a).toDouble()
                            }
                            "8", "9" -> (s[0] + "." + s[1].substring(0, 1)).toDouble() + 0.1
                            else -> orgValue
                        }
                    } else orgValue
                } else orgValue
            }
            else -> orgValue
        }
    }
}