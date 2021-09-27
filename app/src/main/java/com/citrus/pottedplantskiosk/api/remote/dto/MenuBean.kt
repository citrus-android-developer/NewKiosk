package com.citrus.pottedplantskiosk.api.remote.dto

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class MenuBean(
    @SerializedName("data")
    val data: Data,
    @SerializedName("status")
    val status: Int
) : Serializable

data class Data(
    @SerializedName("MainGroup")
    val mainGroup: List<MainGroup>
) : Serializable


data class MainGroup(
    @SerializedName("GroupName")
    val groupName: String,
    @SerializedName("Kind")
    val kind: List<Kind>
) : Serializable

data class Kind(
    @SerializedName("Desc")
    val desc: String,
    @SerializedName("GKID")
    val gKID: String,
    @SerializedName("Goods")
    val goods: List<Good>
) : Serializable


data class Good(
    @SerializedName("add")
    val add: List<Add>,
    @SerializedName("BarCode")
    val barCode: String,
    @SerializedName("ChgPrice")
    val chgPrice: String,
    @SerializedName("flavor")
    val flavor: List<Flavor>,
    @SerializedName("GID")
    val gID: String,
    @SerializedName("GKID")
    val gKID: String,
    @SerializedName("GName")
    val gName: String,
    @SerializedName("GName2")
    val gName2: String,
    @SerializedName("GType")
    val gType: String,
    @SerializedName("IsPrint")
    val isPrint: String,
    @SerializedName("picname")
    val picName: String,
    @SerializedName("Price")
    val price: Double,
    @SerializedName("PrintGroup")
    val printGroup: String,
    @SerializedName("Size")
    val size: List<Size>,
    @SerializedName("Stock")
    val stock: Int,
    @SerializedName("Tax")
    val tax: Int,
    @SerializedName("TaxID")
    val taxID: String
) : Serializable


data class Add(
    @SerializedName("GID")
    val gID: String,
    @SerializedName("GName")
    val gName: String,
    @SerializedName("GName2")
    val gName2: String,
    @SerializedName("Price")
    val price: Double
) : Serializable


data class Flavor(
    @SerializedName("Desc")
    val desc: String,
    @SerializedName("FlavorID")
    val flavorID: Int,
    @SerializedName("GName2")
    val gName2: String
) : Serializable


data class Size(
    @SerializedName("Desc")
    val desc: String,
    @SerializedName("GID")
    val gID: String,
    @SerializedName("Price")
    val price: Double,
    @SerializedName("SID")
    val sID: String,
    @SerializedName("SName2")
    val sName2: String,
    @SerializedName("Stock")
    val stock: Int
) : Serializable