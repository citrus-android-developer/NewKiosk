package com.citrus.pottedplantskiosk.api.remote.dto

import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.add
import com.citrus.pottedplantskiosk.util.div
import com.citrus.pottedplantskiosk.util.mul
import com.citrus.pottedplantskiosk.util.round
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
    var add: List<Add>,
    @SerializedName("Note")
    var note: String?,
    @SerializedName("BarCode")
    val barCode: String,
    @SerializedName("ChgPrice")
    val chgPrice: String,
    @SerializedName("flavor")
    var flavor: List<Flavor>,
    @SerializedName("GID")
    var gID: String,
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
    @SerializedName("PrintGroup")
    val printGroup: String,
    @SerializedName("Price")
    var price: Double,
    @SerializedName("Size")
    var size: List<Size>?,
    @SerializedName("Stock")
    val stock: Int,
    @SerializedName("Tax")
    var tax: Double,
    @SerializedName("TaxID")
    var taxID: String,
    var isEdit: Boolean = false,
    var isScan: Boolean = false,
    var gst: Double = 0.0,
    /**bundle會遺失內部參數,以下兩個屬性的存在是為了保留內部屬性*/
    var _qty:Int,
    var _sPrice:Double
) : Serializable {


    init {
        setQtyChangedListener { qty ->
            sPrice = qty * price
            _qty = qty
        }

        setPriceChangedListener { price ->
            sPrice = qty * price
            _sPrice = sPrice
        }

        /**
         * 單品稅額以3位小數計算加總後小數第二位四捨五入,現金和信用卡有所區別，前者依設定內的小數位數+進位法計算結果
         * */
        setsPriceChangedListener { sPrice ->
            _sPrice = sPrice
            val itemTaxPct = tax
            if (itemTaxPct != 0.0) {
                var taxBase = 0.0
                when (prefs.taxFunction) {
                    0 -> return@setsPriceChangedListener
                    1 -> taxBase = add(itemTaxPct, 100.0)
                    2 -> taxBase = 100.0
                }
                gst = round(mul(sPrice, div(itemTaxPct, taxBase)), 3)
            }
        }
}

private var qtyChangedListener: ((Int) -> Unit)? = null
private fun setQtyChangedListener(listener: (Int) -> Unit) {
    qtyChangedListener = listener
}

private var priceChangedListener: ((Double) -> Unit)? = null
private fun setPriceChangedListener(listener: (Double) -> Unit) {
    priceChangedListener = listener
}

private var sPriceChangedListener: ((Double) -> Unit)? = null
private fun setsPriceChangedListener(listener: (Double) -> Unit) {
    sPriceChangedListener = listener
}


var sPrice = 0.0
    set(value) {
        synchronized(field) {
            field = value
            sPriceChangedListener?.let { change ->
                change(value)
            }
        }
    }

var qty = 0
    set(value) {
        synchronized(field) {
            field = value
            qtyChangedListener?.let { change ->
                change(value)
            }
        }
    }

var _Price: Double = 0.0
    get() = price
    set(value) {
        synchronized(field) {
            field = value
            priceChangedListener?.let { change ->
                change(value)
            }
        }
    }

/**avoid shallow copy*/
fun deepCopy(
    add: List<Add> = this.add.map { it.copy() },
    flavor: List<Flavor> = this.flavor.map { it.copy() },
    size: List<Size>? = this.size?.map { it.copy() } ?: null,
) = this.copy(add = add, flavor = flavor, size = size)

}


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
    val stock: String,
    var isCheck: Boolean = false
) : Serializable