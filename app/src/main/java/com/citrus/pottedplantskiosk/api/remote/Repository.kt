package com.citrus.pottedplantskiosk.api.remote


import android.util.Log
import com.citrus.pottedplantskiosk.api.remote.dto.*
import com.citrus.pottedplantskiosk.api.remote.dto.StatusCode
import com.skydoves.sandwich.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.lang.Exception

interface Repository {
    suspend fun getMenu(url: String, rsNo: String): Flow<Resource<MenuBean>>
    suspend fun getBanner(url: String, jsonData: String): Flow<Resource<BannerResponse>>
    suspend fun postOrders(url: String, jsonData: String): Flow<Resource<UploadResponse>>
    suspend fun getGenericResultByInt(url: String): Flow<Resource<StatusCode<Int>>>
    suspend fun getGenericResultByObj(url: String): Flow<Resource<StatusCode<ResultMock>>>
}

sealed class Resource<out T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

class RetryCondition(val errorMsg: String) : Exception()

fun <T> resultFlowData(
    apiQuery: suspend () -> ApiResponse<T>,
    onSuccess: (ApiResponse.Success<T>) -> Resource<T>
) = flow {
    apiQuery.invoke().suspendOnSuccess {
        emit(onSuccess(this))
    }.suspendOnError {
        throw RetryCondition(this.statusCode.name)
    }.suspendOnException {
        throw RetryCondition(this.exception.message!!)
    }
}.retryWhen { cause, attempt ->
    if (cause is RetryCondition && attempt < 3) {
        delay(1500)
        return@retryWhen true
    } else {
        emit(Resource.Error(cause.message!!, null))
        return@retryWhen false
    }
}.catch {
    Log.e("catch error", "--")
}.onStart { emit(Resource.Loading(null)) }
    .onCompletion { emit(Resource.Loading(null)) }
    .flowOn(Dispatchers.IO)