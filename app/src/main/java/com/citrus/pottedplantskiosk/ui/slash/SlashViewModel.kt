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
import okhttp3.Dispatcher
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class SlashViewModel @Inject constructor(
    private val repository: RemoteRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _allMenuData = MutableSharedFlow<Resource<MenuBean>>()
    val allMenuData: SharedFlow<Resource<MenuBean>> = _allMenuData

    private val _allBannerData = MutableSharedFlow<BannerResponse>()
    val allBannerData: SharedFlow<BannerResponse> = _allBannerData

    private val _allData = MutableSharedFlow<Boolean>()
    val allData: SharedFlow<Boolean> = _allData

    private val _errorNotify = MutableSharedFlow<Boolean>()
    val errorNotify: SharedFlow<Boolean> = _errorNotify


    fun asyncTask() = viewModelScope.launch(Dispatchers.IO) {
        val deferred = listOf(
            async { getMenu() },
            async { getBanner() }
        )
        deferred.awaitAll()
    }


    private fun getMenu() =
        viewModelScope.launch {
            repository.getMenu("http://"+ prefs.serverIp + Constants.POS_GET_MENU)
                .collect { result ->
                    _allMenuData.emit(result)
                }
        }

    private fun getBanner() =
        viewModelScope.launch(Dispatchers.IO) {
            val data = getPicUrlFromFolder()
            if (data.isNotEmpty()) {
                _allBannerData.emit(BannerResponse(1, data))
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

    private fun getPicUrlFromFolder(): List<BannerData> {
        val folderUrl = "http://" + prefs.serverIp +"/advImages"
        var data = listOf<BannerData>()
        val pattern = """<A HREF="/[^/]+/([^"]+\.(?:jpg|jpeg|png))">""".toRegex()

        try {
            val url = URL(folderUrl)
            val urlConnection = url.openConnection()
            val inputStream = BufferedReader(InputStreamReader(urlConnection.getInputStream()))

            var line: String?
            while (inputStream.readLine().also { line = it } != null) {
                if (line?.contains("jpg|jpeg|png".toRegex()) == true) {
                    val matches = pattern.findAll(line!!)
                    val imageNames = matches.map { it.groups[1]?.value }.toList()

                    if (imageNames.isNotEmpty()) {
                        data = imageNames.map {
                            BannerData(pic = "$folderUrl/$it")
                        }.toList()
                    }
                }; continue
            }
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return data
    }
}