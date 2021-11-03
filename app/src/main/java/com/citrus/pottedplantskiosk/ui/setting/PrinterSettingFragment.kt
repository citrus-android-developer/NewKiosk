package com.citrus.pottedplantskiosk.ui.setting

import android.util.Log
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.databinding.FragmentPrinterSettingBinding
import com.citrus.pottedplantskiosk.util.base.BindingFragment

class PrinterSettingFragment : BindingFragment<FragmentPrinterSettingBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentPrinterSettingBinding::inflate

    override fun initView() {
        Log.e("--","--")
    }

    override fun initObserve() {
        Log.e("--","--")
    }

    override fun initAction() {
        Log.e("--","--")
    }


}