package com.yannis.ledcard.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.yannis.ledcard.R;
import com.yannis.ledcard.base.BaseMVPActivity;
import com.yannis.ledcard.bean.SendContent;
import com.yannis.ledcard.contract.MainContract;
import com.yannis.ledcard.presenter.MainPresenter;
import com.yannis.ledcard.util.DialogUtil;
import com.yannis.ledcard.util.GlideImageLoader;
import com.yannis.ledcard.util.LedDataUtil;
import com.yannis.ledcard.util.PrefUtils;
import com.yannis.ledcard.widget.ItemView;
import com.yannis.ledcard.widget.LEDView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.yannis.ledcard.activity.MainActivity.PIX;

/**
 * @author : Yannis.Ywx
 * @createTime : 2018/11/5 17:18
 * @email : 923080261@qq.com
 * @description : TODO
 */
public class SendPictureActivity extends BaseMVPActivity<MainContract.Presenter> implements MainContract.View {

    @BindView(R.id.iv_pic)
    public ImageView ivPic;
    @BindView(R.id.iv_wb)
    public ImageView ivWB;
    @BindView(R.id.iv_ble)
    public LEDView ivBle;
    @BindView(R.id.sb)
    public SeekBar sb;
    @BindView(R.id.tv_tmp)
    public TextView tvTmp;
    @BindView(R.id.tv_toolbar_center)
    public TextView tvContext;
    @BindView(R.id.tv_right)
    public TextView tvRight;
    @BindView(R.id.item_speed)
    public ItemView itemViewSpeed;
    @BindView(R.id.item_mode)
    public ItemView itemViewMode;
    @BindView(R.id.cb_marquee)
    public CheckBox checkBoxMarquee;
    @BindView(R.id.cb_flash)
    public CheckBox checkBoxFlash;
    @BindView(R.id.btn_send)
    public Button btnSend;
    ImagePicker mImagePicker;
    ArrayList<ImageItem> images = null;

    SoftReference<Bitmap> bitmap;
    SoftReference<Bitmap> bleBitmap;
    private SendContent sendContent;

    @Override
    protected void init() {

    }

    @Override
    protected void initData() {
        pix = PrefUtils.getIntFromPrefs(this, PIX, 12);
        sendContent = new SendContent();
        sendContent.setMode(1);
        sendContent.setSpeed(3);
        itemViewSpeed.setValue(sendContent.getSpeed() + "");
        itemViewMode.setValue(getResources().getStringArray(R.array.modestr)[sendContent.getMode() - 1]);
        tvRight.setText("选择图片");
        tvRight.setVisibility(View.VISIBLE);
        tvContext.setText("发送图片");
        sb.setMax(255);
        mImagePicker = ImagePicker.getInstance();
        mImagePicker.setImageLoader(new GlideImageLoader());
        mImagePicker.setMultiMode(false);

        mImagePicker.setStyle(CropImageView.Style.RECTANGLE);
        Integer width = 280;
        Integer height = 280;
        width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, getResources().getDisplayMetrics());
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics());
        mImagePicker.setFocusWidth(width);
        mImagePicker.setFocusHeight(height);
    }

    @Override
    protected void initEvent() {
        registerClickEvent(itemViewSpeed, itemViewMode, tvRight);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (bitmap == null || !fromUser) {
                    return;
                }
                tvTmp.setText(String.valueOf(progress));
                Bitmap bitmapTmp = bitmap.get();
                bitmapTmp = convertToBMW(bitmapTmp, bitmap.get().getWidth(), bitmap.get().getHeight(), progress);
                ivWB.setImageBitmap(bitmapTmp);
                bleBitmap = new SoftReference<>(convertBitmap(bitmapTmp));
                ivBle.setLEDData(LedDataUtil.getLedData(bleBitmap.get()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.item_mode:
                DialogUtil.showWheelViewDialog(this, DialogUtil.DialogMode.MODE, sendContent.getMode() - 1, getString(R.string.mode), new DialogUtil.OnWheelViewSelectListener() {
                    @Override
                    public void OnWheelViewSelect(Object obj, int index) {
                        itemViewMode.setValue((String) obj);
                        sendContent.setMode(index + 1);
                    }
                });
                break;
            case R.id.item_speed:
                DialogUtil.showWheelViewDialog(this, DialogUtil.DialogMode.SPEED, sendContent.getSpeed() - 1, getString(R.string.speed), new DialogUtil.OnWheelViewSelectListener() {
                    @Override
                    public void OnWheelViewSelect(Object obj, int index) {
                        itemViewSpeed.setValue((String) obj);
                        sendContent.setSpeed(index + 1);
                    }
                });
                break;
            case R.id.tv_right:
                Intent intent = new Intent(this, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_IMAGES, images);
                startActivityForResult(intent, 100);
                break;
            default:
                break;
        }
    }


    @Override
    protected int setLayoutResId() {
        return R.layout.activity_send_picture;
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
                tvTmp.setText("80");
                Bitmap bitmap1 = convertToBMW(bitmap.get(), bitmap.get().getWidth(), bitmap.get().getHeight(), 80);
                ivWB.setImageBitmap(bitmap1);
                bleBitmap = new SoftReference<>(convertBitmap(bitmap1));
                ivBle.setLEDData(LedDataUtil.getLedData(bleBitmap.get()));
            } else {
                Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap convertBitmap(Bitmap bitmap) {
        int bigWidth = bitmap.getWidth();
        int bigHeight = bitmap.getHeight();
        int pix = PrefUtils.getIntFromPrefs(this, PIX, 12);
        Bitmap minBitmap = ThumbnailUtils.extractThumbnail(bitmap, pix, pix);
//        return ThumbnailUtils.extractThumbnail(minBitmap, bigWidth, bigHeight);
        return ThumbnailUtils.extractThumbnail(bitmap, pix, pix);
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

    @Override
    public MainContract.Presenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public void logTv(String msg) {

    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    @Override
    public void startScan() {

    }

    @Override
    public void scanSuccess() {

    }

    @Override
    public void startSend() {

    }

    @Override
    public void sendFinished() {

    }

    @Override
    public void setSendBtnIsEnable(final boolean isEnable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnSend.setEnabled(isEnable);
            }
        });

    }

    @OnClick(R.id.btn_send)
    public void onBtnSendData() {
        if (bleBitmap == null || bleBitmap.get() == null) {
            showMsg("请设置发送图片");
            return;
        }
        sendContent = new SendContent();
        sendContent.setMarquee(checkBoxMarquee.isChecked());
        sendContent.setFlash(checkBoxFlash.isChecked());
        sendContent.setSelect(true);
        sendContent.setMessage("");
        if (presenter != null) {
            presenter.sendData(getSendContentList(sendContent), pix);
        }
    }

    private int pix;

    public List<SendContent> getSendContentList(SendContent sendContent) {
        List<SendContent> sendContentList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            if (i == 0) {
                sendContentList.add(sendContent);
            } else {
                SendContent temp = new SendContent();
                temp.setMarquee(false);
                temp.setMessage("");
                temp.setSelect(false);
                temp.setFlash(false);
                temp.setReverse(false);
                temp.setMode(1);
                temp.setSpeed(1);
                sendContentList.add(temp);
            }
        }
        return sendContentList;
    }
}
