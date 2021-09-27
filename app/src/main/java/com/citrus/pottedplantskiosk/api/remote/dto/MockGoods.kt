package com.citrus.pottedplantskiosk.api.remote.dto


import java.io.Serializable


data class MockGoods(var imageRes: Int, var ItemName: String, var ItemPrice:Int, var qty:Int): Serializable


