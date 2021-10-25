package com.citrus.pottedplantskiosk.api.remote.dto

import com.citrus.pottedplantskiosk.R

data class PayWay(val imgRes: Int, val desc: String)


fun payWayList(): List<PayWay> {
    return listOf(
        PayWay(R.drawable.credit_cards, "Credit Cards"),
        PayWay(R.drawable.dollar, "Cash")
    )
}

