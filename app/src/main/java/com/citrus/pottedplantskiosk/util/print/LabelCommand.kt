package com.citrus.pottedplantskiosk.util.print

import com.citrus.pottedplantskiosk.di.prefs

class LabelCommand {



    enum class DIRECTION(val value: Int) {
        FORWARD(0), BACKWARD(1)
    }

    enum class MIRROR_CMD(val value: Int) {
        NORMAL(0), MIRROR(1);
    }

    enum class CODEPAGE(val value: Int) {
        PC437(437), PC850(850), PC852(852), PC860(860), PC863(863), PC865(865), WPC1250(1250), WPC1252(1252), WPC1253(1253), WPC1254(1254);
    }

    enum class FONTTYPE(val value: String) {
        //6. printerFont(a,b,c,d,e,f,g) 說明: 使用條碼機內建文字列印參數:
        //a: 字串型別，文字 X 方向起始點，以點(point)表示。(200 DPI，1 點=1/8 mm, 300 DPI，1 點=1/12 mm)
        //b: 字串型別，文字 Y 方向起始點，以點(point)表示。(200 DPI，1 點=1/8 mm, 300 DPI，1 點=1/12 mm)
        //c: 字串型別，內建字型名稱，共 12 種。
        //1: 8*/12 dots
        //2: 12*20 dots
        //3: 16*24 dots
        //4: 24*32 dots
        //5: 32*48 dots
        //TST24.BF2: 繁體中文 24*24
        //TST16.BF2: 繁體中文 16*16
        //TTT24.BF2: 繁體中文 24*24 (電信碼)
        //TSS24.BF2: 簡體中文 24*24
        //TSS16.BF2: 簡體中文 16*16 K: 韓文 24*24
        //L: 韓文 16*16 d: 字串型別，設定文字旋轉角度0: 旋轉 0 度90: 旋轉 90 度180: 旋轉 180 度
        FONT_1("1"), FONT_2("2"), FONT_3("3"), FONT_4("4"), FONT_5("5"), FONT_6("6"), FONT_7("7"), FONT_8("8"), SIMPLIFIED_CHINESE("TSS24.BF2"), SIMPLIFIED_CHINESE16(
            "TSS16.BF2"
        ),
        TRADITIONAL_CHINESE("TST24.BF2"), TRADITIONAL_CHINESE16("TST16.BF2"), KOREAN("K");
    }

    enum class ROTATION(val value: Int) {
        ROTATION_0(0), ROTATION_90(90), ROTATION_180(180), ROTATION_270(270);
    }

    enum class FONTMUL(val value: Int) {
        MUL_1(1), MUL_2(2), MUL_3(3), MUL_4(4), MUL_5(5), MUL_6(6), MUL_7(7), MUL_8(8), MUL_9(9), MUL_10(10);
    }

    enum class FOOT(val value: Int) {
        F2(0), F5(1);

    }

    var command: ByteArray = byteArrayOf()
    val charSet = prefs.charSet

    fun getBytes() = command

    fun addSizeAndGap(width: Int, height: Int, gap: Int) {
        var str = "SIZE $width mm,$height mm\n"
        str += "GAP $gap mm,0 mm\n"
        addStrToCommand(str)
    }

    //列印方向
    fun addDirection(direction: DIRECTION, mirror: MIRROR_CMD) {
        val str = "DIRECTION ${direction.value},${mirror.value}\n"
        addStrToCommand(str)
    }

    //圓點座標
    fun addReference(x: Int, y: Int) {
        val str = "REFERENCE $x,$y\n"
        addStrToCommand(str)
    }

    //清除緩衝區
    fun addCls() {
        val str = "CLS\n"
        addStrToCommand(str)
    }

    fun addCodePage(page: CODEPAGE) {
        String()
        val str = "CODEPAGE ${page.value}\n"
        addStrToCommand(str)
    }

    fun addNormalText(x: Int, y: Int, text: String) {
        val str = "TEXT $x,$y,\"${getFontTypeByLanguage(FONTTYPE.FONT_2).value}\",${ROTATION.ROTATION_0.value},${FONTMUL.MUL_1.value},${FONTMUL.MUL_1.value},\"$text\"\n"
        addStrToCommand(str)
    }

    fun addENText(x: Int, y: Int, font: FONTTYPE, text: String) {
        val str = "TEXT $x,$y,\"${font.value}\",${ROTATION.ROTATION_0.value},${FONTMUL.MUL_1.value},${FONTMUL.MUL_1.value},\"$text\"\n"
        addStrToCommand(str)
    }

    fun addNumBigText(x: Int, y: Int, text: String) {
        String()
        val str = "TEXT $x,$y,\"${FONTTYPE.FONT_3.value}\",${ROTATION.ROTATION_0.value},${FONTMUL.MUL_1.value},${FONTMUL.MUL_2.value},\"$text\"\n"
        addStrToCommand(str)
    }

    fun addMidText(x: Int, y: Int, text: String) {
        val str = "TEXT $x,$y,\"${getFontTypeByLanguage(FONTTYPE.FONT_3).value}\",${ROTATION.ROTATION_0.value},${FONTMUL.MUL_1.value},${FONTMUL.MUL_2.value},\"$text\"\n"
        addStrToCommand(str)
    }

    fun addBigText(x: Int, y: Int, text: String) {
        val str = "TEXT $x,$y,\"${getFontTypeByLanguage(FONTTYPE.FONT_3).value}\",${ROTATION.ROTATION_0.value},${FONTMUL.MUL_2.value},${FONTMUL.MUL_2.value},\"$text\"\n"
        addStrToCommand(str)
    }

    fun addPrint(m: Int, n: Int) {
        val str = "PRINT $m,$n\n"
        addStrToCommand(str)
    }

    private fun getFontTypeByLanguage(font: FONTTYPE): FONTTYPE {
        return when (charSet) {
            "BIG5" -> FONTTYPE.TRADITIONAL_CHINESE
            "GBK" -> FONTTYPE.SIMPLIFIED_CHINESE
            else -> font
        }
    }

    private fun addStrToCommand(str: String) {
        command = b(command, str.toByteArray(charset(charSet)))
    }
}