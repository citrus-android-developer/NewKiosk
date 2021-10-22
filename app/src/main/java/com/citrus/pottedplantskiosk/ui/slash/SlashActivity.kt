package com.citrus.pottedplantskiosk.ui.slash


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.Resource
import com.citrus.pottedplantskiosk.api.remote.dto.BannerData
import com.citrus.pottedplantskiosk.api.remote.dto.BannerResponse
import com.citrus.pottedplantskiosk.api.remote.dto.Data
import com.citrus.pottedplantskiosk.databinding.ActivitySlashBinding
import com.citrus.pottedplantskiosk.databinding.LayoutMotionBinding
import com.citrus.pottedplantskiosk.ui.menu.MenuActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SlashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySlashBinding
    private val viewModel: SlashViewModel by viewModels()

    private var menuData: Data? = null
    private var bannerData: BannerResponse? = null

    override fun onResume() {
        super.onResume()
        setFullScreen()
        YoYo.with(Techniques.Landing).pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT).duration(1500)
            .playOn(binding.ivLogo)
        YoYo.with(Techniques.Landing).pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT).duration(1500)
            .playOn(binding.tvKiosk)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initObserve()
        startTimer()
    }

    private fun initObserve() {
        lifecycleScope.launchWhenStarted {
            viewModel.allMenuData.collect { result ->
                when (result) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        menuData = result.data
                        checkEachFun()
                    }
                    is Resource.Error -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.allBannerData.collect { result ->
                when (result) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        bannerData = result.data
                        checkEachFun()
                    }
                    is Resource.Error -> {
                        Log.e("banner",result.message.toString())
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.allData.collect {
                this@SlashActivity.overridePendingTransition(
                    R.anim.nav_default_enter_anim,
                    R.anim.nav_default_exit_anim
                )
                val intent = Intent()
                intent.setClass(this@SlashActivity, MenuActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("data", menuData)
                bundle.putSerializable("banner", bannerData)
                intent.putExtras(bundle)
                this@SlashActivity.startActivity(intent)
                finish()
            }
        }
    }

    private fun startTimer() {
        val timer = object : Thread() {
            override fun run() {
                try {
                    sleep(1500)
                    viewModel.asyncTask()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        timer.start()
    }

    private fun checkEachFun() {
        if (menuData != null && bannerData != null) {
            viewModel.intentNext()
        }
    }

    private fun setFullScreen() {
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
}
