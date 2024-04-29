package com.yannis.ledcard.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;

import com.yannis.ledcard.LedBleApplication;
import com.yannis.ledcard.R;
import com.yannis.ledcard.adapter.ImgAdapter;
import com.yannis.ledcard.base.BaseActivity;
import com.yannis.ledcard.bean.LEDBmp;
import com.yannis.ledcard.bean.LedImg;
import com.yannis.ledcard.bean.SendContent;
import com.yannis.ledcard.util.BitmapUtils;
import com.yannis.ledcard.util.DialogUtil;
import com.yannis.ledcard.util.PermisionUtils;
import com.yannis.ledcard.widget.ItemView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * File:com.ls.yannis.activity.LedSettingsActivity.java
 *
 * @version V1.0 <描述当前版本功能>
 * Email:923080261@qq.com
 * @Description: ${todo}
 * Author Yannis
 * Create on: 2017-07-27 14:53
 */
public class LedSettingsActivity extends BaseActivity implements LEDBmpFragment.OnLEDBmpClickListener {
    @BindView(R.id.gv_icons)
    public GridView gridView;
    @BindView(R.id.item_speed)
    public ItemView itemViewSpeed;
    @BindView(R.id.item_mode)
    public ItemView itemViewMode;
    @BindView(R.id.cb_reverse)
    public CheckBox checkBoxReverse;
    @BindView(R.id.cb_marquee)
    public CheckBox checkBoxMarquee;
    @BindView(R.id.cb_flash)
    public CheckBox checkBoxFlash;
    @BindView(R.id.ed_send_content)
    public EditText et_content;
    @BindView(R.id.rl_color)
    public RelativeLayout rl_color;
    //    @BindView(R.id.ci_color)
//    public CircleImageView circleImageView;
    @BindView(R.id.tv_toolbar_left)
    public TextView tvLeft;
    @BindView(R.id.tv_right)
    public TextView tvRight;
    @BindView(R.id.tv_toolbar_center)
    public TextView tvContext;
    @BindView(R.id.bmp)
    public ImageView ivBmp;

    private ImgAdapter adapter;
    private List<LedImg> ledImgList;
    private SendContent sendContent;
    private int index;
    private boolean isEditMode = false;

    private LEDBmpFragment mLEDBmpFragment;

    @Override
    protected void init() {
    }

    @Override
    protected void initData() {
        PermisionUtils.verifyStoragePermissions(this);
        tvRight.setText("删除图片");
        tvRight.setVisibility(View.VISIBLE);
        tvLeft.setText(getString(R.string.return_back));
        index = getIntent().getIntExtra(MainActivity.LED_SEND_CONTENT_INDEX, 0);
//        ledImgList = LedDataUtil.getLedImgList();
//        adapter = new ImgAdapter(this, ledImgList);
//        gridView.setAdapter(adapter);

        sendContent = (SendContent) getIntent().getBundleExtra(EXTRA).getSerializable(MainActivity.LED_SEND_CONTENT);
        checkBoxFlash.setChecked(sendContent.isFlash());
        checkBoxMarquee.setChecked(sendContent.isMarquee());
        checkBoxReverse.setChecked(sendContent.isReverse());
        et_content.setText(TextUtils.isEmpty(sendContent.getMessage()) ? "" : sendContent.getMessage());
        parseLEDBmp(LedBleApplication.instance, TextUtils.isEmpty(sendContent.getMessage()) ? "" : sendContent.getMessage(), et_content);
        itemViewSpeed.setValue(sendContent.getSpeed() + "");
        itemViewMode.setValue(getResources().getStringArray(R.array.modestr)[sendContent.getMode() - 1]);

        mLEDBmpFragment = LEDBmpFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl, mLEDBmpFragment);
        fragmentTransaction.commit();
        mLEDBmpFragment.setEditText(et_content);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.isEditMode = false;
        mLEDBmpFragment.refreshLEDBmpView(isEditMode);
    }

    @Override
    protected void initEvent() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int start = et_content.getSelectionStart();
                et_content.getText().insert(start, ledImgList.get(i).getImgMsg());
            }
        });
        registerClickEvent(itemViewSpeed, itemViewMode, tvLeft, tvRight, rl_color);
        findViewById(R.id.tv_toolbar_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseMessage(et_content.getText().toString());
            }
        });
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_led_settings;
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
            case R.id.tv_toolbar_left:
                Intent intent = new Intent();
                sendContent.setMessage(et_content.getText().toString());
                sendContent.setMarquee(checkBoxMarquee.isChecked());
                sendContent.setFlash(checkBoxFlash.isChecked());
                sendContent.setReverse(checkBoxReverse.isChecked());
                intent.putExtra(MainActivity.LED_SEND_CONTENT, sendContent);
                intent.putExtra(MainActivity.LED_SEND_CONTENT_INDEX, index);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.tv_right:
                Log.d("Edit Touched Down", "111111111");
                if (!isEditMode) {
                    tvRight.setText("完成");
                } else {
                    tvRight.setText("删除图片");
                }
                isEditMode = !isEditMode;
                mLEDBmpFragment.refreshLEDBmpView(isEditMode);
                break;
