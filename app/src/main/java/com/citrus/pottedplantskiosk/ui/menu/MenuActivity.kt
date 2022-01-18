package com.citrus.pottedplantskiosk.ui.menu

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.citrus.pottedplantskiosk.api.remote.dto.BannerResponse
import com.citrus.pottedplantskiosk.api.remote.dto.Data
import com.citrus.pottedplantskiosk.databinding.ActivityMenuBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.i18n.LocaleHelper
import com.google.android.material.snackbar.Snackbar
import com.pos.sdklib.aidl.newprinter.AidlPrinterResultListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MenuActivity : AppCompatActivity() {
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var binding: ActivityMenuBinding

    lateinit var data:Data
    private var banner:BannerResponse? = null

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
        prefs.orderStr = ""
        var msg: String? = intent.getStringExtra("pos_message")

        msg?.let {
            prefs.orderStr = "true"
            //Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            Snackbar.make(binding.root,it,Snackbar.LENGTH_LONG).show()
            Log.e("onCreate msg from symLink", msg.toString())
        } ?:    Log.e("onCreate msg from symLink", "null")



//        val bundle = intent.extras
//         data = bundle?.getSerializable("data") as Data
//        var bannerData =  bundle?.getSerializable("banner")
//
//        bannerData?.let{
//            banner = it as BannerResponse
//        }
//
//        data?.let {
//            menuViewModel.showData(it)
//        }
//
//        banner?.let{
//            menuViewModel.showBanner(it)
//        }

        lifecycleScope.launchWhenStarted {
            menuViewModel.reLaunchActivity.collect {
                prefs.isNavigate = true
                val intent = Intent()
                intent.setClass(this@MenuActivity, MenuActivity::class.java)
//                val bundle = Bundle()
//                bundle.putSerializable("data", data)
//                bundle.putSerializable("banner", banner)
//                intent.putExtras(bundle)
                this@MenuActivity.startActivity(intent)
                finish()
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        menuViewModel.timeCount = 0
        return super.dispatchTouchEvent(ev)
    }
}