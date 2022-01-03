package com.citrus.pottedplantskiosk.util

class EcrProtocol {
    /** 1.
     * 新 ECR 連線 Indicator"I"
     * 新 ECR 連線 Indicator"E"
     * (僅欄位 37 內容和"I"不同， 其他欄位處理皆和"I"相同)*/
    var ecrIndicator = ""
        set(value) {
            field = fillSpace(1, value)
        }

    /**2.
     * ECR 版本日期*/
    var ecrVersionDate = ""
        set(value) {
            field = fillSpace(6, value)
        }

    /**3.
     * 支援交易別:一般交易、退貨*/
    var transTypeIndicator = ""
        set(value) {
            field = fillSpace(1, value)
        }

    /**4.
     * 交易別*/
    var transType = ""
        set(value) {
            field = fillSpace(2, value)
        }

    /**5.
     * 支援交易別:一般交易、退貨。
     * Indicator=0(數字 0)時，表示由端末機 HOT KEY 選擇
     * Indicator="N"時，表示為一般信用卡交易*/
    var cupIndicator = ""
        set(value) {
            field = fillSpace(1, value)
        }

    /**6.
     * 主機別*/
    var hostID = ""
        set(value) {
            field = fillSpace(2, value)
        }

    /**7.
     * 調閱編號*/
    var receiptNo = ""
        set(value) {
            field = fillSpace(6, value)
        }

    /**8.
     * 信用卡卡號 (左靠右補空白)前 6 後 4 為明碼，卡號是否 遮掩依 TMS 參數設定為準，遮掩字元為”*”*/
    var cardNo = ""
        set(value) {
            field = fillSpace(19, value)
        }

    /**9.
     * 信用卡有效期預設不回傳,避免未來需求需傳有效期，依 TMS 參數設定為準*/
    var cardExpireDate = ""
        set(value) {
            field = fillSpace(4, value)
        }

    /**10.
     * 交易金額(包括兩位小數)(小數為 0) 未使用金額部分填空白，
     * 例如: 123 元，表示為□□□□□□□12300 □表示空白*/
    var transAmount = ""
        set(value) {
            field = fillSpace(12, value)
        }

    /**11.
     * 交易日期 (格式為西元年 YYMMDD)*/
    var transDate = ""
        set(value) {
            field = fillSpace(6, value)
        }

    /**12.
     * 交易時間 (格式為二十四小時制 hhmmss)*/
    var transTime = ""
        set(value) {
            field = fillSpace(6, value)
        }

    /**13.
     * 授權碼 (左靠右補空白)*/
    var approvalNo = ""
        set(value) {
            field = fillSpace(9, value)
        }

    /**14.
     * V=VISA 感應卡
     * J=JCB 感應卡
     * D=DFS(Discover card)感應卡
     * M=M/C 感應卡
     * A=AMEX 感應卡
     * O(英文字母 O)=其它卡*/
    var waveCardIndicator = ""
        set(value) {
            field = fillSpace(1, value)
        }

    /**15.
     * 通訊回應碼*/
    var ecrResponseCode = ""
        set(value) {
            field = fillSpace(4, value)
        }

    /**16.
     * EDC商店代號*/
    var merchantId = ""
        set(value) {
            field = fillSpace(15, value)
        }

    /**17.
     * EDC 端末機代號*/
    var terminalId = ""
        set(value) {
            field = fillSpace(8, value)
        }

    /**18.
     * 其他金額(小費) (包括兩位小數)(小數為 0)
     * 未使用金額部分填空白，
     * 例如: 123 元，表示為□□□□□□□12300 □表示空白*/
    var expAmount = ""
        set(value) {
            field = fillSpace(12, value)
        }

    /**19.
     * 櫃號 (發票號碼)或(櫃號+機號+ECR 交易序號)*/
    var storeId = ""
        set(value) {
            field = fillSpace(18, value)
        }

    /**20.
     * "1"全額扣抵
     * "2"部份扣抵
     * "I"內含手續費
     * "E"外加手續費*/
    var installmentRedeemIndicator = ""
        set(value) {
            field = fillSpace(1, value)
        }

