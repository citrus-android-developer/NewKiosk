package com.citrus.pottedplantskiosk.api.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderStatusEditRequest(
    @SerializedName("RSNO")
    val rsno: String,
    @SerializedName("OrderNO")
    val orderNo: String,
    @SerializedName("PayType")
    var payType: String? = null,
    @SerializedName("IsPay")
    var isPay: String,
)