package com.citrus.pottedplantskiosk.api.remote

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RemoteRepository @Inject constructor(private val apiService: ApiService) : Repository {

    override suspend fun getMenu(url: String, rsNo: String) =
        resultFlowData(apiQuery = { apiService.getMenu(url, rsNo) }, onSuccess = { result ->
            if (result.data.status != 1) {
                Resource.Error("Menu not found", null)
            } else {
                Resource.Success(result.data)
            }
        })

    override suspend fun getBanner(url: String, jsonData: String) =
        resultFlowData(apiQuery = { apiService.getBanner(url, jsonData) }, onSuccess = { result ->
            if (result.data.status != 1) {
                Resource.Error("Banner not found", null)
            } else {
                Resource.Success(result.data)
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

    override suspend fun getGenericResultByInt(url: String) =
        resultFlowData(apiQuery = { apiService.getGenericResultByInt(url) }, onSuccess = { result ->
            if (result.data.status != 1) {
                Resource.Error("Orders upload fail", null)
            } else {
                Resource.Success(result.data)
            }
        })

    override suspend fun getGenericResultByObj(url: String)  =
        resultFlowData(apiQuery = { apiService.getGenericResultByObj(url) }, onSuccess = { result ->
            if (result.data.status != 1) {
                Resource.Error("Orders upload fail", null)
            } else {
                Resource.Success(result.data)
            }
        })

}