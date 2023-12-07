package com.citrus.pottedplantskiosk.api.remote

import android.util.Log
import com.citrus.pottedplantskiosk.api.remote.dto.UploadResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RemoteRepository @Inject constructor(private val apiService: ApiService) : Repository {

    override suspend fun getMenu(url: String) =
        resultFlowData(apiQuery = { apiService.getMenu(url, "") }, onSuccess = { result ->
            try {
                if (result.data.status != 1 || result.data.mainGroup.isEmpty()) {
                    Resource.Error("Menu not found", null)
                } else {
                    if (result.data.mainGroup.find { it.kind.isNotEmpty() } == null) {
                        Resource.Error("Menu not found", null)
                    } else {
                        if (result.data.mainGroup.isEmpty()) {
                            Resource.Error("MainGroup is Empty", null)
                        } else {
                            Log.e("Test", "getMenu: ${result.data.mainGroup}))")
                            Resource.Success(result.data)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Test", "getMenu: ${e.message}")
                Resource.Error("${e.message}", null)
            }
        })


    override suspend fun postOrders(url: String, jsonData: String) =
        resultFlowData(apiQuery = { apiService.setOrders(url, jsonData) }, onSuccess = { result ->
            if (result.data.status != 1) {
                Resource.Error("Orders upload fail", null)
            } else {
                Resource.Success(result.data)
            }
        })



    override suspend fun postOrderPayStatusEdit(
        url: String,
        jsonData: String
    ) =
        resultFlowData(
            apiQuery = { apiService.setOrdersPayStatus(url, jsonData) },
            onSuccess = { result ->
                if (result.data.status != 1) {
                    Resource.Error("Orders edit fail", null)
                } else {
                    Resource.Success(result.data)
                }
            })

}