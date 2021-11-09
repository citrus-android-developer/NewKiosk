package com.citrus.pottedplantskiosk.api.remote


import com.citrus.pottedplantskiosk.api.remote.dto.BannerResponse
import com.citrus.pottedplantskiosk.api.remote.dto.MenuBean
import com.citrus.pottedplantskiosk.api.remote.dto.UploadResponse
import com.skydoves.sandwich.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

interface Repository {
    suspend fun getMenu(url: String, rsNo: String): Flow<Resource<MenuBean>>
    suspend fun getBanner(url: String, jsonData: String): Flow<Resource<BannerResponse>>
    suspend fun postOrders(url: String, jsonData: String): Flow<Resource<UploadResponse>>
}

sealed class Resource<out T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}


fun <T> resultFlowData(
    apiQuery: suspend () -> ApiResponse<T>,
    onSuccess: (ApiResponse.Success<T>) -> Resource<T>
) = flow {
    apiQuery.invoke().suspendOnSuccess {
        emit(onSuccess(this))
    }.suspendOnError {
        emit(Resource.Error(this.statusCode.name, null))
    }.suspendOnException {
        emit(Resource.Error(this.exception.message!!, null))
    }
}.onStart { emit(Resource.Loading(null)) }
    .onCompletion { emit(Resource.Loading(null)) }
    .flowOn(Dispatchers.IO)