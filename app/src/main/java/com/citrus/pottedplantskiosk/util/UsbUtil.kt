package com.citrus.pottedplantskiosk.util

import android.content.Context
import android.hardware.usb.*
import com.citrus.pottedplantskiosk.api.remote.dto.UsbInfo
import java.util.*

object UsbUtil {
    fun getDevice(context: Context): UsbInfo {
        val usbInfoBean = UsbInfo()
        val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val usbShowList = ArrayList<String>()
        val productIdList = ArrayList<String>()
        val deviceKeyList = ArrayList<String>()
        val noPermissionDevice = ArrayList<UsbDevice>()
        val deviceList = manager.deviceList
        deviceList.values.forEach { device ->
            val productName = device.productName
            if (productName != "AX88772C" && productName != "MCP2200 USB Serial Port Emulator" && productName != "USB FLASH DRIVE") {
                usbShowList.add((if (productName.isNullOrEmpty()) "" else "$productName / ") + device.deviceId)
                deviceKeyList.add(device.deviceName)

                if (!manager.hasPermission(device)) {
                    noPermissionDevice.add(device)
                }

                try {
                    productIdList.add(device.productId.toString() + "/" + device.vendorId)
                } catch (e: SecurityException) {
                }

            }
        }
        usbInfoBean.usbManager = manager
        usbInfoBean.usbShowList = usbShowList
        usbInfoBean.productIdList = productIdList
        usbInfoBean.deviceKeyList = deviceKeyList
        usbInfoBean.noPermissionDevice = noPermissionDevice
        usbInfoBean.deviceList = deviceList

        return usbInfoBean
    }

    fun getConnection(context: Context, device: UsbDevice): UsbDeviceConnection? {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

        device.getInterface(0).also { intf ->
            intf.getEndpoint(0)?.also { endpoint ->
                usbManager.openDevice(device)?.apply {
                    claimInterface(intf, true)
                    return this
                }
            }
        }
        return null
    }
//    fun getEndpoint(device: UsbDevice): UsbEndpoint? {
//        device.getInterface(0).also { intf ->
//            return intf.getEndpoint(0)
//        }
//        return null
//    }

    fun getEndpoint(usbDevice: UsbDevice?): UsbEndpoint? {
        var epBulkOut: UsbEndpoint? = null
        if (usbDevice != null) {
            val usbInterface = usbDevice.getInterface(0)
            for (i in 0 until usbInterface.endpointCount) {
                val ep: UsbEndpoint = usbInterface.getEndpoint(i)
                when (ep.type) {
                    UsbConstants.USB_ENDPOINT_XFER_BULK -> if (UsbConstants.USB_DIR_OUT == ep.direction) { //輸出
                        epBulkOut = ep
                    }
                }
            }
        }
        return epBulkOut
    }
}