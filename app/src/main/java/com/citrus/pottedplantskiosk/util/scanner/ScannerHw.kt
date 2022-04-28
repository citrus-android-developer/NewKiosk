package com.citrus.pottedplantskiosk.util.scanner

import android.util.Log
import com.citrus.pottedplantskiosk.di.mDal
import com.pax.dal.IScannerHw
import com.pax.dal.exceptions.ScannerHwDevException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.lang.Runnable

object ScannerHw {
    private val mIScannerHw: IScannerHw = mDal.scannerHw

    private val _result = MutableSharedFlow<String>()
    val result: SharedFlow<String> = _result

    private var scanJob: Job? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    fun startScan() {
        scanJob?.cancel()
        scanJob = coroutineScope.launch {
            mIScannerHw.open()
            val scanResult = mIScannerHw.read(10000)
            if (null != scanResult) {
                _result.emit(scanResult.content)
            }
        }
        scanJob?.start()
    }

    fun stopScan() {
        try {
            mIScannerHw.stop()
            mIScannerHw.close()
            scanJob?.cancel()
        } catch (e: ScannerHwDevException) {
            e.printStackTrace()
        }
    }


}