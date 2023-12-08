package com.citrus.pottedplantskiosk.ui.slash



import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.Resource
import com.citrus.pottedplantskiosk.api.remote.dto.BannerResponse
import com.citrus.pottedplantskiosk.api.remote.dto.MenuBean
import com.citrus.pottedplantskiosk.databinding.ActivitySlashBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.ui.menu.MenuActivity
import com.citrus.pottedplantskiosk.ui.slash.dialog.MsgAlertDialog
import com.citrus.pottedplantskiosk.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SlashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySlashBinding
    private val viewModel: SlashViewModel by viewModels()

    private var menuData: MenuBean? = null
    private var bannerData: BannerResponse? = null

    override fun onResume() {
        super.onResume()
        setFullScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val size = Point()
        val display = windowManager.defaultDisplay
        display.getSize(size)
        /**儲存螢幕size*/
        Constants.screenW = size.x
        Constants.screenH = size.y

        initObserve()

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        var msg: String? = intent?.getStringExtra("pos_message")

        prefs.orderStr = ""
        msg?.let {
            prefs.orderStr = "true"
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        } ?: Log.e("onNewIntent msg from symLink", "null")
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

                    is Resource.Error -> {
                        viewModel.fetchError()
                        delay(2000)
                        var dialog = MsgAlertDialog(this@SlashActivity, result.message!!)
                        dialog?.show()
                        dialog?.window?.setLayout(
                            (Constants.screenW * 0.8).toInt(),
                            (Constants.screenH * 0.1).toInt()
                        )
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.allBannerData.collect { result ->
                bannerData = result
                checkEachFun()
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


    private fun checkEachFun() {
        if (menuData != null) {
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
