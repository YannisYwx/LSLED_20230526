package com.yannis.ledcard.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.yannis.ledcard.R;
import com.yannis.ledcard.base.BaseActivity;
import com.yannis.ledcard.bean.SendContent;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * File:com.ls.yannis.activity.SplashActivity.java
 *
 * @version V1.0 <描述当前版本功能>
 * Email:923080261@qq.com
 * @Description: ${欢迎界面
 * Author Yannis
 * Create on: 2017-07-27 14:27
 */
public class SplashActivity extends BaseActivity {
    @BindView(R.id.startView)
    public ImageView startView;

    @Override
    protected void init() {

    }

    @Override
    protected void initData() {

        int count = DataSupport.count(SendContent.class);
        if (count == 0) {
            List<SendContent> sendContentList = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                SendContent sendContent = new SendContent();
                sendContent.setMarquee(false);
                sendContent.setMessage("");
                sendContent.setSelect(false);
                sendContent.setFlash(false);
                sendContent.setReverse(false);
                sendContent.setMode(1);
                sendContent.setSpeed(4);
                if (i == 0) {
                    sendContent.setMessage("Welcome");
//                    sendContent.setMessage("Chào mừng bạn đến với123");
                    sendContent.setMessage("àừạđếớ-ABCDEFGHIJKLMNOPQRSTUVWXYZ-123567890-abcdefghijklmnopqrstuvwxyz-恭喜发财");
                    sendContent.setSelect(true);
                }
                sendContentList.add(sendContent);
            }
            DataSupport.saveAll(sendContentList);
        }
        toMainActivity();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    public void onClick(View view) {

    }

    public static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 110;

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private boolean isPermissionGranted(String... permissions) {
//        boolean isGranted = false;
//        for (String permission : permissions) {
//            isGranted = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
//            if (!isGranted) return false;
//        }
//        return isGranted;
//    }

    /*
       校验蓝牙权限
      */
//    private void checkBluetoothPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                    showToast("申请权限");
//                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
//                            PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
//                } else {
//                    toMainActivity();
//                }
//            } else {
//                if (!isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                    showToast("申请权限");
//                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                            PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
//                } else {
//                    toMainActivity();
//                }
//            }
//        } else {
//            //系统不高于6.0
//            toMainActivity();
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        doNext(requestCode, grantResults);
//    }

//    private void doNext(int requestCode, int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //同意权限
//                showToast("同意权限");
//            } else {
//                // 权限拒绝
//                // 下面的方法最好写一个跳转，可以直接跳转到权限设置页面，方便用户
//                showToast("权限拒绝");
//            }
//            toMainActivity();
//        }
//    }

    public void toMainActivity() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(2000L);
        startView.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                runOnUiThread(() -> {
                    switchTo(MainActivity.class);
                    finish();
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

}
