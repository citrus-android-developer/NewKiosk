package com.citrus.pottedplantskiosk.ui.menu


import android.animation.Animator
import android.animation.ValueAnimator
import android.app.PendingIntent
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.TransactionData
import com.citrus.pottedplantskiosk.api.remote.dto.TransactionState
import com.citrus.pottedplantskiosk.api.remote.dto.UsbInfo
import com.citrus.pottedplantskiosk.databinding.FragmentMenuBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.ui.menu.adapter.*
import com.citrus.pottedplantskiosk.ui.setting.adapter.UsbNameWithID
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.ACTION_USB_PERMISSION
import com.citrus.pottedplantskiosk.util.Constants.clickAnimation
import com.citrus.pottedplantskiosk.util.Constants.forEachReversedWithIndex
import com.citrus.pottedplantskiosk.util.EcrProtocol
import com.citrus.pottedplantskiosk.util.UsbUtil
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import com.citrus.pottedplantskiosk.util.base.lifecycleFlow
import com.citrus.pottedplantskiosk.util.base.onSafeClick
import com.citrus.pottedplantskiosk.util.navigateSafely
import com.citrus.pottedplantskiosk.util.print.PrintOrderInfo
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.pos.sdklib.aidl.newprinter.AidlNewPrinter
import com.pos.sdklib.util.PosSdkUtils
import com.skydoves.elasticviews.ElasticAnimation
import com.skydoves.transformationlayout.onTransformationStartContainer
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.transformer.AlphaPageTransformer
import com.youth.banner.util.BannerUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.goods_item_view.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MenuFragment : BindingFragment<FragmentMenuBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentMenuBinding::inflate

    private val menuViewModel: MenuViewModel by activityViewModels()
    private var usbInfo = UsbInfo()
    private var snackbar: Snackbar? = null
    private var updateTimerJob: Job? = null
    private var currentClickView: View? = null
    private var dealingTransactionData: TransactionData? = null

    @Inject
    lateinit var mainGroupItemAdapter: MainGroupItemAdapter
    private var updateMainGroupItemJob: Job? = null

    @Inject
    lateinit var groupItemAdapter: GroupItemAdapter
    private var updateGroupItemJob: Job? = null

    @Inject
    lateinit var goodsItemAdapter: GoodsItemAdapter


    override fun onStop() {
        super.onStop()
        binding.banner.stop()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onTransformationStartContainer()
        menuViewModel.intentNavigateToMenu()
        refreshUsbDevice()
    }


    private fun refreshUsbDevice() {
        usbInfo = UsbUtil.getDevice(requireContext())
        requestUsbPermission()
    }

    private fun requestUsbPermission() {
        usbInfo.noPermissionDevice.forEachReversedWithIndex { i, usbDevice ->
            if (usbInfo.usbManager?.hasPermission(usbDevice) == false) {
                val mPermissionIntent = PendingIntent.getBroadcast(
                    requireContext(),
                    0,
                    Intent(ACTION_USB_PERMISSION),
                    0
                )
                usbInfo.usbManager?.requestPermission(usbDevice, mPermissionIntent)
            } else usbInfo.noPermissionDevice.removeAt(i)
        }
    }


    override fun initView() {
        prefs.isNavigate = false
        binding.apply {
            root.background.alpha = 30
            cartMotionLayout.registerLifecycleOwner(viewLifecycleOwner.lifecycle)

            mainGroupRv.apply {
                layoutManager =
                    LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
                adapter = mainGroupItemAdapter
            }

            mainGroupRv.visibility = View.GONE

            groupRv.apply {
                layoutManager =
                    LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
                adapter = groupItemAdapter
            }

            goodsRv.apply {
                layoutManager = GridLayoutManager(requireActivity(), 3)
                adapter = goodsItemAdapter
            }


            homeBtn.onSafeClick {
                it.clickAnimation {
                    backToMain()
                }
            }
        }
    }

    override fun initObserve() {
        lifecycleFlow(menuViewModel.showBannerData) { banners ->
            if (banners.isEmpty()) {
                binding.banner.isVisible = false
                binding.bannerBackground.isVisible = true
                return@lifecycleFlow
            }
            binding.banner.isVisible = true
            binding.bannerBackground.isVisible = false
            binding.banner.adapter = ImageAdapter(banners)
            binding.banner.indicator = RectangleIndicator(activity)
            binding.banner.setIndicatorSpace(BannerUtils.dp2px(4f).toInt())
            binding.banner.setIndicatorRadius(0)
            binding.banner.start()
            binding.banner.setBannerGalleryEffect(50, 10)
            binding.banner.addPageTransformer(AlphaPageTransformer())
        }


        lifecycleFlow(menuViewModel.zoomPageSlidePos) { pos ->
            binding.goodsRv.scrollToPosition(pos)
        }


        lifecycleFlow(menuViewModel.currentCartGoods) { cartGoods ->
            if (cartGoods != null) {
                /**非已編輯物件才有購物車動畫*/
                if (cartGoods.isEdit.not()) {
                    var item =
                        menuViewModel.currentDetailGoodsList.first { it.gID == cartGoods.gID && it.gKID == it.gKID }
                    var pos = menuViewModel.currentDetailGoodsList.indexOf(item)
                    currentClickView =
                        binding.goodsRv.findViewHolderForAdapterPosition(pos)?.itemView
                    currentClickView?.let {
                        addGoodToCarAnimation(it)
                    }
                }
                binding.cartMotionLayout.addCartGoods(cartGoods)
            }
        }


        lifecycleFlow(menuViewModel.menuGroupName) { groupList ->
            updateMainGroupItemJob?.cancel()
            updateMainGroupItemJob = lifecycleScope.launch {
                mainGroupItemAdapter.updateDataset(groupList)
                binding.mainGroupRv.isVisible = true
            }
        }


        lifecycleFlow(menuViewModel.groupDescName) { result ->
            updateGroupItemJob?.cancel()
            updateGroupItemJob = lifecycleScope.launch {
                groupItemAdapter.resetKindIndex()
                groupItemAdapter.updateDataset(result)
                binding.groupRv.scrollToPosition(0)
                binding.groupRvArea.isVisible = true
            }
        }


        lifecycleFlow(menuViewModel.allGoods) { goodsList ->
            goodsItemAdapter.setGoodsList(goodsList)
            binding.goodsRv.scrollToPosition(0)
            binding.goodsRv.scheduleLayoutAnimation()
        }



        lifecycleFlow(menuViewModel.showDetailEvent) { goods ->
            findNavController().navigateSafely(
                R.id.action_menuFragment_to_zoomPageFragment,
                args = bundleOf("goods" to goods.deepCopy())
            )
        }



        lifecycleFlow(menuViewModel.clearCartGoods) {
            binding.cartMotionLayout.clearCartGoods()
        }



        lifecycleFlow(menuViewModel.toPrint) { transactionData ->

            if (usbInfo.deviceList.isEmpty()) {
                transactionData.state = TransactionState.PrinterNotFoundIssue
            }

            var item = usbInfo.deviceList.map {
                UsbNameWithID(
                    it.value.deviceName,
                    it.value.productId
                )
            }.find { it.id.toString() == prefs.printer }


            item?.let {
                transactionData.printer = usbInfo.deviceList[item.name]
            } ?: run {
                    transactionData.state = TransactionState.PrinterNotFoundIssue
            }

            dealingTransactionData = transactionData

            findNavController().navigateSafely(
                R.id.action_menuFragment_to_printFragment,
                args = bundleOf("transaction" to transactionData)
            )
        }


        lifecycleFlow(menuViewModel.tikTok) { timer ->
            if (timer == 0) {
                releaseSnack()
            }

            if (timer == 100) {
                var temp = 100
                snackbar = Snackbar.make(requireView(), "", 20000)
                val customSnackView: View = layoutInflater.inflate(R.layout.custom_snackbar_view, null)
                snackbar!!.view.setBackgroundColor(Color.TRANSPARENT)
                val snackbarLayout = snackbar!!.view as Snackbar.SnackbarLayout
                snackbarLayout.setPadding(0, 0, 0, 50)
                val bGotoWebsite: Button =
                    customSnackView.findViewById(R.id.gotoWebsiteButton)
                val timerHint: TextView = customSnackView.findViewById(R.id.textView2)
                updateTimerJob = lifecycleScope.launch {
                    while (temp < 120) {
                        delay(1000)
                        temp++
                        timerHint.text = getString(R.string.idleHint, (120 - temp))
                    }
                }

                bGotoWebsite.setOnClickListener { v ->
                    ElasticAnimation(v)
                        .setScaleX(0.85f)
                        .setScaleY(0.85f)
                        .setDuration(100)
                        .setOnFinishListener {
                            releaseSnack()
                        }
                        .doAction()
                }
                snackbarLayout.addView(customSnackView, 0)
                snackbar!!.show()
            }

            if (timer == Constants.TWO_MINUTES) {
                backToMain()
            }
        }
    }



    override fun initAction() {
        mainGroupItemAdapter.setOnMainGroupNameClickListener { menuGroupName ->
            menuViewModel.onGroupChange(menuGroupName)
        }

        groupItemAdapter.setOnDescClickListener { desc ->
            menuViewModel.onDescChange(desc)
        }

        goodsItemAdapter.setOnGoodsClickListener { good, list, _ ->
            menuViewModel.onGoodsClick(good, list)
        }

        binding.cartMotionLayout.apply {
            setOnGoodsClickListener { goods ->
                findNavController().navigate(
                    R.id.action_menuFragment_to_zoomPageFragment,
                    bundleOf("goods" to goods)
                )
            }

            setonOrderDoneListener { deliveryInfo ->
                menuViewModel.postOrderItem(deliveryInfo)
            }
        }
    }

    private fun backToMain() {
        menuViewModel.stopTimer()
        binding.cartMotionLayout.releaseAdapter()
        findNavController().popBackStack(R.id.mainFragment, false)
    }

    private fun releaseSnack() {
        updateTimerJob?.cancel()
        updateTimerJob = null
        snackbar?.dismiss()
        snackbar = null
    }

    private fun addGoodToCarAnimation(goodView: View) {
        val mCurrentPosition = FloatArray(2)

        val view = View.inflate(requireContext(), R.layout.goods_item_view, null)
        view.itemImage.setImageDrawable(goodView.itemImage.drawable)
        view.itemImage.scaleType = ImageView.ScaleType.CENTER_CROP
        view.itemImage.layoutParams =
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 100)
        view.tvItemName.text = goodView.tvItemName.text
        view.tvItemName.textSize = 9f
        view.tvItemName.setPadding(0)
        view.tvPrice.visibility = View.GONE

        val layoutParams = FrameLayout.LayoutParams(150, 150)
        binding.outSideCons.addView(view, layoutParams)

        //二、計算動畫開始/結束點的坐標的準備工作
        //得到父佈局的起始點坐標（用於輔助計算動畫開始/結束時的點的坐標）
        val parentLoc = IntArray(2)
        binding.outSideCons.getLocationInWindow(parentLoc)

        //得到商品圖片的坐標（用於計算動畫開始的坐標）
        val startLoc = IntArray(2)
        goodView.getLocationInWindow(startLoc)

        //得到購物車圖片的坐標(用於計算動畫結束後的坐標)
        val endLoc = IntArray(2)
        binding.flyItemTarget.getLocationInWindow(endLoc)
        val startX = startLoc[0] - parentLoc[0] + goodView.width / 2.toFloat()
        val startY = startLoc[1] - parentLoc[1] + goodView.height / 2.toFloat()

        //商品掉落後的終點坐標：購物車起始點-父佈局起始點+購物車圖片的1/5
        val toX = endLoc[0] - parentLoc[0] + binding.flyItemTarget.width / 5.toFloat()
        val toY = endLoc[1] - parentLoc[1].toFloat()
        //開始繪製貝塞爾曲線
        val path = Path()
        path.moveTo(startX, startY)
        //使用二次薩貝爾曲線：注意第一個起始坐標越大，貝塞爾曲線的橫向距離就會越大，一般按照下面的式子取即可
        path.quadTo((startX + toX) / 2, startY, toX, toY)
        val mPathMeasure = PathMeasure(path, false)
        //屬性動畫
        val valueAnimator = ValueAnimator.ofFloat(0f, mPathMeasure.length)
        valueAnimator.duration = 500
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            mPathMeasure.getPosTan(value, mCurrentPosition, null)
            view.translationX = mCurrentPosition[0]
            view.translationY = mCurrentPosition[1]
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                YoYo.with(Techniques.ZoomOut).pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .duration(1000).playOn(view)
            }

            override fun onAnimationEnd(animation: Animator) {
                binding.cartMotionLayout.setCartItemSizePulse()
                //把移動的圖片view從父佈局裡移除
                binding.outSideCons.removeView(view)
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        valueAnimator.start()
    }

}