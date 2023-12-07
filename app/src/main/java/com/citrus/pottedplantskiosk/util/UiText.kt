package com.citrus.pottedplantskiosk.util

import android.content.Context
import com.citrus.pottedplantskiosk.R

sealed class UiText {

    data class DynamicString(val text: String?) : UiText()
    data class StringResource(val resId: Int) : UiText()
    object UnknownError : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> text.orEmpty()
            is StringResource -> context.getString(resId)
            is UnknownError -> context.getString(R.string.error_occurred)
        }
    }
}
