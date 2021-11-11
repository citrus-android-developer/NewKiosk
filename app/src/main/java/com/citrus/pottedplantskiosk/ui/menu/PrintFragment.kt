package com.citrus.pottedplantskiosk.ui.menu

import android.os.Bundle
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
import com.citrus.pottedplantskiosk.util.print.PrintOrderInfo
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrintFragment : BottomSheetDialogFragment() {
    private val menuViewModel: MenuViewModel by activityViewModels()

    private var _binding: FragmentPrintBinding? = null
    private val binding get() = _binding!!
    private val args: PrintFragmentArgs by navArgs()
    var job: Job? = null
    var data:TransactionData? = null

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
                return false
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

        when(data!!.state){
            is TransactionState.NetworkIssue -> {
                showError()
            }

            is TransactionState.PrinterNotFoundIssue -> {
                showError()
            }

            is TransactionState.WorkFine -> {
                printStart(data!!)
            }
        }
    }


    private fun showError() {
        binding.apply {
            menuViewModel.setPrintStatus(0)
            lottieHint.setAnimation("error.json")
            printing.isVisible = false
            hintArea.isVisible = true

            if(data!!.state == TransactionState.NetworkIssue){
                tvHint.text = "The order has failed, please contact the service staff"
            }else{
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

}