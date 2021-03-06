package com.citrus.pottedplantskiosk.ui.slash


import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.databinding.FragmentSlashBinding
import com.citrus.pottedplantskiosk.ui.setting.SettingFragment
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import com.citrus.pottedplantskiosk.util.base.lifecycleFlow
import com.citrus.pottedplantskiosk.util.base.onSevenClick
import com.citrus.pottedplantskiosk.util.navigateSafely
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SlashFragment : BindingFragment<FragmentSlashBinding>() {

    private val viewModel: SlashViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentSlashBinding::inflate

    override fun initView() {
        binding.apply {
            YoYo.with(Techniques.Landing).pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT).duration(1500)
                .playOn(ivLogo)
            YoYo.with(Techniques.Landing).pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT).duration(1500)
                .playOn(tvKiosk)

            startTimer()
        }
    }

    override fun initObserve() {
        lifecycleFlow(viewModel.errorNotify) {
            val dialog = SettingFragment(true)
            dialog.show(childFragmentManager, "SettingFragment")
        }
    }

    override fun initAction() {
        binding.ivLogo.onSevenClick {
            val dialog = SettingFragment(true)
            dialog.show(childFragmentManager, "SettingFragment")
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


}