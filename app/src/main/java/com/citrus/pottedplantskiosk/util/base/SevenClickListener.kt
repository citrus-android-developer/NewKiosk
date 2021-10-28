package com.citrus.pottedplantskiosk.util.base

import android.content.Context
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.citrus.pottedplantskiosk.R
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon

class SevenClickListener(
    private var defaultInterval: Int = 1000,
    private val onSafeClick: (View,Int) -> Unit
) : View.OnClickListener {
    private var count = 0
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {

        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            count++
        } else {
            count = 0
        }

        lastTimeClicked = SystemClock.elapsedRealtime()

        onSafeClick(v, count)

        if (count >= 7) {
            count = 0
            lastTimeClicked = 0
        }
    }
}

fun View.onSevenClick(
    onSafeClick: (View, Int) -> Unit
) {
    val safeClickListener = SevenClickListener { view,count ->
        onSafeClick(view,count) }
    setOnClickListener(safeClickListener)
}