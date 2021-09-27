package com.citrus.pottedplantskiosk.api.remote

import com.citrus.pottedplantskiosk.api.remote.dto.MenuBean
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.*


interface ApiService {

    /**取得菜單資訊 */
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST
    suspend fun getMenu(
        @Url url: String,
        @Field("rsNo") rsNo: String
    ): ApiResponse<MenuBean>


}