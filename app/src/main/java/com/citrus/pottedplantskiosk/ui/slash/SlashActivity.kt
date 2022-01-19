package com.citrus.pottedplantskiosk.ui.slash


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.Resource
import com.citrus.pottedplantskiosk.api.remote.dto.BannerResponse
import com.citrus.pottedplantskiosk.api.remote.dto.Data
import com.citrus.pottedplantskiosk.databinding.ActivitySlashBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.ui.menu.MenuActivity
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs.orderStr = ""
        var msg: String? = intent.getStringExtra("pos_message")

        msg?.let {
            prefs.orderStr = "true"
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        } ?:    Log.e("onCreate msg from symLink", "null")
        initObserve()

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        var msg: String? = intent?.getStringExtra("pos_message")

        prefs.orderStr = ""
        msg?.let {
            prefs.orderStr = "true"
        } ?:    Log.e("onNewIntent msg from symLink", "null")
    }

    private fun initObserve() {
        lifecycleScope.launchWhenStarted {
            viewModel.allMenuData.collect { result ->
                when (result) {
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        menuData = result.data?.data
                        checkEachFun()
                    }
                    is Resource.Error -> {
                        Toast.makeText(baseContext,result.message!!,Toast.LENGTH_LONG).show()
                        viewModel.fetchError()
                    }
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
                        checkEachFun()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.allData.collect {
//                this@SlashActivity.overridePendingTransition(
//                    R.anim.nav_default_enter_anim,
//                    R.anim.nav_default_exit_anim
//                )
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
