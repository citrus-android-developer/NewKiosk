package com.citrus.pottedplantskiosk.api.remote

import com.citrus.pottedplantskiosk.api.remote.dto.BannerData
import com.citrus.pottedplantskiosk.api.remote.dto.BannerResponse
import com.citrus.pottedplantskiosk.api.remote.dto.Data
import com.citrus.pottedplantskiosk.api.remote.dto.UploadResponse
import kotlinx.coroutines.flow.Flow

interface Repository{
    fun getMenu(url:String,rsNo:String): Flow<Resource<Data>>
    fun getBanner(url:String,jsonData:String): Flow<Resource<BannerResponse>>
    fun postOrders(url:String,jsonData:String): Flow<Resource<UploadResponse>>

}