    /**21.
     * 實際支付金額(含小數 2 位) (小數為 0)
     * 未使用金額部分填空白，
     * 例如: 123 元，表示為□□□□□□□12300 □表示空白*/
    var rdmPaidAmt = ""
        set(value) {
            field = fillSpace(12, value)
        }

    /**22.
     * 信用卡紅利扣抵點數(沒有小數位) [右靠，左補空白]*/
    var rdmPoint = ""
        set(value) {
            field = fillSpace(8, value)
        }

    /**23.
     * 剩餘紅利點數(沒有小數位) [右靠，左補空白]*/
    var pointsOfBalance = ""
        set(value) {
            field = fillSpace(8, value)
        }

    /**24.
     * 折抵金額(含小數 2 位) (小數為 0) 未使用金額部分填空白，
     * 例如: 123 元，表示為□□□□□□□12300 □表示空白*/
    var redeemAmt = ""
        set(value) {
            field = fillSpace(12, value)
        }

    /**25.
     * 信用卡分期期數*/
    var installmentPeriod = ""
        set(value) {
            field = fillSpace(2, value)
        }

    /**26.
     * 首期金額(含小數 2 位) (小數為 0) 未使用金額部分填空白，
     * 例如: 123 元，表示為□□□□□□□12300 □表示空白*/
    var downPaymentAmount = ""
        set(value) {
            field = fillSpace(12, value)
        }

    /**27.
     * 每期金額(含小數 2 位) (小數為 0) 未使用金額部分填空白，
     * 例如: 123 元，表示為□□□□□□□12300 □表示空白*/
    var installmentPaymentAmount = ""
        set(value) {
            field = fillSpace(12, value)
        }

    /**28.
     * 分期手續費(含小數 2 位) (小數為 0) 未使用金額部分填空白，
     * 例如: 123 元，表示為□□□□□□□12300 □表示空白*/
    var formalityFee = ""
        set(value) {
            field = fillSpace(12, value)
        }

    /**29.
     * 卡別*/
    var cardType = ""
        set(value) {
            field = fillSpace(2, value)
        }

    /**30.
     *批次號碼*/
    var batchNo = ""
        set(value) {
            field = fillSpace(6, value)
        }

    /**31.*/
    var startTransType = ""
        set(value) {
            field = fillSpace(2, value)
        }

    /**32.
     * 是否為小額支付交易
     * "M"=小額支付
     * Space(0x20)=非小額支付*/
    var mpFlag = ""
        set(value) {
            field = fillSpace(1, value)
        }

    /**33.
     * Smart Pay 發卡行代號(依卡片讀到的格式寫入)*/
    var spIssuerId = ""
        set(value) {
            field = fillSpace(8, value)
        }

    /**34.
     * 原 Smart Pay 消費扣款之交易日期(YYYYMMDD) (Smart Pay 消費扣款退費，收銀機需傳送此欄位)*/
    var spCardOriginDate = ""
        set(value) {
            field = fillSpace(8, value)
        }

    /**35.
     * 原 Smart Pay 消費扣款之調單編號。(Smart Pay 消費扣款退費，收銀機需傳送此欄位)*/
    var spRrn = ""
        set(value) {
            field = fillSpace(12, value)
        }

    /**36.
     * 繳費項目*/
    var payItem = ""
        set(value) {
            field = fillSpace(5, value)
        }

    /**37.
     * 信用卡交易:
     * ECR Indicator="I"，電子發票加密卡號(卡號前 6 碼+44 碼 HASH 值)
     * ECR Indicator="E"，電子發票加密卡號(“B00xxx”+44 碼 HASH 值，xxx 為金融機構代碼)*/
    var cardNoHashValue = ""
        set(value) {
            field = fillSpace(50, value)
        }

    /**38.
     * 小額支付回覆訊息碼(小額支付被拒絕才回覆)*/
    var mpResponseCode = ""
        set(value) {
            field = fillSpace(6, value)
        }

    /**39.
     * 本交易是否符合優惠平台之優惠活動
     * ‘A'=符合優惠
     * Space(0x20)=無優惠*/
    var asmAwardFlag = ""
        set(value) {
            field = fillSpace(1, value)
        }

