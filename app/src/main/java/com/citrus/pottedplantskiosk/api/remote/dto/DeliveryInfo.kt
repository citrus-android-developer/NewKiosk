package com.citrus.pottedplantskiosk.api.remote.dto

data class DeliveryInfo(
    val goodsList: List<Good>,
    val payWay: PayWay,
    val orderNo: String,
    val gst: Double,
    val grandTotal: Double
)