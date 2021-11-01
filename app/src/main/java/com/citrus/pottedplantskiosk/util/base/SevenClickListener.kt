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
    private val onSafeClick: () -> Unit
) : View.OnClickListener {
    private var count = 1
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {

        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            count++
        } else {
            count = 1
        }

        lastTimeClicked = SystemClock.elapsedRealtime()

        Log.e("count",count.toString())

        if (count > 6) {
            onSafeClick()
            count = 0
            lastTimeClicked = 0
        }
    }
}

fun View.onSevenClick(
    onSafeClick: () -> Unit
) {
    val safeClickListener = SevenClickListener {
        onSafeClick() }
    setOnClickListener(safeClickListener)
}