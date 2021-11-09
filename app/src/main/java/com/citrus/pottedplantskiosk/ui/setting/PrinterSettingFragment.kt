package com.citrus.pottedplantskiosk.ui.setting

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.api.remote.dto.UsbInfo
import com.citrus.pottedplantskiosk.databinding.FragmentPrinterSettingBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.ui.setting.adapter.MySpinnerAdapter
import com.citrus.pottedplantskiosk.ui.setting.adapter.UsbNameWithID
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.forEachReversedWithIndex
import com.citrus.pottedplantskiosk.util.Constants.trimSpace
import com.citrus.pottedplantskiosk.util.UsbUtil
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem

class PrinterSettingFragment : BindingFragment<FragmentPrinterSettingBinding>() {


    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentPrinterSettingBinding::inflate

    private var usbInfo = UsbInfo()
    private var adapter:MySpinnerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("PrinterSetting","view created")
    }


    override fun initView() {
        binding.apply {
            etStoreName.setText(prefs.storeName)
            etKioskId.setText(prefs.kioskId)
            etStoreAddress.setText(prefs.storeAddress)
            etHeader.setText(prefs.header)
            etFooter.setText(prefs.footer)
            printerSpinner.hint = prefs.printer
            is80mmSpinner.hint = if(prefs.printerIs80mm) "80mm" else "58mm"
            charSetSpinner.hint = prefs.charSet
        }
    }

    override fun initObserve() = Unit

    override fun initAction() {
        refreshUsbDevice()
        binding.apply {
            etStoreName.doOnTextChanged { text, _, _, _ ->
                prefs.storeName = text.toString().trimSpace()
            }
            etKioskId.doOnTextChanged { text, _, _, _ ->
                prefs.kioskId = text.toString().trimSpace()
            }
            etStoreAddress.doOnTextChanged { text, _, _, _ ->
                prefs.storeAddress = text.toString().trimSpace()
            }
            etHeader.doOnTextChanged { text, _, _, _ ->
                prefs.header = text.toString()
            }
            etFooter.doOnTextChanged { text, _, _, _ ->
                prefs.footer = text.toString()
            }

            is80mmSpinner.apply {
                lifecycleOwner = viewLifecycleOwner
                setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
                    prefs.printerIs80mm = newItem == "80mm"
                }
                setOnSpinnerOutsideTouchListener { _, _ ->
                    is80mmSpinner.dismiss()
                }
            }

            charSetSpinner.apply {
                lifecycleOwner = viewLifecycleOwner
                setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
                    prefs.charSet = newItem
                }
                setOnSpinnerOutsideTouchListener { _, _ ->
                    charSetSpinner.dismiss()
                }
            }
        }
    }

    private fun refreshUsbDevice() {
        usbInfo = UsbUtil.getDevice(requireContext())
        requestUsbPermission()
    }

    private fun requestUsbPermission() {
        usbInfo.noPermissionDevice.forEachReversedWithIndex { i, usbDevice ->
            if (usbInfo.usbManager?.hasPermission(usbDevice) == false) {
                val mPermissionIntent = PendingIntent.getBroadcast(
                    requireContext(),
                    0,
                    Intent(Constants.ACTION_USB_PERMISSION),
                    0
                )
                usbInfo.usbManager?.requestPermission(usbDevice, mPermissionIntent)
            } else usbInfo.noPermissionDevice.removeAt(i)
        }
        setupPrinterSpinner()
    }

    private fun setupPrinterSpinner(){
        val collectList =   usbInfo.deviceList.map { UsbNameWithID(name = it.value.productName!!, id = it.value.productId) }
        adapter = MySpinnerAdapter(binding.printerSpinner)

        binding.printerSpinner.apply {
            lifecycleOwner = viewLifecycleOwner
            adapter?.let {
                this.setSpinnerAdapter(it)
            }
            setOnSpinnerOutsideTouchListener { _, _ ->
                binding.printerSpinner.dismiss()
            }
            setItems(collectList)
            getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 1)
            var item = collectList.find { it.id.toString() == prefs.printer }
            hint = item?.name ?: "None Selected"
            setOnSpinnerItemSelectedListener<UsbNameWithID> { _, _, _, newItem ->
                prefs.printer = newItem.id.toString()
            }
        }
    }

    override fun onDestroyView() {
        adapter = null
        Log.e("PrinterSetting","view destroy")
        super.onDestroyView()
    }

}