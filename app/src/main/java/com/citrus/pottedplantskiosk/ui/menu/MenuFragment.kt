package com.citrus.pottedplantskiosk.ui.menu


import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.UsbInfo
import com.citrus.pottedplantskiosk.databinding.FragmentMenuBinding
import com.citrus.pottedplantskiosk.ui.menu.adapter.*
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.ACTION_USB_PERMISSION
import com.citrus.pottedplantskiosk.util.Constants.forEachReversedWithIndex
import com.citrus.pottedplantskiosk.util.UsbUtil
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import com.citrus.pottedplantskiosk.util.print.PrintOrderInfo
import com.google.android.material.snackbar.Snackbar
import com.skydoves.elasticviews.ElasticAnimation
import com.skydoves.transformationlayout.onTransformationStartContainer
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.transformer.AlphaPageTransformer
import com.youth.banner.util.BannerUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MenuFragment : BindingFragment<FragmentMenuBinding>() {
    private val menuViewModel: MenuViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentMenuBinding::inflate

    private var usbInfo = UsbInfo()
    private var snackbar: Snackbar? = null
    private var updateTimerJob: Job? = null

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
        refreshUsbDevice()
    }

    private fun refreshUsbDevice() {
        usbInfo = UsbUtil.getDevice(requireContext())

        requestUsbPermission()
    }

    private fun requestUsbPermission() {
        usbInfo.noPermissionDevice.forEachReversedWithIndex { i, usbDevice ->
            if (usbInfo.usbManager?.hasPermission(usbDevice) == false) {
                val mPermissionIntent = PendingIntent.getBroadcast(requireContext(), 0, Intent(ACTION_USB_PERMISSION), 0)
                usbInfo.usbManager?.requestPermission(usbDevice, mPermissionIntent)
            } else usbInfo.noPermissionDevice.removeAt(i)
        }

        Log.e("usbInfo",usbInfo.noPermissionDevice.toString())

        usbInfo.deviceList.forEach{ item ->
            Log.e("usbInfo",item.value.deviceName + "'-'" + item.value.productName)
        }

        usbInfo.noPermissionDevice
    }


    override fun initView() {
        binding.apply {
            root.background.alpha = 30
            cartMotionLayout.registerLifecycleOwner(lifecycle)

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
        }
    }

    override fun initObserve() {
        lifecycleScope.launchWhenStarted {
            menuViewModel.showBannerData.collect { banners ->
                binding.banner.adapter = ImageAdapter(banners)
                binding.banner.indicator = RectangleIndicator(activity)
                binding.banner.setIndicatorSpace(BannerUtils.dp2px(4f).toInt())
                binding.banner.setIndicatorRadius(0)
                binding.banner.start()
                binding.banner.setBannerGalleryEffect(50, 10)
                binding.banner.addPageTransformer(AlphaPageTransformer())
            }
        }



        lifecycleScope.launchWhenStarted {
            menuViewModel.currentCartGoods.collect { cartGoods ->
                if (cartGoods != null) {
                    binding.cartMotionLayout.addCartGoods(cartGoods)
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            menuViewModel.menuGroupName.collect { groupList ->
                updateMainGroupItemJob?.cancel()
                updateMainGroupItemJob = lifecycleScope.launch {
                    mainGroupItemAdapter.updateDataset(groupList)
                    binding.mainGroupRv.isVisible = true
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            menuViewModel.groupDescName.collect { result ->
                updateGroupItemJob?.cancel()
                updateGroupItemJob = lifecycleScope.launch {
                    groupItemAdapter.resetKindIndex()
                    groupItemAdapter.updateDataset(result)
                    binding.groupRv.scrollToPosition(0)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            menuViewModel.allGoods.collect { goodsList ->
                goodsItemAdapter.setGoodsList(goodsList)
                binding.goodsRv.scrollToPosition(0)
                binding.goodsRv.scheduleLayoutAnimation()
            }

        }

        lifecycleScope.launchWhenStarted {
            menuViewModel.showDetailEvent.collect { goods ->
                findNavController().navigate(
                    R.id.action_menuFragment_to_zoomPageFragment,
                    bundleOf("goods" to goods.deepCopy())
                )
            }
        }

        lifecycleScope.launchWhenStarted {
            menuViewModel.toPrint.collect { orderDeliveryData ->

                orderDeliveryData?.let {
                    binding.cartMotionLayout.clearCartGoods()
                    PrintOrderInfo(requireContext(), it,usbInfo.deviceList["/dev/bus/usb/002/014"]) { isSuccess, err ->

                    }.startPrint()

                    findNavController().navigate(
                        R.id.action_menuFragment_to_printFragment
                    )
                }
            }
        }


        lifecycleScope.launchWhenStarted {
            menuViewModel.tikTok.collect { timer ->
                if (timer == 0) {
                    releaseSnack()
                }

                if (timer == 100) {
                    var temp = 100
                    snackbar = Snackbar.make(requireView(), "", 20000)
                    val customSnackView: View =
                        layoutInflater.inflate(R.layout.custom_snackbar_view, null)
                    snackbar!!.view.setBackgroundColor(Color.TRANSPARENT)
                    val snackbarLayout = snackbar!!.view as Snackbar.SnackbarLayout
                    snackbarLayout.setPadding(0, 0, 0, 50)
                    val bGotoWebsite: Button = customSnackView.findViewById(R.id.gotoWebsiteButton)
                    val timerHint: TextView = customSnackView.findViewById(R.id.textView2)
                    updateTimerJob = lifecycleScope.launch {
                        while (temp < 120) {
                            delay(1000)
                            temp++
                            timerHint.text = (120 - temp).toString() + "秒後將返回主畫面"
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
                    menuViewModel.stopTimer()
                    binding.cartMotionLayout.releaseAdapter()
                    findNavController().popBackStack(R.id.mainFragment, false)
                }
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

        goodsItemAdapter.setOnGoodsClickListener { good, list ->
            menuViewModel.onGoodsClick(good, list)
        }

        binding.cartMotionLayout.setOnGoodsClickListener { goods ->
            findNavController().navigate(
                R.id.action_menuFragment_to_zoomPageFragment,
                bundleOf("goods" to goods)
            )
        }


        binding.cartMotionLayout.setOnPayButtonClickListener { list ->

            Log.e("list", list.toString())
        }

        binding.cartMotionLayout.setonOrderDoneListener { list, payWay ->
            menuViewModel.postOrderItem(list, payWay)
        }


    }

    private fun releaseSnack() {
        updateTimerJob?.cancel()
        updateTimerJob = null
        snackbar?.dismiss()
        snackbar = null
    }

}