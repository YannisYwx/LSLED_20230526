package com.yannis.ledcard.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * $desc$
 *
 * @author yannis
 *         Created on 2016/3/25 10:54
 *         Email:923080261@qq.com
 */
public class ViewUtils {


    /**
     *
     * @param colorA
     *            the startColor
     * @param colorB
     *            the endColor
     * @param degree
     *            the count of color what you want between startColor & endColor
     * @param progress
     *            the index in degree
     * @return
     */
    public static int getColorBetweenAB(int colorA, int colorB, float degree, int progress) {
        // calculate R
        float r = (((colorB & 0xFF0000) >> 16) - ((colorA & 0xFF0000) >> 16)) / degree * progress + ((colorA & 0xFF0000) >> 16);
        // calculate G
        float g = (((colorB & 0x00FF00) - (colorA & 0x00FF00)) >> 8) / degree * progress + ((colorA & 0x00FF00) >> 8);
        // calculate B
        float b = ((colorB & 0x0000FF) - (colorA & 0x0000FF)) / degree * progress + (colorA & 0x0000FF);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    /**
     * @param b       需要模糊的bitmap
     * @param context
     * @return 模糊后的bitmap
     */
    public static Bitmap blur(Bitmap b, Context context) {
        RenderScript rs = RenderScript.create(context);
        Allocation overlayAlloc = Allocation.createFromBitmap(rs, b);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(25);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(b);
        return b;

    }

    /**
     * @param paint 测试前，必须保证paint已经设置过textSize , Typeface;
     * @return 该paint画出文字的理应高度
     */
    public static float getTextHeight(Paint paint) {
        Paint.FontMetrics m = paint.getFontMetrics();
        return m.bottom - m.top;
    }

    /**
     * @param paint
     * @param content
     * @return
     */
    public static float getTextRectWidth(Paint paint, String content) {
        Rect rect = new Rect();
        paint.getTextBounds(content, 0, content.length(), rect);
        return paint.measureText(content);
    }

    /**
     * 这个返回值<={@link #getTextHeight(Paint)}
     *
     * @return
     */
    public static float getTextRectHeight(Paint paint, String content) {
        Rect rect = new Rect();
        paint.getTextBounds(content, 0, content.length(), rect);
        return rect.height();
    }

    public static float px2Dp(int px, Context context) {
        return px * context.getResources().getDisplayMetrics().density;
    }

    /**
     * 矫正text的y轴位置
     *
     * @param centerY
     * @param mPaint
     * @return
     */
    public static float correctTextY(float centerY, Paint mPaint) {
        return centerY - (mPaint.ascent() + mPaint.descent()) / 2.0f;
    }

    /**
     * 把设备特定的像素(px)转换成密度无关的像素(dp)
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float pixelsToDp(Context context, float px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }


}
