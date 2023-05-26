package com.yannis.ledcard.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.yannis.ledcard.util.PrefUtils;
import com.yannis.ledcard.util.ViewUtils;

import static com.yannis.ledcard.activity.MainActivity.PIX;

/**
 * @author : Yannis.Ywx
 * @createTime : 2018/11/21 13:30
 * @email : 923080261@qq.com
 * @description : 显示led图片的控件
 */
public class LEDView extends View {

    /**
     * led灯珠灯亮颜色
     */
    private int ledBrightColor = Color.parseColor("#F8171D");
    /**
     * led灯灭
     */
    private int lightOffColor = Color.parseColor("#ABADA8");
    /**
     * 背景颜色
     */
    private int backgroundColor = Color.parseColor("#FFFFFF");

    /**
     * led灯珠个数
     */
    private int ledSize = 11;
    /**
     * led灯珠列数
     */
    private int ledColSize = 11;
    /**
     * ledView宽高
     */
    private int w, h;
    /**
     * 灯珠半径
     */
    private float ledBeadRadius;
    /**
     * led数据
     */
    private String ledData;
    private long startTouchTime;
    private float startX = 0;
    private float startY = 0;
    private boolean isCanTouch = true;
    private float bias = 0;

    private Paint mPaint;


    public LEDView(Context context) {
        super(context, null);
        init();
    }

    public LEDView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public LEDView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.h = Math.min(w, h);
        ledSize = PrefUtils.getIntFromPrefs(this.getContext(), PIX, 12);
        ledBeadRadius = this.h / (ledSize * 2.0f);
        this.w = this.h;
        Log.d("PY", "初始化数据  宽高[ width = " + this.w + ", height = " + this.h + " ]");
        Log.d("PY", "初始化数据  直径 = " + ledBeadRadius * 2 + ", 半径 = " + ledBeadRadius);
        super.onSizeChanged(this.w, this.h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Log.e("LEDView OnMeasure","col size = " + ledColSize);
        if(ledColSize * ledBeadRadius * 2 <= dm.widthPixels)
            setMeasuredDimension(getDefaultSize(dm.widthPixels, widthMeasureSpec), getDefaultSize(ViewUtils.dp2px(getContext(), 11), heightMeasureSpec));
        else {
            Log.d("LONG PIC",">>>>>>>>>IS A LONG PIC");
            setMeasuredDimension(getDefaultSize((int)(ledColSize * ledBeadRadius * 2 + 1), widthMeasureSpec), getDefaultSize(ViewUtils.dp2px(getContext(), 11), heightMeasureSpec));
        }
    }

