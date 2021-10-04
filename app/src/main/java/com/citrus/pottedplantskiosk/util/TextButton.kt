package com.citrus.pottedplantskiosk.util

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView


class TextButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    fun clickToStart(){
        Log.e("TextButton","clickToStart")
    }

    fun clickToEnd(){
        Log.e("TextButton","ClickToEnd")
    }
}