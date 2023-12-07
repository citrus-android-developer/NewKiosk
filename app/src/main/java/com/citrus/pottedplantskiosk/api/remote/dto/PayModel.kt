package com.citrus.pottedplantskiosk.api.remote.dto

data class PayModel(
    var mPayName: String,
    var mPayPath: Int,
    var isWallet: Boolean,
    var orderDeliveryData : OrderDeliveryData? = null
)