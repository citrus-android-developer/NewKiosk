package com.citrus.pottedplantskiosk.util


import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
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

object Constants {
    //const val BASE_URL = "http://cms.citrus.tw/soramenLAB"
    const val IMG_URL = "http://cms.citrus.tw/soramenLAB/images/"
    const val GET_MENU = "/POSServer/UploadDataWS/Service1.asmx/getAllKindGoods_KIOSK"
    const val GET_BANNER = "/POSServer/UploadDataWS/Service1.asmx/GetAdvertise_KIOSK"
    const val SET_ORDERS = "/POSServer/UploadDataWS/Service1.asmx/SetOrdersDeliveryData_KIOSK"
    const val SET_ORDER_EDIT = "/POSServer/UploadDataWS/Service1.asmx/UpdateOrdersDeliveryData_KIOSK"
    const val USB_NO_PERMISSION = "USB_NO_PERMISSION"
    const val SHARED_PREFERENCES_NAME = "sharedPref"
    const val TWO_MINUTES = 120

    const val POS_GET_MENU = "/CitrusKioskC/Service.asmx/Q_AllKindGoods"
    const val POS_SET_ORDERS = "/CitrusKioskC/Service.asmx/I_OrdersData"
    const val POS_EDIT_ORDERS = "/CitrusKioskC/Service.asmx/U_Orders"


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


    /**Fail Message Type*/
    const val RefundSuccess = "RefundSuccess"
    const val OrderUploadFail = "OrderUploadFail"

    var screenW = 0
    var screenH = 0

    sealed class LanguageType {
        object SimpleChinese : LanguageType()
        object English : LanguageType()
    }

    sealed class PayWayType(values: String) {
        object CreditCard : PayWayType("01")
        object Cash : PayWayType("02")
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
            .setDuration(50)
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


object GlobalConstants {
    const val ECR_SALE = "C200"
    const val ECR_PRE_AUTH = "C201"
    const val ECR_PRE_AUTH_CAPTURE = "C202"
    const val ECR_REFUND = "C203"
    const val ECR_QUASI_CASH_ADVANCE = "C204"
    const val ECR_GET_CARD_DATA = "C209"
    const val ECR_VOID = "C300"
    const val ECR_PRE_AUTH_CANCEL =
        "C301" // 2017032101 Rajesh: Added PRE AUTH CANCEL DEV TMS-13131313
    const val ECR_INQUIRY = "C400"
    const val ECR_ECHO = "C902"
    const val ECR_BEGIN_SHIFT = "C800"
    const val ECR_GET_MAGSTRIPE = "C810"
    const val ECR_TIP_ADJUST = "C500"
    const val ECR_CASH_ADVANCE = "C205"
    const val ECR_SETTLEMENT = "C700"
    const val ECR_WAIT = "C920"
    const val ECR_RETRIEVE_TERMINAL_INFO =
        "C900" // Retrieve terminal info ie. TID, Serial Number Etc..
    const val ECR_EZLINK_SALE = "C610" //Ezlink sale
    const val ECR_CUSTOM_RECEIPT = "C620" //Ezlink Blacklist Download
    const val ECR_WALLET_SALE = "C640" //Ezlink Blacklist Download
}


fun Fragment.showDialog(
    title: String,
    msg: String,
    icon: Int = R.drawable.ic_warning,
    onConfirmListener: (() -> Unit)? = null,
): CustomAlertDialog {
    return CustomAlertDialog(
        requireActivity(), title, msg,
        icon,
        onConfirmListener = { onConfirmListener?.invoke() }
    )
}