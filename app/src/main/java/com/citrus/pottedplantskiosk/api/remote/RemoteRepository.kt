package com.citrus.pottedplantskiosk.api.remote

import android.util.Log
import com.citrus.pottedplantskiosk.api.remote.dto.BannerData
import com.citrus.pottedplantskiosk.api.remote.dto.BannerResponse
import com.citrus.pottedplantskiosk.api.remote.dto.Data
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRepository @Inject constructor(private val apiService: ApiService) : Repository {

    override fun getMenu(url: String, rsNo: String): Flow<Resource<Data>> =
        flow {
            apiService.getMenu(url, rsNo).suspendOnSuccess {
                data?.let {
                    if (it.status == 1) {
                        emit(Resource.Success(data.data))
                    } else {
                        emit(Resource.Error("UnExpect Error", null))
                    }
                }
            }.suspendOnError {
                emit(Resource.Error(this.statusCode.name, null))
            }.suspendOnException {
                emit(Resource.Error(this.exception.message!!,null))
            }
        }.onStart { Resource.Loading(true) }.onCompletion { Resource.Loading(false) }
            .flowOn(Dispatchers.IO)


    override fun getBanner(url: String, jsonData: String): Flow<Resource<BannerResponse>> =
        flow {
            apiService.getBanner(url,jsonData).suspendOnSuccess {
                data?.let {
                    if (it.status == 1) {
                        emit(Resource.Success(data))
                    } else {
                        emit(Resource.Error("UnExpect Error", null))
                    }
                }
            }.suspendOnError {
                emit(Resource.Error(this.statusCode.name, null))
            }.suspendOnException {
                emit(Resource.Error(this.exception.message!!,null))
            }
        }.onStart { Resource.Loading(true) }.onCompletion { Resource.Loading(false) }
            .flowOn(Dispatchers.IO)
}