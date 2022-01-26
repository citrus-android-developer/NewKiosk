package com.citrus.pottedplantskiosk.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log


class Prefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    var isNavigate: Boolean
        get() = prefs.getBoolean("isNavigate", false)
        set(value) = prefs.edit().putBoolean("isNavigate", value).apply()

    var isLanChanged: Boolean
        get() = prefs.getBoolean("isLanChanged", false)
        set(value) = prefs.edit().putBoolean("isLanChanged", value).apply()

    var punchId: String
        get() = prefs.getString("Test", "init")?: ""
        set(value) = prefs.edit().putString("Test", value).apply()

    var transactionData: String
        get() = prefs.getString("transactionData", "")?: ""
        set(value) = prefs.edit().putString("transactionData", value).apply()


    var languagePos: Int
        get() = prefs.getInt(Constants.KEY_LANGUAGE_POS, -1)
        set(value) = prefs.edit().putInt(Constants.KEY_LANGUAGE_POS, value).apply()

    var charSet: String
        get() = prefs.getString("charSet", "UTF-8") ?: ""
        set(value) = prefs.edit().putString("charSet", value).apply()

    var printer: String
        get() = prefs.getString("printer", "None Selected") ?: "None Selected"
        set(value) = prefs.edit().putString("printer", value).apply()

    var isLargeLineSpacing: Boolean
        get() = prefs.getBoolean("isLargeLineSpacing", false)
        set(value) = prefs.edit().putBoolean("isLargeLineSpacing", value).apply()


    var header: String
        get() = prefs.getString(Constants.KEY_HEADER, "")?: ""
        set(value) = prefs.edit().putString(Constants.KEY_HEADER, value).apply()

    var footer: String
        get() = prefs.getString(Constants.KEY_FOOTER, "")?: ""
        set(value) = prefs.edit().putString(Constants.KEY_FOOTER, value).apply()

    var kioskId: String
        get() = prefs.getString(Constants.KEY_KIOSK_ID, "")?: ""
        set(value) = prefs.edit().putString(Constants.KEY_KIOSK_ID, value).apply()

    var storeName: String
        get() = prefs.getString(Constants.KEY_STORE_NAME, "")?: ""
        set(value) = prefs.edit().putString(Constants.KEY_STORE_NAME, value).apply()

    var storeAddress: String
        get() = prefs.getString(Constants.KEY_STORE_ADDRESS, "")?: ""
        set(value) = prefs.edit().putString(Constants.KEY_STORE_ADDRESS, value).apply()

    var storeId: String
        get() = prefs.getString(Constants.KEY_STORE_ID, "")?: ""
        set(value) = prefs.edit().putString(Constants.KEY_STORE_ID, value).apply()

    var serverIp: String
        get() = prefs.getString(Constants.KEY_SEVER_IP, "")?: ""
        set(value) = prefs.edit().putString(Constants.KEY_SEVER_IP, value).apply()

    var idleTime: Int
        get() = prefs.getInt(Constants.KEY_IDLE_TIME, 120)
        set(value) = prefs.edit().putInt(Constants.KEY_IDLE_TIME, value).apply()

    var decimalPlace: Int
        get() = prefs.getInt(Constants.KEY_DECIMAL_PLACES, 0)
        set(value) = prefs.edit().putInt(Constants.KEY_DECIMAL_PLACES, value).apply()

    var taxFunction: Int
        get() = prefs.getInt(Constants.KEY_TAX_FUNCTION, 0)
        set(value) = prefs.edit().putInt(Constants.KEY_TAX_FUNCTION, value).apply()

    var methodOfOperation: Int
        get() = prefs.getInt(Constants.KEY_METHOD_OF_OPERATION, 0)
        set(value) = prefs.edit().putInt(Constants.KEY_METHOD_OF_OPERATION, value).apply()

    var printerIs80mm: Boolean
        get() = prefs.getBoolean(Constants.KEY_PRINTER_IS80MM, false)
        set(value) = prefs.edit().putBoolean(Constants.KEY_PRINTER_IS80MM, value).apply()

    var tax: Int
        get() = prefs.getInt(Constants.KEY_TAX, 0)
        set(value) = prefs.edit().putInt(Constants.KEY_TAX, value).apply()


    var orderStr: String
        get() = prefs.getString(Constants.KEY_ORDER_STRING, "")?: ""
        set(value) = prefs.edit().putString(Constants.KEY_ORDER_STRING, value).apply()


}
