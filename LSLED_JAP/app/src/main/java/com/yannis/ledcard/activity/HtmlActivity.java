package com.yannis.ledcard.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.yannis.ledcard.R;
import com.yannis.ledcard.base.BaseActivity;


/**
 * Created by theo on 2019-12-11.
 * e-mail: theochen@hotmail.com
 */
public class HtmlActivity extends BaseActivity implements View.OnClickListener {

    static void start(Context context, int type) {
        Intent intent = new Intent(context, HtmlActivity.class);
        intent.putExtra("_TYPE", type);
        context.startActivity(intent);
    }

    private WebView mWebView;
    private TextView tvCenter;

    @Override
    protected void init() {

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initData() {
        int type = getIntent().getIntExtra("_TYPE", 0);
        findViewById(R.id.iv_back).setOnClickListener(this);
        tvCenter = findViewById(R.id.tv_toolbar_center);
        tvCenter.setText(type == 0 ? R.string.user_agreement : R.string.privacy_policy);
        mWebView = findViewById(R.id.webview);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
        //缩放开关，设置此属性，仅支持双击缩放，不支持触摸缩放
        mWebView.getSettings().setSupportZoom(true);
        //设置是否可缩放，会出现缩放工具（若为true则上面的设值也默认为true）
        mWebView.getSettings().setBuiltInZoomControls(true);
        //隐藏缩放工具
        mWebView.getSettings().setDisplayZoomControls(true);
        final String htmlPrivacyPolicy = "file:///android_asset/052610484855.html";
        final String htmlUserAgreement = "file:///android_asset/user_agreement.html";
        mWebView.loadUrl(type == 0 ? htmlUserAgreement : htmlPrivacyPolicy);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_webview;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            this.finish();
        }
    }

}
