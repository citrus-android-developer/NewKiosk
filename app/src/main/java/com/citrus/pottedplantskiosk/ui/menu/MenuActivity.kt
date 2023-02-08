package com.citrus.pottedplantskiosk.ui.menu

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.citrus.pottedplantskiosk.api.remote.dto.BannerResponse
import com.citrus.pottedplantskiosk.api.remote.dto.Data
import com.citrus.pottedplantskiosk.databinding.ActivityMenuBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.i18n.LocaleHelper
import com.citrus.pottedplantskiosk.util.phycicalScanner.ScanKeyManager
import com.citrus.pottedplantskiosk.util.phycicalScanner.SoftKeyBoardListener
import com.pos.sdklib.aidl.newprinter.AidlPrinterResultListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MenuActivity : AppCompatActivity() {
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var binding: ActivityMenuBinding

    lateinit var data: Data
    private var banner: BannerResponse? = null

    /**掃描槍回調支援*/
    private var isInput = false
    private lateinit var scanKeyManager: ScanKeyManager
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        data = bundle?.getSerializable("data") as Data
        var bannerData = bundle?.getSerializable("banner")

        scanKeyManager = ScanKeyManager(object : ScanKeyManager.OnScanValueListener {
            override fun onScanValue(value: String?) {
                value?.let {
                    onScanListener?.invoke(it)
                }
            }
        })

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

        setOnScanListener { scanValue ->
            if (!menuViewModel.isIdentify) {
                menuViewModel.isIdentify = true
                menuViewModel.setScanResult(scanValue)
            }
        }
    }

    fun setFullScreen() {
        val decorView = setSystemUiVisibilityMode()
        decorView.setOnSystemUiVisibilityChangeListener {
            setSystemUiVisibilityMode() // Needed to avoid exiting immersive_sticky when keyboard is displayed
        }
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
            scanKeyManager.analysisKeyEvent(event)
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    private fun onKeyBoardListener() {
        SoftKeyBoardListener(this).setListener(object :
            SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(height: Int) {
                isInput = true
            }

            override fun keyBoardHide(height: Int) {
                isInput = false
            }
        })
    }
}