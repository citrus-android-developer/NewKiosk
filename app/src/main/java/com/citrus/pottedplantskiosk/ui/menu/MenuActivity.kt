package com.citrus.pottedplantskiosk.ui.menu

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.BannerResponse
import com.citrus.pottedplantskiosk.api.remote.dto.Data
import com.citrus.pottedplantskiosk.databinding.ActivityMenuBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.PrintUtils
import com.citrus.pottedplantskiosk.util.i18n.LocaleHelper
import com.pos.poslibusb.MCS7840Driver
import com.pos.poslibusb.PosLibUsb
import com.pos.poslibusb.UsbDeviceFilter
import com.pos.poslibusb.Utils
import com.pos.printersdk.PrinterFunctions
import com.pos.printersdk.PrinterManager
import com.pos.printersdk.PrinterNetworkReceiveListener
import com.pos.printersdk.PrinterReceiveListener
import com.pos.printersdk.PrinterStatusInfo

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException

@AndroidEntryPoint
class MenuActivity : AppCompatActivity(), PrinterNetworkReceiveListener, PrinterReceiveListener {
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var binding: ActivityMenuBinding

    lateinit var data: Data
    private var banner: BannerResponse? = null

    val READ_REQUEST_CODE = 42
    protected val ACTION_USB_PERMISSION = "com.android.hardware.USB_PERMISSION"
    protected var MCS7840: MCS7840Driver? = null
    protected var mUsbManager: UsbManager? = null
    protected var mUsbDevice: UsbDevice? = null
    private var mPermissionIntent: PendingIntent? = null
    protected var mMatchDevice: ArrayList<UsbDevice>? = null
    var mPortName = ""
    private val m_iFilter = 0
    protected var mImManager: InputMethodManager? = null
    val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE
    )


    var mPortSettings = 115200
    var mUnderline = 0
    var mInvertColor = 0
    var mEmphasized = 0
    var mUpsideDown = 0
    var mHeight = 0
    var mWidth = 0
    var mLeftMargin = 0
    var mAlignment = 0

    private val mUsbPermissionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device =
                        intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    // this is simple sample, you can change design best flow when user agree/denied usb permission.
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED,
                            false
                        ) && device != null
                    ) {
                        Utils.logd("Permission granted for device " + device.deviceName)
                        val mcs7840Device: UsbDevice? =
                            MCS7840?.MCS7840GetDevices(mUsbManager)
                        if (mcs7840Device != null && mcs7840Device.deviceName == device.deviceName) {
                            // this is SerialCOM get permission flow.
                            detectMCS7840Ports()
                        }
                    } else {
                        // user denied usb permission, you can ask again.
                        Utils.logd("Permission denied for device " + device!!.deviceName)
                    }
                }
            }
        }
    }

    private val mUsbDeviceAttachedDetachedReceiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                    synchronized(this) {
                        // this is USB attached(plugin) flow.
                        val device =
                            intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                        if (device != null) {
                            // this is simple sample, you can change design best flow.
                            Utils.logd("Device Attached: " + device.deviceName)
                            scanDevices()
                        }
                    }
                } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                    synchronized(this) {
                        val device =
                            intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                        if (device != null) {
                            Utils.logd("Device Detached: " + device.deviceName)
                            val mcs7840Device =
                                if (MCS7840 == null) null else MCS7840!!.MCS7840GetDevices(
                                    mUsbManager
                                )
                            if (mcs7840Device != null && mcs7840Device.deviceName == device.deviceName) {
                                // this is USB to SerialCOM mcu detached flow, it will not happen.
                                // TODO: need get SerialCOM port name.
                                MCS7840!!.MCS7840CloseAll()
                            } else {
                                // this is USB detached(unplug) flow.
                                mMatchDevice?.remove(device)
                            }
                        }
                    }
                }
            }
        }

    /**掃描槍回調支援*/
    private var isInput = false
   // private lateinit var scanKeyManager: ScanKeyManager
    private var onScanListener: ((String) -> Unit)? = null
    fun setOnScanListener(listener: (String) -> Unit) {
        onScanListener = listener
    }

    override fun onResume() {
        super.onResume()
        setFullScreen()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onDestroy() {
        super.onDestroy()
        PrinterFunctions.setNetworkReceiveEventListener(null)
        unregisterUsbReceiver()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)


        registerUsbReceiver()
        scanDevices()

        // If want use SerialCOM need create MCS7840Driver object, or not MCS7840 = null.
        // If want use SerialCOM need create MCS7840Driver object, or not MCS7840 = null.
        MCS7840 = MCS7840Driver(this)
        val mcs7840Device = if (MCS7840 == null) null else MCS7840!!.MCS7840GetDevices(mUsbManager)
        if (mcs7840Device != null && mUsbManager!!.hasPermission(mcs7840Device)) detectMCS7840Ports()

        val callback = PosLibUsb.Callback(mUsbManager, null)
        PosLibUsb.setCallback(callback)

        val printerFunctionsCallback = PrinterFunctions.Callback(this)
        PrinterFunctions.setCallback(printerFunctionsCallback)

        val bundle = intent.extras
        data = bundle?.getSerializable("data") as Data
        var bannerData = bundle?.getSerializable("banner")

//        scanKeyManager = ScanKeyManager(object : ScanKeyManager.OnScanValueListener {
//            override fun onScanValue(value: String?) {
//                value?.let {
//                    onScanListener?.invoke(it)
//                }
//            }
//        })

        bannerData?.let {
            banner = it as BannerResponse
        }

        data?.let {
            menuViewModel.showData(it)
        }

        banner?.let {
            menuViewModel.showBanner(it)
        }

        lifecycleScope.launchWhenStarted {
            menuViewModel.reLaunchActivity.collect {
                prefs.isNavigate = true
                val intent = Intent()
                intent.setClass(this@MenuActivity, MenuActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("data", data)
                bundle.putSerializable("banner", banner)
                intent.putExtras(bundle)
                this@MenuActivity.startActivity(intent)
                finish()
            }
        }

        lifecycleScope.launchWhenStarted {
            menuViewModel.toActivityPrint.collect {
                SampleReceipt_CHT("BIG5")
            }
        }

        setOnScanListener { scanValue ->
            if (!menuViewModel.isIdentify) {
                menuViewModel.isIdentify = true
                menuViewModel.setScanResult(scanValue)
            }
        }

        PrinterFunctions.setNetworkReceiveEventListener(this)
    }

    fun setFullScreen() {
        val decorView = setSystemUiVisibilityMode()
        decorView.setOnSystemUiVisibilityChangeListener {
            setSystemUiVisibilityMode() // Needed to avoid exiting immersive_sticky when keyboard is displayed
        }
    }

    open fun scanDevices() {
        try {
            mUsbManager = getSystemService(USB_SERVICE) as UsbManager
            mMatchDevice = UsbDeviceFilter.getMatchingHostDevices(this, R.xml.printer_device_filter)

            onOpenPort()
            Utils.logd("scanDevices mMatchDevice = $mMatchDevice")
        } catch (e: Exception) {
            Utils.loge("Failed to parse devices.xml: " + e.message)
            return
        }
    }

    open fun onOpenPort() {
        for (device in mMatchDevice!!) {
            Log.e("device", device.deviceName)
            if ("/dev/bus/usb/001/004" == device.deviceName) {
                mUsbDevice = device
                mPortName = device.deviceName
                Utils.logd("open port: " + mUsbDevice!!.deviceName)
                requestPermission(mUsbDevice)
            }
        }

        // usbDevice port name need check has permission, new flow must use this.
        if (mUsbDevice != null && mUsbManager!!.hasPermission(mUsbDevice)) {
            lifecycleScope.launch(Dispatchers.IO) {
                delay(3000)
                val iOpenRes =
                    PrinterFunctions.OpenPort(mPortName, mPortSettings)

                if (iOpenRes == Utils.RESULT_TOKEN_DEF.RESULT_SUCCESS) {
                    PrinterManager.ModelName(
                        mPortName,
                        mPortSettings
                    )
                }
            }

        }
    }


    private fun registerUsbReceiver() {
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        registerReceiver(mUsbPermissionReceiver, filter)
        val filterAttached = IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        val filterDetached = IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
        registerReceiver(mUsbDeviceAttachedDetachedReceiver, filterAttached)
        registerReceiver(mUsbDeviceAttachedDetachedReceiver, filterDetached)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mPermissionIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(Constants.ACTION_USB_PERMISSION),
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            mPermissionIntent = PendingIntent.getBroadcast(
                this,
                0,
                Intent(Constants.ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE
            )
        }

    }

    open fun unregisterUsbReceiver() {
        Utils.logd("unregisterUsbReceiver")
        unregisterReceiver(mUsbPermissionReceiver)
        unregisterReceiver(mUsbDeviceAttachedDetachedReceiver)
    }

    open fun requestPermission(usbDevice: UsbDevice?) {
        if (usbDevice == null) {
            Utils.logd("device can not null.")
            return
        }
        for (i in 0 until usbDevice.interfaceCount) {
            val usbInterface = usbDevice.getInterface(i)
            Utils.logd("usbInterface.getInterfaceClass() = " + usbInterface.interfaceClass + ", COMM(2), HID(3), PRINTER(7), CDC_DATA(10)")
        }
        if (!mUsbManager!!.hasPermission(usbDevice)) {
            // Wait user to permit the permission
            mUsbManager!!.requestPermission(usbDevice, mPermissionIntent)
            Utils.logd("Requesting permission to use " + usbDevice.deviceName)
        }
    }

    open fun SampleReceipt_CHT(characterSet: String?) {
        Utils.logd("SampleReceipt_CHT characterSet = %s", characterSet)
        try {
            PrinterFunctions.setReceiveEventListener(null)
            mUnderline = PrintUtils.UNDERLINE_TOKEN_DEF.UNDERLINE_ON
            mHeight = 1
            mWidth = 2
            mAlignment = PrintUtils.ALIGNMENT_TOKEN_DEF.ALIGNMENT_CENTER
            PrintTextByteArray("POS便利店\n\n".toByteArray(charset(characterSet!!)))
            mUnderline = PrintUtils.UNDERLINE_TOKEN_DEF.UNDERLINE_OFF
            mHeight = 0
            mWidth = 0
            PrintTextByteArray(
                "統一編號:12345678\n電話:(02)2299-1234\n\n".toByteArray(
                    charset(
                        characterSet!!
                    )
                )
            )
            mAlignment = PrintUtils.ALIGNMENT_TOKEN_DEF.ALIGNMENT_RIGHT
            PrintTextByteArray("2013-01-01 13:33\n".toByteArray(charset(characterSet!!)))
            mAlignment = PrintUtils.ALIGNMENT_TOKEN_DEF.ALIGNMENT_LEFT
            PrintTextByteArray(
                "商店編號:0001              收銀機編號:0001\n".toByteArray(
                    charset(
                        characterSet!!
                    )
                )
            )
            PrintTextByteArray(
                "收銀員編號:0001              銷售編號:0003\n\n".toByteArray(
                    charset(
                        characterSet!!
                    )
                )
            )
            PrintTextByteArray(
                "牛肉漢堡                          $40.0 元\n".toByteArray(
                    charset(
                        characterSet!!
                    )
                )
            )
            PrintTextByteArray(
                "蔬菜莎拉                          $20.0 元\n".toByteArray(
                    charset(
                        characterSet!!
                    )
                )
            )
            PrintTextByteArray(
                "柳橙汁                            $30.0 元\n".toByteArray(
                    charset(
                        characterSet!!
                    )
                )
            )
            PrintTextByteArray(
                "烤雞腿                            $30.0 元\n".toByteArray(
                    charset(
                        characterSet!!
                    )
                )
            )
            val iCount = 41
            for (i in 0 until iCount) {
                PrintTextByteArray(
                    String.format("蔬菜汁 %02d                         $10.0 元\n", i + 1)
                        .toByteArray(
                            charset(
                                characterSet!!
                            )
                        )
                )
            }
            PrintTextByteArray("\n".toByteArray(charset(characterSet!!)))
            mEmphasized = PrintUtils.EMPHASIZED_TOKEN_DEF.EMPHASIZED_OFF
            PrintTextByteArray(
                "總計:                            $530.0 元\n".toByteArray(
                    charset(
                        characterSet!!
                    )
                )
            )
            //SampleReceipt_BarCode_QRCode()
            PrinterFunctions.setReceiveEventListener(this)
            PrintTextByteArray("".toByteArray(charset(characterSet!!)))
            PrinterFunctions.PreformCut(
                mPortName,
                mPortSettings,
                PrintUtils.CUT_TYPE_TOKEN_DEF.CUT_TYPE_WITH_FEED
            )
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    open fun PrintTextByteArray(textData: ByteArray?): Int {
        return PrinterFunctions.PrintTextByteArray(
            mPortName,
            mPortSettings,
            mUnderline,
            mInvertColor,
            mEmphasized,
            mUpsideDown,
            mHeight,
            mWidth,
            mLeftMargin,
            mAlignment,
            textData
        )
    }

    open fun detectMCS7840Ports(): Int {
        Utils.logd("Do detectMCS7840Ports()")
        var numberOfPorts = 0
        val mcs7840Device = if (MCS7840 == null) null else MCS7840!!.MCS7840GetDevices(mUsbManager)
        if (mcs7840Device != null && mUsbManager!!.hasPermission(mcs7840Device)) {
            numberOfPorts = MCS7840!!.MCS7840Detect(mUsbManager)
            Utils.logd("Detect numberOfPorts = $numberOfPorts")
            MCS7840!!.MCS7840Init()
        } else {
            Utils.logd("No permission!")
        }
        return numberOfPorts
    }


    private fun setSystemUiVisibilityMode(): View {
        val decorView = window.decorView
        val options = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        decorView.systemUiVisibility = options
        return decorView
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode != KeyEvent.KEYCODE_BACK && !isInput && event.keyCode != KeyEvent.KEYCODE_VOLUME_UP && event.keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && event.keyCode != KeyEvent.KEYCODE_VOLUME_MUTE) {
           // scanKeyManager.analysisKeyEvent(event)
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onPrinterNetworkDisconnectReceive(portName: String?) {
        Utils.logd("onPrinterNetworkDisconnectReceive portName = $portName")
    }

    override fun onPrintReceive(result: Int, statusInfo: PrinterStatusInfo?) {
        runOnUiThread {
            var msg: String? = ""
            val errMsg: String = PrintUtils.makeErrorMessage(applicationContext, statusInfo)
            msg = if (errMsg.isEmpty()) java.lang.String.format(
                "%s\n%s",
                Utils.getResultString(result),
                errMsg
            ) else java.lang.String.format(
                "%s\n%s\n%s",
                Utils.getResultString(result),
                "Error",
                errMsg
            )

            if(errMsg.isEmpty()) {
                Log.e("print", "success")
                menuViewModel.setPrintResult(true)
            }else {
                menuViewModel.setPrintResult(false)
            }

        }




    }

//    private fun onKeyBoardListener() {
//        SoftKeyBoardListener(this).setListener(object :
//            SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
//            override fun keyBoardShow(height: Int) {
//                isInput = true
//            }
//
//            override fun keyBoardHide(height: Int) {
//                isInput = false
//            }
//        })
//    }
}