package com.citrus.pottedplantskiosk.api.remote.dto

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import java.util.*

data class UsbInfo(
    var deviceList: HashMap<String, UsbDevice>,
    var usbShowList: ArrayList<String>,
    var deviceKeyList: ArrayList<String>,
    var productIdList: ArrayList<String>,
    var noPermissionDevice: ArrayList<UsbDevice>,
    var usbManager: UsbManager?
) {
    constructor() : this(HashMap(), ArrayList(), ArrayList(), ArrayList(),  ArrayList(), null)
}