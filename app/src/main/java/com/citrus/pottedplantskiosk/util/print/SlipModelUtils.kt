package com.citrus.pottedplantskiosk.util.print

import android.content.Context
import com.pos.sdklib.aidl.newprinter.param.BitmapPrintItemParam
import com.pos.sdklib.aidl.newprinter.param.MultipleTextPrintItemParam
import com.pos.sdklib.aidl.newprinter.param.PrintItemAlign
import com.pos.sdklib.aidl.newprinter.param.TextPrintItemParam
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

class SlipModelUtils {

    fun createMultipleTextPrintItemParam(
        scales: FloatArray?,
        list: Array<TextPrintItemParam>?
    ): MultipleTextPrintItemParam? {
        return MultipleTextPrintItemParam(scales, list)
    }

    fun addLeftAndRightItem(
        left: String?,
        right: String?,
        scales: FloatArray?
    ): MultipleTextPrintItemParam? {
        return addLeftAndRightItem(left, right, -1, -1, scales)
    }

    fun addCenterAndRightItem(center: String?, right: String?): MultipleTextPrintItemParam? {
        val scales = floatArrayOf(2f, 1f)
        val leftItem = addTextOnePrint(center, false, -1, PrintItemAlign.RIGHT)
        val rightItem = addTextOnePrint(right, false, -1, PrintItemAlign.RIGHT)
        val list = arrayOf(leftItem, rightItem)
        return createMultipleTextPrintItemParam(scales, list)
    }

    fun addTextOnePrint(
        content: String?,
        isBold: Boolean,
        textSize: Int,
        align: PrintItemAlign?
    ): TextPrintItemParam {
        val param = TextPrintItemParam()
        param.content = content
        if (textSize > 0) {
            param.textSize = textSize
        } else {
            param.textSize = 16
            param.scaleHeight = 1.6f
            param.scaleWidth = 1.2f
        }
        param.itemAlign = align
        param.isBold = isBold
        return param
    }

    fun addFourItem(
        first: String?,
        second: String?,
        third: String?,
        fourth: String?,
        scales: FloatArray?
    ): MultipleTextPrintItemParam? {
        var scales = scales
        if (scales == null) {
            scales = floatArrayOf(1f, 3.5f, 1f, 1.5f)
        }
        val firstItem = addTextOnePrint(first, false, -1, PrintItemAlign.LEFT)
        val secondItem = addTextOnePrint(second, false, -1, PrintItemAlign.CENTER)
        val thirdItem = addTextOnePrint(third, false, -1, PrintItemAlign.CENTER)
        val fourthItem = addTextOnePrint(fourth, false, -1, PrintItemAlign.RIGHT)
        val list = arrayOf(firstItem, secondItem, thirdItem, fourthItem)
        return MultipleTextPrintItemParam(scales, list)
    }

    fun addBitmapItem(context: Context, nameInAssets: String): BitmapPrintItemParam? {
        val printItemParam = BitmapPrintItemParam()
        try {
            printItemParam.bitmap = inputStream2byte(context.assets.open("image/$nameInAssets"))
            printItemParam.height = 100
            printItemParam.width = 300
            printItemParam.itemAlign = PrintItemAlign.CENTER
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return printItemParam
    }

    fun addLeftAndRightItem(
        left: String?,
        right: String?,
        leftSize: Int,
        rightSize: Int,
        scales: FloatArray?
    ): MultipleTextPrintItemParam? {
        var scales = scales
        if (scales == null) {
            scales = floatArrayOf(1f, 1f)
        }
        val leftItem = addTextOnePrint(left, false, leftSize, PrintItemAlign.LEFT)
        val rightItem = addTextOnePrint(right, false, rightSize, PrintItemAlign.RIGHT)
        val list = arrayOf(leftItem, rightItem)
        return createMultipleTextPrintItemParam(scales, list)
    }


    @Throws(IOException::class)
    fun inputStream2byte(inputStream: InputStream): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buff = ByteArray(100)
        var rc: Int
        while (inputStream.read(buff, 0, 100).also { rc = it } > 0) {
            byteArrayOutputStream.write(buff, 0, rc)
        }
        return byteArrayOutputStream.toByteArray()
    }
}