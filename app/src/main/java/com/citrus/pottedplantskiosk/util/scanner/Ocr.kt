package com.citrus.pottedplantskiosk.util.scanner

import android.os.Bundle
import android.os.Message
import com.citrus.pottedplantskiosk.di.mDal
import com.citrus.pottedplantskiosk.util.print.PrinterEvent
import com.citrus.pottedplantskiosk.util.print.PrinterState
import com.pax.dal.IOCR
import com.pax.dal.IOCR.IOCRListener
import com.pax.dal.entity.OCRMRZResult
import com.pax.dal.entity.OCRResult
import com.pax.dal.exceptions.OCRException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

object Ocr {
    private val iocr: IOCR = mDal.ocr

    private val _ocrEvent = MutableSharedFlow<OcrEvent>()
    val ocrEvent: SharedFlow<OcrEvent> = _ocrEvent

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun init() {
        try {
            iocr.setAuthId("000004")
        } catch (e: OCRException) {
            e.printStackTrace()
        }
    }

    fun ocr(cameraId: Int) {
        try {
            iocr.open()
            // iocr.setCameraId(camerId);
            val bundle = Bundle()
            bundle.putInt("cameraId", cameraId)
            bundle.putBoolean("isFlashOn", true)
            bundle.putBoolean("isAutoFocus", true)
            iocr.setPreviewParam(bundle)
            iocr.startPreview(IOCR.TYPE_MRZ, 60000, object : IOCRListener {
                override fun onSucess(ocrResult: OCRResult) {
                    val ocrmrzResult = ocrResult as OCRMRZResult

                    coroutineScope.launch {
                        _ocrEvent.emit(OcrEvent(ocrmrzResult.toString()))
                    }


                    release()
                }

                override fun onError(i: Int) {

                    coroutineScope.launch {
                        _ocrEvent.emit(OcrEvent(errorMsg = "Errcode = $i"))
                    }
                
                    release()
                }
            })
        } catch (e: OCRException) {
            e.printStackTrace()
            release()
        }
    }


    private fun release() {
        try {
            iocr.stopPreview()
            iocr.close()
        } catch (e: OCRException) {
            e.printStackTrace()
        }
    }

}

data class OcrEvent(var result: String = "", var errorMsg: String = "")