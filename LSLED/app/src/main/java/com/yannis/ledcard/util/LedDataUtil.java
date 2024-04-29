package com.yannis.ledcard.util;

import static com.yannis.ledcard.LedBleApplication._TEXT_SIZE;
import static com.yannis.ledcard.activity.MainActivity.SDF;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.yannis.ledcard.LedBleApplication;
import com.yannis.ledcard.R;
import com.yannis.ledcard.bean.LEDBmp;
import com.yannis.ledcard.bean.SendContent;
import com.yannis.ledcard.mode.MainMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Yannis on 2017/7/24.
 * Led数据处理
 */

public class LedDataUtil {
    private static final String TAG = "LedDataUtil";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final String IMG_DIR = LedBleApplication.instance.getExternalFilesDir(null) + File.separator + "LED_IMG" + File.separator;
    //    private static final String APP_DIR = "LED_IMG" + File.separator;
    public static String ENCODE = "EUC_KR";
    //    public static String ZK_PATH_ch = "HZK12";
//    public static String ZK_PATH_en = "ASC12";
    public static String CHARACTER_PATH = "HZK12";
    public static String ASC_PATH = "ASC12";


    public static String getContentFromPictureMatrix(byte[] contentBytes, int matrix) {
        int len = contentBytes.length;
        StringBuilder sb = new StringBuilder();
        int col = len / matrix;
        int zeroBits = 8 - (matrix % 8);

        for (int i = 0; i < len; i++) {
            int num = contentBytes[i];
            StringBuilder s = new StringBuilder();
            if ((i + 1) % col == 0 && zeroBits != 8) {
                num = num >> zeroBits;
                int tms = 8 - zeroBits;
                while (tms > 0) {
                    s.append((num & 0x01) == 1 ? "1" : "0");
                    num = num >> 1;
                    tms--;
                }
            } else {
                int tms = 8;
                while (tms > 0) {
                    s.append((num & 0x01) == 1 ? "1" : "0");
                    num = num >> 1;
                    tms--;
                }
            }
            s.reverse();
            sb.append(s);
        }
        return sb.toString();
    }

    public static final HashMap<String,List<LEDBmp>> ledBmpMatrixArray = new HashMap<>();

    public static void loadDefaultLEDBmp2Cache(){
        ledBmpMatrixArray.clear();
        configureDefaultPics(11);
        configureDefaultPics(12);
        configureDefaultPics(16);
    }

