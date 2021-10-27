package com.citrus.pottedplantskiosk.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.databinding.FragmentPrintBinding
import com.citrus.pottedplantskiosk.databinding.FragmentZoomPageBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrintFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPrintBinding? = null
    private val binding get() = _binding!!

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

    }

    private fun setFullScreen() {
        val decorView = setSystemUiVisibilityMode()
        decorView?.setOnSystemUiVisibilityChangeListener {
            setSystemUiVisibilityMode() // Needed to avoid exiting immersive_sticky when keyboard is displayed
        }
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