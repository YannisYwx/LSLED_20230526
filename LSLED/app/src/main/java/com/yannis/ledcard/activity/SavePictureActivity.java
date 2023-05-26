package com.yannis.ledcard.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.yannis.ledcard.LedBleApplication;
import com.yannis.ledcard.R;
import com.yannis.ledcard.base.BaseActivity;
import com.yannis.ledcard.bean.LEDBmp;
import com.yannis.ledcard.util.BitmapUtils;
import com.yannis.ledcard.util.GlideImageLoader;
import com.yannis.ledcard.util.LedDataUtil;
import com.yannis.ledcard.util.PrefUtils;
import com.yannis.ledcard.util.ViewUtils;
import com.yannis.ledcard.widget.LEDView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import org.litepal.crud.DataSupport;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.yannis.ledcard.activity.MainActivity.PIX;

/**
 * @author : Yannis.Ywx
 * @createTime : 2018/11/21 16:56
 * @email : 923080261@qq.com
 * @description : TODO
 */
public class SavePictureActivity extends BaseActivity {

    public static final String LEDBMP_UNDER_EDITING = "_led_under_editing";

    @BindView(R.id.iv_pic)
    public ImageView ivPic;
    @BindView(R.id.iv_reverse)
    public Button ivReverse;
    @BindView(R.id.iv_clear)
    public Button ivClear;
    @BindView(R.id.iv_wb)
    public ImageView ivWB;
    @BindView(R.id.led_big)
    public LEDView ledBig;
    @BindView(R.id.ll_leds)
    public LinearLayout llLeds;
    @BindView(R.id.sb)
    public SeekBar sb;
    @BindView(R.id.tv_toolbar_center)
    public TextView tvContext;
    @BindView(R.id.tv_right)
    public TextView tvRight;
    @BindView(R.id.hscrollview)
    public HorizontalScrollView hscrollview;
    @BindView(R.id.iv_save)
    public Button btnSave;
    @BindView(R.id.iv_scroll)
    public Button ivScroll;
    @BindView(R.id.title_origin)
    public TextView titleOrigin;
    @BindView(R.id.title_bw)
    public TextView titleBW;
    @BindView(R.id.ll_sb)
    public LinearLayout llSb;
    @BindView(R.id.rl_pics)
    public LinearLayout llpics;
    @BindView(R.id.l_leds)
    public LinearLayout llLEDs;
    @BindView(R.id.ll_parent)
    public LinearLayout llParent;
    ImagePicker mImagePicker;
    ArrayList<ImageItem> images = null;

    SoftReference<Bitmap> bitmap;
    SoftReference<Bitmap> bleBitmap;

    private int matrix = 11;
    private LEDBmp ledBmp;