    /**40.
     * ‘T’=MCP(手機支付)
     * Space(0x20)=一般信用卡支付*/
    var mcpIndicator = ""
        set(value) {
            field = fillSpace(1, value)
        }

    /**41.
     * 金融機構代碼=國內卡
     * ‘999’=國外卡*/
    var codeNo = ""
        set(value) {
            field = fillSpace(3, value)
        }

    /**42.
     * 保留欄位*/
    var reserved = ""
        set(value) {
            field = fillSpace(83, value)
        }


    private fun fillSpace(protocolLimit: Int, param: String): String {
        var resultStringBuilder = StringBuilder()
        var paramLength = param.length
        var needFillSpace = protocolLimit - paramLength
        for (i in 1..needFillSpace) {
            resultStringBuilder.append((0x20).toChar())
        }
        resultStringBuilder.append(param)

        return resultStringBuilder.toString()
    }

    fun initErcPayload():EcrProtocol {
        val ecrProtocol = EcrProtocol()
        ecrProtocol.ecrIndicator = ""
        ecrProtocol.ecrVersionDate = ""
        ecrProtocol.transTypeIndicator = ""
        ecrProtocol.transType = ""
        ecrProtocol.cupIndicator = ""
        ecrProtocol.hostID = ""
        ecrProtocol.receiptNo = ""
        ecrProtocol.cardNo = ""
        ecrProtocol.cardExpireDate = ""
        ecrProtocol.transAmount = ""
        ecrProtocol.transDate = ""
        ecrProtocol.transTime = ""
        ecrProtocol.approvalNo = ""
        ecrProtocol.waveCardIndicator = ""
        ecrProtocol.ecrResponseCode = ""
        ecrProtocol.merchantId = ""
        ecrProtocol.terminalId = ""
        ecrProtocol.expAmount = ""
        ecrProtocol.storeId = ""
        ecrProtocol.installmentRedeemIndicator = ""
        ecrProtocol.rdmPaidAmt = ""
        ecrProtocol.rdmPoint = ""
        ecrProtocol.pointsOfBalance = ""
        ecrProtocol.redeemAmt = ""
        ecrProtocol.installmentPeriod = ""
        ecrProtocol.downPaymentAmount = ""
        ecrProtocol.installmentPaymentAmount = ""
        ecrProtocol.formalityFee = ""
        ecrProtocol.cardType = ""
        ecrProtocol.batchNo = ""
        ecrProtocol.startTransType = ""
        ecrProtocol.mpFlag = ""
        ecrProtocol.spIssuerId = ""
        ecrProtocol.spCardOriginDate = ""
        ecrProtocol.spRrn = ""
        ecrProtocol.payItem = ""
        ecrProtocol.cardNoHashValue = ""
        ecrProtocol.mpResponseCode = ""
        ecrProtocol.asmAwardFlag = ""
        ecrProtocol.mcpIndicator = ""
        ecrProtocol.codeNo = ""
        ecrProtocol.reserved = ""
        return ecrProtocol
    }

    fun ercPayload(): String {
        return ecrIndicator +
                ecrVersionDate +
                transTypeIndicator +
                transType +
                cupIndicator +
                hostID +
                receiptNo +
                cardNo +
                cardExpireDate +
                transAmount +
                transDate +
                transTime +
                approvalNo +
                waveCardIndicator +
                ecrResponseCode +
                merchantId +
                terminalId +
                expAmount +
                storeId +
                installmentRedeemIndicator +
                rdmPaidAmt +
                rdmPoint +
                pointsOfBalance +
                redeemAmt +
                installmentPeriod +
                downPaymentAmount +
                installmentPaymentAmount +
                formalityFee +
                cardType +
                batchNo +
                startTransType +
                mpFlag +
                spIssuerId +
                spCardOriginDate +
                spRrn +
                payItem +
                cardNoHashValue +
                mpResponseCode +
                asmAwardFlag +
                mcpIndicator +
                codeNo +
                reserved
    }

}



