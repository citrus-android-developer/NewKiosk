package com.citrus.pottedplantskiosk.ui.menu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.*
import com.citrus.pottedplantskiosk.databinding.FragmentPrintBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.CENTER
import com.citrus.pottedplantskiosk.util.Constants.LEFT
import com.citrus.pottedplantskiosk.util.Constants.LINE_SPACE
import com.citrus.pottedplantskiosk.util.Constants.LINE_SPACE_BLOCK
import com.citrus.pottedplantskiosk.util.Constants.LINE_SPACE_L
import com.citrus.pottedplantskiosk.util.Constants.PAGE_WIDTH
import com.citrus.pottedplantskiosk.util.Constants.RIGHT
import com.citrus.pottedplantskiosk.util.Constants.SEPARATE_MID
import com.citrus.pottedplantskiosk.util.Constants.SEPARATE_NORMAL
import com.citrus.pottedplantskiosk.util.Constants.SPACE
import com.citrus.pottedplantskiosk.util.Constants.TXT_L
import com.citrus.pottedplantskiosk.util.Constants.TXT_M
import com.citrus.pottedplantskiosk.util.Constants.TXT_S
import com.citrus.pottedplantskiosk.util.Constants.getGstStr
import com.citrus.pottedplantskiosk.util.base.onSafeClick
import com.citrus.pottedplantskiosk.util.navigateSafely
import com.citrus.pottedplantskiosk.util.print.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.pax.gl.page.PaxGLPage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PrintFragment : BottomSheetDialogFragment() {
    private val menuViewModel: MenuViewModel by activityViewModels()

    private var _binding: FragmentPrintBinding? = null
    private val binding get() = _binding!!
    private val args: PrintFragmentArgs by navArgs()
    private var idleJob: Job? = null
    var data: TransactionData? = null
    var payType = ""
    var printJob: Job? = null
    private lateinit var startActivityLauncher: ActivityResultLauncher<Intent>

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

        startActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.data != null && it.resultCode == Activity.RESULT_OK) {
                    it.data?.getStringExtra("EDC_RESPONSE")?.let { msg ->
                        var edcResponse = Gson().fromJson(msg, EdcData::class.java)

                        Log.e("test", edcResponse.toString())
                        if (edcResponse.ECR_Response_Code == "0000") {

                        }
                    }
                }
            }
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
    ): View {
        _binding = FragmentPrintBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFullScreen()
        binding.printing.isVisible = true

        data = args.transaction

        data?.let {
            when (it.state) {
                is TransactionState.NetworkIssue -> {
                    showError()
                }

                is TransactionState.PrinterNotFoundIssue -> {
                    showError()
                }

                is TransactionState.WorkFine -> {
                    payType = it.orders?.ordersDelivery?.payType ?: ""
                    when {
                        it.printer != null -> {
                            printStart(it)
                        }
                        else -> {
                            printJob?.cancel()
                            printJob = MainScope().launch {
                                withContext(IO) {
                                    a920PrintStart(it)
                                }
                            }
                            printJob?.start()
                        }
                    }
                }
            }
        }

        binding.tvBtn.onSafeClick {
            if (payType == payWayList()[0].desc) {
                var edcData = EdcData(Trans_Type = "11")

                val intent = Intent()
                intent.setClassName(
                    "com.cybersoft.a920",
                    "com.cybersoft.a920.activity.MainActivity"
                )
                val bundle = Bundle()
                bundle.putString(
                    "POS_REQUEST",
                    Gson().toJson(edcData)
                )
                intent.putExtras(bundle)
                startActivityLauncher.launch(intent)
            } else {
                printJob?.cancel()
                printJob = MainScope().launch {
                    withContext(IO) {
                        a920PrintStart(data!!)
                    }
                }
                printJob?.start()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            A920Printer.printerEvent.collect {
                when (it.result) {
                    PrinterState.DONE -> {
                        binding.tvBtn.visibility = View.GONE
                        showSuccess()
                        idleJob?.cancel()
                        idleJob = MainScope().launch {
                            delay(8000)
                            findNavController().popBackStack(R.id.mainFragment, false)
                        }
                        idleJob?.start()
                    }
                    PrinterState.OUT_OF_PAPER, PrinterState.LOW_BATTERY -> {
                        showAlert(it.errorMsg)
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
            tvBtn.isVisible = false
            menuViewModel.setPrintStatus(1)
            lottieHint.setAnimation("success.json")
            printing.isVisible = false
            hintArea.isVisible = true
            tvHint.text = "Thank you for coming!"
        }

    }

    private fun showAlert(msg: String) {
        binding.apply {
            tvBtn.isVisible = true
            printing.isVisible = false
            hintArea.isVisible = true
            lottieHint.setAnimation("error.json")
            tvHint.text = msg
        }
    }


    private fun a920PrintStart(data: TransactionData) {
        val paxGLPage =
            PaxGLPage.getInstance(requireContext())
        val iPage = paxGLPage.createPage()

        val deliveryInfo = data.orders!!
        var deliveryItemList = deliveryInfo.ordersItemDelivery

        A920Printer.init()
        A920Printer.step(LINE_SPACE_L)
        //LEFT
        iPage.addLine().addUnit(SPACE, LINE_SPACE, LEFT)
        iPage.addLine().addUnit(deliveryInfo.ordersItemDelivery[0].orderNO, TXT_L, CENTER)
        if (prefs.header.isNotEmpty()) {
            iPage.addLine().addUnit(SPACE, LINE_SPACE, LEFT)
            iPage.addLine().addUnit(prefs.header, TXT_M, CENTER)
        }
        if (prefs.kioskId.isNotEmpty()) {
            iPage.addLine().addUnit(SPACE, LINE_SPACE, LEFT)
            iPage.addLine().addUnit("Take Away: " + prefs.kioskId, TXT_M, LEFT)
        }
        if (prefs.storeName.isNotEmpty()) {
            iPage.addLine().addUnit(SPACE, LINE_SPACE, LEFT)
            iPage.addLine().addUnit(prefs.storeName, TXT_M, LEFT)
        }
        if (prefs.storeAddress.isNotEmpty()) {
            iPage.addLine().addUnit(prefs.storeAddress, TXT_M, LEFT)
        }
        iPage.addLine().addUnit(SPACE, LINE_SPACE, LEFT)
        iPage.addLine().addUnit(
            resources.getString(R.string.printTime) + Constants.getCurrentTime(),
            TXT_M,
            LEFT
        )

        iPage.addLine().addUnit(SEPARATE_MID, TXT_M, CENTER)

        var sum = 0
        for (item in deliveryItemList) {
            sum += item.qty

            var itemTitle = if (prefs.languagePos == 1) item.gName2 else item.gname

            iPage.addLine().addUnit(SPACE, LINE_SPACE, LEFT)
            iPage.addLine().addUnit(itemTitle, TXT_M, LEFT)
                .addUnit(item.qty.toString(), TXT_M, CENTER)
                .addUnit(Constants.getValByMathWay(item.gPrice), TXT_M, RIGHT)


            val flavorAdd =
                if (!item.addGName.isNullOrEmpty() && !item.flavorDesc.isNullOrEmpty()) item.addGName + "/" + item.flavorDesc
                else if (!item.addGName.isNullOrEmpty()) item.addGName
                else if (!item.flavorDesc.isNullOrEmpty()) item.flavorDesc
                else null


            flavorAdd?.let {
                iPage.addLine().addUnit(SPACE, LINE_SPACE, LEFT)
                iPage.addLine().addUnit("    #$it", TXT_S, LEFT)
            }
        }


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

        iPage.addLine().addUnit(resources.getString(R.string.Total), TXT_M, RIGHT)
            .addUnit(qtyStr, TXT_M, CENTER).addUnit(
                orgAmtStr, TXT_M, LEFT
            )

        iPage.addLine().addUnit(SEPARATE_MID, TXT_M, CENTER)


        iPage.addLine().addUnit(SPACE, LINE_SPACE, LEFT)
        iPage.addLine().addUnit(resources.getString(R.string.paymentType), TXT_M, LEFT)
            .addUnit(deliveryInfo.ordersDelivery.payType, TXT_M, LEFT)
        iPage.addLine().addUnit(resources.getString(R.string.SubTotal), TXT_M, LEFT)
            .addUnit(orgAmtStr, TXT_M, LEFT)
        iPage.addLine().addUnit(requireContext().getGstStr(), TXT_M, LEFT).addUnit(gst, TXT_M, LEFT)

        if (prefs.taxFunction == 2) {
            iPage.addLine().addUnit(resources.getString(R.string.grandTotal), TXT_L, LEFT)
                .addUnit(grandTotal, TXT_L, LEFT)
        } else {
            iPage.addLine().addUnit(resources.getString(R.string.grandTotal), TXT_L, LEFT)
                .addUnit(orgAmtStr, TXT_L, LEFT)
        }
        if (prefs.footer.isNotEmpty()) {
            iPage.addLine().addUnit(SEPARATE_NORMAL, TXT_M, CENTER)
            iPage.addLine().addUnit(prefs.footer, TXT_M, CENTER)
        }

        var printData = paxGLPage.pageToBitmap(iPage, PAGE_WIDTH)
        A920Printer.printBitmap(printData)
        A920Printer.step(LINE_SPACE_BLOCK)
        A920Printer.start()
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
            idleJob = MainScope().launch {
                delay(8000)
                findNavController().popBackStack(R.id.mainFragment, false)
            }
            idleJob?.start()
        }.startPrint()
    }


    private fun setFullScreen() {
        val decorView = setSystemUiVisibilityMode()
        decorView?.setOnSystemUiVisibilityChangeListener {
            setSystemUiVisibilityMode() // Needed to avoid exiting immersive_sticky when keyboard is displayed
        }
    }

    override fun onDestroyView() {
        idleJob?.cancel()
        printJob?.cancel()
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
}