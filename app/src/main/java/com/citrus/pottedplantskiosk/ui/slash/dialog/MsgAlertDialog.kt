package com.citrus.pottedplantskiosk.ui.slash.dialog

import android.content.Context
import com.citrus.pottedplantskiosk.databinding.DialogViewBinding
import com.citrus.pottedplantskiosk.util.base.BindingAlertDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo


class MsgAlertDialog(private val mContext: Context,private val showMsg:String) :
    BindingAlertDialog<DialogViewBinding>(mContext, DialogViewBinding::inflate) {


    override fun initView() {
        binding.apply {
            msg.text = showMsg
            YoYo.with(Techniques.Shake)
                .duration(700)
                .playOn(msg)
        }
    }
}