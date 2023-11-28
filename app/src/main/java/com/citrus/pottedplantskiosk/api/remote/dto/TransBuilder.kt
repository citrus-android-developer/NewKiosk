package com.citrus.pottedplantskiosk.api.remote.dto


import com.citrus.pottedplantskiosk.util.GlobalConstants.ECR_ECHO
import com.citrus.pottedplantskiosk.util.GlobalConstants.ECR_INQUIRY
import com.citrus.pottedplantskiosk.util.GlobalConstants.ECR_PRE_AUTH
import com.citrus.pottedplantskiosk.util.GlobalConstants.ECR_PRE_AUTH_CAPTURE
import com.citrus.pottedplantskiosk.util.GlobalConstants.ECR_REFUND
import com.citrus.pottedplantskiosk.util.GlobalConstants.ECR_SALE
import com.citrus.pottedplantskiosk.util.GlobalConstants.ECR_SETTLEMENT
import com.citrus.pottedplantskiosk.util.GlobalConstants.ECR_TIP_ADJUST
import com.citrus.pottedplantskiosk.util.GlobalConstants.ECR_VOID
import com.citrus.pottedplantskiosk.util.GlobalConstants.ECR_WALLET_SALE
import org.json.JSONException
import org.json.JSONObject

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

class TransBuilder {
    val REAL_FORMATTER = DecimalFormat("0.00")

    //BUILD SALE OBJECT TO BE SENT TO PAYMENT APP
    fun getSaleObject(amount: String?,orderNo:String): TransRequestData {
        val `object` = TransRequestData()
        `object`.transaction_amount = getConvertedAmount(amount)
        `object`.transaction_type = ECR_SALE
        `object`.command_identifier = orderNo
        return `object`
    }

    //BUILD SALE OBJECT TO BE SENT TO PAYMENT APP
    fun getWalletSaleObject(amount: String?): TransRequestData {
        val `object` = TransRequestData()
        `object`.transaction_amount = getConvertedAmount(amount)
        `object`.transaction_type = ECR_WALLET_SALE
        `object`.command_identifier = currentDate
        `object`.custom_data_3 = "GrabPay" //ex: Dash, GrabPay,Alipay,Wechat
        return `object`
    }

    //BUILD REFUND OBJECT TO BE SENT TO PAYMENT APP
    fun getRefundObject(amount: String?,orderNo:String): TransRequestData {
        val `object` = TransRequestData()
        `object`.transaction_amount = getConvertedAmount(amount)
        `object`.transaction_type = ECR_REFUND
        `object`.command_identifier = currentDate
        return `object`
    }

    val settleObject: TransRequestData
        //BUILD SETTLEMENT OBJECT TO BE SENT TO PAYMENT APP
        get() {
            val `object` = TransRequestData()
            `object`.transaction_type = ECR_SETTLEMENT
            `object`.command_identifier = currentDate
            return `object`
        }

    //BUILD VOID OBJECT TO BE SENT TO PAYMENT APP
    fun getVoidObject(invoiceNo: String?): TransRequestData {
        val `object` = TransRequestData()
        `object`.transaction_type = ECR_VOID
        `object`.invoice_number = invoiceNo!!
        `object`.command_identifier = currentDate
        return `object`
    }

    //BUILD PREAUTH OBJECT TO BE SENT TO PAYMENT APP
    fun getPreauthObject(amount: String?): TransRequestData {
        val `object` = TransRequestData()
        `object`.transaction_amount = getConvertedAmount(amount)
        `object`.transaction_type = ECR_PRE_AUTH
        `object`.command_identifier = currentDate
        return `object`
    }

    //BUILD OFFLINE OBJECT TO BE SENT TO PAYMENT APP
    fun getOfflineObject(amount: String?): TransRequestData {
        val `object` = TransRequestData()
        `object`.transaction_amount = getConvertedAmount(amount)
        `object`.transaction_type = ECR_PRE_AUTH_CAPTURE
        `object`.command_identifier = currentDate
        return `object`
    }

    val echoObject: String
        //BUILD ECHO OBJECT TO BE SENT TO PAYMENT APP FOR GETTING THE STATUS
        get() {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("transaction_type", ECR_ECHO)
                jsonObject.put("command_identifier", "1")
                jsonObject.put("transaction_amount", "0")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jsonObject.toString()
        }

    //BUILD TIP/ADJUST OBJECT TO BE SENT TO PAYMENT APP
    fun getTipObject(invoiceNo: String?, tipAmount: String?): String {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("invoice_number", invoiceNo)
            jsonObject.put("transaction_type", ECR_TIP_ADJUST)
            jsonObject.put("command_identifier", "1")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject.toString()
    }

    //BUILD INQUIRY OBJECT TO BE SENT TO PAYMENT APP TO GET LAST TRANSACTION DETAILS
    fun getInquiryObject(traceNumber: String?): String {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("transaction_type", ECR_INQUIRY)
            jsonObject.put("command_identifier", traceNumber)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject.toString()
    }

    private fun getConvertedAmount(amount: String?): String {
        var amount = amount
        amount = if (!amount.isNullOrEmpty()) {
            amount.replace(".", "")
        } else {
            "0"
        }
        return amount
    }

    companion object {
        private var instance: TransBuilder? = null
        fun getInstance(): TransBuilder? {
            if (instance == null) instance = TransBuilder()
            return instance
        }

        val currentDate: String
            get() {
                val today = Date()
                val format =
                    SimpleDateFormat("yyyyMMddHHmmss")
                return format.format(today)
            }
    }
}