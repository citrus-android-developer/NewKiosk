package com.citrus.pottedplantskiosk.ui.menu

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.citrus.pottedplantskiosk.api.remote.dto.BannerResponse
import com.citrus.pottedplantskiosk.api.remote.dto.Data
import com.citrus.pottedplantskiosk.databinding.ActivityMenuBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.i18n.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MenuActivity : AppCompatActivity() {
    private val menuViewModel: MenuViewModel by viewModels()
    private lateinit var binding: ActivityMenuBinding

    lateinit var data:Data
    lateinit var banner:BannerResponse

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
         banner = bundle?.getSerializable("banner") as BannerResponse

        data?.let {
            menuViewModel.showData(it)
        }

        banner?.let{
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