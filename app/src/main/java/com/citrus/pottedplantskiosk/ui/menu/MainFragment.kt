package com.citrus.pottedplantskiosk.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.DataBean
import com.citrus.pottedplantskiosk.databinding.FragmentMainBinding
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.elasticviews.ElasticAnimation
import com.youth.banner.Banner
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import dagger.hilt.android.AndroidEntryPoint


import com.skydoves.balloon.BalloonSizeSpec.WRAP
import com.skydoves.balloon.createBalloon
import com.skydoves.balloon.overlay.BalloonOverlayAnimation
import com.skydoves.balloon.overlay.BalloonOverlayRect
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.citrus.pottedplantskiosk.di.prefs


@AndroidEntryPoint
class MainFragment : BindingFragment<FragmentMainBinding>() {
    private val menuViewModel: MenuViewModel by activityViewModels()
    lateinit var balloon: Balloon

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentMainBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBalloon()
    }

    override fun initView() {

        binding.apply {
            val anim: Animation = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 1000
            anim.startOffset = 20
            anim.repeatMode = Animation.REVERSE
            anim.repeatCount = Animation.INFINITE
            tvStart.startAnimation(anim)

            showBanner(
                binding.banner as Banner<DataBean, BannerImageAdapter<DataBean>>,
                DataBean.testData
            )

            touchStart.setOnClickListener { v ->
                ElasticAnimation(v)
                    .setScaleX(0.85f)
                    .setScaleY(0.85f)
                    .setDuration(100)
                    .setOnFinishListener {
                        balloon.showAlignTop(v)
                    }
                    .doAction()
            }
        }

        Log.e("initView", "notDefine")
    }

    override fun initObserve() {
        Log.e("initObserve", "notDefine")
    }

    override fun initAction() {
        Log.e("initAction", "notDefine")
    }

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
            setLifecycleOwner(lifecycleOwner)
            setIsVisibleOverlay(true)
            setOverlayColorResource(R.color.overlay)
            setOverlayPadding(5f)
            setOverlayShape(BalloonOverlayRect)
            setBalloonOverlayAnimation(BalloonOverlayAnimation.FADE)
            setDismissWhenOverlayClicked(false)
        }

        val btnEN = balloon.getContentView().findViewById<Button>(R.id.btnEN)
        val btnTW = balloon.getContentView().findViewById<Button>(R.id.btnTW)
        btnEN.setOnClickListener {
            prefs.languagePos = 2
            balloon.dismiss()
            intentToMenu()
        }
        btnTW.setOnClickListener {
            prefs.languagePos = 1
            balloon.dismiss()
            intentToMenu()
        }

    }

    private fun intentToMenu() {
        menuViewModel.intentNavigateToMenu()
        findNavController().navigate(R.id.action_mainFragment_to_menuFragment)
    }

    private fun showBanner(
        myBanner: Banner<DataBean, BannerImageAdapter<DataBean>>,
        imgList: List<DataBean>
    ) {
        myBanner.scrollTime = 300
        myBanner.setLoopTime(5000)
        var banner: Banner<DataBean, BannerImageAdapter<DataBean>> = myBanner
        banner.setAdapter(object : BannerImageAdapter<DataBean>(imgList) {
            override fun onBindView(
                holder: BannerImageHolder,
                data: DataBean,
                position: Int,
                size: Int
            ) {
                holder.imageView.scaleType = ImageView.ScaleType.FIT_XY

                Glide.with(holder.itemView)
                    .load(
                        ResourcesCompat.getDrawable(
                            requireContext().resources,
                            data.imageRes!!,
                            null
                        )
                    )
                    .into(holder.imageView)

                holder.imageView.setOnClickListener {
                    ElasticAnimation(binding.touchStart)
                        .setScaleX(0.85f)
                        .setScaleY(0.85f)
                        .setDuration(100)
                        .setOnFinishListener {
                            balloon.showAlignTop(binding.touchStart)
                        }
                        .doAction()
                }
            }
        }).addBannerLifecycleObserver(viewLifecycleOwner).indicator =
            CircleIndicator(requireContext())
    }

}