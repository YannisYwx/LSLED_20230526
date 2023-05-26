package com.yannis.ledcard.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.yannis.ledcard.R;
import com.yannis.ledcard.util.ViewUtils;

import java.util.Locale;


/**
 * $desc$
 *
 * @author yannis
 *         Created on 2016/3/26 17:13
 *         Email:923080261@qq.com
 */
public class ItemView extends View {
    private static final float TEXT_SIZE_OF_HEIGHT_SCALE = 1 / 3.5f;
    private static final float MARGIN_LEFT_RIGHT_OF_WIDTH = 1 / 20.0f;

    private String label;
    private String value;
    private boolean hasBottomLine;
    private boolean hasTopLine;
    private int lineColor;
    private int labelColor;
    private int valueColor;

    private Paint paint;

    private float strokeWidth = 4;

    private int width, height;
    private float textSize;
    private float marginLR;

    public ItemView(Context context) {
        super(context);
        init();
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ItemView);
        label = ta.getString(R.styleable.ItemView_label);
        value = ta.getString(R.styleable.ItemView_value);
        hasBottomLine = ta.getBoolean(R.styleable.ItemView_hasBottomLine, false);
        hasTopLine = ta.getBoolean(R.styleable.ItemView_hasTopLine, false);
        lineColor = ta.getColor(R.styleable.ItemView_lineColor, Color.parseColor("#D9D9D9"));
        labelColor = ta.getColor(R.styleable.ItemView_labelColor, Color.parseColor("#FFFFFF"));
        valueColor = ta.getColor(R.styleable.ItemView_valueColor, Color.parseColor("#F0F3F3"));
        ta.recycle();

        label = label == null || label.equals("") ? "" : label;
        value = value == null || value.equals("") ? "" : value;
        init();

    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        this.textSize = height * TEXT_SIZE_OF_HEIGHT_SCALE;
        this.marginLR = width * MARGIN_LEFT_RIGHT_OF_WIDTH;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(lineColor);
        paint.setStrokeWidth(strokeWidth);
        if (hasBottomLine) {
            canvas.drawLine(0, height, width, height, paint);
        }

        if (hasTopLine) {
            canvas.drawLine(0, 0, width, 0, paint);
        }
        paint.setTextSize(textSize);
        paint.setColor(labelColor);
        paint.setTextAlign(Paint.Align.LEFT);
        if (Locale.getDefault().getLanguage().equalsIgnoreCase("ar")) {
            //如果是阿拉伯语 那么翻转
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(label, width - marginLR, ViewUtils.correctTextY(height / 2.0f, paint), paint);
        } else {
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(label, marginLR, ViewUtils.correctTextY(height / 2.0f, paint), paint);
        }
        paint.setTextSize(textSize);
        paint.setColor(valueColor);
        if (Locale.getDefault().getLanguage().equalsIgnoreCase("ar")) {
            //如果是阿拉伯语 那么翻转
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(value, marginLR, ViewUtils.correctTextY(height / 2.0f, paint), paint);
        } else {
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(value, width - marginLR, ViewUtils.correctTextY(height / 2.0f, paint), paint);
        }
    }

    public void setValue(String value) {
        this.value = value;
        invalidate();
    }

    public String getValue() {
        return value;
    }
}
