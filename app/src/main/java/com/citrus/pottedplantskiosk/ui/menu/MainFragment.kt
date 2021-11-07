package com.citrus.pottedplantskiosk.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.databinding.FragmentMainBinding
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import dagger.hilt.android.AndroidEntryPoint


import com.skydoves.balloon.BalloonSizeSpec.WRAP
import com.skydoves.balloon.overlay.BalloonOverlayAnimation
import com.skydoves.balloon.overlay.BalloonOverlayRect
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.citrus.pottedplantskiosk.api.remote.dto.BannerData
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.clickAnimation
import com.citrus.pottedplantskiosk.util.base.onSevenClick
import com.citrus.pottedplantskiosk.util.navigateSafely
import com.skydoves.balloon.*
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class MainFragment : BindingFragment<FragmentMainBinding>() {
    private val menuViewModel: MenuViewModel by activityViewModels()
    lateinit var balloon: Balloon

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentMainBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBalloon()
        menuViewModel.stopTimer()
    }

    override fun initView() {

        binding.apply {
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 1000
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.INFINITE
            tvStart.startAnimation(anim)


            touchStart.setOnClickListener {
                it.clickAnimation {
                    balloon.showAlignTop(it)
                }
            }

            logo.onSevenClick {
                findNavController().navigateSafely(
                    R.id.action_mainFragment_to_settingFragment,
                    bundleOf("isFromSlash" to false)
                )
            }
        }
    }

    override fun initObserve() {
        lifecycleScope.launchWhenStarted {
            menuViewModel.showBannerData.collect { banners ->
                showBanner(
                    binding.banner as Banner<BannerData, BannerImageAdapter<BannerData>>,
                    banners
                )
            }
        }
    }

    override fun initAction() = Unit

    private fun initBalloon() {
        balloon = createBalloon(requireContext()) {
            setLayout(R.layout.dialog_language)
            setArrowSize(15)
            setArrowOrientation(ArrowOrientation.TOP)
            setWidth(WRAP)
            setHeight(WRAP)
            setCornerRadius(10f)
            setElevation(5)
            setAlpha(0.9f)
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            setLifecycleOwner(viewLifecycleOwner)
            setIsVisibleOverlay(true)
            setOverlayColorResource(R.color.overlay)
            setOverlayPadding(5f)
            setOverlayShape(BalloonOverlayRect)
            setBalloonOverlayAnimation(BalloonOverlayAnimation.FADE)
            setDismissWhenOverlayClicked(false)
        }

        val btnEN = balloon.getContentView().findViewById<Button>(R.id.btnEN)
        val btnTW = balloon.getContentView().findViewById<Button>(R.id.btnTW)
        btnEN?.setOnClickListener {
            prefs.languagePos = 2
            balloon.dismiss()
            intentToMenu()
        }
        btnTW?.setOnClickListener {
            prefs.languagePos = 1
            balloon.dismiss()
            intentToMenu()
        }

    }

    private fun intentToMenu() {
        menuViewModel.intentNavigateToMenu()
        findNavController().navigateSafely(R.id.action_mainFragment_to_menuFragment)
    }

    private fun showBanner(
        myBanner: Banner<BannerData, BannerImageAdapter<BannerData>>,
        imgList: List<BannerData>
    ) {
        myBanner.scrollTime = 300
        myBanner.setLoopTime(5000)
        var banner: Banner<BannerData, BannerImageAdapter<BannerData>> = myBanner
        banner.setAdapter(object : BannerImageAdapter<BannerData>(imgList) {
            override fun onBindView(
                holder: BannerImageHolder,
                data: BannerData,
                position: Int,
                size: Int
            ) {
                holder.imageView.scaleType = ImageView.ScaleType.FIT_CENTER

                Glide.with(holder.itemView)
                    .load(Constants.IMG_URL + data.pic)
                    .into(holder.imageView)

                holder.imageView.setOnClickListener {
                    binding.touchStart.clickAnimation {
                        balloon.showAlignTop(binding.touchStart)
                    }
                }
            }
        }).addBannerLifecycleObserver(viewLifecycleOwner).indicator =
            CircleIndicator(requireContext())

    }
}