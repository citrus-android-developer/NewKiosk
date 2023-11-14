package com.citrus.pottedplantskiosk.util

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.citrus.pottedplantskiosk.databinding.DialogAlertBinding
import com.citrus.pottedplantskiosk.util.base.BaseAlertDialog
import com.citrus.pottedplantskiosk.util.base.SafeClickListener
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

class CustomAlertDialog(
    mContext: Context,
    private var title: String,
    private var msg: String,
    private val icon: Int,
    private val tint: Int? = null,
    private val onConfirmListener: (() -> Unit)? = null
) : BaseAlertDialog<DialogAlertBinding>(mContext, DialogAlertBinding::inflate) {

    override fun initView() {
        binding.apply {
            YoYo.with(Techniques.FlipInX).delay(100).duration(1000).pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT).playOn(ivIcon)
            ivIcon.setImageResource(icon)
            tint?.let { ivIcon.imageTintList = ContextCompat.getColorStateList(context, it) }
            tvTitle.text = title

            if (msg.isNotEmpty()) {
                tvMessage.visibility = View.VISIBLE
                tvMessage.text = msg
            }else{
                tvServerError.visibility = View.VISIBLE
            }

            btnOK.onCallBackSafeClick {
                onConfirmListener?.invoke()
                dismiss()
            }

        }
    }


}


inline fun View.onCallBackSafeClick(crossinline onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}