package com.yannis.ledcard.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yannis.ledcard.LedBleApplication;
import com.yannis.ledcard.bean.LEDBmp;
import com.yannis.ledcard.widget.LEDView;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Yannis.Ywx
 * @createTime : 2018/11/6 16:06
 * @email : 923080261@qq.com
 * @description : 位图工具
 */
public class BitmapUtils {
    private static final String TAG = "BitmapUtils";
    private static final String sdCardDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;// SD卡的根目录地址
    private static final String APP_DIR = "LED_BMP" + File.separator;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);

    /**
     * 转为二值图像
     *
     * @param bmp 原图bitmap
     * @param tmp 中间值 150
     * @return
     */
    public static Bitmap convertToBMW(Bitmap bmp, int tmp) {
        // 获取位图的宽
        int width = bmp.getWidth();
        // 获取位图的高
        int height = bmp.getHeight();
        // 通过位图的大小创建像素点数组
        int[] pixels = new int[width * height];
        // 设定二值化的域值，默认值为100
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                // 分离三原色
                alpha = ((grey & 0xFF000000) >> 24);
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                if (red > tmp) {
                    red = 255;
                } else {
                    red = 0;
                }
                if (blue > tmp) {
                    blue = 255;
                } else {
                    blue = 0;
                }
                if (green > tmp) {
                    green = 255;
                } else {
                    green = 0;
                }
                pixels[width * i + j] = alpha << 24 | red << 16 | green << 8
                        | blue;
                if (pixels[width * i + j] == -1) {
                    pixels[width * i + j] = -1;
                } else {
                    pixels[width * i + j] = -16777216;
                }
            }
        }
        // 新建图片
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return ThumbnailUtils.extractThumbnail(newBmp, width, height);
    }

    /**
     * 转为二值图像
     *
     * @param bmp 原图bitmap
     * @param w   转换后的宽
     * @param h   转换后的高
     * @return
     */
    public static Bitmap convertToBMW(Bitmap bmp, int w, int h, int tmp) {
        // 获取位图的宽
        int width = bmp.getWidth();
        // 获取位图的高
        int height = bmp.getHeight();
        // 通过位图的大小创建像素点数组
        int[] pixels = new int[width * height];
        // 设定二值化的域值，默认值为100
        //tmp = 180;
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                // 分离三原色
                alpha = ((grey & 0xFF000000) >> 24);
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                if (red > tmp) {
                    red = 255;
                } else {
                    red = 0;
                }
                if (blue > tmp) {
                    blue = 255;
                } else {
                    blue = 0;
                }
                if (green > tmp) {
                    green = 255;
                } else {
                    green = 0;
                }
                pixels[width * i + j] = alpha << 24 | red << 16 | green << 8
                        | blue;
                if (pixels[width * i + j] == -1) {
                    pixels[width * i + j] = -1;
                } else {
                    pixels[width * i + j] = -16777216;
                }
            }
        }
        // 新建图片
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return ThumbnailUtils.extractThumbnail(newBmp, w, h);
    }

    /**
     * 重新设置bitmap的宽和高
     *
     * @param bitmap 点阵图
     * @param w      width
     * @param h      height
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int w, int h) {
        return ThumbnailUtils.extractThumbnail(bitmap, w, h);
    }

    /**
     * 将Bitmap存为 .bmp格式图片
     *
     * @param bitmap 点阵图
     */
    public static void saveBitmapToBmpFile(Bitmap bitmap) {
        saveBitmapToBmpFile(bitmap, generateFileName());
    }

    public static Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(h, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.draw(c);
        return bmp;
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height, boolean isLongPic) {
        int w = v.getWidth();
        int h = v.getHeight();
//        Log.e("ViewSize: ", "w = " + w + " h = " + h);
        Bitmap bmp;
        if (isLongPic) {
            bmp = Bitmap.createBitmap(h, h, Bitmap.Config.ARGB_8888);
        }
        else {
            bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.draw(c);
        return ThumbnailUtils.extractThumbnail(bmp, width, height);
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }


    /**
     * 将Bitmap存为 .bmp格式图片
     *
     * @param bitmap   点阵图
     * @param fileName 文件名
     */
    public static void saveBitmapToBmpFile(Bitmap bitmap, String fileName) {
        if (bitmap == null) {
            return;
        }
        // 位图大小
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
        // 图像数据大小
        int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
        try {
            // 存储文件名
            File dir = new File(sdCardDirPath + APP_DIR);
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                if (!dir.exists()) {
                    boolean isOK = dir.mkdirs();
                    Log.e(TAG, "新建文件 dir = " + dir.getAbsolutePath() + " fileName = " + fileName + " isOK = " + isOK);
                }
            }
            File file = new File(dir, fileName);
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
            writeDWord(fileos, bfSize);
            writeWord(fileos, bfReserved1);
            writeWord(fileos, bfReserved2);
            writeDWord(fileos, bfOffBits);
            // bmp信息头
            long biSize = 40L;
            int biPlanes = 1;
            int biBitCount = 24;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXPixelPerMeter = 0L;
            long biYPixelPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // 保存bmp信息头
            writeDWord(fileos, biSize);
            writeLong(fileos, (long) nBmpWidth);
            writeLong(fileos, (long) nBmpHeight);
            writeWord(fileos, biPlanes);
            writeWord(fileos, biBitCount);
            writeDWord(fileos, biCompression);
            writeDWord(fileos, biSizeImage);
            writeLong(fileos, biXPixelPerMeter);
            writeLong(fileos, biYPixelPerMeter);
            writeDWord(fileos, biClrUsed);
            writeDWord(fileos, biClrImportant);
            // 像素扫描
            byte bmpData[] = new byte[bufferSize];
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeWord(FileOutputStream stream, int value) throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        stream.write(b);
    }

    private static void writeDWord(FileOutputStream stream, long value) throws IOException {
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

    public static String generateFileName() {
        return "LEDBmp_" + sdf.format(new Date()) ;
    }

    /**
     * 获取自动生成的文件目录
     *
     * @return
     */
    public static String generateFilePath() {
        return sdCardDirPath + APP_DIR + generateFileName();
    }

    public static String getBmpDir() {
        return sdCardDirPath + APP_DIR;
    }

    /**
     * 获取bitmap保存的目录
     *
     * @return
     */
    public static String getBitmapSaveDir() {
        return sdCardDirPath + APP_DIR;
    }


    public static Bitmap getBitmapWithName(String name, int width, int height) {
        Bitmap bitmap;
        if (name.startsWith("[LED") && name.endsWith("]")) {
            String idStr = name.replace("[LED", "");
            idStr = idStr.replace("]", "");
            int id = Integer.parseInt(idStr);
            LEDBmp ledBmp = LedBleApplication.instance.getLEDBmpById(id,11);
//            LEDBmp ledBmp = DataSupport.find(LEDBmp.class, id);
            String filePath = ledBmp.getFilePath();
            if (new File(filePath).exists()) {
                bitmap = BitmapFactory.decodeFile(filePath);
                Log.e(TAG, "getBitmapWithName --- name = " + name + " width = " + width + " ---- height = " + height);
                return ThumbnailUtils.extractThumbnail(bitmap, width, height);
            }
        } else {
            return null;
        }
        return null;
    }


    private void parseMessage(Context context, String str, EditText editText) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        Pattern pattern = Pattern.compile("\\[(\\S+?)\\]");//匹配[xx]的字符串
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String group = matcher.group();
            group = group.substring(1, group.length() - 1);
            Log.e("399", group);
            LEDView ledView = new LEDView(context);
            ImageSpan imageSpan = new ImageSpan(context, loadBitmapFromView(ledView));
            spannableStringBuilder.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        editText.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
    }
}
