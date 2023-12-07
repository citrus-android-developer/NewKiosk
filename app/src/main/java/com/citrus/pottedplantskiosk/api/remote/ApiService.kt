package com.citrus.pottedplantskiosk.api.remote

import com.citrus.pottedplantskiosk.api.remote.dto.*
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.*


interface ApiService {

    /**取得菜單資訊 */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    suspend fun getMenu(
        @Url url: String,
        @Field("jsonData") jsonData: String
    ): ApiResponse<MenuBean>




    /**上傳訂單 */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    suspend fun setOrders(
        @Url url: String,
        @Field("jsonData") jsonData: String
    ): ApiResponse<UploadResponse>



    /**編輯付款方式、已付款狀態 */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    suspend fun setOrdersPayStatus(
        @Url url: String,
        @Field("jsonData") jsonData: String
    ): ApiResponse<UploadResponse>

}