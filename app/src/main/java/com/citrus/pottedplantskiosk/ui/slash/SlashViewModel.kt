package com.citrus.pottedplantskiosk.ui.slash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.pottedplantskiosk.api.remote.RemoteRepository
import com.citrus.pottedplantskiosk.api.remote.Resource
import com.citrus.pottedplantskiosk.api.remote.dto.Data
import com.citrus.pottedplantskiosk.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SlashViewModel @Inject constructor(
    private val repository: RemoteRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _allMenuData = MutableSharedFlow<Resource<Data>>()
    val allMenuData: SharedFlow<Resource<Data>> = _allMenuData


    fun getMenu() =
        viewModelScope.launch {
            repository.getMenu(Constants.BASE_URL + Constants.GET_MENU, "").collect { result ->
                _allMenuData.emit(result)
            }
        }


}