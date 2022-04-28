package com.citrus.pottedplantskiosk.api.remote.dto

import java.io.Serializable

data class EdcData(
    /**交易別 maxLength(2)*/
    val Trans_Type: String,

    /**銀行別(授權中心編碼) maxLength(2)*/
    val Host_ID: String? = null,

    /**端末機簽單序號(調閱編號)(取消須送此欄位) maxLength(6)*/
    val Receipt_No: String? = null,

    /**信用卡卡號(左靠右補空白) maxLength(19)*/
    val Card_No: String? = null,

    /**交易金額 maxLength(var.)*/
    val Trans_Amount: String? = null,

    /**交易日期 maxLength(6)*/
    val Trans_Date: String? = null,

    /**交易時間 maxLength(6)*/
    val Trans_Time: String? = null,

    /**授權碼(左靠右補空白) maxLength(9)*/
    val Approval_No: String? = null,

    /**通訊回應碼 maxLength(4)*/
    val ECR_Response_Code: String? = null,

    /**EDC 端末機代號 maxLength(8)*/
    val EDC_Terminal_ID: String? = null,

    /**Req：櫃號、機號或發票號碼 Resp：RRN 或訂單編號 maxLength(18)*/
    val Store_ID: String? = null,

    /**卡別 maxLength(2)*/
    val Card_Type: String? = null,

    /**C：CUP(銀聯)，N：Default maxLength(1)*/
    val CUP_Indicator: String? = null,

    /**Y：檢核聯名卡，N：不檢核 maxLength(1)*/
    val Member_Check: String? = null,

    /**發卡單位代號(Smart Pay 回送) Smart Pay 交易：發卡單位代號，左靠右補空白。 maxLength(8)*/
    val Issuer_ID: String? = null,

    /**第二代電子發票載具前6碼(B00+3碼銀行代碼)，
    ex: B00812
    第二代電子發票載具由 Bank_ ID (6 Bytes) +
    Card_ No_Vehicle (44 Bytes)所組成共 50Bytes；
    當此欄位值為空白，代表不支援二代電子發票載
    具。 maxLength(6)*/
    val Bank_ID: String? = null,

    /**電子發票信用卡載具 maxLength(44)*/
    val Card_No_Vehicle: String? = null,

    /**Y=交易結束(不繼續接收資料) N=交易尚未結束(繼續接收資料) maxLength(1)*/
    val Transaction_Finished: String? = null,

    /**逾時時間重置
    Transaction_Finished 欄位值=N，POS 需判斷此欄
    位
    1. 欄位值=“ ”或“0000”，時間不需重置
    2. 欄位值=“0060”，POS 需重新設置逾時時間並
    從 60 秒起始 maxLength(4)*/
    val Timer_Block_Reset: String? = null,

    /**影像簽單交易類別
    ‘1’=電簽交易
    ‘2’=紙本交易
    ‘3’=免簽交易 maxLength(1)*/
    val Signature_Type: String? = null,

    /**交易序號(RRN)或訂單編號 maxLength(12)*/
    val Reference_No: String? = null,

    /**小費金額(供小費一條龍回傳給 POS) maxLength(var.)*/
    val Tip_Amount: String? = null,

    /**分期作業處理費 maxLength(var.)*/
    val Installment_Process_Fee: String? = null,

    /**過卡方式 maxLength(2)*/
    val Entry_Mode: String? = null,

    /**特店代號 maxLength(15)*/
    val EDC_Merchant_ID: String? = null,

    /**批次號碼 maxLength(8)*/
    val Batch_No: String? = null
) : Serializable