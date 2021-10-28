package com.citrus.pottedplantskiosk.util.print

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import androidx.fragment.app.FragmentActivity
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.DeliveryInfo
import com.citrus.pottedplantskiosk.api.remote.dto.Orders
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.dfShow
import com.citrus.pottedplantskiosk.util.UsbUtil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException

class PrintOrderInfo(
    private val context: Context,
    private val deliveryInfo: Orders.OrderDeliveryData,
    private val usbDevice: UsbDevice?,
    private val onResult: (isSuccess: Boolean, err: String?) -> Unit
) {

    //USB
    private var endpoint: UsbEndpoint? = null
    private var mDeviceConnection: UsbDeviceConnection? = null

    private var mOutputStream: OutputStream? = null
    private var socket: Socket? = null

    fun startPrint() {
        val is80mm = prefs.printerIs80mm
        var deliveryItemList = deliveryInfo.ordersItemDelivery

        var data: ByteArray
        data = initCmd()
        data = b(data, fontSizeCmd(FontSize.Big))
        data = b(data, boldCmd(true))
        data = b(data, setLineSpace(55))
        if (prefs.storeName.isNotEmpty()) data = b(data, text(prefs.storeName))
        data = b(data, fontSizeCmd(FontSize.Normal))
        data = b(data, boldCmd(false))
        data = b(
            data,
            text(context.resources.getString(R.string.orderTime) + Constants.getCurrentTime())
        )
        data = b(
            data,
            text(context.resources.getString(R.string.orderNo) + deliveryInfo.ordersItemDelivery[0].orderNO)
        )
        data = b(
            data,
            text(context.resources.getString(R.string.printTime) + Constants.getCurrentTime())
        )

        data = b(data, dashLine(is80mm))
        if (is80mm) {
            data = b(data, boldCmd(true))
            data = b(
                data,
                twoColumn(
                    context.getString(R.string.item),
                    context.getString(R.string.qty) + "    " + context.getString(R.string.Total),
                    is80mm
                )
            )
            data = b(data, boldCmd(false))
            data = b(data, dashLine(is80mm))
        }

        var sum = 0
        for (item in deliveryItemList) {
            sum += item.qty

            if (is80mm) {
                val priceStr = String.format("%7s", dfShow.format(item.gPrice))
                val qtyStr = String.format("%-3s", item.qty)

                //最多48個字 48-11=37
                if (item.gname.toByteArray(charset("GBK")).size <= 37) {
                    data = b(data, twoColumn( if (prefs.languagePos == 1) item.gName2 else item.gname, qtyStr + priceStr, is80mm))
                } else {
                    data = b(data, text(if (prefs.languagePos == 1) item.gName2 else item.gname))
                    data = b(data, twoColumn("", qtyStr + priceStr, is80mm))
                }
            } else {
                var itemTitle = item.qty.toString() + "x " + if (prefs.languagePos == 1) item.gName2 else item.gname

                if (getStringPixLength(itemTitle + dfShow.format(item.gPrice), 12, 24) / 12 > 33) {
                    data = b(data, text(itemTitle))
                    data = b(data, twoColumn("", dfShow.format(item.gPrice), is80mm))
                } else {
                    data = b(data, twoColumn(itemTitle, dfShow.format(item.gPrice), is80mm))
                }
            }

            val flavorAdd =
                if (!item.addGName.isNullOrEmpty() && !item.flavorDesc.isNullOrEmpty()) item.addGName + "/" + item.flavorDesc
                else if (!item.addGName.isNullOrEmpty()) item.addGName
                else if (!item.flavorDesc.isNullOrEmpty()) item.flavorDesc
                else null

            flavorAdd?.let { data = b(data, text((if (is80mm) "  #" else "    #") + it)) }
        }
        data = b(data, dashLine(is80mm))

        var orgAmtStr = String.format("%7s", dfShow.format(deliveryInfo.ordersDelivery.sPrice))
        val qtyStr = String.format("%-3s", sum)

        data = if (is80mm) {
            b(data, twoColumn(context.getString(R.string.Total), qtyStr + orgAmtStr, is80mm))
        } else {
            b(data, twoColumn(context.getString(R.string.Total), "$qtyStr $orgAmtStr", is80mm))
        }
        data = b(data, text("\n"))

        if (data.isEmpty()) {
            onResult(true, null)
        } else {
            data = b(data, cutPaperCmd())
            send(data)
        }
    }


    private fun send(buffer: ByteArray) {
        if (usbDevice == null) {
            onError("usbDevice is null")
            return
        }
        mDeviceConnection = UsbUtil.getConnection(context, usbDevice)
        endpoint = UsbUtil.getEndpoint(usbDevice)

        mDeviceConnection?.let {
            if (it.bulkTransfer(endpoint, buffer, buffer.size, 0) < 0) {
                onError("bulkTransfer error")
            } else {
                onResult(true, null)
            }
        } ?: run {
            onError("mDeviceConnection is null")
        }
    }

    private fun onError(message: String) {
        onResult(false, message)
    }


}