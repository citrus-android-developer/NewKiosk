package com.citrus.pottedplantskiosk.api.remote

import com.citrus.pottedplantskiosk.api.remote.dto.*
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.*
import javax.net.ssl.SSLEngineResult


interface ApiService {

    /**取得菜單資訊 */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    suspend fun getMenu(
        @Url url: String,
        @Field("rsNo") rsNo: String
    ): ApiResponse<MenuBean>

    /**取得廣告輪播資訊 */
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GET
    suspend fun getBanner(@Url url: String,@Query("jsonData") jsonData: String): ApiResponse<BannerResponse>


    /**上傳訂單 */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    suspend fun setOrders(
        @Url url: String,
        @Field("jsonData") jsonData: String
    ): ApiResponse<UploadResponse>


    /**getGenericResult Test*/
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GET
    suspend fun getGenericResultByInt(@Url url: String): ApiResponse<StatusCode<Int>>

    /**getGenericResult Test*/
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GET
    suspend fun getGenericResultByObj(@Url url: String): ApiResponse<StatusCode<ResultMock>>


}