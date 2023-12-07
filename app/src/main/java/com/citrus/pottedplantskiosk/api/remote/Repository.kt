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
    suspend fun getMenu(url: String): Flow<Resource<MenuBean>>
    suspend fun postOrders(url: String, jsonData: String): Flow<Resource<UploadResponse>>
    suspend fun postOrderPayStatusEdit(url: String, jsonData: String): Flow<Resource<UploadResponse>>
}

sealed class Resource<out T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null, isLoading:Boolean) : Resource<T>(data)
}

class RetryCondition(val errorMsg: String) : Exception()

fun <T> resultFlowData(
    apiQuery: suspend () -> ApiResponse<T>,
    onSuccess: (ApiResponse.Success<T>) -> Resource<T>
) = flow {
    apiQuery.invoke().suspendOnSuccess {
        Log.e("Test", "resultFlowData: ${this.data}")
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
    Log.e("Test", "resultFlowData: ${it.message}")
    emit(Resource.Error("unexpected error", null))
}.onStart { emit(Resource.Loading(null,true)) }
    .onCompletion { emit(Resource.Loading(null,false)) }
    .flowOn(Dispatchers.IO)