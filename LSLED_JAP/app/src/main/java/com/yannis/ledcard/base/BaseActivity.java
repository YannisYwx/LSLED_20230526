package com.yannis.ledcard.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.toast.ToastUtils;
import com.yannis.ledcard.R;
import com.yannis.ledcard.util.StatusBarUtils;
import com.yannis.ledcard.util.ToastUtil;

import butterknife.ButterKnife;

/**
 * File:com.ls.yannis.base.BaseActivity.java
 *
 * @version V1.0 <便于管理>
 *          Email:923080261@qq.com
 * @Description: ${activity的基类规定一些功能方法}
 * Author Yannis
 * Create on: 2017-07-27 12:49
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA = "Extra";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(setLayoutResId());
        steepStatusBar();
        StatusBarUtils.statusBarLightMode(this);
        ButterKnife.bind(this);
        init();
        initView(savedInstanceState);
        initEvent();
        initData();
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }


    /**
     * 沉浸状态栏
     */
    private void steepStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
            }

        }
    }


    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 绑定事件
     */
    protected abstract void initEvent();

    /**
     * 初始化视图
     *
     * @param savedInstanceState 保存数据
     */
    protected void initView(@Nullable Bundle savedInstanceState) {

    }

    /**
     * 设置布局文件
     *
     * @return 布局文件id
     */
    protected abstract int setLayoutResId();

    /**
     * 注册点击事件
     *
     * @param args 需要绑定的控件
     */
    protected void registerClickEvent(View... args) {
        for (View view : args) {
            if (view != null) {
                view.setOnClickListener(this);
            }
        }
    }

    /**
     * 弹出一个土司
     *
     * @param msg 显示内容
     */
    public void showToast(final String msg) {
        ToastUtils.setView(R.layout.toast_custom_view);
        ToastUtils.setGravity(Gravity.BOTTOM);
        ToastUtils.show(msg);
    }

    /**
     * 带参数切换activity
     *
     * @param bundle
     * @param cls
     */
    protected void switchTo(Bundle bundle, Class<? extends Activity> cls) {
        final Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null) {
            intent.putExtra(EXTRA, bundle);
        }
        intent.setClass(this, cls);
        startActivity(intent);
    }

    /**
     * 不带参数切换activity
     *
     * @param cls
     */
    protected void switchTo(Class<? extends Activity> cls) {
        switchTo(null, cls);
    }

    /**
     * 不带参数切换activity，并且销毁当前Activity
     *
     * @param cls
     */
    public void switchToAndFinish(Class<? extends Activity> cls) {
        switchTo(cls);
        finish();
    }

}
