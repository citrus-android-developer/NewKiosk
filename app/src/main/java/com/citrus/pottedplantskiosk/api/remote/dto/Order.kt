package com.citrus.pottedplantskiosk.api.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable


    data class OrderDeliveryData (
        @SerializedName("OrdersDelivery")
        val ordersDelivery: OrdersDelivery,

        @SerializedName("OrdersItemDelivery")
        val ordersItemDelivery: List<OrdersItemDelivery>
    ):Serializable


    data class OrdersDelivery (
        @SerializedName("StoreID")
        val storeID: Int,

        @SerializedName("OrderHR")
        val orderHR: String = "",

        @SerializedName("GPrice")
        val gPrice: Double = 0.0,

        @SerializedName("DisPrice")
        val disPrice: Double = 0.0,

        @SerializedName("SPrice")
        val sPrice: Double ,

        @SerializedName("IsSend")
        val isSend: String = "Y",

        @SerializedName("CloseTime")
        val closeTime: String ="",

        @SerializedName("IsClose")
        val isClose: String ="",

        @SerializedName("IsDel")
        val isDel: String ="",

        @SerializedName("DelReason")
        val delReason: String ="",

        @SerializedName("ServiceType")
        val serviceType: String ="",

        @SerializedName("PayType")
        val payType: String,

        @SerializedName("WorkClass")
        val workClass: String ="",

        @SerializedName("Qty")
        val qty: Int,

        @SerializedName("DisID")
        val disID: String ="",

        @SerializedName("DisQty")
        val disQty: Int = 0,

        @SerializedName("CName")
        val cName: String ="",

        @SerializedName("Tel")
        val tel: String ="",

        @SerializedName("IsDataToERP")
        val isDataToERP: String ="",

        @SerializedName("UserID")
        val userID: String ="",

        @SerializedName("Status")
        val status: String ="",

        @SerializedName("Invamt")
        val invamt: Int = 0,

        @SerializedName("CustNo")
        val custNo: String ="",

        @SerializedName("ServiceOutStatus")
        val serviceOutStatus: String ="",

        @SerializedName("InvoiceFail")
        val invoiceFail: String ="",

        @SerializedName("cust_num")
        val custNum: Int = 1,

        val deskno: String = "",

        @SerializedName("Deliver")
        val deliver: String = "",

        @SerializedName("CardType")
        val cardType: String = "",

        @SerializedName("CardUser")
        val cardUser: String ="",

        @SerializedName("CardNO")
        val cardNO: String ="",

        @SerializedName("ServiceCust")
        val serviceCust: String ="",

        @SerializedName("OrderState")
        val orderState: String ="",

        @SerializedName("ServicePric")
        val servicePrice: Double = 0.0,

        @SerializedName("OrderNote")
        val orderNote: String ="",

        @SerializedName("Rounding")
        val rounding: Int = 0,

        @SerializedName("Memo")
        val memo: String ="",

        @SerializedName("DelUser")
        val delUser: String ="",

        val totaltax: Double = 0.0,
        val weight: Int = 0,

        @SerializedName("IsPay")
        val isPay: String,

        val tkey: String = ""
    ):Serializable


    data class OrdersItemDelivery (
        @SerializedName("StoreID")
        val storeID: Int,

        @SerializedName("OrderNO")
        var orderNO: String = "",

        @SerializedName("OrderSeq")
        val orderSeq: Int,

        @SerializedName("GID")
        val gid: String,

        @SerializedName("GPrice")
        val gPrice: Double = 0.0,

        @SerializedName("DisPrice")
        val disPrice: Double = 0.0,

        @SerializedName("SPrice")
        val sPrice: Double,

        @SerializedName("Qty")
        val qty: Int,

        @SerializedName("AddGID")
        val addGID: String = "",

        @SerializedName("AddGName")
        val addGName: String = "",

        @SerializedName("FlavorID")
        val flavorID: String = "",

        @SerializedName("FlavorDesc")
        val flavorDesc: String = "",

        @SerializedName("IsDel")
        val isDel: String = "",

        @SerializedName("DelReason")
        val delReason: String = "",

        @SerializedName("Gname")
        val gname: String,

        @SerializedName("AddPrice")
        val addPrice: Double = 0.0,

        @SerializedName("IsSend")
        val isSend: String = "Y",

        @SerializedName("GKID")
        val gkid: String,

        @SerializedName("IsPrint")
        val isPrint: String = "",

        @SerializedName("GType")
        val gType: String,

        @SerializedName("SubGKID")
        val subGKID: String = "",

        @SerializedName("MGKID")
        val mgkid: String = "",

        @SerializedName("MGID")
        val mgid: String = "",

        @SerializedName("SubCnt")
        val subCnt: Int = 0,

        @SerializedName("KindPageID")
        val kindPageID: String = "",

        @SerializedName("GoodsPageID")
        val goodsPageID: String = "",

        @SerializedName("KitStatic")
        val kitStatic: String = "",

        @SerializedName("PrintGroup")
        val printGroup: String  = "",

        @SerializedName("ServiceType")
        val serviceType: String  = "",

        @SerializedName("ItemStatus")
        val itemStatus: String  = "",

        @SerializedName("DisID")
        val disID: String  = "",

        val tax: Double  = 0.0,
        val weight: Int = 0,

        @SerializedName("GName2")
        val gName2: String  = "",

        val kdstime: String  = "",

        @SerializedName("Flag")
        val flag: String  = "",

        @SerializedName("CreateUser")
        val createUser: String  = "",

        @SerializedName("CreateDate")
        val createDate: String  = "",

        @SerializedName("UpdateUser")
        val updateUser: String  = "",

        @SerializedName("UpdateDate")
        val updateDate: String  = "",

        @SerializedName("AddGname2")
        val addGname2: String  = "",

        @SerializedName("FlavorDesc2")
        val flavorDesc2: String  = ""
    ):Serializable
