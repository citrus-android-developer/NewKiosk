package com.citrus.pottedplantskiosk.ui.menu

import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.OrderDeliveryData
import com.citrus.pottedplantskiosk.api.remote.dto.TransactionData
import com.citrus.pottedplantskiosk.api.remote.dto.TransactionState
import com.citrus.pottedplantskiosk.api.remote.dto.payWayList
import com.citrus.pottedplantskiosk.databinding.FragmentPrintBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.getGstStr
import com.citrus.pottedplantskiosk.util.print.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pos.sdklib.aidl.newprinter.AidlNewPrinter
import com.pos.sdklib.aidl.newprinter.AidlPrinterResultListener
import com.pos.sdklib.aidl.newprinter.param.PrintItemAlign
import com.pos.sdklib.aidl.newprinter.param.TextPrintItemParam
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.StringBuilder

@AndroidEntryPoint
class PrintFragment : BottomSheetDialogFragment() {
    private val menuViewModel: MenuViewModel by activityViewModels()

    private var _binding: FragmentPrintBinding? = null
    private val binding get() = _binding!!
    private val args: PrintFragmentArgs by navArgs()
    var job: Job? = null
    var data: TransactionData? = null
    var payType = ""

    override fun onStart() {
        super.onStart()
        val view: FrameLayout = dialog?.findViewById(R.id.design_bottom_sheet)!!

        view.layoutParams.height = resources.getDimension(R.dimen.dp_400).toInt()
        val behavior = BottomSheetBehavior.from(view)
        behavior.peekHeight = resources.getDimension(R.dimen.dp_400).toInt()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        return object : BottomSheetDialog(requireContext(), theme) {
            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                return super.dispatchTouchEvent(ev)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrintBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreen()
        binding.printing.isVisible = true

        data = args.transaction

        when (data!!.state) {
            is TransactionState.NetworkIssue -> {
                showError()
            }

            is TransactionState.PrinterNotFoundIssue -> {
                showError()
            }

            is TransactionState.WorkFine -> {
                payType = data!!.orders!!.ordersDelivery.payType
                when {
                    data!!.printer != null -> {
                        printStart(data!!)
                    }
                    data!!.mNewPrinter != null -> {
                        symLinkPrint(data!!)
                    }
                    else -> {
                        showError()
                    }
                }
            }
        }
    }


    private fun showError() {
        binding.apply {
            menuViewModel.setPrintStatus(0)
            lottieHint.setAnimation("error.json")
            printing.isVisible = false
            hintArea.isVisible = true

            if (data!!.state == TransactionState.NetworkIssue) {
                tvHint.text = "The order has failed, please contact the service staff"
            } else {
                tvHint.text = "Please contact the service staff to confirm the problem"
            }

        }
    }

    private fun showSuccess(isCash: Boolean = false) {
        binding.apply {
            if (isCash) {
                menuViewModel.setPrintStatus(1)
            }
            lottieHint.setAnimation("success.json")
            printing.isVisible = false
            hintArea.isVisible = true
            tvHint.text = "Thank you for coming!"
        }

    }

    private fun symLinkPrint(data: TransactionData) {
        val slip = SlipModelUtils()
        var deliveryItemList = data.orders?.ordersItemDelivery
        var printer = data.mNewPrinter
        printer?.addTextPrintItem(
            slip.addTextOnePrint(
                deliveryItemList?.get(0)?.orderNO,
                true,
                40,
                PrintItemAlign.CENTER
            )
        )
        printTest(printer, data.orders)
    }


    private fun printStart(data: TransactionData) {
        PrintOrderInfo(
            requireContext(),
            data.orders!!,
            data.printer
        ) { isSuccess, err ->
            if (!isSuccess) {
                showError()
                return@PrintOrderInfo
            }
            showSuccess()
            job = MainScope().launch {
                delay(8000)
                findNavController().popBackStack(R.id.mainFragment, false)
            }
            job?.start()
        }.startPrint()
    }


    private val printerResultListener: AidlPrinterResultListener =
        object : AidlPrinterResultListener.Stub() {
            override fun onPrintFinish() {
                lifecycleScope.launch(Main) {
                    if (payType == payWayList()[1].desc) {
                        showSuccess(true)
                    } else {
                        showSuccess()
                    }
                    prefs.transactionData = ""
                    prefs.orderStr = ""
                }
            }

            override fun onPrintError(errorCode: Int, msg: String) {
                Log.e("Error", String.format("列印失敗, 錯誤碼 : %d, 錯誤訊息 : %s", errorCode, msg))
            }
        }

    private fun setFullScreen() {
        val decorView = setSystemUiVisibilityMode()
        decorView?.setOnSystemUiVisibilityChangeListener {
            setSystemUiVisibilityMode() // Needed to avoid exiting immersive_sticky when keyboard is displayed
        }
    }

    override fun onDestroyView() {
        job?.cancel()
        super.onDestroyView()
    }

    private fun setSystemUiVisibilityMode(): View? {

        val decorView = dialog?.window?.decorView
        val options = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        decorView?.systemUiVisibility = options
        return decorView
    }

    private fun printTest(mNewPrinter: AidlNewPrinter?, orders: OrderDeliveryData?) {
        val slip = SlipModelUtils()
        mNewPrinter?.let {

            if (prefs.header.isNotEmpty()) {
                it.addTextPrintItem(
                    slip.addTextOnePrint(
                        prefs.header,
                        true,
                        32,
                        PrintItemAlign.CENTER
                    )
                )
            }
            addLine(slip, mNewPrinter)

            if (prefs.kioskId.isNotEmpty()) {
                it.addTextPrintItem(
                    slip.addTextOnePrint(
                        "Take Away: " + prefs.kioskId,
                        false,
                        -1,
                        PrintItemAlign.LEFT
                    )
                )
            }

            if (prefs.storeName.isNotEmpty()) {
                it.addTextPrintItem(
                    slip.addTextOnePrint(
                        prefs.storeName,
                        false,
                        -1,
                        PrintItemAlign.LEFT
                    )
                )
            }

            if (prefs.storeAddress.isNotEmpty()) {
                it.addTextPrintItem(
                    slip.addTextOnePrint(
                        prefs.storeAddress,
                        false,
                        -1,
                        PrintItemAlign.LEFT
                    )
                )
            }

            it.addTextPrintItem(
                slip.addTextOnePrint(
                    requireContext().resources.getString(R.string.printTime) + Constants.getCurrentTime(),
                    false,
                    -1,
                    PrintItemAlign.LEFT
                )
            )
            addLine(slip, mNewPrinter)

            var sum = 0
            orders?.ordersItemDelivery?.forEach { item ->
                sum += item.qty
                it.addMultipleTextPrintItem(
                    slip.addLeftAndRightItem(
                        if (prefs.languagePos == 1) item.gName2 else item.gname,
                        " x " + item.qty.toString(),
                        null
                    )
                )
                it.addTextPrintItem(
                    slip.addTextOnePrint(
                        Constants.getValByMathWay(item.gPrice),
                        false,
                        -1,
                        PrintItemAlign.RIGHT
                    )
                )

                val flavorAdd =
                    if (!item.addGName.isNullOrEmpty() && !item.flavorDesc.isNullOrEmpty()) item.addGName + "/" + item.flavorDesc
                    else if (!item.addGName.isNullOrEmpty()) item.addGName
                    else if (!item.flavorDesc.isNullOrEmpty()) item.flavorDesc
                    else null

                flavorAdd?.let { flavor ->
                    it.addTextPrintItem(
                        slip.addTextOnePrint(
                            "    #$flavor",
                            false,
                            -1,
                            PrintItemAlign.RIGHT
                        )
                    )
                }
            }
            addLine(slip, mNewPrinter)

            var orgAmtStr =
                String.format("%7s", Constants.getValByMathWay(orders!!.ordersDelivery.sPrice))
            val qtyStr = String.format("%-3s", sum)
            val gst =
                String.format("%7s", Constants.getValByMathWay(orders!!.ordersDelivery.totaltax))
            val grandTotal =
                String.format(
                    "%7s",
                    Constants.getValByMathWay(orders!!.ordersDelivery.sPrice + orders!!.ordersDelivery.totaltax)
                )

            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    requireContext().getString(R.string.Total),
                    qtyStr + orgAmtStr,
                    null
                )
            )

            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    requireContext().getString(R.string.paymentType),
                    orders.ordersDelivery.payType,
                    null
                )
            )


            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    requireContext().getString(R.string.SubTotal),
                    orgAmtStr,
                    null
                )
            )

            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    requireContext().getGstStr(),
                    gst,
                    null
                )
            )


            if (prefs.taxFunction == 2) {
                it.addMultipleTextPrintItem(
                    slip.addLeftAndRightItem(
                        requireContext().getString(R.string.grandTotal),
                        grandTotal,
                        null
                    )
                )
            } else {
                it.addMultipleTextPrintItem(
                    slip.addLeftAndRightItem(
                        requireContext().getString(R.string.grandTotal),
                        orgAmtStr,
                        null
                    )
                )
            }

            if (prefs.footer.isNotEmpty()) {
                it.addTextPrintItem(
                    slip.addTextOnePrint(
                        prefs.footer,
                        true,
                        32,
                        PrintItemAlign.CENTER
                    )
                )
            }

            it.addTextPrintItem(addLines(2))
            try {
                val bundle = Bundle()
                //you can also set grayscale here, bundle.putInt(PrinterParamTag.TAG_PRINT_GRAY, 0x18);
                it.print(bundle, printerResultListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    @Throws(RemoteException::class)
    private fun addLine(slip: SlipModelUtils, mNewPrinter: AidlNewPrinter?) {
        mNewPrinter!!.addTextPrintItem(
            slip.addTextOnePrint(
                "--------------------------------------",
                true,
                -1,
                PrintItemAlign.CENTER
            )
        )
    }

    private fun addLines(lines: Int): TextPrintItemParam? {
        val sb = StringBuilder()
        val t1 = TextPrintItemParam()
        for (i in 0 until lines) {
            sb.append("\n")
        }
        t1.content = sb.toString()
        return t1
    }
}