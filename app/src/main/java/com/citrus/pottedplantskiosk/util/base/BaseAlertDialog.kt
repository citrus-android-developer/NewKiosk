package com.citrus.pottedplantskiosk.util.base

import android.app.AlertDialog
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.R


abstract class BaseAlertDialog< VB: ViewBinding>(private val mContext: Context,private val inflate: InflateActivity<VB>) : AlertDialog(mContext,  R.style.CustomDialogTheme) {

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflate.invoke(layoutInflater)
        setContentView(binding.root)
//        setOwnerActivity(mContext as Activity) //important!!! OwnerActivity is for dispatchTouchEvent
//        setFullScreen()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        setCancelable(false)
        initView()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        ownerActivity?.dispatchTouchEvent(event)
        return false
    }

    override fun dismiss() {
        _binding = null
        super.dismiss()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    ownerActivity?.let {
                        val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
        }
//        setFullScreen()
        return super.dispatchTouchEvent(event)
    }

    abstract fun initView()

    open fun setFullScreen() {
        val decorView = window?.decorView
        decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
}