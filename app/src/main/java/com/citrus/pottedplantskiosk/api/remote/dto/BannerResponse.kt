package com.citrus.pottedplantskiosk.api.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class BannerRequest(
    @SerializedName("RSNO")
    val rsno:String
): Serializable


data class BannerResponse(
    val status: Int,
    val data: List<BannerData>
): Serializable

data class BannerData(
    val autoNo: Long,
    @SerializedName("OnSaleName")
    val onSaleName: String,
    @SerializedName("Seq")
    val seq: Long,
    @SerializedName("Pic")
    val pic: String,
    @SerializedName("Hyperlink")
    val hyperlink: String
): Serializable
