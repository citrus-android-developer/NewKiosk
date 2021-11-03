package com.citrus.pottedplantskiosk.ui.setting

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.databinding.FragmentBasicSettingBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.base.BindingFragment

class BasicSettingFragment : BindingFragment<FragmentBasicSettingBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentBasicSettingBinding::inflate

    override fun initView() {
        binding.apply {
            etServerIp.setText(prefs.serverIp)
            etStoreId.setText(prefs.storeId)

            idleSpinner.hint = prefs.idleTime.toString()

            taxSpinner.hint =
                context?.resources?.getStringArray(R.array.TaxFunction)?.get(prefs.taxFunction)
                    ?: ""
            decimalSpinner.hint = prefs.decimalPlace.toString()
            operationSpinner.hint = context?.resources?.getStringArray(R.array.MethodOfOperation)
                ?.get(prefs.methodOfOperation) ?: ""
        }
    }

    override fun initObserve() {
       Log.e("--","--")
    }

    override fun initAction() {

        binding.etServerIp.doOnTextChanged { text, _, _, _ ->
            prefs.serverIp = text.toString()
        }

        binding.etStoreId.doOnTextChanged { text, _, _, _ ->
            prefs.storeId = text.toString()
        }

        binding.idleSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
            var idleTime = newItem
            prefs.idleTime = idleTime.toInt()
        }
        binding.taxSpinner.setOnSpinnerItemSelectedListener<String> { _, _, newIndex, _ ->
            prefs.taxFunction = newIndex
        }
        binding.decimalSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
            var decimalType = newItem
            prefs.decimalPlace = decimalType.toInt()
        }
        binding.operationSpinner.setOnSpinnerItemSelectedListener<String> { _, _, newIndex, _ ->
            prefs.methodOfOperation = newIndex
        }
    }



}