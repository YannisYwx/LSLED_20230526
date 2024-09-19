package com.yannis.ledcard.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.view.View;
import android.widget.TextView;

import com.yannis.ledcard.R;
import com.yannis.ledcard.base.BaseActivity;

import org.litepal.util.LogUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author : Yannis.Ywx
 * CreateTime : 2018/1/25 23:29
 * Email : 923080261@qq.com
 * Description :
 */
public class AboutActivity extends BaseActivity {
    @BindView(R.id.tv_appVersionName)
    TextView mTextView;
    @BindView(R.id.tv_enduserAgreement)
    TextView enduserAgmentTv;
    @BindView(R.id.tv_privacyPolicy)
    TextView privacyPolicyTv;
    @BindView(R.id.tv_toolbar_left)
    public TextView tvLeft;
    @BindView(R.id.tv_right)
    public TextView tvRight;
    @BindView(R.id.tv_toolbar_center)
    public TextView tvContext;

    @Override
    protected void init() {

    }

    @Override
    protected void initData() {
        tvContext.setText(R.string.about);
        String versionName = getLocalVersionName(this);
        mTextView.setText("VersionName : " + versionName);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_about;
    }

    @Override
    public void onClick(View v) {

    }

    @OnClick({R.id.iv_back})
    public void onBackClick() {
        finish();
    }

    @OnClick(R.id.tv_enduserAgreement)
    public void showAgreement() {
        HtmlActivity.start(this, 0);
    }

    @OnClick(R.id.tv_privacyPolicy)
    public void showPolicy() {
        HtmlActivity.start(this, 1);
    }


    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
            LogUtil.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localVersion;
    }
}
