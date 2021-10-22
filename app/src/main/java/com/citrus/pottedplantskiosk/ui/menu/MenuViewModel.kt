package com.citrus.pottedplantskiosk.ui.menu

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.pottedplantskiosk.api.remote.RemoteRepository
import com.citrus.pottedplantskiosk.api.remote.dto.*
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

    private var _currentCartGoods = MutableStateFlow<Good?>(null)
    val currentCartGoods: StateFlow<Good?> = _currentCartGoods


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
    }

    fun showData(data: Data) = viewModelScope.launch {
        _menuData.emit(data.mainGroup)
        var groupList = data.mainGroup.map { it.groupName }

        _menuGroupName.emit(groupList)
        onGroupChange(groupList[0])

        currentGroup = data.mainGroup.first()
    }

    fun onGroupChange(groupName: String) = viewModelScope.launch {

        currentGroup = _menuData.value.find { it.groupName == groupName }

        currentGroup?.let { mainGroup ->
            var list = mainGroup.kind.map { it.desc }
            _groupDescName.emit(list)
            onDescChange(mainGroup.kind.first().desc)
        }
    }

    fun onDescChange(desc:String) = viewModelScope.launch{
        currentGroup?.let{ mainGroup ->
            var goods = mainGroup.kind.find { it.desc == desc}?.goods ?: listOf()
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

    fun setCartGoods(good: Good) = viewModelScope.launch {
        _currentCartGoods.emit(good)
    }

    fun showBanner(bannerResponse: BannerResponse) = viewModelScope.launch {
        _showBannerData.emit(bannerResponse.data)
    }


}