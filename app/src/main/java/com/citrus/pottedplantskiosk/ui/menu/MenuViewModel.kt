package com.citrus.pottedplantskiosk.ui.menu

import android.app.Application
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

    private var timerJob: Job?= null
    var timeCount = 0
    private val _tikTok = MutableStateFlow(0)
    val tikTok: StateFlow<Int> = _tikTok

    private val _menuData = MutableStateFlow<List<MainGroup>>(listOf())
    val menuData: StateFlow<List<MainGroup>> = _menuData

    private val _menuGroupName = MutableStateFlow<List<GroupItem>>(listOf())
    val menuGroupName: StateFlow<List<GroupItem>> = _menuGroupName

    private val _kindList = MutableStateFlow<List<Kind>>(listOf())
    val kindList: StateFlow<List<Kind>> = _kindList

    private val _showDetailEvent = MutableSharedFlow<Good>()
    val showDetailEvent: SharedFlow<Good> = _showDetailEvent

    var currentDetailGoodsList: List<Good> = listOf()

    private var _currentCartGoods = MutableStateFlow<List<Good>>(listOf())
    val currentCartGoods: StateFlow<List<Good>> = _currentCartGoods

    data class GroupItem(val name:String, val imgUrl:String?)

    private fun tickerFlow() = flow {
        while (true) {
            emit(timeCount++)
            delay(1000)
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch(Default) {
            tickerFlow().collect{
                _tikTok.emit(it)
            }
        }
    }

    fun stopTimer()= viewModelScope.launch{
        timerJob?.cancel()
        _currentCartGoods.emit(listOf())
    }

    fun showData(data: Data) = viewModelScope.launch {
        _menuData.emit(data.mainGroup)
       // var groupList = data.mainGroup.map { GroupItem(it.groupName, if(it.kind.isNotEmpty()) it.kind[0].goods[0].picName else null) }
        var groupList = data.mainGroup.map { GroupItem(it.groupName,  null) }

        _menuGroupName.emit(groupList)
        onGroupChange(groupList[0].name)
    }

    fun onGroupChange(groupName: String) = viewModelScope.launch {
        var data = _menuData.value
        var kindList = data.first { it.groupName == groupName }.kind

        /**MockSize*/
//        val size1 = Size("Bottle","101",4500.0,"107","Bottle","")
//        val size2 = Size("Glass","123",780.0,"103","Glass","")
//        var sizeList = listOf<Size>()
//        sizeList = sizeList + size1
//        sizeList = sizeList + size2
//        kindList[0].goods[0].size = sizeList


        /**MockSize*/
//        val size3 = Size("Small","101",8.9,"107","Small","")
//        val size4 = Size("Middle","123",10.7,"103","Middle","")
//        val size5 = Size("Big","123",12.9,"103","Big","")
//        var sizeList2 = listOf<Size>()
//        sizeList2 = sizeList2 + size3
//        sizeList2 = sizeList2 + size4
//        sizeList2 = sizeList2 + size5
//
//        kindList[0].goods[1].size = sizeList2

        _kindList.emit(kindList)
    }

    fun onGoodsClick(good: Good, list: List<Good>) = viewModelScope.launch {
        currentDetailGoodsList = list
        _showDetailEvent.emit(good)
    }

    fun intentNavigateToMenu() {
        startTimer()
    }

    fun setCartGoods(good:Good) = viewModelScope.launch{
        var list = _currentCartGoods.value
        list = list + good
        _currentCartGoods.emit(list)
    }


}