//            case R.id.rl_color:
//                DialogUtil.getChoiceColorDialog(this, new DialogUtil.OnColorSelectListener() {
//                    @Override
//                    public void OnColorSelect(int pos) {
//                        showToast("" + pos);
//                        setColorIV(pos);
//                    }
//                });
//                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        sendContent.setMessage(et_content.getText().toString());
        sendContent.setMarquee(checkBoxMarquee.isChecked());
        sendContent.setFlash(checkBoxFlash.isChecked());
        sendContent.setReverse(checkBoxReverse.isChecked());
        intent.putExtra(MainActivity.LED_SEND_CONTENT, sendContent);
        intent.putExtra(MainActivity.LED_SEND_CONTENT_INDEX, index);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.iv_back)
    public void onBack() {
        Intent intent = new Intent();
        sendContent.setMessage(et_content.getText().toString());
        sendContent.setMarquee(checkBoxMarquee.isChecked());
        sendContent.setFlash(checkBoxFlash.isChecked());
        sendContent.setReverse(checkBoxReverse.isChecked());
        intent.putExtra(MainActivity.LED_SEND_CONTENT, sendContent);
        intent.putExtra(MainActivity.LED_SEND_CONTENT_INDEX, index);
        setResult(RESULT_OK, intent);
        finish();
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public void setColorIV(int pos) {
//        int res = R.drawable.bg_color_1;
//        switch (pos) {
//            case 0:
//                res = R.drawable.bg_color_1;
//                break;
//            case 1:
//                res = R.drawable.bg_color_2;
//                break;
//            case 2:
//                res = R.drawable.bg_color_3;
//                break;
//            case 3:
//                res = R.drawable.bg_color_4;
//                break;
//            case 4:
//                res = R.drawable.bg_color_5;
//                break;
//            case 5:
//                res = R.drawable.bg_color_6;
//                break;
//            case 6:
//                res = R.drawable.bg_color_7;
//                break;
//            default:
//                break;
//        }
//        circleImageView.setImageDrawable(getDrawable(res));
//    }

    @Override
    public void onLEDBmpDelete(LEDBmp ledBmp) {
        if (ledBmp.getFilePath() != null) {
            BitmapUtils.deleteFile(ledBmp.getFilePath());
            ledBmp.delete();
        }
    }

    @Override
    public void onLEDBmpChoice(LEDBmp ledBmp, Bitmap bitmap) {
        if (ledBmp == null) {
            showToast("点击为空");
        } else {
            Log.e("alvin", ledBmp.toString());
            String msg = "[LED" + ledBmp.getId() + "]";
            putLEDViewToEditText(ledBmp, bitmap, msg);
            Log.e("alvin", "发送内容 = " + et_content.getText().toString());
        }
        ivBmp.setImageBitmap(bitmap);
    }


    List<String> ledBmpList;
    List<String> msgList;


    public static void parseLEDBmp(Context context, String content, TextView textView) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
        Pattern pattern = Pattern.compile("\\[(\\S+?)\\]");//匹配[xx]的字符串
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            String group = matcher.group();
            if (group.startsWith("[LED") && group.endsWith("]")) {
                String idStr = group.replace("[LED", "");
                idStr = idStr.replace("]", "");
                int id = Integer.valueOf(idStr);
                LEDBmp ledBmp = DataSupport.find(LEDBmp.class, id);
                if (ledBmp != null) {
                    String filePath = ledBmp.getFilePath();
                    Drawable drawable;
                    if (filePath != null) {
                        drawable = Drawable.createFromPath(filePath);
                    } else {
                        try {
                            drawable = context.getResources().getDrawable(ledBmp.getResourceID());
                        } catch (Exception e) {
                            e.printStackTrace();
                            drawable = null;
                        }
                    }
                    if (drawable != null) {
                        int size = (int) (33 * context.getResources().getDisplayMetrics().density);
                        drawable.setBounds(0, 0, size, size);
                        ImageSpan imageSpan = new ImageSpan(drawable);
                        spannableStringBuilder.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }
        textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
    }

    public void parseMessage(String msg) {
        ledBmpList = new ArrayList<>();
        msgList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(\\S+?)\\]");//匹配[xx]的字符串
        Matcher matcher = pattern.matcher(msg);
        while (matcher.find()) {
            String group = matcher.group();
            ledBmpList.add(group);
        }
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
    }

    private void putLEDViewToEditText(LEDBmp ledBmp, Bitmap bitmap, String ledName) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(ledName);
        ImageSpan imageSpan = new ImageSpan(this, bitmap);
        spannableStringBuilder.setSpan(imageSpan, 0, ledName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Editable editable = et_content.getText();
        int selectionEnd = et_content.getSelectionEnd();
        if (selectionEnd < editable.length()) {
            editable.insert(selectionEnd, spannableStringBuilder);
        } else {
            editable.append(spannableStringBuilder);
        }
    }


    @Override
    public void onLEDBmpAdd() {
        startActivity(new Intent(this, SavePictureActivity.class));
    }
}
