package com.citrus.pottedplantskiosk.api.remote.dto

import android.hardware.usb.UsbDevice
import com.pos.sdklib.aidl.newprinter.AidlNewPrinter
import java.io.Serializable

sealed class TransactionState {
    object NetworkIssue:TransactionState()
    object PrinterNotFoundIssue:TransactionState()
    object WorkFine:TransactionState()
}

data class TransactionData (val orders: OrderDeliveryData?, var state:TransactionState, var printer: UsbDevice?, var mNewPrinter: AidlNewPrinter?) :
    Serializable