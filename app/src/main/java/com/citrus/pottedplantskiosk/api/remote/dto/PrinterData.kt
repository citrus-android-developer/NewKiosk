package com.citrus.pottedplantskiosk.api.remote.dto

data class PrinterData(
    var printerIndex: Int,
    var usbIndex: Int,
    var usbProductId: String,
    var usbKey: String,
    var port: String,
    var ip: String,
    var footer: String,
    var is80mm: Boolean
) {
    constructor() : this(0, 0, "", "", "9100", "", "", false)
}