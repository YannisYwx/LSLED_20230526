package com.yannis.ledcard.activity;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yannis.ledcard.R;
import com.yannis.ledcard.adapter.SendListAdapter;
import com.yannis.ledcard.base.BaseMVPActivity;
import com.yannis.ledcard.bean.LedImg;
import com.yannis.ledcard.bean.SendContent;
import com.yannis.ledcard.contract.MainContract;
import com.yannis.ledcard.presenter.MainPresenter;
import com.yannis.ledcard.util.DialogUtil;
import com.yannis.ledcard.util.LedDataUtil;
import com.yannis.ledcard.util.PermisionUtils;
import com.yannis.ledcard.util.PrefUtils;

import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * File:
 * com.ls.yannis.activity.MainActivity.java
 *
 * @version V1.0 <描述当前版本功能>
 * Email:923080261@qq.com
 * @Description: ${todo}
 * Author Yannis
 * Create on: 2017-07-27 12:47
 */
public class MainActivity extends BaseMVPActivity<MainContract.Presenter> implements MainContract.View {

    public static final String LED_SEND_CONTENT = "_led_send_content";
    public static final String LED_SEND_CONTENT_INDEX = "_led_send_content_index";
    /**
     * 是否第一次进入app
     */
    public static final String IS_FIRST_IN_APP = "_is_first_in_app";
    /**
     * 是否同意隐私政策
     */
    public static final String IS_AGREEMENT_PRIVACY = "_is_agreement_privacy";
    public static final String PIX = "_pix";
    public static final String TAG = "MainActivity";
    private static final String SP_IS_FIRST_ENTER_APP = "SP_IS_FIRST_ENTER_APP";

    private List<LedImg> ledImgList;

    @BindView(R.id.lv_sendContent)
    public ListView listView;
    @BindView(R.id.btn_send)
    public Button btnSend;
    @BindView(R.id.btn_settings)
    public Button btnSettings;
    @BindView(R.id.tv_toolbar_center)
    public TextView tvContext;

    private Context context;

    public AlertDialog dialog;

    private List<SendContent> sendContentList;
    private SendListAdapter adapter;

    private int pix;

    @Override
    public MainContract.Presenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    protected void init() {
        context = this;
        pix = PrefUtils.getIntFromPrefs(this, PIX, 12);
    }