    @Override
    protected void init() {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        ledBmp = (LEDBmp) intent.getSerializableExtra(LEDBMP_UNDER_EDITING);
//        Log.e("getSerializableExtra :", ledBmp.toString());

        ivScroll.setText(R.string.scroll_btn);
        ledBig.setIsCanTouch(true);
        ledBig.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hscrollview.requestDisallowInterceptTouchEvent(false);
                } else {
                    hscrollview.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        });
        tvRight.setText(R.string.pick_pic);
        tvRight.setVisibility(View.VISIBLE);
        tvContext.setText(R.string.edit_pic);
        sb.setMax(255);
        mImagePicker = ImagePicker.getInstance();
        mImagePicker.setImageLoader(new GlideImageLoader());
        mImagePicker.setMultiMode(false);
        mImagePicker.setCrop(false);
        mImagePicker.setShowCamera(false);  //不使用相机
        mImagePicker.setStyle(CropImageView.Style.RECTANGLE);
        Integer width = 280;
        Integer height = 280;
        matrix = PrefUtils.getIntFromPrefs(this, PIX, 11);
        width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics());
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
        mImagePicker.setFocusWidth(width);
        mImagePicker.setFocusHeight(height);
        if(ledBmp ==null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < matrix * matrix; i++) {
                sb.append("0");
            }
            ledBig.setLEDData(sb.toString());
        }else {
//            titleBW.setVisibility(View.INVISIBLE);
//            titleOrigin.setVisibility(View.INVISIBLE);
//            sb.setVisibility(View.INVISIBLE);
//            ivPic.setVisibility(View.INVISIBLE);
//            ivWB.setVisibility(View.INVISIBLE);
            tvRight.setVisibility(View.INVISIBLE);
            llSb.removeAllViews();
            llpics.removeAllViews();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = 100;
            llLEDs.setLayoutParams(params);

            ledBig.setLEDData(ledBmp.getContent());
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("PY", "获取宽高[ width = " + ledBig.getWidth() + ", height = " + ledBig.getHeight() + " ]");
            }
        }, 2000);

    }

    @Override
    protected void initEvent() {
        registerClickEvent(tvRight, btnSave, ivClear, ivReverse, ivScroll);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (bitmap == null || !fromUser) {
                    return;
                }
//                tvTmp.setText(String.valueOf(progress));
                Bitmap bitmapTmp = bitmap.get();
                bitmapTmp = BitmapUtils.convertToBMW(bitmapTmp, bitmap.get().getWidth(), bitmap.get().getHeight(), progress);
                ivWB.setImageBitmap(bitmapTmp);
                int pix = PrefUtils.getIntFromPrefs(SavePictureActivity.this, PIX, 11);
                if(bitmap.get().getWidth() > bitmap.get().getHeight()) {
                    bleBitmap = new SoftReference<>(BitmapUtils.scaleBitmap(bitmapTmp, bitmap.get().getWidth(), pix));
                }
                else
                    bleBitmap = new SoftReference<>(BitmapUtils.scaleBitmap(bitmapTmp, pix, pix));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(bleBitmap != null)
                    ledBig.setLEDData(LedDataUtil.getLedData(bleBitmap.get()));
            }
        });
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_save_picture;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right:
                Intent intent = new Intent(this, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, images);
                startActivityForResult(intent, 100);
                break;
            case R.id.iv_save:
                String content = ledBig.getLedData();
                DisplayMetrics dm = getResources().getDisplayMetrics();
                int screenWidth = dm.widthPixels;
                boolean isLongPic = (ledBig.getWidth() > screenWidth)?true:false;
                List<LEDBmp> ledBmps = DataSupport.where("content = ? and matrix = ?", content, matrix + "").find(LEDBmp.class);
                if (ledBmps != null && ledBmps.size() > 0) {
                    showToast(getResources().getString(R.string.same_model_exist));
                } else if (ledBmp == null){
                    String fileName = BitmapUtils.generateFileName();
                    String dir = BitmapUtils.getBmpDir();
                    LEDBmp ledBmp = new LEDBmp(content, matrix, dir + fileName);
                    ledBmp.save();
                    BitmapUtils.saveBitmapToBmpFile(BitmapUtils.loadBitmapFromView(ledBig
                            , ViewUtils.dp2px(this, 33), ViewUtils.dp2px(this, 33), isLongPic)
                            , fileName);
                    showToast(getResources().getString(R.string.save_msg_success));
                }else {
                    Log.d("SAVING...","here");
                    String dir = BitmapUtils.getBmpDir();
                    String fileName;
                    if(ledBmp.getFilePath() != null) {
                        fileName = ledBmp.getFilePath().replace(dir, "");
                    }
                    else {
                        fileName = BitmapUtils.generateFileName();
                    }
                    BitmapUtils.saveBitmapToBmpFile(BitmapUtils.loadBitmapFromView(ledBig
                            , ViewUtils.dp2px(this, 33), ViewUtils.dp2px(this, 33), isLongPic)
                            , fileName);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("content", content);
                    contentValues.put("filePath", dir + fileName);
                    DataSupport.update(LEDBmp.class, contentValues, ledBmp.getId());
                    showToast(getResources().getString(R.string.save_msg_success));
                }
                LedBleApplication.instance.loadLEDBmpFromDB();
                break;
            case R.id.iv_clear:
                if(ledBmp == null) {
                    ledBig.clear();
                }else {
                    ledBig.setLEDData(ledBmp.getContent());
                }
                break;
            case R.id.iv_scroll:
                if(ivScroll.getText().toString() == getResources().getString(R.string.scroll_btn)) {
                    ivScroll.setText(R.string.draw_btn);
                    ledBig.setIsCanTouch(false);
                    ledBig.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                hscrollview.requestDisallowInterceptTouchEvent(true);
                            } else {
                                hscrollview.requestDisallowInterceptTouchEvent(false);
                            }
                            return true;
                        }
                    });
                }
                else {
                    ivScroll.setText(R.string.scroll_btn);
                    ledBig.setIsCanTouch(true);
                    ledBig.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                hscrollview.requestDisallowInterceptTouchEvent(false);
                            } else {
                                hscrollview.requestDisallowInterceptTouchEvent(true);
                            }
                            return false;
                        }
                    });
                }
                break;
//            case R.id.iv_restore:
//                ledBig.setLEDData(ledSmall.getLedData());
//                break;
            case R.id.iv_reverse:
                ledBig.reverse();
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                ImageItem imageItem = images.get(0);
                Log.e("SendPictureActivity", " w = " + imageItem.width + " h = " + imageItem.height + " path = " + imageItem.path);
                mImagePicker.getImageLoader().displayImage(this, images.get(0).path, ivPic, 280, 280);
                bitmap = new SoftReference<>(BitmapFactory.decodeFile(imageItem.path));
                sb.setProgress(80);
//                tvTmp.setText("80");

                Bitmap bitmap1 = BitmapUtils.convertToBMW(bitmap.get(), bitmap.get().getWidth(), bitmap.get().getHeight(), 80);
                ivWB.setImageBitmap(bitmap1);
                int pix = PrefUtils.getIntFromPrefs(this, PIX, 11);
                Log.e("Long Pic","DETECTED width : " + bitmap.get().getWidth() + "  height : "+ bitmap.get().getHeight());
                if(bitmap.get().getWidth() > bitmap.get().getHeight()) {
                    int a = (int)Math.ceil(bitmap.get().getWidth() * pix/bitmap.get().getHeight());
                    Log.e("Long Pic","DETECTED width : " + a);
                    bleBitmap = new SoftReference<>(BitmapUtils.scaleBitmap(bitmap1, a, pix));
                    llLeds.removeView(ledBig);
                }
                else {
                    bleBitmap = new SoftReference<>(BitmapUtils.scaleBitmap(bitmap1, pix, pix));
                    llLeds.removeView(ledBig);
                }
                ledBig.setLEDData(LedDataUtil.getLedData(bleBitmap.get()));
                llLeds.addView(ledBig);
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_data), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.iv_back)
    public void onBack() {
        finish();
    }
}
