package com.citrus.pottedplantskiosk.util.base

import android.os.SystemClock
import android.util.Log
import android.view.View

class SevenClickListener(
    private var defaultInterval: Int = 1000,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var count = 0
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            count++
        }else{
            count = 0
        }

        lastTimeClicked = SystemClock.elapsedRealtime()

        Log.e("now Click",count.toString())
        if(count>= 7){
            Log.e("archive","----")
            onSafeCLick(v)
        }
    }
}

fun View.onSevenClick(onSafeClick: (View) -> Unit) {
    val safeClickListener = SevenClickListener { onSafeClick(it) }
    setOnClickListener(safeClickListener)
}