    /**
     * 作用是返回一个默认的值，如果MeasureSpec没有强制限制的话则使用提供的大小.否则在允许范围内可任意指定大小
     * 第一个参数size为提供的默认大小，第二个参数为测量的大小
     */
    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            // Mode = UNSPECIFIED,AT_MOST时使用提供的默认大小
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                // Mode = EXACTLY时使用测量的大小
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ledData = initLEDData();
    }


    private String initLEDData() {
        StringBuilder sb = new StringBuilder();
        ledSize = PrefUtils.getIntFromPrefs(this.getContext(), PIX, 12);
        for (int i = 0; i < ledSize * ledSize; i++) {
            sb.append("0");
        }
        return sb.toString();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ledSize = PrefUtils.getIntFromPrefs(this.getContext(), PIX, 12);
        Log.e("888888", "ledColSize = " + this.ledColSize + " ledData = " + ledData );
        drawAllLedBead(canvas, ledColSize);
        if(ledData.length() == ledSize*ledColSize)
            drawBrightLedBead(canvas, ledData);
    }

    /**
     * 画所有的灯珠
     *
     * @param canvas
     */
    private void drawAllLedBead(Canvas canvas, int width) {
        float cX, cY;
        ledSize = PrefUtils.getIntFromPrefs(this.getContext(), PIX, 12);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if(width == ledSize)
            this.bias = (dm.widthPixels - w)/2;
        else
            this.bias = 0;
        mPaint.setColor(lightOffColor);
        for (int i = 0; i < ledSize; i++) {
            for (int j = 0; j < width; j++) {
                cX = j * ledBeadRadius * 2 + ledBeadRadius + bias;
                cY = i * ledBeadRadius * 2 + ledBeadRadius;
//                Log.e("888888","cX " + cX + "  cY "+cY);
                canvas.drawCircle(cX, cY, ledBeadRadius, mPaint);
            }
        }
    }

    /**
     * 花要亮的灯珠颜色
     *
     * @param canvas
     * @param ledData
     */
    private void drawBrightLedBead(Canvas canvas, String ledData) {
        float cX, cY;
        mPaint.setColor(ledBrightColor);
        ledSize = PrefUtils.getIntFromPrefs(this.getContext(), PIX, 12);
        for (int x = 0; x < ledSize; x++) {
            for (int y = 0; y < ledColSize; y++) {
                boolean isBright = isLEDBeadBright(ledData, x, y);
                if (isBright) {
                    cX = y * ledBeadRadius * 2 + ledBeadRadius + bias;
                    cY = x * ledBeadRadius * 2 + ledBeadRadius;
                    canvas.drawCircle(cX, cY, ledBeadRadius, mPaint);
                }
            }
        }
    }

    /**
     * 指定led位置的灯珠是否为亮
     *
     * @param data led数据
     * @param x    x坐标
     * @param y    y坐标
     * @return true 该坐标亮 false 灯灭
     */
    private boolean isLEDBeadBright(String data, int x, int y) {
        boolean isBright = false;
        if (TextUtils.isEmpty(data)) {
            return false;
        }
        int index = x * ledColSize + y;
        if (data.charAt(index) == '1') {
            isBright = true;
        }
        return isBright;
    }

    public void setLEDData(String ledData) {
        Log.e("888888", "setLEDData - " + ledData);
        if (TextUtils.isEmpty(ledData)) {
            ledData = initLEDData();
        }
        ledSize = PrefUtils.getIntFromPrefs(this.getContext(), PIX, 12);
        this.ledColSize = ledData.length() / ledSize;
        ledBeadRadius = this.w / (ledSize * 2.0f);
        this.ledData = ledData;
        invalidate();
    }

    public void setIsCanTouch(boolean isCanTouch) {
        this.isCanTouch = isCanTouch;
    }

    public String getLedData() {
        return ledData;
    }

    public void clear(){
        this.ledData = initLEDData();
        ledSize = PrefUtils.getIntFromPrefs(this.getContext(), PIX, 12);
        ledBeadRadius = this.w / (ledSize * 2.0f);
        invalidate();
    }

    public void reverse(){
        ledData = ledData.replace('0','2');
        ledData = ledData.replace('1','3');
        ledData = ledData.replace('2','1');
        ledData = ledData.replace('3','0');
        ledSize = PrefUtils.getIntFromPrefs(this.getContext(), PIX, 12);
        ledBeadRadius = this.w / (ledSize * 2.0f);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {//获取用户的行动
            case MotionEvent.ACTION_DOWN://按下时回调
                startTouchTime = System.currentTimeMillis();
                Log.d("PY", "按下");
                //获取用户手指按下时的坐标
                startX = motionEvent.getX();
                startY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE://手指滑动时调用
                float moveX = motionEvent.getX();
                float moveY = motionEvent.getY();
                if (Math.abs(moveX - startX) <= 10 && Math.abs(moveY - startY) <= 10) {
                    break;
                }
                Log.d("PY", "Start  [ x = " + startX + ", y = " + startY + " ] Move  [ x = " + moveX + ", y = " + moveY + " ] 触摸差  [ x = " + Math.abs(moveX - startX) + ", y = " + Math.abs(moveY - startY) + " ] ");
                drawMoveDot(false, moveX, moveY);
                break;
            case MotionEvent.ACTION_UP://松开（抬起）的调用
                float endX = motionEvent.getX();
                float endY = motionEvent.getY();
                long endTouchTime = System.currentTimeMillis();
                Log.e("PY", "松开-------startTouchTime = " + startTouchTime + " -- endTouchTime = " + endTouchTime + " 时差 = " + (endTouchTime - startTouchTime));
                Log.d("PY", "松开 startX = " + startX + ", startY = " + startY);
                Log.d("PY", "松开 endX = " + endX + ", endY = " + endY);
                if (endTouchTime - startTouchTime <= 500) {
                    if (Math.abs(endX - startX) <= 10 && Math.abs(endY - startY) <= 10) {
                        Log.d("PY", "点击事件");
                        drawMoveDot(true, endX, endY);
                    }
                }
                Log.d("PY", "松开");
                break;
        }
        return isCanTouch;
    }

    private void drawMoveDot(boolean isOnClick, float x, float y) {
        x = Math.max(0, x);
        y = Math.max(0, y);
        int indexX = (int) (y / (ledBeadRadius * 2));
        int indexY = (int) ((x - bias) / (ledBeadRadius * 2));
        if(indexX + 1 > ledSize || indexY + 1 > ledColSize)
            return;
        int index = indexX * ledColSize + indexY;
        Log.e("PY", "\n " + (isOnClick ? "点击事件 " : "触摸事件") + " 格子Size = " + (ledBeadRadius * 2) + ",正在滑动  [ x = " + x + ", y = " + y + " ] -- Index [ X = " + indexX + ", Y = " + indexY + " ] index = " + index);
        StringBuilder sb = new StringBuilder(ledData);
        if (index <= ledData.length() - 1) {
            String str = isOnClick ? ledData.charAt(index) == '1' ? "0" : "1" : "1";
            Log.e("PY", (isOnClick ? "点击事件 " : "触摸事件") + " 坐标 = " + index + " 状态 = " + str);
            sb.replace(index, index + 1, str);
            ledData = sb.toString();
            invalidate();
        }
    }
}
