package com.citrus.pottedplantskiosk.util


import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.citrus.pottedplantskiosk.R
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
import com.pax.gl.page.IPage


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
    const val KEY_ORDER_STRING = "KEY_ORDER_STRING"


    // 列印用
    val LEFT: IPage.EAlign = IPage.EAlign.LEFT
    val CENTER: IPage.EAlign = IPage.EAlign.CENTER
    val RIGHT: IPage.EAlign = IPage.EAlign.RIGHT
    const val SPACE = " "
    const val LINE_SPACE = 10
    const val LINE_SPACE_S = 5
    const val LINE_SPACE_M = 20
    const val LINE_SPACE_L = 30
    const val LINE_SPACE_XL = 40
    const val LINE_SPACE_XXL = 60
    const val LINE_SPACE_BLOCK = 150
    const val TXT_S = 16
    const val TXT_SS = 18
    const val TXT_M = 22
    const val TXT_L = 26
    const val TXT_XL = 28
    val TXT_BOLD: Int = IPage.ILine.IUnit.TEXT_STYLE_BOLD
    val TXT_NORMAL: Int = IPage.ILine.IUnit.TEXT_STYLE_NORMAL
    val TXT_UNDERLINE: Int = IPage.ILine.IUnit.TEXT_STYLE_UNDERLINE
    const val REDEEM = "REDEEM"
    const val IPP = "IPP"
    const val ADJUST_LINE_SPACE = -6
    const val LINE_SPACE_PARAM = -2
    const val PAGE_WIDTH = 384
    const val FLOAT_2 = 0.2f
    const val FLOAT_25 = 0.25f
    const val FLOAT_3 = 0.3f
    const val FLOAT_35 = 0.35f
    const val FLOAT_4 = 0.4f
    const val FLOAT_5 = 0.5f
    const val FLOAT_6 = 0.6f
    const val FLOAT_7 = 0.7f
    const val FLOAT_8 = 0.8f
    const val SEPARATE_PARAM = "=============================================="
    const val SEPARATE_MID = "================================"
    const val SEPARATE = "============="
    const val SEPARATE_ISSUER = "***"
    const val SEPARATE_NORMAL = "—————————————————"
    const val SEPARATE_STAR = "****************************"
    const val UNDERLINE_TIP = "————————————"
    const val UNDERLINE_TOTAL = "———————————"
    const val GRAY = 4
    const val CUT_PAPER_CUT_ALL = 0
    const val CUT_PAPER_CUT_PART = 1

    sealed class LanguageType {
        object SimpleChinese : LanguageType()
        object English : LanguageType()
    }


    fun String.trimSpace(): String {
        return this.replace("\\s".toRegex(), "")
    }

    fun Balloon.setDuration(sec: Long) {
        MainScope().launch {
            val duration = TimeUnit.SECONDS.toMillis(sec)
            delay(duration)
        }
        this.dismiss()
    }


    fun MotionLayout.setTransitionExecute(transitionId: Int, milliseconds: Int) {
        setTransition(transitionId)
        setTransitionDuration(milliseconds)
        transitionToEnd()
    }

    fun MotionLayout.setTransitionReverse(transitionId: Int, milliseconds: Int) {
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
            .setDuration(100)
            .setOnFinishListener {
                this.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    block()
                }
            }.doAction()
    }


    private fun getDecimalFormat(): DecimalFormat {
        return when (prefs.decimalPlace) {
            0 -> DecimalFormat("0")
            1 -> DecimalFormat("0.0")
            2 -> DecimalFormat("0.00")
            else -> DecimalFormat("0.00")
        }
    }


    fun Context.getGstStr():String {
        return when (prefs.taxFunction) {
            0 -> this.getString(R.string.gstF)
            1 -> this.getString(R.string.gstI)
            2 -> this.getString(R.string.gstE)
            else -> this.getString(R.string.gstF)
        }
    }

    fun getValByMathWay(orgValue: Double): String {
        return when (prefs.methodOfOperation) {
            0 -> {
                val value =
                    BigDecimal.valueOf(orgValue)
                        .setScale(prefs.decimalPlace, BigDecimal.ROUND_HALF_UP)
                        .toDouble()
                getDecimalFormat().format(value)
            }
            1 -> {
                val value =
                    BigDecimal.valueOf(orgValue).setScale(prefs.decimalPlace, BigDecimal.ROUND_UP)
                        .toDouble()
                getDecimalFormat().format(value)
            }
            2 -> {
                val value =
                    BigDecimal.valueOf(orgValue).setScale(prefs.decimalPlace, BigDecimal.ROUND_DOWN)
                        .toDouble()
                getDecimalFormat().format(value)
            }
            3 -> {
                val priceString: String = getDecimalFormat().format(orgValue)
                if (priceString.contains(".")) {
                    val s = priceString.split("\\.".toRegex()).toTypedArray()
                    if (s[1].length == 2) {
                        var a = s[1].substring(1, 2)
                        when (a) {
                            "0", "1", "2" -> {
                                a = "0"
                                val value = (s[0] + "." + s[1].substring(0, 1) + a).toDouble()
                                getDecimalFormat().format(value)
                            }
                            "3", "4", "5", "6", "7" -> {
                                a = "5"
                                val value = (s[0] + "." + s[1].substring(0, 1) + a).toDouble()
                                getDecimalFormat().format(value)
                            }
                            "8", "9" -> {
                                val value = (s[0] + "." + s[1].substring(0, 1)).toDouble() + 0.1
                                getDecimalFormat().format(value)
                            }
                            else -> getDecimalFormat().format(orgValue)
                        }
                    } else getDecimalFormat().format(orgValue)
                } else getDecimalFormat().format(orgValue)
            }
            else -> getDecimalFormat().format(orgValue)
        }
    }
}