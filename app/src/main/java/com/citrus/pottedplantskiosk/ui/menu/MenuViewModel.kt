package com.citrus.pottedplantskiosk.ui.menu

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.pottedplantskiosk.api.remote.RemoteRepository
import com.citrus.pottedplantskiosk.api.remote.dto.Data
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.api.remote.dto.Kind
import com.citrus.pottedplantskiosk.api.remote.dto.MainGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: RemoteRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _menuData = MutableStateFlow<List<MainGroup>>(listOf())
    val menuData: StateFlow<List<MainGroup>> = _menuData

    private val _menuGroupName = MutableStateFlow<List<GroupItem>>(listOf())
    val menuGroupName: StateFlow<List<GroupItem>> = _menuGroupName

    private val _kindList = MutableStateFlow<List<Kind>>(listOf())
    val kindList: StateFlow<List<Kind>> = _kindList

    private val _showDetailEvent = MutableSharedFlow<Good>()
    val showDetailEvent: SharedFlow<Good> = _showDetailEvent

    var currentDetailGoodsList: List<Good> = listOf()

    data class GroupItem(val name:String, val imgUrl:String?)

    fun showData(data: Data) = viewModelScope.launch {
        _menuData.emit(data.mainGroup)
        var groupList = data.mainGroup.map { GroupItem(it.groupName, if(it.kind.isNotEmpty()) it.kind[0].goods[0].picName else null) }
        _menuGroupName.emit(groupList)
        onGroupChange(groupList[0].name)
    }

    fun onGroupChange(groupName: String) = viewModelScope.launch {
        var data = _menuData.value
        var kindList = data.first { it.groupName == groupName }.kind
        _kindList.emit(kindList)
    }

    fun onGoodsClick(good: Good, list: List<Good>) = viewModelScope.launch {
        currentDetailGoodsList = list
        _showDetailEvent.emit(good)
    }


}