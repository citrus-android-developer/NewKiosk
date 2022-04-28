package com.citrus.pottedplantskiosk.util.print

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import com.citrus.pottedplantskiosk.di.MyApplication.Companion.mDal
import com.citrus.pottedplantskiosk.util.Constants
import com.pax.dal.IPrinter
import com.pax.dal.entity.EFontTypeAscii
import com.pax.dal.entity.EFontTypeExtCode
import com.pax.dal.exceptions.PrinterDevException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch


object A920Printer {
    private val printer: IPrinter = mDal!!.printer
    var printType: String? = null

    private val _printerEvent = MutableSharedFlow<PrinterEvent>()
    val printerEvent: SharedFlow<PrinterEvent> = _printerEvent

    private val coroutineScope = CoroutineScope(Dispatchers.Default)


    fun init() {
        try {
            Log.d(TAG, "init()")
            printer.init()
            printer.setGray(Constants.GRAY)
            printType = "default"
        } catch (e: PrinterDevException) {
            e.printStackTrace()
        }
    }

    val status: String
        get() = try {
            val status: Int = printer.status
            statusCode2Str(status)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
            ""
        }

    fun fontSet(asciiFontType: EFontTypeAscii?, cFontType: EFontTypeExtCode?) {
        try {
            printer.fontSet(asciiFontType, cFontType)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
        }
    }

    fun spaceSet(wordSpace: Byte, lineSpace: Byte) {
        try {
            printer.spaceSet(wordSpace, lineSpace)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
        }
    }

    fun printStr(str: String?, charset: String?) {
        try {
            printer.printStr(str, charset)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
        }
    }

    fun step(b: Int) {
        try {
            printer.step(b)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
        }
    }

    fun printBitmap(bitmap: Bitmap?) {
        try {
            Log.d(TAG, "printBitmap()")
            printer.printBitmap(bitmap)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
        }
    }

    fun start(): String {
        return try {
            Log.d(TAG, "start()")
            val res: Int = printer.start()
            statusCode2Str(res)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
            ""
        }
    }

    fun leftIndents(indent: Short) {
        try {
            printer.leftIndent(indent.toInt())
        } catch (e: PrinterDevException) {
            e.printStackTrace()
        }
    }

    val dotLine: Int
        get() = try {
            printer.dotLine
        } catch (e: PrinterDevException) {
            e.printStackTrace()
            -2
        }

    fun setGray(level: Int) {
        try {
            printer.setGray(level)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
        }
    }

    fun setDoubleWidth(isAscDouble: Boolean, isLocalDouble: Boolean) {
        try {
            printer.doubleWidth(isAscDouble, isLocalDouble)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
        }
    }

    fun setDoubleHeight(isAscDouble: Boolean, isLocalDouble: Boolean) {
        try {
            printer.doubleHeight(isAscDouble, isLocalDouble)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
        }
    }

    fun setInvert(isInvert: Boolean) {
        try {
            printer.invert(isInvert)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
        }
    }

    fun cutPaper(mode: Int) {
        try {
            printer.cutPaper(mode)
        } catch (e: PrinterDevException) {
            e.printStackTrace()
        }
    }

    fun statusCode2Str(status: Int): String {
        var res = ""
        when (status) {
            0 -> {
                res = PrinterState.DONE.name
                if (printType == "default") {
                    coroutineScope.launch {
                        _printerEvent.emit(PrinterEvent(PrinterState.DONE, ""))
                    }
                }
            }
            1 -> res = "Printer is busy "
            2 -> {
                res = PrinterState.OUT_OF_PAPER.name
                if (printType == "default") {
                    coroutineScope.launch {
                        _printerEvent.emit(
                            PrinterEvent(
                                PrinterState.OUT_OF_PAPER,
                                "列印機缺紙，請更換紙捲後按下確認鍵以重新列印"
                            )
                        )
                    }
                }
            }
            3 -> {
                res = PrinterState.FORMAT_ERROR.name
                coroutineScope.launch {
                    _printerEvent.emit(
                        PrinterEvent(PrinterState.FORMAT_ERROR, "")
                    )
                }
            }
            4 -> res = "Printer malfunctions "
            8 -> res = "Printer over heats "
            9 -> {
                res = PrinterState.LOW_BATTERY.name
                coroutineScope.launch {
                    _printerEvent.emit(
                        PrinterEvent(
                            PrinterState.LOW_BATTERY,
                            "請先連接電源供應器，確認連接後，按下確認鍵以重新列印"
                        )
                    )
                }
            }
            240 -> {
                res = PrinterState.UNFINISHED.name
                coroutineScope.launch {
                    _printerEvent.emit(
                        PrinterEvent(PrinterState.UNFINISHED, "")
                    )
                }
            }
            252 -> res = " The printer has not installed font library "
            254 -> {
                res = PrinterState.DATA_TOO_LONG.name
                coroutineScope.launch {
                    _printerEvent.emit(
                        PrinterEvent(PrinterState.DATA_TOO_LONG, "")
                    )
                }
            }
            else -> {}
        }
        Log.d(TAG, "result: $res")
        return res
    }

}

enum class PrinterState {
    PRINTING, DONE, FIRST_DONE, ALL_DONE, OUT_OF_PAPER, FORMAT_ERROR, LOW_BATTERY, UNFINISHED, DATA_TOO_LONG
}

data class PrinterEvent(var result: PrinterState, var errorMsg: String)