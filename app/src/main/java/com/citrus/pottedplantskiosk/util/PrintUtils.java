/*
 * Created by StanleyChen
 * Last modified 2022/08/31 10:59
 * Copyright (c) 2013-2022. Posiflex. All rights reserved.
 */

package com.citrus.pottedplantskiosk.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.pos.poslibusb.PosLog;
import com.pos.printersdk.PrinterStatusInfo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PrintUtils {
    private static final String TAG = "PrinterDemo";

    public static void loge(String msg) {
        PosLog.loge(TAG, msg);
    }

    public static void logw(String msg) {
        PosLog.logw(TAG, msg);
    }

    public static void logi(String msg) {
        PosLog.logw(TAG, msg);
    }

    public static void logd(String msg) {
        PosLog.logd(TAG, msg);
    }

    public static void logv(String msg) {
        PosLog.logv(TAG, msg);
    }

    public static void loge(String tag, String msg) {
        PosLog.loge(tag, msg);
    }

    public static void logw(String tag, String msg) {
        PosLog.logw(tag, msg);
    }

    public static void logi(String tag, String msg) {
        PosLog.logw(tag, msg);
    }

    public static void logd(String tag, String msg) {
        PosLog.logd(tag, msg);
    }

    public static void logv(String tag, String msg) {
        PosLog.logv(tag, msg);
    }

    public static final class RESULT_TOKEN_DEF {
        public static final int RESULT_SUCCESS = 0;
        public static final int RESULT_ERROR = -1;
        public static final int RESULT_ERROR_INVALID_PARAM = -2;
        public static final int RESULT_ERROR_ACCESS = -3;
        public static final int RESULT_ERROR_NO_DEVICE = -4;
        public static final int RESULT_ERROR_NOT_FOUND = -5;
        public static final int RESULT_ERROR_BUSY = -6;
        public static final int RESULT_ERROR_TIMEOUT = -7;
        public static final int RESULT_ERROR_OVERFLOW = -8;
        public static final int RESULT_ERROR_PIPE = -9;
        public static final int RESULT_ERROR_INTERRUPTED = -10;
        public static final int RESULT_ERROR_NO_MEM = -11;
        public static final int RESULT_ERROR_NOT_SUPPORTED = -12;
        public static final int RESULT_ERROR_IO = -13;

        public static final int RESULT_ERROR_OTHER = -99;
    }

    public static final class PRINTER_STATUS {
        public static final int UNKNOWN = -1;
        public static final int FALSE = 0;
        public static final int TRUE = 1;
    }

    public static String getResultString(int error) {
        String strRes = "Unknown: result " + error;
        switch(error) {
            case RESULT_TOKEN_DEF.RESULT_SUCCESS:
                strRes = "RESULT_SUCCESS";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR:
                strRes = "RESULT_ERROR";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_INVALID_PARAM:
                strRes = "RESULT_ERROR_INVALID_PARAM";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_ACCESS:
                strRes = "RESULT_ERROR_ACCESS";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_NO_DEVICE:
                strRes = "RESULT_ERROR_NO_DEVICE";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_NOT_FOUND:
                strRes = "RESULT_ERROR_NOT_FOUND";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_BUSY:
                strRes = "RESULT_ERROR_BUSY";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_TIMEOUT:
                strRes = "RESULT_ERROR_TIMEOUT";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_OVERFLOW:
                strRes = "RESULT_ERROR_OVERFLOW";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_PIPE:
                strRes = "RESULT_ERROR_PIPE";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_INTERRUPTED:
                strRes = "RESULT_ERROR_INTERRUPTED";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_NO_MEM:
                strRes = "RESULT_ERROR_NO_MEM";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_NOT_SUPPORTED:
                strRes = "RESULT_ERROR_NOT_SUPPORTED";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_IO:
                strRes = "RESULT_ERROR_IO";
                break;
            case RESULT_TOKEN_DEF.RESULT_ERROR_OTHER:
                strRes = "RESULT_ERROR_OTHER";
                break;
        }

        return strRes;
    }

    public static final class BARCODE_TOKEN_DEF {
        public static final int BARCODE_UPC_A = 65;
        public static final int BARCODE_UPC_E = 66;
        public static final int BARCODE_JAN13 = 67;
        public static final int BARCODE_JAN8 = 68;
        public static final int BARCODE_CODE39 = 69;
        public static final int BARCODE_ITF = 70;
        public static final int BARCODE_CODABAR = 71;
        public static final int BARCODE_CODE93 = 72;
        public static final int BARCODE_CODE128 = 73;
    }

    public static final class UNDERLINE_TOKEN_DEF {
        public static final int UNDERLINE_OFF = 0;
        public static final int UNDERLINE_ON = 1;
    }

    public static final class INVERT_COLOR_TOKEN_DEF {
        public static final int INVERT_COLOR_OFF = 0;
        public static final int INVERT_COLOR_ON = 1;
    }

    public static final class EMPHASIZED_TOKEN_DEF {
        public static final int EMPHASIZED_OFF = 0;
        public static final int EMPHASIZED_ON = 1;
    }

    public static final class UPSIDE_DOWN_TOKEN_DEF {
        public static final int UPSIDE_DOWN_OFF = 0;
        public static final int UPSIDE_DOWN_ON = 1;
    }

    public static final class ALIGNMENT_TOKEN_DEF {
        public static final int ALIGNMENT_LEFT = 0;
        public static final int ALIGNMENT_CENTER = 1;
        public static final int ALIGNMENT_RIGHT = 2;
    }

    public static final class CUT_TYPE_TOKEN_DEF {
        public static final int CUT_TYPE_STANDARD = 0;
        public static final int CUT_TYPE_WITH_FEED = 1;
    }

    /**
     * Simple example printer error message, you can customized.
     * @param context The context to use. Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     * @param statusInfo non null PrinterStatusInfo object.
     * @return
     */
    public static String makeErrorMessage(Context context, PrinterStatusInfo statusInfo) {
        String msg = "";

//        if (statusInfo.getConnection() == PRINTER_STATUS.FALSE) {
//            msg += context.getString(R.string.err_no_response);
//        }
//        if (statusInfo.getOnline() == PRINTER_STATUS.FALSE) {
//            msg += context.getString(R.string.err_offline);
//        }
//        if (statusInfo.getCoverOpen() == PRINTER_STATUS.TRUE) {
//            msg += context.getString(R.string.err_cover_open);
//        }
//        if (statusInfo.getPaperNearEnd() == PRINTER_STATUS.TRUE || statusInfo.getPaperEmpty() == PRINTER_STATUS.TRUE) {
//            msg += context.getString(R.string.err_receipt_end);
//        }
//        if (statusInfo.getPaperFeed() == PRINTER_STATUS.TRUE) {
//            msg += context.getString(R.string.err_paper_feed);
//        }
//        if (statusInfo.getAutoCutterError() == PRINTER_STATUS.TRUE) {
//            msg += context.getString(R.string.err_auto_cutter);
//            msg += context.getString(R.string.err_need_recover);
//        }
//        if (statusInfo.getUnrecoverableError() == PRINTER_STATUS.TRUE) {
//            msg += context.getString(R.string.err_unrecoverable);
//        }
//        if (statusInfo.getAutoRecoverError() == PRINTER_STATUS.TRUE) {
//            msg += context.getString(R.string.err_overheat);
//            msg += context.getString(R.string.err_head_hot);
//        }

        return msg;
    }

    /**
     * Simple example printer error message, you can customized.
     * @param statusInfo non null PrinterStatusInfo object.
     * @return
     */
    public static String makeStatusMessage(PrinterStatusInfo statusInfo) {
        String msg = "";

        msg += "TransmitASBStatus: [";
        byte mTransmitASBStatus[] = statusInfo.getTransmitASBStatus();
        for (int i = 0; i < mTransmitASBStatus.length; ++i) {
            msg += String.format("0x%02X", mTransmitASBStatus[i]);
            msg += (i < mTransmitASBStatus.length - 1)  ? ", " : "]";
        }
        msg += "\n";

        msg += "Connection: ";
        switch (statusInfo.getConnection()) {
            case PRINTER_STATUS.FALSE:
                msg += "DISCONNECT";
                break;
            case PRINTER_STATUS.TRUE:
                msg += "CONNECT";
                break;
            case PRINTER_STATUS.UNKNOWN:
            default:
                msg += "UNKNOWN(" + statusInfo.getConnection() + ")";
                break;
        }
        msg += "\n";

        msg += "Online: ";
        switch (statusInfo.getOnline()) {
            case PRINTER_STATUS.FALSE:
                msg += "OFFLINE";
                break;
            case PRINTER_STATUS.TRUE:
                msg += "ONLINE";
                break;
            case PRINTER_STATUS.UNKNOWN:
            default:
                msg += "UNKNOWN(" + statusInfo.getOnline() + ")";
                break;
        }
        msg += "\n";

        msg += "DrawerOpen: ";
        switch (statusInfo.getDrawerOpen()) {
            case PRINTER_STATUS.FALSE:
                msg += "DRAWER_HIGH (Drawer close)";
                break;
            case PRINTER_STATUS.TRUE:
                msg += "DRAWER_LOW (Drawer open)";
                break;
            case PRINTER_STATUS.UNKNOWN:
            default:
                msg += "UNKNOWN(" + statusInfo.getDrawerOpen() + ")";
                break;
        }
        msg += "\n";

        msg += "CoverOpen: ";
        switch (statusInfo.getCoverOpen()) {
            case PRINTER_STATUS.FALSE:
                msg += "COVER_CLOSE";
                break;
            case PRINTER_STATUS.TRUE:
                msg += "COVER_OPEN";
                break;
            case PRINTER_STATUS.UNKNOWN:
            default:
                msg += "UNKNOWN(" + statusInfo.getCoverOpen() + ")";
                break;
        }
        msg += "\n";

        // ASB not receive PaperFeed status
//        msg += "PaperFeed: ";
//        switch (statusInfo.getPaperFeed()) {
//            case Utils.PRINTER_STATUS.FALSE:
//                msg += "PAPER_STOP";
//                break;
//            case Utils.PRINTER_STATUS.TRUE:
//                msg += "PAPER_FEED";
//                break;
//            case Utils.PRINTER_STATUS.UNKNOWN:
//            default:
//                msg += "UNKNOWN(" + statusInfo.getPaperFeed() + ")";
//                break;
//        }
//        msg += "\n";

        msg += "HasError: ";
        switch (statusInfo.getHasError()) {
            case PRINTER_STATUS.FALSE:
                msg += "FALSE";
                break;
            case PRINTER_STATUS.TRUE:
                msg += "TRUE";
                break;
            case PRINTER_STATUS.UNKNOWN:
            default:
                msg += "UNKNOWN(" + statusInfo.getHasError() + ")";
                break;
        }
        msg += "\n";

        msg += "AutoCutterError: ";
        switch (statusInfo.getAutoCutterError()) {
            case PRINTER_STATUS.FALSE:
                msg += "FALSE";
                break;
            case PRINTER_STATUS.TRUE:
                msg += "TRUE";
                break;
            case PRINTER_STATUS.UNKNOWN:
            default:
                msg += "UNKNOWN(" + statusInfo.getAutoCutterError() + ")";
                break;
        }
        msg += "\n";

        msg += "UnrecoverableError: ";
        switch (statusInfo.getUnrecoverableError()) {
            case PRINTER_STATUS.FALSE:
                msg += "FALSE";
                break;
            case PRINTER_STATUS.TRUE:
                msg += "TRUE";
                break;
            case PRINTER_STATUS.UNKNOWN:
            default:
                msg += "UNKNOWN(" + statusInfo.getUnrecoverableError() + ")";
                break;
        }
        msg += "\n";

        msg += "AutoRecoverError: ";
        switch (statusInfo.getAutoRecoverError()) {
            case PRINTER_STATUS.FALSE:
                msg += "FALSE";
                break;
            case PRINTER_STATUS.TRUE:
                msg += "TRUE (HEAD_OVERHEAT)";
                break;
            case PRINTER_STATUS.UNKNOWN:
            default:
                msg += "UNKNOWN(" + statusInfo.getAutoRecoverError() + ")";
                break;
        }
        msg += "\n";

        msg += "PaperNearEnd: ";
        switch (statusInfo.getPaperNearEnd()) {
            case PRINTER_STATUS.FALSE:
                msg += "FALSE";
                break;
            case PRINTER_STATUS.TRUE:
                msg += "TRUE";
                break;
            case PRINTER_STATUS.UNKNOWN:
            default:
                msg += "UNKNOWN(" + statusInfo.getPaperNearEnd() + ")";
                break;
        }
        msg += "\n";

        msg += "PaperEmpty: ";
        switch (statusInfo.getPaperEmpty()) {
            case PRINTER_STATUS.FALSE:
                msg += "FALSE";
                break;
            case PRINTER_STATUS.TRUE:
                msg += "TRUE";
                break;
            case PRINTER_STATUS.UNKNOWN:
            default:
                msg += "UNKNOWN(" + statusInfo.getPaperEmpty() + ")";
                break;
        }
        msg += "\n";

        return msg;
    }

    public static final class ALERT_BUTTON_TOKEN_DEF {
        public static final int OK = 1;
        public static final int OKCANCEL = 2;
    }

    public static final String[] fileSize(float size) {
        String str[] = {"Byte", "KiB", "MiB"};
        int index = 0;
        while (size >= 1024) {
            size /= 1024;
            ++index;
            if (index >= 2)
                break;
        }

        DecimalFormat formatter = new DecimalFormat("#.##");
        formatter.setGroupingSize(3);
        String result[] = new String[2];
        result[0] = formatter.format(size);
        result[1] = str[index];
        return result;
    }

    public static boolean isMatch(byte[] source, byte[] pattern, int pos) {
        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != source[pos + i]) {
                return false;
            }
        }
        return true;
    }

    public static List<byte[]> split(byte[] source, byte[] pattern) {
        List<byte[]> l = new LinkedList<byte[]>();
        int blockStart = 0;
        for (int i = 0; i < source.length; i++) {
            if (isMatch(source, pattern, i)) {
                l.add(Arrays.copyOfRange(source, blockStart, i));
                blockStart = i + pattern.length;
                i = blockStart;
            }
        }
        l.add(Arrays.copyOfRange(source, blockStart, source.length));
        return l;
    }

    // Java, Keil C51(TW100) is BIG_ENDIAN
    // Jni C++, C# is LITTLE_ENDIAN
    public static float byteArray2Float(byte[] b) {
        ByteBuffer buf = ByteBuffer.wrap(b).order(ByteOrder.BIG_ENDIAN);
        return buf.getFloat();
    }

    public static int HexStr2Int(String str) {
        char str_chr[] = str.toCharArray();
        int str_int = 0;

        for (int i = 0; i < str.length(); i++) {
            str_int = str_int * 16;
            if (((int) str_chr[i] >= 48) && ((int) str_chr[i] <= 57)) { //0~9
                str_int = str_int + (int) str_chr[i] - 48;
            } else if ((str_chr[i] >= 65) && (str_chr[i] <= 70)) { //A~F
                str_int = str_int + ((int) str_chr[i] - 65) + 10;
            } else {
                return -1;
            }
        }

        return str_int;
    }

    public static int parseToInt(String stringToParse, int defaultValue) {
        try {
            return Integer.parseInt(stringToParse);
        } catch (NumberFormatException ex) {
            return defaultValue; //Use default value if parsing failed
        }
    }

    public static String ArabicEncode(String data) {
        String ArabicText = Utilities.Encoding.encode(data);
        ArabicText = Utilities.ReverseString.reverseStringLeftToRight(ArabicText);
        return ArabicText;
    }

    public static String FarsiEncode(String data) {
        String FarsiText = Utilities.EncodingFa.encode(data);
        FarsiText = Utilities.ReverseStringFa.reverseStringLeftToRight(FarsiText);
        return FarsiText;
    }

    public static float convertPixelsToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static float convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public int convertDpToPx(int dp) {
        return Math.round(dp * (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int convertPxToDp(int px) {
        return Math.round(px / (Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public float dpFromPx(float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    public float pxFromDp(float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    // Checks if a volume containing external storage is available
    // for read and write.
    private boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED;
    }

    // Checks if a volume containing external storage is available to at least read.
    private boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED ||
                Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY;
    }
}
