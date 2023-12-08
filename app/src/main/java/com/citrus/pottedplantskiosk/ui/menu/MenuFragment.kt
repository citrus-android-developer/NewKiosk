package com.citrus.pottedplantskiosk.ui.menu


import android.animation.Animator
import android.animation.ValueAnimator
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Path
import android.graphics.PathMeasure
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.material3.MaterialTheme
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.alibaba.fastjson.JSON
import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.api.remote.dto.CreditInfo
import com.citrus.pottedplantskiosk.api.remote.dto.OrderDeliveryData
import com.citrus.pottedplantskiosk.api.remote.dto.PayModel
import com.citrus.pottedplantskiosk.api.remote.dto.TransBuilder
import com.citrus.pottedplantskiosk.api.remote.dto.TransRequestData
import com.citrus.pottedplantskiosk.api.remote.dto.TransResponse
import com.citrus.pottedplantskiosk.api.remote.dto.TransactionData
import com.citrus.pottedplantskiosk.api.remote.dto.UsbInfo
import com.citrus.pottedplantskiosk.databinding.FragmentMenuBinding
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.ui.menu.adapter.GoodsItemAdapter
import com.citrus.pottedplantskiosk.ui.menu.adapter.GroupItemAdapter
import com.citrus.pottedplantskiosk.ui.menu.adapter.ImageAdapter
import com.citrus.pottedplantskiosk.ui.menu.adapter.MainGroupItemAdapter
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.Constants.ACTION_USB_PERMISSION
import com.citrus.pottedplantskiosk.util.Constants.clickAnimation
import com.citrus.pottedplantskiosk.util.Constants.forEachReversedWithIndex
import com.citrus.pottedplantskiosk.util.CustomAlertDialog
import com.citrus.pottedplantskiosk.util.UsbUtil
import com.citrus.pottedplantskiosk.util.base.BindingFragment
import com.citrus.pottedplantskiosk.util.base.lifecycleFlow
import com.citrus.pottedplantskiosk.util.base.onSafeClick
import com.citrus.pottedplantskiosk.util.navigateSafely
import com.citrus.pottedplantskiosk.util.showDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.material.snackbar.Snackbar
import com.pax.paxsemilinklibrary.api.SemiLinkApi
import com.pax.paxsemilinklibrary.listener.ITransListener
import com.pax.paxsemilinklibrary.listener.SearchDeviceListener
import com.pax.paxsemilinklibrary.model.BaseSemiRequest
import com.pax.paxsemilinklibrary.model.DeviceInfo
import com.pax.paxsemilinklibrary.util.LogUtils
import com.skydoves.elasticviews.ElasticAnimation
import com.skydoves.transformationlayout.onTransformationStartContainer
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.transformer.AlphaPageTransformer
import com.youth.banner.util.BannerUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pax.comm.protocol.api.Debug
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

    private var progressDialog: ProgressDialog? = null
    private val TAG = "EcrGatewayActivity"
    private val currCommType = DeviceInfo.CommType.USB
    private val mDeviceInfoListMap: MutableList<Map<String, String>> = mutableListOf()
    private val mDeviceInfoList: MutableList<DeviceInfo> = mutableListOf()
    private var payModel: PayModel? = null
    private val transAmount = "0.01"

    private var processingData: OrderDeliveryData? = null

    @Inject
    lateinit var mainGroupItemAdapter: MainGroupItemAdapter
    private var updateMainGroupItemJob: Job? = null

    @Inject
    lateinit var groupItemAdapter: GroupItemAdapter
    private var updateGroupItemJob: Job? = null

    @Inject
    lateinit var goodsItemAdapter: GoodsItemAdapter

    var customDialog: CustomAlertDialog? = null


    override fun onStop() {
        super.onStop()
        binding.banner.stop()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onTransformationStartContainer()
        menuViewModel.intentNavigateToMenu()
        //refreshUsbDevice()
    }


    private fun refreshUsbDevice() {
        usbInfo = UsbUtil.getDevice(requireContext())
        requestUsbPermission()
    }

    private fun requestUsbPermission() {
//        usbInfo.noPermissionDevice.forEachReversedWithIndex { i, usbDevice ->
//            if (usbInfo.usbManager?.hasPermission(usbDevice) == false) {
//                val mPermissionIntent: PendingIntent
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    mPermissionIntent = PendingIntent.getActivity(
//                        requireContext(),
//                        0,
//                        Intent(ACTION_USB_PERMISSION),
//                        PendingIntent.FLAG_MUTABLE
//                    )
//                } else {
//                    mPermissionIntent = PendingIntent.getBroadcast(
//                        requireContext(),
//                        0,
//                        Intent(ACTION_USB_PERMISSION),
//                        PendingIntent.FLAG_IMMUTABLE
//                    )
//                }
//
//                usbInfo.usbManager?.requestPermission(usbDevice, mPermissionIntent)
//            } else usbInfo.noPermissionDevice.removeAt(i)
//        }
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
                    //backToMain()
                    findDevices(null)
                }
            }

            btnCancel.onSafeClick {
                failCons.visibility = View.GONE
            }

            btnRetry.onSafeClick {
                processingData?.let {
                    menuViewModel.setCreditFlow(it)
                }
                failCons.visibility = View.GONE
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
                if (cartGoods.isEdit.not() && cartGoods.isScan.not()) {
                    val item =
                        menuViewModel.currentDetailGoodsList.first { it.gID == cartGoods.gID && it.gKID == cartGoods.gKID }
                    val pos = menuViewModel.currentDetailGoodsList.indexOf(item)
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
            binding.itemRvArea.isVisible = true
            goodsItemAdapter.setGoodsList(goodsList)
            binding.goodsRv.scrollToPosition(0)
            binding.goodsRv.scheduleLayoutAnimation()
        }



        lifecycleFlow(menuViewModel.showDetailEvent) { goods ->
            menuViewModel.isIdentify = false
            Log.e("showDetailEvent", goods.toString())
            findNavController().navigateSafely(
                R.id.action_menuFragment_to_zoomPageFragment,
                args = bundleOf("goods" to goods.deepCopy())
            )
        }

        lifecycleFlow(menuViewModel.showMGoodsDialogEvent) { goods ->
            menuViewModel.isIdentify = false
            binding.composeViewMgoodsDialog.setContent {
                MaterialTheme {
                    if (goods != null) {
                        MGoodsDialog(onDismiss = {
                            menuViewModel.hideMGoodsDialog()
                        })
                    }
                }
            }
        }

//        lifecycleFlow(menuViewModel.needSetting) {
//            if(it.isNotBlank()) {
//                val dialog = SettingFragment(false)
//                dialog.show(childFragmentManager, "SettingFragment")
//
//                Toast.makeText(requireContext(),it,Toast.LENGTH_LONG).show()
//            }
//        }

        lifecycleFlow(menuViewModel.clearCartGoods) {
            binding.cartMotionLayout.clearCartGoods()
        }

        lifecycleFlow(menuViewModel.errMsg) { type ->

            val msg = when (type) {
                Constants.RefundSuccess -> context?.getString(R.string.refundHint)
                Constants.OrderUploadFail -> context?.getString(R.string.orderFail)
                else -> ""
            }

            //Toast.makeText(requireContext(),it,Toast.LENGTH_LONG).show()
            customDialog?.dismiss()
            customDialog = showDialog(
                title = context?.getString(R.string.ErrorOccurred) ?: "",
                msg = msg ?: "",
                icon = R.drawable.ic_warning,
            ) {
                customDialog?.dismiss()
            }
            customDialog?.show()
        }


        lifecycleFlow(menuViewModel.creditFlow) { deliveryInfo ->
            deliveryInfo.let {
                processingData = deliveryInfo
                findDevices(deliveryInfo)
            }
        }

        lifecycleFlow(menuViewModel.creditRefundFlow) { deliveryInfo ->
            deliveryInfo.let {
                findDevices(deliveryInfo, isRefund = true)
            }
        }


        lifecycleFlow(menuViewModel.dispatcherTouch) {
            if (snackbar != null) {
                updateTimerJob?.cancel()
                updateTimerJob = null
                snackbar?.dismiss()
                snackbar = null
            }
        }


        lifecycleFlow(menuViewModel.toPrint) { transactionData ->

//            if (usbInfo.deviceList.isEmpty()) {
//                transactionData.state = TransactionState.PrinterNotFoundIssue
//            }
//
//            val item = usbInfo.deviceList.map {
//                UsbNameWithID(
//                    it.value.deviceName,
//                    it.value.productId
//                )
//            }.find { it.id.toString() == prefs.printer }
//
//
//            item?.let {
//                transactionData.printer = usbInfo.deviceList[item.name]
//            } ?: run {
//                transactionData.state = TransactionState.PrinterNotFoundIssue
//            }


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
                val customSnackView: View =
                    layoutInflater.inflate(R.layout.custom_snackbar_view, null)
                snackbar!!.view.setBackgroundColor(Color.TRANSPARENT)
                val snackbarLayout = snackbar!!.view as Snackbar.SnackbarLayout
                snackbarLayout.setPadding(0, 0, 0, 50)
                val timerHint: TextView = customSnackView.findViewById(R.id.textView2)
                val snackBarArea: CardView = customSnackView.findViewById(R.id.snackbar_view)
                updateTimerJob = lifecycleScope.launch {
                    while (temp < Constants.TWO_MINUTES) {
                        delay(1000)
                        temp++
                        timerHint.text = getString(R.string.idleHint, (120 - temp))
                    }
                }

                snackBarArea.setOnClickListener {
                   releaseSnack()
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

                if (processingData != null && deliveryInfo.payWay.payNo == Constants.PayWayType.Cash) {
                    menuViewModel.postWhenChangeToCash(processingData)
                    return@setonOrderDoneListener
                }

                menuViewModel.postNewOrder(deliveryInfo)
            }
        }
    }

    private fun findDevices(
        orderDeliveryData: OrderDeliveryData?,
        isRefund: Boolean = false
    ) {

        Log.e(TAG, "onClick: current comm type:$currCommType")
        mDeviceInfoListMap.clear()
        mDeviceInfoList.clear()
        SemiLinkApi.getInstance(requireContext())
            .getDevices(currCommType, object : SearchDeviceListener {
                override fun onDiscovered(deviceInfo: DeviceInfo) {
                    Log.e(
                        TAG,
                        "onDiscovered:name: " + deviceInfo.deviceName + ", id:" + deviceInfo.identifier
                    )
                    val map = LinkedHashMap<String, String>()
                    map["deviceName"] = deviceInfo.deviceName
                    map["identifier"] = deviceInfo.identifier
                    mDeviceInfoListMap.add(map)
                    mDeviceInfoList.add(deviceInfo)
                    connectOnlyDevice(orderDeliveryData, isRefund)
                }

                override fun onFinished() {

                    Log.e(
                        TAG,
                        "onFinished, device size:" + mDeviceInfoList.size
                    )


                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                        LogUtils.setDebugLevel(LogUtils.EDebugLevel.DEBUG_LEVEL_ALL)
                        Debug.setDebugLevel(Debug.EDebugLevel.DEBUG_LEVEL_ALL)
                        Log.e(
                            TAG,
                            "onFinished, device size:" + mDeviceInfoList.size
                        )
                    }
                }

                override fun onError(errorCode: Int) {
                    Log.e("Error", "設備異常，請選擇其他付款方式")
                }
            })
    }

    private fun connectOnlyDevice(
        orderDeliveryData: OrderDeliveryData?,
        isRefund: Boolean = false
    ) {
        Thread {
            val ret =
                SemiLinkApi.getInstance(requireContext()).connect(mDeviceInfoList[0])
            requireActivity().runOnUiThread {
                if (ret == 0) {
                    LogUtils.i(
                        TAG,
                        "connect success"
                    )


                    //1st Item
                    payModel = PayModel("VISA", 0, false, orderDeliveryData)

                    if (isRefund) {
                        Log.e("isRefund", "isRefund")
                        startRefund(payModel!!)
                    } else {
                        Log.e("notRefund", "notRefund")
                        startSale(payModel!!)
                    }

                } else {
                    LogUtils.i(
                        TAG,
                        "connect failure"
                    )
                }
            }
        }.start()
    }

    fun startSale(payModel: PayModel) {
        sendRequest("sale", "C200", payModel.isWallet.toString(), payModel.orderDeliveryData)
    }

    fun startRefund(payModel: PayModel) {
        sendRefundRequest(
            "refund",
            "C203",
            payModel.isWallet.toString(),
            payModel.orderDeliveryData
        )
    }


    //    TransResponse(source_package_name=, destination_package_name=, transaction_type=R200, card_number=XXXXXXXXXXXX9551, transaction_amount=000000000160, date_time=20231114111400, expiry_date=XXXX, entry_mode=P, retrieval_reference_number=231114111400, approval_code=111400, response_code=00, terminal_id=23060101, merchant_id=168353110196, host_label=DBS, emv_data=00000000000000A0000000031010  Visa Credit                     , card_label=VISA, card_type=V, host_type=3, command_identifier=, custom_data_2=, custom_data_3=, ecr_unique_trace_number=, invoice_number=000072, transaction_info=000000000160000000000160000000000000, batch_number=000006, coupons_vouchers=, additional_printing_flag=, external_device_invoice=, card_holder_name= /, employee_id=, original_trans_type=003134)
    private fun sendRequest(

        task: String,
        command: String,
        walletName: String,
        orderDeliveryData: OrderDeliveryData?
    ) {
        //BUILD SALE OBJECT TO BE SENT TO PAYMENT APP
        binding.ProceedCons.visibility = View.VISIBLE


        Thread {
            val saleTrans: BaseSemiRequest<TransRequestData> = BaseSemiRequest<TransRequestData>()
            saleTrans.task = task


            saleTrans.data = TransBuilder().getSaleObject(
                String.format(
                    "%.2f",
                    orderDeliveryData?.ordersDelivery?.sPrice ?: 0.01
                ),
                orderDeliveryData?.ordersItemDelivery?.get(0)?.orderNO ?: "1234"
            )
            LogUtils.d(
                TAG,
                "mBaseReque=" + JSON.toJSONString(saleTrans)
            )
            SemiLinkApi.getInstance(requireContext())
                .doTransaction(saleTrans, 200, object : ITransListener {
                    override fun onResponseSuccess(transResultInfo: String) {
                        LogUtils.d("transResultInfo", transResultInfo)
                        val jsonObject: com.alibaba.fastjson.JSONObject? =
                            JSON.parseObject(transResultInfo)
                        val data: String = jsonObject?.getJSONObject("data").toString()
                        // Toast.makeText(context, data, Toast.LENGTH_SHORT).show();

                        //Toast.makeText(context, saleResponseStr, Toast.LENGTH_SHORT).show();
                        val saleResponse: TransResponse =
                            com.google.gson.Gson().fromJson(data, TransResponse::class.java)
                        //SaleResponse saleResponse = GsonUt.fromJson(data, SaleResponse.class);
                        val responseCode: String = saleResponse.response_code //"00"
                        val responseMsg: String = saleResponse.custom_data_2


                        requireActivity().runOnUiThread {
                            binding.ProceedCons.visibility = View.GONE
                            if (responseCode == "00") {

                                orderDeliveryData?.creditInfo = CreditInfo(
                                    saleResponse.card_number,
                                    saleResponse.transaction_amount,
                                    saleResponse.date_time,
                                    saleResponse.retrieval_reference_number,
                                    saleResponse.approval_code,
                                    saleResponse.batch_number,
                                    saleResponse.terminal_id,
                                    saleResponse.merchant_id,
                                    saleResponse.card_label
                                )
                                menuViewModel.setCreditCardSuccess(orderDeliveryData)

                                Log.e("saleResponse", saleResponse.toString())
                                Log.e("custom_data_2", saleResponse.custom_data_2)
                            } else {
                                requireActivity().runOnUiThread {
                                    binding.ProceedCons.visibility = View.GONE
                                }
                            }
                        }
                    }

                    override fun onResponseFailure(errorCode: Int) {
                        requireActivity().runOnUiThread {
                            binding.ProceedCons.visibility = View.GONE
                            binding.failCons.visibility = View.VISIBLE
                        }
                    }
                })
        }.start()
    }


    private fun sendRefundRequest(
        task: String,
        command: String,
        walletName: String,
        orderDeliveryData: OrderDeliveryData?
    ) {

        Thread {
            val saleTrans: BaseSemiRequest<TransRequestData> = BaseSemiRequest<TransRequestData>()
            saleTrans.task = task


            saleTrans.data = TransBuilder().getRefundObject(
                String.format(
                    "%.2f",
                    orderDeliveryData?.ordersDelivery?.sPrice ?: 0.01
                ),
                orderDeliveryData?.ordersItemDelivery?.get(0)?.orderNO ?: "1234",
            )
            LogUtils.d(
                TAG,
                "mBaseReque=" + JSON.toJSONString(saleTrans)
            )
            SemiLinkApi.getInstance(requireContext())
                .doTransaction(saleTrans, 200, object : ITransListener {
                    override fun onResponseSuccess(transResultInfo: String) {
                        LogUtils.d("transResultInfo", transResultInfo)
                        val jsonObject: com.alibaba.fastjson.JSONObject? =
                            JSON.parseObject(transResultInfo)
                        val data: String = jsonObject?.getJSONObject("data").toString()
                        // Toast.makeText(context, data, Toast.LENGTH_SHORT).show();

                        //Toast.makeText(context, saleResponseStr, Toast.LENGTH_SHORT).show();
                        val saleResponse: TransResponse =
                            com.google.gson.Gson().fromJson(data, TransResponse::class.java)
                        //SaleResponse saleResponse = GsonUt.fromJson(data, SaleResponse.class);
                        val responseCode: String = saleResponse.response_code //"00"
                        val responseMsg: String = saleResponse.custom_data_2


                        requireActivity().runOnUiThread {
                            if (responseCode == "00") {

                                menuViewModel.setCreditRefundSuccess()

                                Log.e("saleResponse", saleResponse.toString())
                                Log.e("custom_data_2", saleResponse.custom_data_2)
                            } else {
                                requireActivity().runOnUiThread {
                                    binding.ProceedCons.visibility = View.GONE
                                }
                            }
                        }
                    }

                    override fun onResponseFailure(errorCode: Int) {
                        requireActivity().runOnUiThread {
                            binding.failCons.visibility = View.VISIBLE
                        }
                    }
                })
        }.start()
    }

    private fun backToMain() {
        menuViewModel.stopTimer()
        binding.cartMotionLayout.releaseAdapter()
        findNavController().popBackStack(R.id.mainFragment, false)
    }

    private fun releaseSnack() {
        menuViewModel.setDispatchTouch()
    }

    private fun addGoodToCarAnimation(goodView: View) {
        val mCurrentPosition = FloatArray(2)

        val goodItemImageView = goodView.findViewById<ImageView>(R.id.itemImage)
        val goodItemName = goodView.findViewById<TextView>(R.id.tvItemName)

        val view = View.inflate(requireContext(), R.layout.goods_item_view, null)
        val itemImage = view.findViewById<ImageView>(R.id.itemImage)
        val tvItemName = view.findViewById<TextView>(R.id.tvItemName)
        val tvPrice = view.findViewById<TextView>(R.id.tvPrice)
        itemImage.setImageDrawable(goodItemImageView.drawable)
        itemImage.scaleType = ImageView.ScaleType.CENTER_CROP
        itemImage.layoutParams =
            FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 100)
        tvItemName.text = goodItemName.text
        tvItemName.textSize = 9f
        tvItemName.setPadding(0)
        tvPrice.visibility = View.GONE

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