package com.citrus.pottedplantskiosk.ui.setting

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.databinding.FragmentSettingBinding
import com.citrus.pottedplantskiosk.databinding.FragmentZoomPageBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants.clickAnimation
import com.citrus.pottedplantskiosk.util.Constants.trimSpace
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skydoves.elasticviews.ElasticAnimation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        val view: FrameLayout = dialog?.findViewById(R.id.design_bottom_sheet)!!

        view.layoutParams.height = resources.getDimension(R.dimen.dp_500).toInt()
        val behavior = BottomSheetBehavior.from(view)
        behavior.peekHeight = resources.getDimension(R.dimen.dp_500).toInt()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        return object : BottomSheetDialog(requireContext(), theme) {
            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                val v = currentFocus
                if (v != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
                    v is AutoCompleteTextView
                ) {
                    val sourceCoordinates = IntArray(2)
                    v.getLocationOnScreen(sourceCoordinates)
                    val x = ev.rawX + v.getLeft() - sourceCoordinates[0]
                    val y = ev.rawY + v.getTop() - sourceCoordinates[1]
                    if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                        hideKeyboard(activity, v)
                    }
                }
                return super.dispatchTouchEvent(ev)
            }
        }
    }

    private fun hideKeyboard(activity: Activity?, v: View) {
        if (activity != null && activity.window != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        setFullScreen()
        initView()
        initAction()
    }


    private fun initView() {
        binding.apply {
            closeBtn.setOnClickListener { v ->
                v.clickAnimation {
                    findNavController().popBackStack()
                }
            }

            etServerIp.setText(prefs.serverIp)
            etStoreId.setText(prefs.storeId)

            idleSpinner.hint = prefs.idleTime.toString()
            taxSpinner.hint = prefs.taxFunction
            decimalSpinner.hint = prefs.decimalPlace.toString()
            operationSpinner.hint = prefs.methodOfOperation


            applyBtn.setOnClickListener { v ->
                v.clickAnimation {
                    var serverIp = etServerIp.text.toString().trimSpace()
                    var storeId = etStoreId.text.toString().trimSpace()

                    prefs.serverIp = serverIp
                    prefs.storeId = storeId

                    findNavController().popBackStack()
                }
            }




        }

    }

    private fun initAction() {
        binding.idleSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
            var idleTime = newItem
            prefs.idleTime = idleTime.toInt()
        }
        binding.taxSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
            var taxFunction = newItem
            prefs.taxFunction = taxFunction
        }
        binding.decimalSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
            var decimalType = newItem
            prefs.decimalPlace = decimalType.toInt()
        }
        binding.operationSpinner.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
            var methodOfOperation = newItem
            prefs.methodOfOperation = methodOfOperation
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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