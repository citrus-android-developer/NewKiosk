package com.citrus.pottedplantskiosk.util.print

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import androidx.fragment.app.FragmentActivity
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.dfShow
import com.citrus.pottedplantskiosk.util.Constants.getGstStr
import com.citrus.pottedplantskiosk.util.UsbUtil

import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Base64.DEFAULT
import androidx.annotation.RequiresApi
import com.citrus.pottedplantskiosk.api.remote.dto.OrderDeliveryData
import java.io.*
import java.lang.Byte.decode
import java.util.*


class PrintOrderInfo(
    private val context: Context,
    private val deliveryInfo: OrderDeliveryData,
    private val usbDevice: UsbDevice?,
    private val onResult: (isSuccess: Boolean, err: String?) -> Unit
) {

    private var endpoint: UsbEndpoint? = null
    private var mDeviceConnection: UsbDeviceConnection? = null


    fun startPrint() {
        val is80mm = prefs.printerIs80mm
        var deliveryItemList = deliveryInfo.ordersItemDelivery

        var data: ByteArray
        data = initCmd()
        data = b(data, fontSizeCmd(if(is80mm) FontSize.SuperBig else FontSize.Big))
        data = b(data, alignCmd(if(is80mm) 1 else 0))
        data = b(data, boldCmd(true))
        data = b(
            data,
            text(deliveryInfo.ordersItemDelivery[0].orderNO)
        )

        data = b(data, setLineSpace(55))

        data = b(data, fontSizeCmd(if(is80mm) FontSize.Big else FontSize.Normal))
        data = b(data, alignCmd(0))
        if (prefs.header.isNotEmpty()) data = b(data, text(prefs.header))
        if (prefs.kioskId.isNotEmpty()) data = b(data, text("Take Away: " + prefs.kioskId))
        data = b(data, fontSizeCmd(FontSize.Normal))
        if (prefs.storeName.isNotEmpty()) data = b(data, text(prefs.storeName))
        if (prefs.storeAddress.isNotEmpty()) data = b(data, text(prefs.storeAddress))
        data = b(data, boldCmd(false))
        data = b(data, text("\n"))

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
                val priceStr = String.format("%7s", Constants.getValByMathWay(item.gPrice))
                val qtyStr = String.format("%-3s", item.qty)

                //最多48個字 48-11=37
                if (item.gname.toByteArray(charset("GBK")).size <= 37) {
                    data = b(
                        data,
                        twoColumn(
                            if (prefs.languagePos == 1) item.gName2 else item.gname,
                            qtyStr + priceStr,
                            is80mm
                        )
                    )
                } else {
                    data = b(data, text(if (prefs.languagePos == 1) item.gName2 else item.gname))
                    data = b(data, twoColumn("", qtyStr + priceStr, is80mm))
                }
            } else {
                var itemTitle =
                    item.qty.toString() + "x " + if (prefs.languagePos == 1) item.gName2 else item.gname

                if (getStringPixLength(
                        itemTitle + Constants.getValByMathWay(item.gPrice),
                        12,
                        24
                    ) / 12 > 33
                ) {
                    data = b(data, text(itemTitle))
                    data = b(data, twoColumn("", Constants.getValByMathWay(item.gPrice), is80mm))
                } else {
                    data = b(
                        data,
                        twoColumn(itemTitle, Constants.getValByMathWay(item.gPrice), is80mm)
                    )
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

        var orgAmtStr =
            String.format("%7s", Constants.getValByMathWay(deliveryInfo.ordersDelivery.sPrice))
        val qtyStr = String.format("%-3s", sum)
        val gst =
            String.format("%7s", Constants.getValByMathWay(deliveryInfo.ordersDelivery.totaltax))
        val grandTotal =
            String.format(
                "%7s",
                Constants.getValByMathWay(deliveryInfo.ordersDelivery.sPrice + deliveryInfo.ordersDelivery.totaltax)
            )

        data = if (is80mm) {
            b(data, twoColumn(context.getString(R.string.Total), qtyStr + orgAmtStr, is80mm))
        } else {
            b(data, twoColumn(context.getString(R.string.Total), "$qtyStr $orgAmtStr", is80mm))
        }

        data = b(data, dashLine(is80mm))
        data = b(
            data,
            twoColumn(
                context.getString(R.string.paymentType),
                deliveryInfo.ordersDelivery.payType,
                is80mm
            )
        )
        data = b(data, twoColumn(context.getString(R.string.SubTotal), orgAmtStr, is80mm))
        data = b(data, twoColumn(context.getGstStr(), gst, is80mm))

        if(is80mm) {
            data = b(data, fontSizeCmd(FontSize.Big))
            data = b(data, alignCmd(1))
        }
        data = b(data, boldCmd(true))

        if (prefs.taxFunction == 2) {
            data = if (is80mm) {
                b(data, twoColumnBig(context.getString(R.string.grandTotal), grandTotal, is80mm))
            } else {
                b(data, twoColumnBig(context.getString(R.string.grandTotal), "$grandTotal", is80mm))
            }
        } else {
            data = if (is80mm) {
                b(data, twoColumnBig(context.getString(R.string.grandTotal), orgAmtStr, is80mm))
            } else {
                b(data, twoColumnBig(context.getString(R.string.grandTotal), "$orgAmtStr", is80mm))
            }
        }

        data = b(data, fontSizeCmd(FontSize.Normal))
        data = b(data, boldCmd(false))
        data = b(data, alignCmd(0))
        data = b(data, dashLine(is80mm))

        if (prefs.footer.isNotEmpty()) data = b(data, text(prefs.footer))

        data = b(data, text("\n"))

        val isP = context.assets.open("jps_invoice.pdf")
        val size: Int = isP.available()

        val buffer = ByteArray(size)
        isP.read(buffer)
        isP.close()




//        val invoiceBitmapOrg = BitmapFactory.decodeResource(
//            context!!.resources,
//            R.drawable.invoice
//        )


        val icon = writeBytesAsPdf(buffer)!!



        data = b(data, bitmapToBytes(icon))

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


    private fun compressPic(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val newWidth = (width * 1.3).toInt()
        val newHeight = (height * 1.3).toInt()
        val targetBmp = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val targetCanvas = Canvas(targetBmp)
        targetCanvas.drawColor(-0x1)
        targetCanvas.drawBitmap(
            bitmap,
            Rect(0, 0, width, height),
            Rect(0, 0, newWidth, newHeight),
            null
        )
        return targetBmp
    }


    private fun getFileDescriptor(byteArray: ByteArray): ParcelFileDescriptor {
        val file = File.createTempFile("temp", null).also { FileOutputStream(it).write(byteArray) }
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    }

    private fun writeBytesAsPdf(bytes: ByteArray?): Bitmap? {
        if (bytes==null) return null
        val page = PdfRenderer(getFileDescriptor(bytes)).openPage(0)

        val bitmap: Bitmap = Bitmap.createBitmap(context.resources.displayMetrics.densityDpi / 72 * page.width, context.resources.displayMetrics.densityDpi / 72 * page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_PRINT)

        val width = bitmap.width
        val height = bitmap.height
        val newWidth = (width * 1.3).toInt()
        val newHeight = (height * 1.3).toInt()
        val targetBmp = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        val targetCanvas = Canvas(targetBmp)
        targetCanvas.drawColor(-0x1)
        targetCanvas.drawBitmap(
            bitmap,
            Rect(0, 0, width, height),
            Rect(0, 0, newWidth, newHeight),
            null
        )
      return targetBmp
    }


}