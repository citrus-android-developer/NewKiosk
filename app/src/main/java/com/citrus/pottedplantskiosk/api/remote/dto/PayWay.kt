package com.citrus.pottedplantskiosk.api.remote.dto

import com.citrus.pottedplantskiosk.R
import com.citrus.pottedplantskiosk.util.Constants

data class PayWay(val imgRes: Int, val payNo : Constants.PayWayType, val desc: String)


fun payWayList(): List<PayWay> {
    return listOf(
        PayWay(R.drawable.credit_cards, Constants.PayWayType.CreditCard,"Credit Card"),
        PayWay(R.drawable.dollar, Constants.PayWayType.Cash,"Cash")
    )
}

