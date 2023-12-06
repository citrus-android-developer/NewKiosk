package com.citrus.pottedplantskiosk.api.remote.dto

import com.citrus.pottedplantskiosk.di.prefs
import com.citrus.pottedplantskiosk.util.add
import com.citrus.pottedplantskiosk.util.div
import com.citrus.pottedplantskiosk.util.mul
import com.citrus.pottedplantskiosk.util.round
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MenuBean(
    @SerializedName("MainGroup")
    var mainGroup: List<MainGroup>,
    @SerializedName("status")
    var status: Int
) : Serializable

data class MainGroup(
    @SerializedName("GroupName")
    var groupName: String,
    @SerializedName("Kind")
    var kind: List<Kind>
) : Serializable

data class Kind(
    @SerializedName("Desc")
    var desc: String,
    @SerializedName("GKID")
    var gKID: String,
    @SerializedName("Goods")
    var goods: List<Good>
) : Serializable

data class Good(
    @SerializedName("add")
    var add: List<Add>?,
    @SerializedName("BarCode")
    var barCode: String?,
    @SerializedName("ChgPrice")
    var chgPrice: String?,
    @SerializedName("Color")
    var color: String?,
    @SerializedName("Desc")
    var desc: String?,
    @SerializedName("Desc2")
    var desc2: String?,
    @SerializedName("flavor")
    var flavor: List<Flavor>?,
    @SerializedName("GID")
    var gID: String,
    @SerializedName("GKID")
    var gKID: String,
    @SerializedName("GName")
    var gName: String?,
    @SerializedName("GName2")
    var gName2: String?,
    @SerializedName("GType")
    var gType: String,
    @SerializedName("Group")
    var group: List<Group>?,
    @SerializedName("IsPrint")
    var isPrint: String?,
    @SerializedName("Note")
    var note: String?,
    @SerializedName("picname")
    var picname: String?,
    @SerializedName("Price")
    var price: Double?,
    @SerializedName("PrintGroup")
    var printGroup: String?,
    @SerializedName("SID")
    var sID: String?,
    @SerializedName("SOType")
    var sOType: String?,
    @SerializedName("Size")
    var size: List<Size>?,
    @SerializedName("SoldOut")
    var soldOut: String?,
    @SerializedName("Stock")
    var stock: String?,
    @SerializedName("Tax")
    var tax: Int?,
    @SerializedName("TaxID")
    var taxID: String?,
    var isEdit: Boolean = false,
    var isScan: Boolean = false,
    var gst: Double = 0.0,
    /**bundle會遺失內部參數,以下兩個屬性的存在是為了保留內部屬性*/
    var _qty: Int,
    var _sPrice: Double
) : Serializable {

    init {
        setQtyChangedListener { qty ->
            sPrice = qty * (price ?: 0.0)
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
            val itemTaxPct = (tax ?: 0) / 100.0
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
        get() = price ?: 0.0
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
        add: List<Add>? = this.add?.map { it.copy() } ?: listOf(),
        flavor: List<Flavor>? = this.flavor?.map { it.copy() } ?: listOf(),
        size: List<Size>? = this.size?.map { it.copy() },
    ) = this.copy(add = add, flavor = flavor, size = size)

}

data class Group(
    @SerializedName("Goods")
    var goods: List<Good>,
    @SerializedName("MGID")
    var mGID: String,
    @SerializedName("MGKID")
    var mGKID: String,
    @SerializedName("MSGroup")
    var mSGroup: String,
    @SerializedName("SOType")
    var sOType: String,
    @SerializedName("SoldOut")
    var soldOut: String,
    @SerializedName("TotalQTY")
    var totalQTY: Int
) : Serializable

data class Size(
    @SerializedName("Color")
    var color: String,
    @SerializedName("Desc")
    var desc: String,
    @SerializedName("GID")
    var gID: String,
    @SerializedName("Note")
    var note: String,
    @SerializedName("price")
    var price: Double,
    @SerializedName("SID")
    var sID: String,
    @SerializedName("SName2")
    var sName2: String,
    @SerializedName("SOType")
    var sOType: String,
    @SerializedName("Stock")
    var stock: String,
    var isCheck: Boolean = false
) : Serializable


data class Add(
    @SerializedName("GID")
    val gID: String,
    @SerializedName("GName")
    val gName: String,
    @SerializedName("GName2")
    val gName2: String,
    @SerializedName("Price")
    val price: Double,
    @SerializedName("Qty")
    val qty: Int
) : Serializable


data class Flavor(
    @SerializedName("FlavorName")
    val desc: String?,
    @SerializedName("FlavorID")
    val flavorID: String?,
    @SerializedName("GName2")
    val gName2: String?
) : Serializable


