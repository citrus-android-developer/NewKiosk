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
        var groupList = _menuData.value.map { it.groupName }
        onGroupChange(groupList[0])
        currentGroup = _menuData.value.first()
    }

    fun showData(data: Data) = viewModelScope.launch {
        _menuData.emit(data.mainGroup)
        var groupList = data.mainGroup.filter{ it.kind.isNotEmpty() }.map { it.groupName }

        if (groupList.isNotEmpty() && groupList[0] != "") {
            _menuGroupName.emit(groupList)
            onGroupChange(groupList[0])
        } else {
            onGroupChange("")
        }

        currentGroup = data.mainGroup.first()
    }

    fun onGroupChange(groupName: String) = viewModelScope.launch {

        currentGroup = if (groupName != "") {
            _menuData.value.find { it.groupName == groupName }
        } else {
            _menuData.value[0]
        }

        currentGroup?.let { mainGroup ->
            var list = mainGroup.kind.filter { it.goods.isNotEmpty() }.map { it.desc }
            _groupDescName.emit(list)
            onDescChange(mainGroup.kind.first().desc)
        }
    }

    fun onDescChange(desc: String) = viewModelScope.launch {
        currentGroup?.let { mainGroup ->
            var goods = mainGroup.kind.find { it.desc == desc }?.goods!!
            goods[0].price = 4.55
            goods[0].tax = 7.0

            goods[1].price = 3.25
            goods[1].tax = 6.0
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


    fun postOrderItem(deliveryInfo: DeliveryInfo) = viewModelScope.launch {

        var list = deliveryInfo.goodsList
        var sumQty = list.sumOf { goods ->
            goods.qty
        }

        var sumPrice = list.sumOf { goods ->
            goods.sPrice
        }



        var ordersDelivery = Orders.OrdersDelivery(
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
            var ordersItemDelivery =
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

        var orderDeliveryData = Orders.OrderDeliveryData(
            ordersDelivery = ordersDelivery,
            ordersItemDelivery = ordersItemDeliveryList
        )

        viewModelScope.launch {
            repository.postOrders(
                prefs.serverIp + Constants.SET_ORDERS,
                Gson().toJson(orderDeliveryData)
            ).collect { result ->
                var printerData : TransactionData? = null
                when (result) {
                    is Resource.Success -> {
                        orderDeliveryData.ordersItemDelivery.forEach { item ->
                            item.orderNO = result.data?.data!!
                        }
                       printerData =   TransactionData(orders = orderDeliveryData,state = TransactionState.WorkFine, null)
                    }
                    is Resource.Error -> {
                        Log.e("error",result.message!!)
                        printerData =   TransactionData(orders = orderDeliveryData,state = TransactionState.WorkFine, null)
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

    fun setPrintStatus(status: Int) = viewModelScope.launch {
        if (status == 1) {
            _clearCartGoods.emit(true)
        }
    }

    fun chosenLanComplete() = viewModelScope.launch {
        _reLaunchActivity.emit(true)
    }


}