package com.citrus.pottedplantskiosk.util

import android.content.Context
import android.content.SharedPreferences




class Prefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    var punchId: String
        get() = prefs.getString("Test", "init")?: ""
        set(value) = prefs.edit().putString("Test", value).apply()

    var languagePos: Int
        get() = prefs.getInt(Constants.KEY_LANGUAGE_POS, -1)
        set(value) = prefs.edit().putInt(Constants.KEY_LANGUAGE_POS, value).apply()

    var charSet: String
        get() = prefs.getString("charSet", "UTF-8") ?: ""
        set(value) = prefs.edit().putString("charSet", value).apply()

    var isLargeLineSpacing: Boolean
        get() = prefs.getBoolean("isLargeLineSpacing", false)
        set(value) = prefs.edit().putBoolean("isLargeLineSpacing", value).apply()

    var storeName: String
        get() = prefs.getString(Constants.KEY_STORE_NAME, "")?: ""
        set(value) = prefs.edit().putString(Constants.KEY_STORE_NAME, value).apply()

    var printerIs80mm: Boolean
        get() = prefs.getBoolean(Constants.KEY_PRINTER_IS80MM, false)
        set(value) = prefs.edit().putBoolean(Constants.KEY_PRINTER_IS80MM, value).apply()


}