    /**
     * 获取led图片列表
     *
     * @return
     */
    public static void configureDefaultPics(int matrix) {
        String[] content = new String[16];
        switch (matrix) {
            case 11:
                content = MainMode.picture11Content;
                break;
            case 12:
                content = MainMode.picture12Content;
                break;
            case 16:
                content = MainMode.picture16Content;
                break;
        }
        List<LEDBmp> ledBmpList = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            LEDBmp ledImg = null;
            int resourceID = 0;
            switch (i) {
                case 0:
                    resourceID = R.drawable.img5;
                    break;
                case 1:
                    resourceID = R.drawable.img6;
                    break;
                case 2:
                    resourceID = R.drawable.img7;
                    break;
                case 3:
                    resourceID = R.drawable.img8;
                    break;
                case 4:
                    resourceID = R.drawable.img9;
                    break;
                case 5:
                    resourceID = R.drawable.img10;
                    break;
                case 6:
                    resourceID = R.drawable.img11;
                    break;
                case 7:
                    resourceID = R.drawable.img12;
                    break;
                case 8:
                    resourceID = R.drawable.img13;
                    break;
                case 9:
                    resourceID = R.drawable.img14;
                    break;
                case 10:
                    resourceID = R.drawable.img15;
                    break;
                case 11:
                    resourceID = R.drawable.img16;
                    break;
                case 12:
                    resourceID = R.drawable.img17;
                    break;
                case 13:
                    resourceID = R.drawable.img18;
                    break;
                case 14:
                    resourceID = R.drawable.img19;
                    break;
                case 15:
                    resourceID = R.drawable.img20;
                    break;
                default:
                    resourceID = R.drawable.img5;
                    break;
            }
            ledImg = new LEDBmp(content[i], matrix, resourceID);
            ledBmpList.add(ledImg);
            ledImg.save();
        }
//        switch (matrix) {
//            case 11:
//                ledBmpMatrixArray.put("11",ledBmpList);
//                break;
//            case 12:
//                ledBmpMatrixArray.put("12",ledBmpList);
//                break;
//            case 16:
//                ledBmpMatrixArray.put("16",ledBmpList);
//                break;
//        }
    }


    public static byte[] get64(List<SendContent> sendContentList, int pix) {
        byte[] data = new byte[64];
        data[0] = 0x77;
        data[1] = 0x61;
        data[2] = 0x6E;
        data[3] = 0x67;//握手wang

        data[4] = 0x00;
        data[5] = 0x00;
        data[6] = getFlash(sendContentList);
        data[7] = getMarquee(sendContentList);
        byte[] modeAndSpeed = getModeAndSpeed(sendContentList);
        System.arraycopy(modeAndSpeed, 0, data, 8, 8);

        byte[] msgLength = getMsgLength(sendContentList, pix);
        System.arraycopy(msgLength, 0, data, 16, 16);
        data[32] = 0x00;
        data[33] = 0x00;
        data[34] = 0x00;
        data[35] = 0x00;
        data[36] = 0x00;
        data[37] = 0x00;
        byte[] date = getDate();
        System.arraycopy(date, 0, data, 38, 6);
        for (int i = 0; i < 19; i++) {
            data[44 + i] = 0x00;
        }
        data[63] = 0x00;
        return data;
    }


    /**
     * 获得闪动的byte
     *
     * @param sendContentList
     * @return
     */
    public static byte getFlash(List<SendContent> sendContentList) {
        byte flash = (byte) 0xff;
        for (int i = 0; i < sendContentList.size(); i++) {
            SendContent sendContent = sendContentList.get(i);
            boolean isFlash = sendContent.isFlash();
            switch (i) {
                case 0:
                    flash = isFlash ? flash : (byte) (flash & (byte) 0xfe);//1111 1110
                    break;
                case 1:
                    flash = isFlash ? flash : (byte) (flash & (byte) 0xfd);//1111 1101
                    break;
                case 2:
                    flash = isFlash ? flash : (byte) (flash & (byte) 0xfb);//1111 1011
                    break;
                case 3:
                    flash = isFlash ? flash : (byte) (flash & (byte) 0xf7);//1111 0111
                    break;
                case 4:
                    flash = isFlash ? flash : (byte) (flash & (byte) 0xef);//1110 1111
                    break;
                case 5:
                    flash = isFlash ? flash : (byte) (flash & (byte) 0xdf);
                    break;
                case 6:
                    flash = isFlash ? flash : (byte) (flash & (byte) 0xbf);
                    break;
                case 7:
                    flash = isFlash ? flash : (byte) (flash & (byte) 0x7f);
                    break;
                default:
                    break;
            }
        }

        return flash;
    }

    /**
     * 跑马灯
     *
     * @param sendContentList
     * @return
     */
    private static byte getMarquee(List<SendContent> sendContentList) {
        byte marquee = (byte) 0xff;
        for (int i = 0; i < sendContentList.size(); i++) {
            SendContent sendContent = sendContentList.get(i);
            boolean isMarquee = sendContent.isMarquee();
            switch (i) {
                case 0:
                    marquee = isMarquee ? marquee : (byte) (marquee & (byte) 0xfe);//1111 1110
                    break;
                case 1:
                    marquee = isMarquee ? marquee : (byte) (marquee & (byte) 0xfd);//1111 1101
                    break;
                case 2:
//                    marquee = isMarquee ? (byte) (marquee & (byte) 0xff) : (byte) (marquee & (byte) 0xfb);//1111 1011
                    marquee = isMarquee ? marquee : (byte) (marquee & (byte) 0xfb);//1111 1011
                    break;
                case 3:
                    marquee = isMarquee ? marquee : (byte) (marquee & (byte) 0xf7);//1111 0111
                    break;
                case 4:
                    marquee = isMarquee ? marquee : (byte) (marquee & (byte) 0xef);//1110 1111
                    break;
                case 5:
                    marquee = isMarquee ? marquee : (byte) (marquee & (byte) 0xdf);
                    break;
                case 6:
                    marquee = isMarquee ? marquee : (byte) (marquee & (byte) 0xbf);
                    break;
                case 7:
                    marquee = isMarquee ? marquee : (byte) (marquee & (byte) 0x7f);
                    break;
                default:
                    break;
            }
        }

        return marquee;
    }

    private static byte[] getDate() {
        byte[] date = new byte[6];
        Calendar calendar = Calendar.getInstance();
        date[0] = (byte) calendar.get(Calendar.YEAR);
        date[1] = (byte) (calendar.get(Calendar.MONTH) + 1);
        date[2] = (byte) calendar.get(Calendar.DAY_OF_MONTH);
        date[3] = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        date[4] = (byte) calendar.get(Calendar.MINUTE);
        date[5] = (byte) calendar.get(Calendar.SECOND);
        return date;
    }


    private static byte[] getModeAndSpeed(List<SendContent> sendContentList) {
        byte[] modeAndSpeeds = new byte[8];
        for (int i = 0; i < sendContentList.size(); i++) {
            SendContent sendContent = sendContentList.get(i);
            byte mode = (byte) (sendContent.getMode() - 1);
            byte speed = (byte) (sendContent.getSpeed() - 1);
            byte speedByte = (byte) ((byte) (speed << 4) & 0xF0);
            byte modeByte = (byte) (mode & 0x0F);
            modeAndSpeeds[i] = (byte) (speedByte | modeByte);
        }
        return modeAndSpeeds;
    }

    /**
     * 获取发送列表的消息长度
     *
     * @param sendContentList
     * @param matrix
     * @return
     */
    public static byte[] getMsgLength(List<SendContent> sendContentList, int matrix) {
        byte[] msgLength = new byte[16];
        for (int i = 0; i < sendContentList.size(); i++) {
            SendContent sendContent = sendContentList.get(i);
            //发送编辑的文字
            if (TextUtils.isEmpty(sendContent.getMessage()) || !sendContent.isSelect()) {
                msgLength[i * 2] = 0x00;
                msgLength[i * 2 + 1] = 0x00;
            } else {
                byte[] ml = DataUtils.int2ByteArrayLSB2(getMessageByteLength(sendContent.getMessage(), matrix));
                msgLength[i * 2] = ml[1];
                msgLength[i * 2 + 1] = ml[0];
            }
        }
        return msgLength;
    }

    /**
     * 获取单条发送信息的长度(点阵图的像素长度)
     *
     * @param msg
     * @param matrix
     * @return
     */
    private static int getMsgBitmapPixLength(String msg, int matrix) {
        Paint paint = new Paint();
        paint.setTextSize(matrix);
        Typeface typeface = Typeface.createFromAsset(LedBleApplication.instance.getAssets(), "fonts/typeface1456.ttf");
        paint.setTypeface(typeface);
        paint.setStrokeWidth(1);
        paint.setTextAlign(Paint.Align.CENTER);
        int msgBitmapPixLength = (int) ViewUtils.getTextRectWidth(paint, msg);
        Log.e(TAG, "======获取单条发送信息的长度(点阵图的像素长度)=======> msg = " + msg + " , 长度 = " + msgBitmapPixLength);
        return msgBitmapPixLength;
    }

    private static int getMsgBitmapByteLength(String msg, int matrix) {
        int width = getMsgBitmapPixLength(msg, matrix);
        return (width / 8) + (width % 8 == 0 ? 0 : 1);
    }

    /**
     * 获取信息总长度 1：从字库中获取  2：从点阵图中获取
     *
     * @param matrix
     * @param sendContentList
     * @return
     */
    public static int getTotalMessageLength(int matrix, List<SendContent> sendContentList) {
        int msgTotalLength = 0;
        for (int i = 0; i < 8; i++) {
            SendContent sendContent = sendContentList.get(i);
            if (isNeedSendMsg(sendContent)) {
                String message = sendContent.getMessage();
                int singleMsgLength = getMessageByteLength(message, matrix);
                msgTotalLength += singleMsgLength;
            }
        }
        return msgTotalLength;
    }

    private static boolean isNeedSendMsg(SendContent sendContent) {
        return !TextUtils.isEmpty(sendContent.getMessage()) && sendContent.isSelect();
    }

    /**
     * 根据是否存在字库 获取信息byte长度
     *
     * @param message
     * @param matrix
     * @return
     */
    private static int getSingleMsgByteLength(String message, int matrix) {
        boolean isUserFontLib = LangUtils.isUserFontLib(message);
        return getSingleMsgByteLength(message, matrix, isUserFontLib);
    }

    private static int getSingleMsgByteLength(String message, int matrix, boolean isUserFontLib) {
        return isUserFontLib ? getLibMessageByteLength(message, matrix) : getMsgBitmapByteLength(message, matrix);
    }

    /**
     * 获取信息字符长度
     *
     * @param message
     * @param matrix
     * @return
     */
    private static int getLibMessageLength(String message, int matrix) {
//        message = message.trim();
        int length;
        // 英文的个数
        int en = 0;
        // 中文的个数
        int ch = 0;
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) >= 0x80) {
                ch++;
            } else {
                en++;
            }
        }
        en = en * 8;
        ch = ch * matrix;
        length = en + ch;
        return length;
    }

    /**
     * 获取信息字符字节长度
     *
     * @param message
     * @param matrix
     * @return
     */
    private static int getLibMessageByteLength(String message, int matrix) {
//        message = message.trim();
        int length = getLibMessageLength(message, matrix);
        if (length % 8 == 0) {
            length = length / 8;
        } else {
            length = length / 8 + 1;
        }
        return length;
    }


    /**
     * 获取字库信息数据数组
     *
     * @param context
     * @param matrix
     * @param msg
     * @return
     */
    public static byte[] getLibMsgBytes(Context context, int matrix, String msg) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage().trim().toLowerCase();
        String country = locale.getCountry().trim().toLowerCase();
        ASC_PATH = "ASC" + String.valueOf(matrix);
        switch (language) {
            case "zh":
                if (country.equals("tw")) {
                    //中文繁体
                    ENCODE = "BIG5";
                    CHARACTER_PATH = "TWK" + String.valueOf(matrix);
                } else {
                    //中文简体
                    ENCODE = "GB2312";
                    CHARACTER_PATH = "HZK" + String.valueOf(matrix);
                }
                break;
            case "ja":
                ENCODE = "shift-jis";
                CHARACTER_PATH = "JPK" + String.valueOf(matrix);
                break;
            case "ko":
                ENCODE = "ks_c_5601-1987";
                CHARACTER_PATH = "KRK" + String.valueOf(matrix);
                break;
            default:
                ENCODE = "GB2312";
                CHARACTER_PATH = "HZK" + String.valueOf(matrix);
                break;
        }
        int msgLength = msg.length();
        int length = getLibMessageByteLength(msg, matrix);
        int count = 0;
        int shift = 0;
        boolean isHalf = false;
        byte[][] msgByteArray = new byte[matrix][length];
        for (int i = 0; i < msgLength; i++) {
            char[] ch = new char[1];
            char c = msg.charAt(i);
            ch[0] = c;
//            Log.e("读取字库数据", "字符 :" + String.valueOf(ch) + " count = " + count + ", length = " + length);
            //------------------------------------------11点阵
            if (matrix == 11) {
                if (c >= 0x80) {
                    //中文 日文 韩文 或者其他国家语言
                    int[] cod = getByteCodeCH(new String(ch));
                    // 长度22
                    byte[] b = readCharacter(context, cod[0], cod[1], matrix);
//                    Log.e("读取字库数据", "字符 :" + String.valueOf(ch) + "bytes = " + DataUtils.byteArray2BinaryString(b));

                    for (byte s = 0; s < 11; s++) {
                        if (shift > 0) {
                            msgByteArray[s][count] |= (byte) ((b[s * 2] & 0xff) >>> shift);
                            msgByteArray[s][count + 1] = (byte) (b[s * 2] << (8 - shift));
                            msgByteArray[s][count + 1] |= ((b[s * 2 + 1] & 0xff) >>> (shift));
                            if (shift > 5) {
                                msgByteArray[s][count + 2] = (byte) (b[s * 2 + 1] << (8 - shift));
                            }
                        } else {
                            msgByteArray[s][count] = b[s * 2];
                            msgByteArray[s][count + 1] = b[s * 2 + 1];
                        }
                    }
                    count++;
                    if (shift > 4) {
                        count++;
                    }
                    shift = (byte) ((shift + 11) & 0x7);
                } else {
                    //英文
                    try {
                        byte[] b = new String(ch).getBytes(ENCODE);
                        int tt = b[0];
                        // 读取字节11字节
                        byte[] readAsc = readASC(context, tt, matrix);
                        for (byte s = 0; s < 11; s++) {
                            if (shift > 0) {
                                msgByteArray[s][count] |= (byte) ((readAsc[s] & 0xff) >>> shift);
                                msgByteArray[s][count + 1] = (byte) (readAsc[s] << (8 - shift));
                            } else {
                                msgByteArray[s][count] = readAsc[s];
                            }
                        }
                        count++;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

            }
            //------------------------------------------12点阵
            if (matrix == 12) {
                if (c >= 0x80) {
                    // 当前是中文
                    int[] cod = getByteCodeCH(new String(ch));
                    // 长度24
                    byte[] b = readCharacter(context, cod[0], cod[1], matrix);
                    int cc = 0;
                    // 当前不是半个的情况
                    if (!isHalf) {
                        for (int s = 0; s < 12; s++) {
                            msgByteArray[s][count] = b[cc];
                            b[cc + 1] = (byte) (b[cc + 1] & 0xf0);
                            msgByteArray[s][count + 1] = b[cc + 1];
                            cc = cc + 2;

                        }
                        count = count + 2;
                        // 需要分片
                        isHalf = true;
                    } else {
                        // 表示当前是需要分片
                        for (int s = 0; s < 12; s++) {
                            byte tmp = b[cc];
                            tmp = (byte) (((tmp & 0xff) >>> 4) | 0xf0);
                            msgByteArray[s][count - 1] = (byte) (msgByteArray[s][count - 1] | 0x0f);
                            msgByteArray[s][count - 1] = (byte) (msgByteArray[s][count - 1] & tmp);
                            b[cc] = (byte) ((b[cc] << 4) | 0x0f);
                            b[cc + 1] = (byte) ((b[cc + 1] & 0xff) >>> 4 | 0xf0);
                            // 第二个字符为前一个字符的低4位和后一个字符的高4位进行拼接
                            msgByteArray[s][count] = (byte) (b[cc + 1] & b[cc]);
                            cc = cc + 2;
                        }
                        count = count + 1;
                        // 需要分片
                        isHalf = false;
                    }
                } else {// 表示了英文
                    try {
                        byte[] b = new String(ch).getBytes(ENCODE);
                        int tt = b[0];
                        // 读取字节12字节
                        byte[] readAsc = readASC(context, tt, matrix);
                        // 当前不是半个的情况不需要分片
                        if (!isHalf) {
                            for (int s = 0; s < 12; s++) {
                                msgByteArray[s][count] = readAsc[s];
                            }
                        } else {
                            for (int s = 0; s < 12; s++) {
                                byte bb = readAsc[s];
                                bb = (byte) (((bb & 0xff) >>> 4) | 0xf0);
                                msgByteArray[s][count - 1] = (byte) (msgByteArray[s][count - 1] | 0x0f);
                                msgByteArray[s][count - 1] = (byte) (msgByteArray[s][count - 1] & bb);
                                msgByteArray[s][count] = (byte) ((readAsc[s] << 4) & 0xf0);
                            }
                        }
                        count = count + 1;

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            //------------------------------------------16点阵
            if (matrix == 16) {
                if (c >= 0x80) {
                    // 当前是中文
                    int[] cod = getByteCodeCH(new String(ch));
                    // 长度32
                    byte[] b = readCharacter(context, cod[0], cod[1], matrix);
                    int cc = 0;
                    for (int s = 0; s < 16; s++) {
                        assert b != null;
                        msgByteArray[s][count] = b[cc];
                        msgByteArray[s][count + 1] = b[cc + 1];
                        cc = cc + 2;
                    }
                    count = count + 2;
                } else {
                    // 表示了英文
                    try {
                        byte[] b = new String(ch).getBytes(ENCODE);
                        int tt = b[0];
                        // 读取字节16字节
                        byte[] readAsc = readASC(context, tt, matrix);
                        for (int s = 0; s < 16; s++) {
                            msgByteArray[s][count] = readAsc[s];
                        }
                        count = count + 1;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    // 当前是英文
                }
            }
        }
        byte[] data = new byte[matrix * length];
        for (int i = 0; i < matrix; i++) {
//            Log.e("读取字库数据", DataUtils.byteArray2BinaryString(msgByteArray[i]));
            System.arraycopy(msgByteArray[i], 0, data, i * length, length);
        }
//        Log.e("读取字库数据", DataUtils.byteArray2BinaryString(data).length() + "");
//        Log.e("读取字库数据", DataUtils.byteArray2BinaryString(data));
        return data;
    }

    private static int[] getByteCodeCH(String str) {
        int[] byteCode = new int[2];
        try {
            byte[] data = str.getBytes(ENCODE);
            byteCode[0] = data[0] < 0 ? 256 + data[0] : data[0];
            byteCode[1] = data[1] < 0 ? 256 + data[1] : data[1];
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return byteCode;
    }

    /**
     * 读取字库字符
     *
     * @param context
     * @param areaCode
     * @param posCode
     * @param matrix
     * @return
     */
    private static byte[] readCharacter(Context context, int areaCode, int posCode, int matrix) {

        byte[] data = null;
        try {
//            Log.e("读取字库数据", "path = " + CHARACTER_PATH);
//            Log.d("读取字库数据", "readCharacter areaCode = " + areaCode + "  posCode = " + posCode + " path = " + CHARACTER_PATH + " matrix = " + matrix + " ENCODE = " + ENCODE);

            InputStream in = context.getResources().getAssets()
                    .open(CHARACTER_PATH);
            // 获得真实区码
            int area = areaCode & 0x007F;
            long offset = (area * 191 + posCode - 255) * matrix * 2;
            in.skip(offset);
            data = new byte[matrix * 2];
            in.read(data, 0, matrix * 2);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("读取字库数据", "e = " + e.toString());
        }
        return data;
    }

    private static byte[] readASC(Context context, int areaCode, int matrix) {
        byte[] data = null;
        try {
            InputStream in = context.getResources().getAssets().open(ASC_PATH);
            long offset = matrix * areaCode;
            in.skip(offset);
            data = new byte[matrix];
            in.read(data, 0, matrix);
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    /**
     * 获取所有需要发送消息的总数据
     *
     * @param context
     * @param matrix
     * @param sendContentList
     */
    public static byte[][] getMsgBytes1(Context context, int matrix, List<SendContent> sendContentList) {
        int msgLength = getTotalMessageLength(matrix, sendContentList);//有问题
        Log.e(TAG, "getMsgBytes ------------ msgLength = " + msgLength + " matrix = " + matrix);
        //byte length
        int countLength = 0;
        byte[][] msgByteArray = new byte[matrix][msgLength];
        for (int i = 0; i < 8; i++) {
            SendContent sendContent = sendContentList.get(i);
            //当前消息的长度
            int tempMsgLength;
            //当前消息的内容
            byte[] tempArray;
            if (isNeedSendMsg(sendContent)) {
                String message = sendContent.getMessage();
                tempMsgLength = getMessageByteLength(message, matrix);

                Log.e("读取字库数据", "------------------------------*** 时间 = " + SDF.format(new Date()));
                tempArray = getMessageByteArray(context, message, matrix);
                Log.e("读取字库数据", "------------------------------*** 时间 = " + SDF.format(new Date()));
                Log.e("读取字库数据", "------------------------------*** tempMsgLength = " + tempMsgLength);
                Log.e("读取字库数据", "------------------------------*** tempArray.length = " + tempArray.length);


                for (int j = 0; j < tempMsgLength; j++) {
                    for (int k = 0; k < matrix; k++) {
                        msgByteArray[k][countLength + j] = tempArray[k * tempMsgLength + j];
                    }
                }
                countLength += tempMsgLength;
            }
        }
        return msgByteArray;
    }


    /**
     * 获取所有需要发送消息的总数据
     *
     * @param context
     * @param matrix
     * @param sendContentList
     */
    public static byte[][] getMsgBytes(Context context, int matrix, List<SendContent> sendContentList) {
        int msgLength = getTotalMessageLength(matrix, sendContentList);
        Log.e(TAG, "getMsgBytes ------------ msgLength = " + msgLength);
        //byte length
        int countLength = 0;
        byte[][] msgByteArray = new byte[matrix][msgLength];
        for (int i = 0; i < 8; i++) {
            SendContent sendContent = sendContentList.get(i);
            //当前消息的长度
            int tempMsgLength;
            //当前消息的内容
            byte[] tempArray;
            if (isNeedSendMsg(sendContent)) {
                String message = sendContent.getMessage();
//                String message = sendContent.getMessage().trim();
                tempMsgLength = getSingleMsgByteLength(message, matrix);
                boolean isUserFontLib = LangUtils.isUserFontLib(message);
                Log.e(TAG, "message = " + message + (isUserFontLib ? "数据来自字库" : "数据来自点阵图"));
                if (isUserFontLib) {
                    //从字库获取
                    tempArray = getLibMsgBytes(context, matrix, message);
                } else {
                    //绘制点阵图
                    Bitmap bitmap = drawBitmap(message, matrix);
//                    String name = "LedImg_" + sdf.format(new Date());
//                    saveBmp(bitmap, name);
                    //获取点阵图数据
                    tempArray = bitmapToByteArray(bitmap);
                }
                for (int j = 0; j < tempMsgLength; j++) {
                    for (int k = 0; k < matrix; k++) {
                        msgByteArray[k][countLength + j] = tempArray[k * tempMsgLength + j];
                    }
                }
                countLength += tempMsgLength;
            }
        }
        return msgByteArray;
    }

    public static Bitmap drawBitmap_FONT(String text, int matrix) {
        Paint paint = new Paint();
        paint.setTextSize(matrix);
        paint.setStrokeWidth(1f);
        paint.setTextAlign(Paint.Align.CENTER);
        Typeface typeface = Typeface.createFromAsset(LedBleApplication.instance.getAssets(), "fonts/simsun.ttc");
        paint.setTypeface(typeface);
        int width = (int) ViewUtils.getTextRectWidth(paint, text);
        Bitmap bitmap = Bitmap.createBitmap(width, matrix, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(Color.WHITE);
        canvas.drawRect(new RectF(0, 0, width, matrix), paint);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        canvas.drawText(text, width / 2.0f, ViewUtils.correctTextY(matrix / 2.0f, paint), paint);
        return bitmap;
    }

    /**
     * drawBimap
     *
     * @param text
     * @param matrix
     * @return
     */
    public static Bitmap drawBitmap_ta(String text, int matrix) {
        Paint paint = new Paint();
        paint.setTextSize(matrix * 0.95F);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(false);
        Typeface typeface = Typeface.createFromAsset(LedBleApplication.instance.getAssets(), "fonts/tahoma.ttf");
//        Typeface typeface = Typeface.createFromAsset(LedBleApplication.instance.getAssets(), "fonts/ARIAL.TTF");
        paint.setTypeface(typeface);
        int width = (int) ViewUtils.getTextRectWidth(paint, text);
        Bitmap bitmap = Bitmap.createBitmap(width, matrix, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(Color.WHITE);
        canvas.drawRect(new RectF(0, 0, width, matrix), paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, width / 2.0f, ViewUtils.correctTextY(matrix / 2.0f, paint) - 1, paint);
        return bitmap;
    }

    public static Bitmap drawBitmap(String text, int matrix) {
        Paint paint = new Paint();
        paint.setTextSize(matrix + _TEXT_SIZE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(false);
//        Typeface typeface = Typeface.createFromAsset(LedBleApplication.instance.getAssets(), "fonts/typeface1456.ttf");
//        Typeface typeface = Typeface.createFromAsset(LedBleApplication.instance.getAssets(), "fonts/tahoma.ttf");
//        Typeface typeface = Typeface.createFromAsset(LedBleApplication.instance.getAssets(), "fonts/ARIAL.TTF");
//        Typeface typeface = Typeface.createFromAsset(LedBleApplication.instance.getAssets(), "fonts/12.TTF");
//        Typeface typeface = Typeface.createFromAsset(LedBleApplication.instance.getAssets(), "fonts/simsun.ttc");
//        paint.setTypeface(typeface);
        int width = (int) ViewUtils.getTextRectWidth(paint, text);
        Bitmap bitmap = Bitmap.createBitmap(width, matrix, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(Color.WHITE);
        canvas.drawRect(new RectF(0, 0, width, matrix), paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, width / 2.0f, ViewUtils.correctTextY(matrix / 2.0f, paint) /*- 1*/, paint);
        return bitmap;
    }

    public static Bitmap toBlackAndWhite(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int threshold = 140; // 阈值
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int r = (pixel >> 16) & 0xff;
            int g = (pixel >> 8) & 0xff;
            int b = pixel & 0xff;

            // 计算灰度值
            int gray = (int) (0.2989 * r + 0.5870 * g + 0.1140 * b);

            // 将像素设置为黑色或白色
            pixel = (gray > threshold) ? Color.WHITE : Color.BLACK;
            pixels[i] = pixel;
        }

        return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
    }


    /**
     * drawBimap
     *
     * @param text
     * @param matrix
     * @return
     */
    public static Bitmap drawBitmap_(String text, int matrix) {
        Log.e("点阵图", " text = " + text + " 点阵 = " + matrix);
        Paint paint = new Paint();
        int scaleSize = 100;
        int realHeight = matrix;
        int realWidth = 0;
        paint.setTextSize(matrix);
        paint.setStrokeWidth(0.1f);
        paint.setAntiAlias(false);
        paint.setTextAlign(Paint.Align.CENTER);
        realWidth = (int) ViewUtils.getTextRectWidth(paint, text);

//-----------------------------------------------------------
        matrix = (int) (matrix * scaleSize);
        paint.setTextSize(matrix);
        paint.setStrokeWidth(1f);
        paint.setTextAlign(Paint.Align.CENTER);
        int width = (int) ViewUtils.getTextRectWidth(paint, text);
//-----------------------------------------------------------
        //这个表现可以
//        Typeface /*typeface = Typeface.createFromAsset(LedBleApplication.instance.getAssets(), "fonts/tahoma.ttf");
//        paint.setTypeface(typeface);
//
//        typeface = Typeface.createFromAsset(LedBleApplication.instance.getAssets(), "fonts/simsun.ttc");//新宋体-内容识别有误
//        paint.setTypeface(typeface);*/
//
//                typeface = Typeface.createFromAsset(LedBleApplication.instance.getAssets(), "fonts/tahoma.ttf");//新宋体-内容识别有误
//        paint.setTypeface(typeface);
//-----------------------------------------------------------
        Bitmap bitmap = Bitmap.createBitmap(width, matrix, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.WHITE);
        canvas.drawRect(new RectF(0, 0, width, matrix), paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(text, width / 2.0f, ViewUtils.correctTextY(matrix / 2.0f, paint), paint);
//        return bitmap;
        return getScaleBitmap(bitmap, realWidth, realHeight);
    }

    public static Bitmap getScaleBitmap(Bitmap bitmap, int width, int height) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(newBitmap);
        Rect src = new Rect(0, 0, originalWidth, originalHeight);
        Rect dst = new Rect(0, 0, width, height);
        canvas.drawBitmap(bitmap, src, dst, null);
        return newBitmap;
    }

    /**
     * bitmap 根据点阵转 byte数组
     * 取点顺序
     * 1-->2-->3-->
     * 4-->5-->6-->
     * 7-->8-->9-->
     *
     * @param bitmap
     */
    private static byte[] bitmapToByteArray(Bitmap bitmap) {
        Log.e(TAG, "bitmapToByteArray  bitmap " + (bitmap == null ? "为空" : "不为空"));
        StringBuilder sb = new StringBuilder();
        //保存所有的像素的数组，图片宽×高
        assert bitmap != null;
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        Log.e(TAG, "bitmapToByteArray  width = " + bitmap.getWidth() + " height = " + bitmap.getHeight());
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        StringBuilder appendSB = new StringBuilder();
        int bitmapWidth = bitmap.getWidth();
        int needAppendLength = bitmapWidth % 8 == 0 ? 0 : (8 - bitmapWidth % 8);
        for (int i = 0; i < needAppendLength; i++) {
            appendSB.append("0");
        }
        String appendStr = appendSB.toString();
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] == -1) {
                sb.append("0");
            } else {
                sb.append("1");
            }
            if (i % bitmapWidth == bitmapWidth - 1) {
                sb.append(appendStr);
            }
        }
        String imgStr = sb.toString();
        int imgStrLength = imgStr.length();
        Log.e(TAG, "imgStrLength = " + imgStrLength + "\n imgStr = " + imgStr);
        Log.e(TAG, "sbLength = " + sb.length() + "\n sb = " + sb.toString());
        return DataUtils.binaryString2ByteArray(imgStr);
    }

    /**
     * 获取led数据
     *
     * @param bitmap
     * @return
     */
    public static String getLedData(Bitmap bitmap) {
        Log.e(TAG, "bitmapToByteArray  bitmap " + (bitmap == null ? "为空" : "不为空"));
        StringBuilder sb = new StringBuilder();

        //保存所有的像素的数组，图片宽×高
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        Log.e(TAG, "bitmapToByteArray  width = " + bitmap.getWidth() + " height = " + bitmap.getHeight());
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int pixel : pixels) {
            if (pixel == -1) {
                sb.append("1");
            } else {
                sb.append("0");
            }
        }
        String imgStr = sb.toString();
        int imgStrLength = imgStr.length();
        Log.e(TAG, "imgStrLength = " + imgStrLength + "\n imgStr = " + imgStr);
        Log.e(TAG, "sbLength = " + sb.length() + "\n sb = " + sb.toString());
        return imgStr;
    }


    /**
     * 将Bitmap存为 .bmp格式图片
     * storage/emulated/0/LED_IMG/LedImg_20231104103621
     *
     * @param bitmap
     */
    private static void saveBmp(Bitmap bitmap, String fileName) {
        if (bitmap == null) {
            return;
        }
        // 位图大小
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
        // 图像数据大小
        int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
        String bmpFilePath = "";
        try {
            // 存储文件名
            File dir = new File(IMG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            bmpFilePath = file.getAbsolutePath();
            Log.e("LED图片", file.getAbsolutePath());
            // storage/emulated/0/LED_IMG/LedImg_20231104103621
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileos = new FileOutputStream(file);
            // bmp文件头
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40;
            // 保存bmp文件头
            writeWord(fileos, bfType);
            writeDword(fileos, bfSize);
            writeWord(fileos, bfReserved1);
            writeWord(fileos, bfReserved2);
            writeDword(fileos, bfOffBits);
            // bmp信息头
            long biSize = 40L;
            int biPlanes = 1;
            int biBitCount = 24;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // 保存bmp信息头
            writeDword(fileos, biSize);
            writeLong(fileos, (long) nBmpWidth);
            writeLong(fileos, (long) nBmpHeight);
            writeWord(fileos, biPlanes);
            writeWord(fileos, biBitCount);
            writeDword(fileos, biCompression);
            writeDword(fileos, biSizeImage);
            writeLong(fileos, biXpelsPerMeter);
            writeLong(fileos, biYPelsPerMeter);
            writeDword(fileos, biClrUsed);
            writeDword(fileos, biClrImportant);
            // 像素扫描
            byte[] bmpData = new byte[bufferSize];
            int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol) {
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {
                    int clr = bitmap.getPixel(wRow, nCol);
                    bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
                }
            }

            fileos.write(bmpData);
            fileos.flush();
            fileos.close();
            Log.e("LED图片", file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            printBmpFile(bmpFilePath);
        }
    }


    private static void printBmpFile(String bmpFilePath) {

        // 指定BMP文件路径，注意需要读取外部存储权限
        // 使用BitmapFactory加载BMP文件
        Bitmap bitmap = null;
        try {
            InputStream inputStream = new FileInputStream(new File(bmpFilePath));
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 检查Bitmap是否正确加载
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            // 遍历所有像素并打印
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = bitmap.getPixel(x, y);
                    Log.d("PixelColor", "Pixel at (" + x + ", " + y + ") - Color: " + pixel);
                }
            }

        } else {
            Log.d("PixelColor", "Bitmap is null");
        }
    }

    private static void writeWord(FileOutputStream stream, int value) throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        stream.write(b);
    }

    private static void writeDword(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    private static void writeLong(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    /**
     * 解析信息内容
     *
     * @param msg
     * @return
     */
    public static List<String> parseMessage(String msg) {
        List<String> ledBmpList = new ArrayList<>();
        List<String> msgList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(\\S+?)\\]");//匹配[xx]的字符串
        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()) {
            String group = matcher.group();
            ledBmpList.add(group);
        }
        for (int i = 0; i < ledBmpList.size(); i++) {
            Log.d("alvin 消息", "图片" + i + " ---- " + ledBmpList.get(i));
        }

        if (ledBmpList.size() > 0) {
            int startIndex, endIndex;
            for (int i = 0; i < ledBmpList.size(); i++) {
                String ledBmpStr = ledBmpList.get(i);
                startIndex = msg.indexOf(ledBmpStr);
                endIndex = startIndex + ledBmpStr.length();
                String starStr = msg.substring(0, endIndex);
                if (startIndex == 0) {
                    msgList.add(ledBmpStr);
                } else if (startIndex > 0) {
                    msgList.add(starStr.substring(0, startIndex));
                    msgList.add(ledBmpStr);
                }
                msg = msg.substring(endIndex);
                if (i == ledBmpList.size() - 1) {
                    if (!TextUtils.isEmpty(msg)) {
                        msgList.add(msg);
                    }
                }
            }
        } else {
            msgList.add(msg);
        }
        return msgList;
    }

    /**
     * 获取信息的byte数组
     *
     * @param context
     * @param message
     * @param matrix
     * @return
     */
    public static byte[] getMessageByteArray(Context context, String message, int matrix) {
        Log.e(TAG, "alvin 消息 ======= " + message);
        //解析的数据集合
        List<String> msgArray = parseMessage(message);
        //本信息的字节长度
        int messageByteLength = getMessageByteLength(message, matrix);
        //信息的二进制长度
        int binaryLength = getWholeMessageBinaryLength(message, matrix);
        //需要添加的0个数
        int needAppendLength = messageByteLength * 8 - binaryLength;
        StringBuilder appendSB = new StringBuilder();
        for (int i = 0; i < needAppendLength; i++) {
            appendSB.append("0");
        }
        String needAppendStr = appendSB.toString();
        StringBuilder messageStr = new StringBuilder();
        try {
            msgBinaryMap.clear();
            for (int i = 0; i < msgArray.size(); i++) {
                initMsgContentMap(context, msgArray.get(i), matrix);
            }
            for (int i = 0; i < matrix; i++) {
                for (int j = 0; j < msgArray.size(); j++) {
                    String key = msgArray.get(j);
                    HashMap<Integer, String> map = msgBinaryMap.get(key);
                    if (map != null) {
                        String str = map.get(i);
                        messageStr.append(str);
                        if (j == msgArray.size() - 1) {
                            messageStr.append(needAppendStr); //最后一个需要拼接
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String ledStr = messageStr.toString();
        int length = ledStr.length() / matrix;
        Log.e(TAG, "================================================================================================== \n \n \n");
        for (int i = 0; i < matrix; i++) {
            String temp = ledStr.substring(i * length, i * length + length);
            Log.e(TAG, "alvin 消息 = " + convert2Img(temp));
        }
        Log.e(TAG, "================================================================================================== \n \n \n");
        return DataUtils.binaryString2ByteArray(messageStr.toString());
    }

    /**
     * 存放每一个信息的 灯珠排列信息
     */
    private static HashMap<String, HashMap<Integer, String>> msgBinaryMap = new HashMap<>();

    /**
     * @param context
     * @param msg
     * @param matrix
     */
    private static void initMsgContentMap(Context context, String msg, int matrix) {
        Log.e(TAG, "=================initMsgContentMap  msg = " + msg);
        if (msg.startsWith("[LED") && msg.endsWith("]")) {
            String key = msg;
            msg = msg.replace("[LED", "");
            msg = msg.replace("]", "");
            int id = Integer.parseInt(msg);
            LEDBmp ledBmp = LedBleApplication.instance.getLEDBmpById(id, matrix);
            if (ledBmp != null) { //如果是保存的图片
                Log.e(TAG, "alvin 消息 =  " + msg + " 从数据库中获取的图片数据为::" + ledBmp.toString());
                HashMap<Integer, String> map = new HashMap<>();
                String content = ledBmp.getContent();
                int colSize = content.length() / matrix; //一行多少个灯珠
                for (int i = 0; i < matrix; i++) {
                    String str = content.substring(i * colSize, i * colSize + colSize);
                    map.put(i, str);
                }
                msgBinaryMap.put(key, map);
            } else {
                Log.e(TAG, "alvin 消息 =  " + msg + " 从数据库中获取的图片数据为null");

                boolean isUserFontLib = LangUtils.isUserFontLib(msg);
                //信息字节长度
                int msgByteLength = getSingleMsgByteLength(msg, matrix, isUserFontLib);//消息byte数组长度  8的倍数 0xFF
                //实际的灯珠个数
                int binaryLength = getMessageBinaryLength(msg, matrix, isUserFontLib); //横向灯珠的实际个数 可能不是8的倍数
                int needAppendLength = msgByteLength * 8 - binaryLength; //需要拼接的灯珠数量 每一个横排需要拼接的
                byte[] tempMsgCrosswiseByte = new byte[msgByteLength];

                byte[] tempArray;//当前信息的byte数组长度
                //msg对应的byte长度
                if (isUserFontLib) {
                    //从字库获取
                    tempArray = getLibMsgBytes(context, matrix, msg);
                    Log.e(TAG, "=================从字库获取");
                } else {
                    //绘制点阵图
                    Bitmap bitmap = drawBitmap(msg, matrix);
                    Log.e(TAG, "=================绘制点阵图");

                    //获取点阵图数据
                    tempArray = bitmapToByteArray(bitmap);
                }
                HashMap<Integer, String> map = new HashMap<>();
                for (int i = 0; i < matrix; i++) {
                    System.arraycopy(tempArray, i * msgByteLength, tempMsgCrosswiseByte, 0, msgByteLength);
                    String crosswiseStr = DataUtils.byteArray2BinaryString(tempMsgCrosswiseByte);
                    crosswiseStr = crosswiseStr.substring(0, crosswiseStr.length() - needAppendLength);
                    map.put(i, crosswiseStr);
                }
                msgBinaryMap.put(msg, map);
            }
        } else {
            boolean isUserFontLib = LangUtils.isUserFontLib(msg);
            //信息字节长度
            int msgByteLength = getSingleMsgByteLength(msg, matrix, isUserFontLib);//消息byte数组长度  8的倍数 0xFF
            //实际的灯珠个数
            int binaryLength = getMessageBinaryLength(msg, matrix, isUserFontLib); //横向灯珠的实际个数 可能不是8的倍数
            int needAppendLength = msgByteLength * 8 - binaryLength; //需要拼接的灯珠数量 每一个横排需要拼接的

            byte[] tempArray;//当前信息的byte数组长度
            //msg对应的byte长度
            if (isUserFontLib) {
                //从字库获取
                tempArray = getLibMsgBytes(context, matrix, msg);
                Log.e(TAG, "=================从字库获取 111  msgByteLength = " + msgByteLength);
            } else {
                //绘制点阵图
                Bitmap bitmap = drawBitmap(msg, matrix);
                Log.e(TAG, "=================绘制点阵图 111  msgByteLength = " + msgByteLength);
                String name = "LedImg_" + sdf.format(new Date());
                saveBmp(bitmap, name);
                //获取点阵图数据
                tempArray = bitmapToByteArray(bitmap);
            }

            HashMap<Integer, String> map = new HashMap<>();
            msgByteLength = tempArray.length / matrix;
            byte[] tempMsgCrosswiseByte = new byte[msgByteLength];
            Log.e(TAG, "=================tempArray.length = " + tempArray.length + " , msgByteLength = " + msgByteLength);
            for (int i = 0; i < matrix; i++) {
                System.arraycopy(tempArray, i * msgByteLength, tempMsgCrosswiseByte, 0, msgByteLength);
                String crosswiseStr = DataUtils.byteArray2BinaryString(tempMsgCrosswiseByte);
                crosswiseStr = crosswiseStr.substring(0, crosswiseStr.length() - needAppendLength);
                map.put(i, crosswiseStr);
            }
            msgBinaryMap.put(msg, map);
        }
    }

    private static String convert2Img(String str) {
        String temp = str.replace("1", " ● ");
        temp = temp.replace("0", "   ");
        return temp;
    }

    /**
     * 获取某一行数据的 01001字符串
     *
     * @param context
     * @param msg
     * @param index
     * @param matrix
     * @return
     */
    public static String getMsgCrosswiseByte(Context context, String msg, int index, int matrix) {
        byte[] msgCrosswiseByte;
        String crosswiseStr = null;
        if (msg.startsWith("[LED") && msg.endsWith("]")) {
            msg = msg.replace("[LED", "");
            msg = msg.replace("]", "");
            int id = Integer.parseInt(msg);
            LEDBmp ledBmp = LedBleApplication.instance.getLEDBmpById(id, matrix);
//            LEDBmp ledBmp = DataSupport.find(LEDBmp.class, id);
            if (ledBmp != null) {
                String content = ledBmp.getContent();
                int colSize = content.length() / matrix; //一行多少个灯珠
                crosswiseStr = content.substring(index * colSize, index * colSize + colSize);
            } else {
                //信息字节长度
                int msgByteLength = getSingleMsgByteLength(msg, matrix);
                //实际的灯珠个数
                int binaryLength = getMessageBinaryLength(msg, matrix);
                int needAppendLength = msgByteLength * 8 - binaryLength;
                msgCrosswiseByte = new byte[msgByteLength];
                boolean isUserFontLib = LangUtils.isUserFontLib(msg);
                byte[] tempArray;
                //msg对应的byte长度
                if (isUserFontLib) {
                    //从字库获取
                    tempArray = getLibMsgBytes(context, matrix, msg);
                } else {
                    //绘制点阵图
                    Bitmap bitmap = drawBitmap(msg, matrix);
                    //获取点阵图数据
                    tempArray = bitmapToByteArray(bitmap);
                }
                System.arraycopy(tempArray, index * msgByteLength, msgCrosswiseByte, 0, msgByteLength);
                crosswiseStr = DataUtils.byteArray2BinaryString(msgCrosswiseByte);
                crosswiseStr = crosswiseStr.substring(0, crosswiseStr.length() - needAppendLength);
            }
        } else {
            //信息字节长度
            int msgByteLength = getSingleMsgByteLength(msg, matrix);
            //实际的灯珠个数
            int binaryLength = getMessageBinaryLength(msg, matrix);
            int needAppendLength = msgByteLength * 8 - binaryLength;
            msgCrosswiseByte = new byte[msgByteLength];
            boolean isUserFontLib = LangUtils.isUserFontLib(msg);
            byte[] tempArray; //字库的零时数组
            //msg对应的byte长度
            if (isUserFontLib) {
                //从字库获取
                tempArray = getLibMsgBytes(context, matrix, msg);
            } else {
                //绘制点阵图
                Bitmap bitmap = drawBitmap(msg, matrix);
                //获取点阵图数据
                tempArray = bitmapToByteArray(bitmap);
            }
            System.arraycopy(tempArray, index * msgByteLength, msgCrosswiseByte, 0, msgByteLength);
            crosswiseStr = DataUtils.byteArray2BinaryString(msgCrosswiseByte);
            crosswiseStr = crosswiseStr.substring(0, crosswiseStr.length() - needAppendLength);
        }
        return crosswiseStr;
    }

    /**
     * 获取信息的长度
     *
     * @param msg
     * @param matrix
     * @return
     */
    public static int getMessageByteLength(String msg, int matrix) {
        int length = 0;
        List<String> msgArray = parseMessage(msg);
        for (String str : msgArray) {
            Log.d("分解的字符串", "=== str =>" + str);
            length += getMessageBinaryLength(str, matrix);
        }
        return (length + 7) / 8;
    }

    /**
     * 获取一条完整信息的二进制长度
     *
     * @param msg
     * @param matrix
     * @return
     */
    public static int getWholeMessageBinaryLength(String msg, int matrix) {
        int length = 0;
        List<String> msgArray = parseMessage(msg);
        for (String str : msgArray) {
            length += getMessageBinaryLength(str, matrix);
        }
        return length;
    }


    /**
     * 获取信息的二进制长度（灯珠的横向个数）
     *
     * @param content
     * @param matrix
     * @return
     */
    private static int getMessageBinaryLength(String content, int matrix) {
        boolean isUserFontLib = LangUtils.isUserFontLib(content);
        return getMessageBinaryLength(content, matrix, isUserFontLib);
    }

    private static int getMessageBinaryLength(String content, int matrix, boolean isUserFontLib) {
        if (content.startsWith("[LED") && content.endsWith("]")) {
            String idStr = content.replace("[LED", "");
            idStr = idStr.replace("]", "");
            int id = Integer.parseInt(idStr);
            LEDBmp ledBmp = LedBleApplication.instance.getLEDBmpById(id, matrix);
            //LEDBmp ledBmp = DataSupport.find(LEDBmp.class, id);
            if (ledBmp != null) {
                return ledBmp.getContent().length() / matrix;
            } else {
                return 0;
            }
        } else {
            if (isUserFontLib) {
                return getLibMessageLength(content, matrix);
            } else {
                return getMsgBitmapPixLength(content, matrix);
            }
        }
    }

}