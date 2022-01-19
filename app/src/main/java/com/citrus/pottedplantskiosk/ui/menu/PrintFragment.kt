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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.TransactionData
import com.citrus.pottedplantskiosk.api.remote.dto.TransactionState
import com.citrus.pottedplantskiosk.databinding.FragmentPrintBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.print.PrintOrderInfo
import com.citrus.pottedplantskiosk.util.print.SlipModelUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pos.sdklib.aidl.newprinter.AidlNewPrinter
import com.pos.sdklib.aidl.newprinter.AidlPrinterResultListener
import com.pos.sdklib.aidl.newprinter.param.PrintItemAlign
import com.pos.sdklib.aidl.newprinter.param.TextPrintItemParam
import dagger.hilt.android.AndroidEntryPoint
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

    private fun showSuccess() {
        binding.apply {
            menuViewModel.setPrintStatus(1)
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
        printTest(printer)
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
                Log.e("job", "ready to start")
                delay(8000)
                findNavController().popBackStack(R.id.mainFragment, false)
            }
            job?.start()
        }.startPrint()
    }


    private val printerResultListener: AidlPrinterResultListener =
        object : AidlPrinterResultListener.Stub() {
            override fun onPrintFinish() {
                showSuccess()
                prefs.transactionData = ""
                prefs.orderStr = ""
                Log.e("列印完成", "---------")
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

    private fun printTest(mNewPrinter: AidlNewPrinter?) {
        val slip = SlipModelUtils()
        mNewPrinter?.let {
            it.addTextPrintItem(
                slip.addTextOnePrint(
                    "RECEIPT",
                    true,
                    32,
                    PrintItemAlign.CENTER
                )
            )
            addLine(slip,mNewPrinter)
            it.addTextPrintItem(
                slip.addTextOnePrint(
                    "MERCHANT NAME:",
                    false,
                    -1,
                    PrintItemAlign.LEFT
                )
            )
            it.addTextPrintItem(
                slip.addTextOnePrint(
                    "Company test merchant (Hong Kong) HKG",
                    false,
                    -1,
                    PrintItemAlign.LEFT
                )
            )
            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    "TRAIH：01.07.2013",
                    "SAAT:12：30",
                    null
                )
            )
            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    "TERMINAL NO.",
                    "00020004",
                    null
                )
            )
            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    "OPERATOR NO.",
                    "01",
                    null
                )
            )
            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    "ACQUIRER:",
                    "25350344",
                    null
                )
            )
            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    "ISSUER:",
                    "90880060",
                    null
                )
            )
            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    "CARD NO.",
                    "6214094******0008",
                    -1,
                    32,
                    floatArrayOf(1f, 3f)
                )
            )
            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    "EXP DATE:",
                    "3010",
                    null
                )
            )
            it.addTextPrintItem(
                slip.addTextOnePrint(
                    "TRANS TYPE:",
                    false,
                    -1,
                    PrintItemAlign.LEFT
                )
            )
            it.addTextPrintItem(
                slip.addTextOnePrint(
                    "SALE",
                    true,
                    36,
                    PrintItemAlign.RIGHT
                )
            )
            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    "BATCH NO.:",
                    "000114",
                    null
                )
            )
            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    "VOUCHER NO.",
                    "000060",
                    -1,
                    32,
                    floatArrayOf(1f, 1f)
                )
            )
            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    "REF NO.",
                    "200507443660",
                    -1,
                    32,
                    floatArrayOf(1f, 1f)
                )
            )
            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    "TRANS TIME.",
                    "2020/05/07 16:27:55",
                    null
                )
            )
            it.addMultipleTextPrintItem(
                slip.addLeftAndRightItem(
                    "AMOUNT",
                    "HKD 150.00",
                    32,
                    32,
                    null
                )
            )
            addLine(slip,mNewPrinter)
            it.addTextPrintItem(
                slip.addTextOnePrint(
                    "SIGNATURE:",
                    false,
                    16,
                    PrintItemAlign.LEFT
                )
            )
            it.addBitmapPrintItem(slip.addBitmapItem(requireContext(), "sign.png"))
            addLine(slip,mNewPrinter)
            it.addTextPrintItem(
                slip.addTextOnePrint(
                    "I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES",
                    false,
                    16,
                    PrintItemAlign.CENTER
                )
            )
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
    private fun addLine(slip: SlipModelUtils,mNewPrinter: AidlNewPrinter?) {
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