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

}
