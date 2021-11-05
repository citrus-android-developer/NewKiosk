package com.citrus.pottedplantskiosk.ui.setting

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.databinding.FragmentBasicSettingBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants.trimSpace
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

    override fun initObserve() = Unit

    override fun initAction() {
        binding.apply {
            binding.etServerIp.doOnTextChanged { text, _, _, _ ->
                prefs.serverIp = text.toString().trimSpace()
            }

            binding.etStoreId.doOnTextChanged { text, _, _, _ ->
                prefs.storeId = text.toString().trimSpace()
            }


            idleSpinner.apply {
                lifecycleOwner = viewLifecycleOwner
                setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
                    prefs.idleTime = newItem.toInt()
                }
                setOnSpinnerOutsideTouchListener { _, _ ->
                    idleSpinner.dismiss()
                }
            }

            taxSpinner.apply {
                lifecycleOwner = viewLifecycleOwner
                setOnSpinnerItemSelectedListener<String> { _, _, newIndex, _ ->
                    prefs.taxFunction = newIndex
                }
                setOnSpinnerOutsideTouchListener { _, _ ->
                    taxSpinner.dismiss()
                }
            }

            decimalSpinner.apply {
                lifecycleOwner = viewLifecycleOwner
                setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
                    prefs.decimalPlace = newItem.toInt()
                }
                setOnSpinnerOutsideTouchListener { _, _ ->
                    decimalSpinner.dismiss()
                }
            }

            operationSpinner.apply {
                lifecycleOwner = viewLifecycleOwner
                setOnSpinnerItemSelectedListener<String> { _, _, newIndex, _ ->
                    prefs.methodOfOperation = newIndex
                }
                setOnSpinnerOutsideTouchListener { _, _ ->
                    operationSpinner.dismiss()
                }
            }
        }
    }
}