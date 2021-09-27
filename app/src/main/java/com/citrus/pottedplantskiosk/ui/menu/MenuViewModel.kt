package com.citrus.pottedplantskiosk.ui.menu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.pottedplantskiosk.api.remote.RemoteRepository
import com.citrus.pottedplantskiosk.api.remote.dto.Data
import com.citrus.pottedplantskiosk.api.remote.dto.Good
import com.citrus.pottedplantskiosk.api.remote.dto.MainGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _menuGroupName = MutableStateFlow<List<String>>(listOf())
    val menuGroupName: StateFlow<List<String>> = _menuGroupName

    private val _descName = MutableStateFlow<List<String>>(listOf())
    val descName: StateFlow<List<String>> = _descName

    private val _goods = MutableStateFlow<List<Good>>(listOf())
    val goods: StateFlow<List<Good>> = _goods

    fun showData(data: Data) = viewModelScope.launch {
        _menuData.emit(data.mainGroup)
        var nameList = data.mainGroup.map { it.groupName }
        _menuGroupName.emit(nameList)

        onGroupChange(nameList[0])
    }

    fun onGroupChange(groupName: String) = viewModelScope.launch {
        var data = _menuData.value
        var group = data.first { it.groupName == groupName }.kind.map { it.desc }
        _descName.emit(group)

        var goods =  data.first { it.groupName == groupName }.kind[0].goods
        _goods.emit(goods)
    }


}