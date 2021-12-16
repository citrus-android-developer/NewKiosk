package com.citrus.pottedplantskiosk.ui.slash

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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SlashViewModel @Inject constructor(
    private val repository: RemoteRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _allMenuData = MutableSharedFlow<Resource<MenuBean>>()
    val allMenuData: SharedFlow<Resource<MenuBean>> = _allMenuData

    private val _allBannerData = MutableSharedFlow<Resource<BannerResponse>>()
    val allBannerData: SharedFlow<Resource<BannerResponse>> = _allBannerData

    private val _allData = MutableSharedFlow<Boolean>()
    val allData: SharedFlow<Boolean> = _allData

    private val _errorNotify = MutableSharedFlow<Boolean>()
    val errorNotify: SharedFlow<Boolean> = _errorNotify


    fun asyncTask() = viewModelScope.launch {
        val deferred = listOf(
            async { getMenu() },
            async { getBanner() }
        )
        deferred.awaitAll()
    }

    init {
        getGenericResultByInt()
        getGenericResultByString()
    }

    private  fun getGenericResultByInt() =
        viewModelScope.launch {
            repository.getGenericResultByInt("http://192.168.0.56:8080/api/getGenericResultByInt").collect {
                Log.e("getGeneric",it.toString())
                it.data?.toString()?.let { it1 -> Log.e("getGenericResultByInt", it1) }
            }
        }

    private  fun getGenericResultByString() =
        viewModelScope.launch {
            repository.getGenericResultByObj("http://192.168.0.56:8080/api/getGenericResultByObject").collect {
                Log.e("getGeneric",it.toString())

                it.data?.data?.let { it1 -> Log.e("getGenericResultByObj", it1.desc) }
            }
        }

    private fun getMenu() =
        viewModelScope.launch {
            repository.getMenu(prefs.serverIp + Constants.GET_MENU, prefs.storeId).collect { result ->
                _allMenuData.emit(result)
            }
        }

    private fun getBanner() =
        viewModelScope.launch {
            repository.getBanner(
                prefs.serverIp + Constants.GET_BANNER,
                Gson().toJson(BannerRequest(rsno = "33014"))
            ).collect { result ->
                Log.e("result",result.data.toString())

                _allBannerData.emit(result)
            }
        }

    fun intentNext() = viewModelScope.launch {
        _allData.emit(true)
    }

    fun fetchError() = viewModelScope.launch {
        _errorNotify.emit(true)
    }

    fun reFetch() {
        asyncTask()
    }

}