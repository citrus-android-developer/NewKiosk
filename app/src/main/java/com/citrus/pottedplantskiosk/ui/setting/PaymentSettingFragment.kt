package com.citrus.pottedplantskiosk.ui.setting
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.databinding.FragmentPaymentSettingBinding
import com.citrus.pottedplantskiosk.util.base.BindingFragment

class PaymentSettingFragment : BindingFragment<FragmentPaymentSettingBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentPaymentSettingBinding::inflate

    override fun initView() = Unit

    override fun initObserve() = Unit

    override fun initAction() = Unit


}