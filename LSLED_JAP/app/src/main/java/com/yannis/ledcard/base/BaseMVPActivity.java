package com.yannis.ledcard.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.yannis.ledcard.base.inter.IPresenter;

/**
 * File:com.ls.yannis.base.BaseMVPActivity.java
 * @version V1.0 <描述当前版本功能>
 *          Email:923080261@qq.com
 * @Description: ${todo}
 * Author Yannis
 * Create on: 2017-07-27 12:49
 */
public abstract class BaseMVPActivity<P extends IPresenter> extends BaseActivity{
    protected P presenter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindMVP();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(presenter != null){
            presenter.onDetach();
        }
    }

    private void bindMVP(){
        if(presenter ==null){
            presenter = createPresenter();
        }
        presenter.onAttach();
    }

    public abstract P createPresenter();
}
