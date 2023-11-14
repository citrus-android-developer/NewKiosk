package com.citrus.pottedplantskiosk.ui.menu

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.pottedplantskiosk.api.remote.RemoteRepository
import com.citrus.pottedplantskiosk.api.remote.Resource
import com.citrus.pottedplantskiosk.api.remote.dto.*
import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.Constants
import com.citrus.pottedplantskiosk.util.base.fineEmit
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: RemoteRepository,
    application: Application
) : AndroidViewModel(application) {

    private var timerJob: Job? = null
    var timeCount = 0
    private val _tikTok = MutableStateFlow(0)
    val tikTok: StateFlow<Int> = _tikTok

    private val _reLaunchActivity = MutableSharedFlow<Boolean>()
    val reLaunchActivity: SharedFlow<Boolean> = _reLaunchActivity

    private val _zoomPageSlidePos = MutableSharedFlow<Int>()
    val zoomPageSlidePos: SharedFlow<Int> = _zoomPageSlidePos

    private val _printStatus = MutableSharedFlow<Int>()
    val printStatus: SharedFlow<Int> = _printStatus

    private val _clearCartGoods = MutableSharedFlow<Boolean>()
    val clearCartGoods: SharedFlow<Boolean> = _clearCartGoods

    private val _menuData = MutableStateFlow<List<MainGroup>>(listOf())
    val menuData: StateFlow<List<MainGroup>> = _menuData

    private val _menuGroupName = MutableStateFlow<List<String>>(listOf())
    val menuGroupName: StateFlow<List<String>> = _menuGroupName

    private val _groupDescName = MutableStateFlow<List<String>>(listOf())
    val groupDescName: StateFlow<List<String>> = _groupDescName

    private val _allGoods = MutableStateFlow<List<Good>>(listOf())
    val allGoods: StateFlow<List<Good>> = _allGoods

    private val _kindList = MutableStateFlow<List<Kind>>(listOf())
    val kindList: StateFlow<List<Kind>> = _kindList

    private val _showDetailEvent = MutableSharedFlow<Good>()
    val showDetailEvent: SharedFlow<Good> = _showDetailEvent


    private val _showBannerData = MutableStateFlow<List<BannerData>>(listOf())
    val showBannerData: StateFlow<List<BannerData>> = _showBannerData

    var currentDetailGoodsList: List<Good> = listOf()
    private var currentGroup: MainGroup? = null

    private var _currentCartGoods = MutableSharedFlow<Good?>()
    val currentCartGoods: SharedFlow<Good?> = _currentCartGoods

    private val _toPrint = MutableSharedFlow<TransactionData>()
    val toPrint: SharedFlow<TransactionData> = _toPrint

    private val _toActivityPrint = MutableSharedFlow<TransactionData>()
    val toActivityPrint: SharedFlow<TransactionData> = _toActivityPrint

    private val _printResult = MutableSharedFlow<Boolean>()
    val printResult: SharedFlow<Boolean> = _printResult

    private val _creditFlow = MutableSharedFlow<Orders.OrderDeliveryData>()
    val creditFlow: SharedFlow<Orders.OrderDeliveryData> = _creditFlow

    private val _errMsg = MutableSharedFlow<String>()
    val errMsg: SharedFlow<String> = _errMsg

    var allGoodsForScan: List<Good>? = null
    var isIdentify = false

    private val _scanResult = MutableSharedFlow<String>()
    val scanResult: SharedFlow<String> = _scanResult

    fun setScanResult(result: String) = viewModelScope.launch {
        Log.e("scanResult", result)
        allGoodsForScan?.let { goodList ->
            val goods = goodList.find { it.barCode == result }
            goods?.let {
                it.isScan = true
                _showDetailEvent.emit(it)
            }
        }
    }

    private fun tickerFlow() = flow {
        while (true) {
            emit(timeCount++)
            delay(1000)
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch(Default) {
            tickerFlow().collect {
                _tikTok.emit(it)
            }
        }
    }

    fun stopTimer() = viewModelScope.launch {
        timerJob?.cancel()
        _currentCartGoods.emit(null)
        resetMenu()
    }

    private fun resetMenu() = viewModelScope.launch {
        val groupList = _menuData.value.map { it.groupName }
        onGroupChange(groupList[0])
        currentGroup = _menuData.value.first()
    }

    fun showData(data: Data) = viewModelScope.launch {
        _menuData.emit(data.mainGroup)
        val groupList = data.mainGroup.filter { it.kind.isNotEmpty() }.map { it.groupName }

        if (groupList.isNotEmpty() && groupList[0] != "") {
            _menuGroupName.emit(groupList)
            onGroupChange(groupList[0])
        } else {
            onGroupChange("")
        }


        allGoodsForScan = data.mainGroup.flatMap { it.kind }.flatMap { it.goods }

        currentGroup = data.mainGroup.first()
    }

    fun onGroupChange(groupName: String) = viewModelScope.launch {

        currentGroup = if (groupName != "") {
            _menuData.value.find { it.groupName == groupName }
        } else {
            _menuData.value[0]
        }

        currentGroup?.let { mainGroup ->
            val list = mainGroup.kind.filter { it.goods.isNotEmpty() }.map { it.desc }
            _groupDescName.emit(list)
            if (mainGroup.kind.isNotEmpty()) {
                onDescChange(mainGroup.kind.first().desc)
            }
        }
    }

    fun onDescChange(desc: String) = viewModelScope.launch {
        currentGroup?.let { mainGroup ->
            val goods = mainGroup.kind.find { it.desc == desc }?.goods!!
            _allGoods.emit(goods)
        }
    }

    fun onGoodsClick(good: Good, list: List<Good>) = viewModelScope.launch {
        currentDetailGoodsList = list
        _showDetailEvent.emit(good)
    }

    fun intentNavigateToMenu() {
        startTimer()
    }

    fun setZoomPagePos(pos: Int) = viewModelScope.launch {
        _zoomPageSlidePos.emit(pos)
    }

    fun setCartGoods(good: Good) = viewModelScope.launch {
        _currentCartGoods.emit(good)
    }

    fun showBanner(bannerResponse: BannerResponse) = viewModelScope.launch {
        _showBannerData.emit(bannerResponse.data)
    }


    fun postWhenChangeToCash(deliveryInfo: Orders.OrderDeliveryData?) = viewModelScope.launch {
        deliveryInfo?.ordersDelivery?.payType = "Cash"
        deliveryInfo?.ordersDelivery?.isPay = "N"
        var printerData = TransactionData(
            orders = deliveryInfo,
            state = TransactionState.WorkFine,
            null,
            null
        )
        _toPrint.emit(printerData)
    }

    fun postOrderItem(deliveryInfo: DeliveryInfo) = viewModelScope.launch {

        val list = deliveryInfo.goodsList
        val sumQty = list.sumOf { goods ->
            goods.qty
        }

        val sumPrice = list.sumOf { goods ->
            goods.sPrice
        }


        val ordersDelivery = Orders.OrdersDelivery(
            storeID = 0,
            qty = sumQty,
            payType = deliveryInfo.payWay.desc,
            isPay = "Y",
            gPrice = deliveryInfo.grandTotal,
            sPrice = sumPrice,
            totaltax = deliveryInfo.gst
        )

        var ordersItemDeliveryList = listOf<Orders.OrdersItemDelivery>()

        var seq = 1
        list.forEach { goods ->
            val ordersItemDelivery =
                Orders.OrdersItemDelivery(
                    storeID = 0,
                    orderSeq = seq,
                    gid = goods.gID,
                    sPrice = goods.sPrice,
                    gPrice = goods.price,
                    qty = goods.qty,
                    gkid = goods.gKID,
                    gType = goods.gType,
                    gname = goods.gName,
                    gName2 = goods.gName2,
                    tax = goods.gst

                )
            seq++
            ordersItemDeliveryList = ordersItemDeliveryList + ordersItemDelivery
        }

        val orderDeliveryData = Orders.OrderDeliveryData(
            ordersDelivery = ordersDelivery,
            ordersItemDelivery = ordersItemDeliveryList
        )

        viewModelScope.launch {
            repository.postOrders(
                prefs.serverIp + Constants.SET_ORDERS,
                Gson().toJson(orderDeliveryData)
            ).collect { result ->
                var printerData: TransactionData? = null
                when (result) {
                    is Resource.Success -> {

                        orderDeliveryData.ordersItemDelivery.forEach { item ->
                            item.orderNO = result.data?.data!!
                        }

                        if (deliveryInfo.payWay.payNo == Constants.PayWayType.CreditCard) {
                            Log.e("credit", "credit");
                            _creditFlow.emit(orderDeliveryData)
                            return@collect
                        } else {
                            printerData = TransactionData(
                                orders = orderDeliveryData,
                                state = TransactionState.WorkFine,
                                null,
                                null
                            )
                        }


                    }

                    is Resource.Error -> {
                        _errMsg.emit(result.message ?: "error")
                        return@collect
                        //printerData =   TransactionData(orders = null,state = TransactionState.NetworkIssue, null)
                    }

                    is Resource.Loading -> Unit
                }


                printerData?.let {
                    _toPrint.emit(it)
                }
            }
        }
    }

    fun setCreditFlow(data: Orders.OrderDeliveryData) = viewModelScope.launch {
        _creditFlow.emit(data)
    }

    fun setPrintStatus(status: Int) = viewModelScope.launch {
        if (status == 1) {
            _clearCartGoods.emit(true)
        }
    }

    fun chosenLanComplete() = viewModelScope.launch {
        _reLaunchActivity.emit(true)
    }

    fun setCreditCardSuccess(orderDeliveryData: Orders.OrderDeliveryData) = viewModelScope.launch {
        val printerData = TransactionData(
            orders = orderDeliveryData,
            state = TransactionState.WorkFine,
            null,
            null
        )
        _toPrint.emit(printerData)
    }

    fun setPrintData(data: TransactionData?) = viewModelScope.launch {
        delay(2000)
        data?.let {
            _toActivityPrint.emit(data!!)
        }
    }

    fun setPrintResult(result: Boolean) = viewModelScope.launch {
        _printResult.fineEmit(result)
    }


}