    @Override
    protected void initData() {
        try {
            findViewById(R.id.tv_right).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_back).setVisibility(View.INVISIBLE);
            tvContext.setText(getString(R.string.title_sendList));
            sendContentList = DataSupport.findAll(SendContent.class);
            Log.d(TAG, sendContentList.toString());
            adapter = new SendListAdapter(sendContentList, this);
            listView.setAdapter(adapter);
            boolean isAgreePrivacy = PrefUtils.getBooleanFromPrefs(this, IS_AGREEMENT_PRIVACY, false);
            if (!isAgreePrivacy) {
                showPrivacyDialog();
            } else {
                ifIsNeedShowPixDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 是否需要显示设置点阵弹框
     */
    private void ifIsNeedShowPixDialog() {
        boolean isFirstInApp = PrefUtils.getBooleanFromPrefs(this, IS_FIRST_IN_APP, true);
        if (isFirstInApp) {
            PrefUtils.saveBooleanToPrefs(this, IS_FIRST_IN_APP, false);

            LedDataUtil.configureDefaultPics(this, 11);
            LedDataUtil.configureDefaultPics(this, 12);
            LedDataUtil.configureDefaultPics(this, 16);

//            showSetPixDialog(0);
        }
    }

    private void showPrivacyDialog() {
        dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        //对话框弹出后点击或按返回键不消失;
        dialog.setCancelable(false);

        final Window window = dialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_intimate);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            params.dimAmount = 0.5f;
            window.setAttributes(params);
            TextView textView = window.findViewById(R.id.tv_1);
            TextView tvCancel = window.findViewById(R.id.tv_cancel);
            TextView tvAgree = window.findViewById(R.id.tv_agree);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startFinish();
                }
            });
            tvAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterApp();
                }
            });
            String strRes = getResources().getString(R.string.privacy_dialog_content);
            String strPrivacyPolicy = getResources().getString(R.string.privacy_policy_tag);
            String strUserAgreement = getResources().getString(R.string.user_agreement_tag);
            strRes = String.format(strRes, strPrivacyPolicy, strUserAgreement);
            String str_ = "感谢您选择我们!我们非常重视您的个人信息和隐私保护。" +
                    "为了更好地保障您的个人权益，在您使用我们的产品前，" +
                    "请务必审慎阅读《隐私政策》和《用户协议》内的所有条款，" +
                    "尤其是:1.我们对您的个人信息的收集/保存/使用/对外提供/保护等规则条款，以及您的用户权利等条款; " +
                    "2. 约定我们的限制责任、免责条款; 3.其他以颜色或加粗进行标识的重要条款。" +
                    "您点击\"同意并继续”的行为即表示您已阅读完毕并同意以上协议的全部内容。" +
                    "如您同意以上协议内容，请点击\"同意并继续”，开始使用我们的产品和服务!";
            textView.setText(strRes);

            SpannableStringBuilder ssb = new SpannableStringBuilder();
            ssb.append(strRes);
            final int start = strRes.indexOf(strPrivacyPolicy);
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    HtmlActivity.start(context, 1);
                    PrefUtils.saveBooleanToPrefs(MainActivity.this, IS_AGREEMENT_PRIVACY, false);
                }

                @Override

                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, start, start + strPrivacyPolicy.length(), 0);

            final int end = strRes.lastIndexOf(strUserAgreement);//最后一个出现的位置

            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    HtmlActivity.start(context, 0);
                    PrefUtils.saveBooleanToPrefs(MainActivity.this, IS_AGREEMENT_PRIVACY, false);
                }

                @Override

                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }

            }, end, end + strUserAgreement.length(), 0);

            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(ssb, TextView.BufferType.SPANNABLE);
        }
    }

    private void enterApp() {//同意并继续，进入APP
        dialog.cancel();
        PrefUtils.saveBooleanToPrefs(this, IS_AGREEMENT_PRIVACY, true);
        ifIsNeedShowPixDialog();
    }

    private void startFinish() {//更改状态，finish APP
        PrefUtils.saveBooleanToPrefs(this, IS_AGREEMENT_PRIVACY, false);
        dialog.cancel();
        this.finish();
    }

    List<String> ledBmpList;
    List<String> msgList;

    @Override
    protected void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, LedSettingsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(LED_SEND_CONTENT, sendContentList.get(i));
                intent.putExtra(EXTRA, bundle);
                intent.putExtra(LED_SEND_CONTENT_INDEX, i);
                startActivityForResult(intent, 15);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        presenter.unregisterReceiver();
        super.onPause();
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @OnClick(R.id.btn_send)
    public void onBtnSendData() {
        checkBluetoothAndStoragePermission();
    }

    private void sendData() {
        boolean isSelect = false;
        for (SendContent content : sendContentList) {
            if (content.isSelect()) {
                isSelect = true;
                break;
            }
        }
        if (!isSelect) {
            showMsg(getString(R.string.cannot_send_empty));
            return;
        }
        presenter.registerBroadcastReceiver();
        if (presenter != null) {
            presenter.sendData(sendContentList, pix);
        }
    }

    @OnClick({R.id.tv_right})
    public void toAbout() {
        switchTo(AboutActivity.class);
    }

    @OnClick({R.id.btn_settings})
    public void onBtnSettings() {
        int defaultIndex = 0;
        if (pix == 12) {
            defaultIndex = 1;
        }
        if (pix == 16) {
            defaultIndex = 2;
        }
        showSetPixDialog(defaultIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode != 520) {
            SendContent sendContent = (SendContent) data.getSerializableExtra(LED_SEND_CONTENT);
            int index = data.getIntExtra(LED_SEND_CONTENT_INDEX, 0);
            sendContentList.remove(index);
            sendContentList.add(index, sendContent);
            adapter.notifyDataSetChanged();
            ContentValues contentValues = new ContentValues();
            contentValues.put("message", sendContent.getMessage());
            contentValues.put("speed", sendContent.getSpeed());
            contentValues.put("mode", sendContent.getMode());
            contentValues.put("isFlash", sendContent.isFlash());
            contentValues.put("isReverse", sendContent.isReverse());
            contentValues.put("isMarquee", sendContent.isMarquee());
            contentValues.put("isSelect", sendContent.isSelect());
            DataSupport.update(SendContent.class, contentValues, sendContent.getId());
        }

        if (requestCode == 520 && resultCode == Activity.RESULT_CANCELED) {
            showMsg(getString(R.string.open_ble));
        }
    }

    /**
     * 弹出一个设置点阵大小的dialog
     *
     * @param defaultIndex
     */
    public void showSetPixDialog(int defaultIndex) {
        DialogUtil.showWheelViewDialog(this, DialogUtil.DialogMode.PIX, defaultIndex, getString(R.string.pix), new DialogUtil.OnWheelViewSelectListener() {
            @Override
            public void OnWheelViewSelect(Object obj, int index) {
                int oldPix = pix;
                if (index == 0) {
                    pix = 11;
                }
                if (index == 1) {
                    pix = 12;
                }
                if (index == 2) {
                    pix = 16;
                }
                if (oldPix != pix) {
//                    for (int i = 0; i < 8; i++) {
//                        SendContent sendContent = sendContentList.get(i);
//                        sendContentList.remove(i);
//                        sendContent.setMarquee(false);
//                        sendContent.setMessage("");
//                        sendContent.setSelect(false);
//                        sendContent.setFlash(false);
//                        sendContent.setReverse(false);
//                        sendContent.setMode(1);
//                        sendContent.setSpeed(4);
//                        sendContentList.add(i, sendContent);
//                        ContentValues contentValues = new ContentValues();
//                        contentValues.put("message", sendContent.getMessage());
//                        contentValues.put("speed", sendContent.getSpeed());
//                        contentValues.put("mode", sendContent.getMode());
//                        contentValues.put("isFlash", sendContent.isFlash());
//                        contentValues.put("isReverse", sendContent.isReverse());
//                        contentValues.put("isMarquee", sendContent.isMarquee());
//                        contentValues.put("isSelect", sendContent.isSelect());
//                        DataSupport.update(SendContent.class, contentValues, sendContent.getId());
//                    }
//                    adapter.notifyDataSetChanged();
                    PrefUtils.saveIntToPrefs(MainActivity.this, PIX, pix);
                }
            }
        });
    }

    @Override
    public void logTv(String msg) {
        //runOnUiThread(() -> tvContext.setText(msg));
        Log.e(TAG, "logTv:" + msg);
    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    public static SimpleDateFormat SDF = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS");

    @Override
    public void startScan() {
        Log.e(TAG, "发送计算:" + "开始搜索 = " + SDF.format(new Date()));
    }

    @Override
    public void scanSuccess() {
        Log.e(TAG, "发送计算:" + "搜索成功 = " + SDF.format(new Date()));
    }

    @Override
    public void startSend() {
        Log.e(TAG, "发送计算:" + "开始发送 = " + SDF.format(new Date()));
    }

    @Override
    public void sendFinished() {
        Log.e(TAG, "发送计算:" + "发送完成 = " + SDF.format(new Date()));
    }

    @Override
    public void setSendBtnIsEnable(final boolean isEnable) {
        runOnUiThread(() -> btnSend.setEnabled(isEnable));
    }

    public static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 110;
    public static final int PERMISSIONS_REQUEST_BLUETOOTH_SCAN_CONNECT = 111;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isPermissionGranted(String... permissions) {
        boolean isGranted = false;
        for (String permission : permissions) {
            isGranted = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            if (!isGranted) return false;
        }
        return isGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //同意权限
                    showToast("同意权限");
                    sendData();
                } else {
                    // 权限拒绝
                    // 下面的方法最好写一个跳转，可以直接跳转到权限设置页面，方便用户
                    showToast("权限拒绝");
                }
            } else if (requestCode == PERMISSIONS_REQUEST_BLUETOOTH_SCAN_CONNECT) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //同意权限
                    showToast("同意权限");
                    sendData();
                } else {
                    // 权限拒绝
                    // 下面的方法最好写一个跳转，可以直接跳转到权限设置页面，方便用户
                    showToast("权限拒绝");
                }
            }
        }
    }

    /*
       校验蓝牙权限
      */
    private void checkBluetoothAndStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    showToast("申请权限");
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                } else {
                    Log.e(TAG, "=====================权限都已经获取");
                    sendData();
                }
            } else {
                if (!isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    showToast("申请权限");
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                } else {
                    Log.e(TAG, "=====================权限都已经获取");
                    sendData();
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isPermissionGranted(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)) {
                showToast("申请权限");
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                        PERMISSIONS_REQUEST_BLUETOOTH_SCAN_CONNECT);
            } else {
                Log.e(TAG, "=====================权限都已经获取");
                sendData();
            }
        } else {
            //系统不高于6.0
            sendData();
        }
    }

    private void toCheckStoragePermissions() {
        PermisionUtils.verifyStoragePermissions(this);
    }

    /**
     * 是否获取BLE相关权限
     */
    private boolean isGetBLEPermission() {
        boolean isScanPermissionGranted;
        if (Build.VERSION.SDK_INT <= 22) {
            isScanPermissionGranted = true; //android 6.0 以下直接通过
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isScanPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    == PERMISSION_GRANTED; //android 12 需要BLUETOOTH_SCAN新权限
        } else {
            isScanPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PERMISSION_GRANTED;
        }
        return isScanPermissionGranted;
    }

}
