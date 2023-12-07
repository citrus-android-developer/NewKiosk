package com.citrus.pottedplantskiosk.api.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class OrdersRequest(
    @SerializedName("DeviceID")
    var deviceID: String,
    @SerializedName("Orders")
    var orders: Orders,
    @SerializedName("OrdersItem")
    var ordersItem: List<OrdersItem>,
    @SerializedName("OrdersPayway")
    var ordersPayway: OrdersPayway
) : Serializable

data class Orders(
    @SerializedName("CName")
    var cName: String,
    @SerializedName("CardNO")
    var cardNO: String,
    @SerializedName("CardType")
    var cardType: String,
    @SerializedName("CardUser")
    var cardUser: String,
    @SerializedName("CloseTime")
    var closeTime: Any,
    @SerializedName("commission")
    var commission: Int,
    @SerializedName("CustNo")
    var custNo: String,
    @SerializedName("cust_num")
    var custNum: Int,
    @SerializedName("DelReason")
    var delReason: String,
    @SerializedName("DelUser")
    var delUser: String,
    @SerializedName("Deliver")
    var deliver: String,
    @SerializedName("deskno")
    var deskno: String,
    @SerializedName("DisAMT")
    var disAMT: String,
    @SerializedName("DisID")
    var disID: String,
    @SerializedName("DisPrice")
    var disPrice: Int,
    @SerializedName("DisQty")
    var disQty: Int,
    @SerializedName("DisUnit")
    var disUnit: String,
    @SerializedName("DisValue")
    var disValue: String,
    @SerializedName("GPrice")
    var gPrice: Int,
    @SerializedName("Invamt")
    var invamt: Int,
    @SerializedName("InvoiceFail")
    var invoiceFail: String,
    @SerializedName("IsClose")
    var isClose: String,
    @SerializedName("IsDataToERP")
    var isDataToERP: String,
    @SerializedName("IsDel")
    var isDel: String,
    @SerializedName("IsSend")
    var isSend: String,
    @SerializedName("Memo")
    var memo: String,
    @SerializedName("minispend")
    var minispend: Int,
    @SerializedName("OrderHR")
    var orderHR: String,
    @SerializedName("OrderNote")
    var orderNote: String,
    @SerializedName("OrderState")
    var orderState: String,
    @SerializedName("OrderTime")
    var orderTime: String,
    @SerializedName("PayType")
    var payType: String,
    @SerializedName("Payee")
    var payee: String,
    @SerializedName("Qty")
    var qty: Int,
    @SerializedName("RVC")
    var rVC: String,
    @SerializedName("RandomCode")
    var randomCode: String,
    @SerializedName("Rounding")
    var rounding: Int,
    @SerializedName("SPrice")
    var sPrice: Int,
    @SerializedName("ServiceCust")
    var serviceCust: String,
    @SerializedName("ServiceOutStatus")
    var serviceOutStatus: String,
    @SerializedName("ServicePric")
    var servicePric: Int,
    @SerializedName("ServiceType")
    var serviceType: String,
    @SerializedName("Status")
    var status: String,
    @SerializedName("Tel")
    var tel: String,
    @SerializedName("totaltax")
    var totaltax: Int,
    @SerializedName("UserID")
    var userID: String,
    @SerializedName("weight")
    var weight: Int,
    @SerializedName("WorkClass")
    var workClass: String
) : Serializable

data class OrdersItem(
    @SerializedName("AddGID")
    var addGID: String,
    @SerializedName("AddGName")
    var addGName: String,
    @SerializedName("AddGname2")
    var addGname2: String,
    @SerializedName("AddPrice")
    var addPrice: Int,
    @SerializedName("commission")
    var commission: Int,
    @SerializedName("CreateDate")
    var createDate: String,
    @SerializedName("CreateUser")
    var createUser: String,
    @SerializedName("DelReason")
    var delReason: String,
    @SerializedName("DisID")
    var disID: String,
    @SerializedName("DisPrice")
    var disPrice: Int,
    @SerializedName("finishtime")
    var finishtime: String,
    @SerializedName("FlavorDesc")
    var flavorDesc: String,
    @SerializedName("FlavorDesc2")
    var flavorDesc2: String,
    @SerializedName("FlavorID")
    var flavorID: String,
    @SerializedName("GID")
    var gID: String,
    @SerializedName("GKID")
    var gKID: String,
    @SerializedName("GName2")
    var gName2: String,
    @SerializedName("GPrice")
    var gPrice: Int,
    @SerializedName("GType")
    var gType: String,
    @SerializedName("Gname")
    var gname: String,
    @SerializedName("GoodsPageID")
    var goodsPageID: String,
    @SerializedName("IsDel")
    var isDel: String,
    @SerializedName("IsPrint")
    var isPrint: String,
    @SerializedName("IsSend")
    var isSend: String,
    @SerializedName("ItemStatus")
    var itemStatus: String,
    @SerializedName("kdstime")
    var kdstime: String,
    @SerializedName("KindPageID")
    var kindPageID: String,
    @SerializedName("KitStatic")
    var kitStatic: String,
    @SerializedName("MGID")
    var mGID: String,
    @SerializedName("MGKID")
    var mGKID: String,
    @SerializedName("OrderSeq")
    var orderSeq: Int,
    @SerializedName("PrintGroup")
    var printGroup: String,
    @SerializedName("processtime")
    var processtime: String,
    @SerializedName("Qty")
    var qty: Int,
    @SerializedName("RVC")
    var rVC: String,
    @SerializedName("SPrice")
    var sPrice: Int,
    @SerializedName("ServiceType")
    var serviceType: String,
    @SerializedName("SubCnt")
    var subCnt: Int,
    @SerializedName("SubGKID")
    var subGKID: String,
    @SerializedName("tax")
    var tax: Int,
    @SerializedName("weight")
    var weight: Int
) : Serializable


data class OrdersPayway(
    var orderId: String,
    var payNo: String,
    var paydesc: String,
    var unitCnt: String,
    var pPrice: String,
    var overPrice: String,
    var overStatus: String,
    var totaltax: String,
    var cardNo: String,
    var balance: String,
    var tmlID: String,
    var tnsdata: String
) : Serializable