package com.yannis.ledcard.base;

import android.app.Activity;

import com.yannis.ledcard.base.inter.IMode;
import com.yannis.ledcard.base.inter.IPresenter;
import com.yannis.ledcard.base.inter.IView;

/**
 * File:com.ls.yannis.base.BasePresenter.java
 * @version V1.0 <描述当前版本功能>
 *          Email:923080261@qq.com
 * @Description: ${todo}
 * Author Yannis
 * Create on: 2017-07-27 12:49
 */
public abstract class BasePresenter<V extends IView, M extends IMode> implements IPresenter {
    protected V view;
    protected M mode;

    public BasePresenter(V view) {
        if (view == null) {
            throw new NullPointerException("The view must not be null");
        }
        this.view = view;
        this.mode = createMode();
    }

    protected abstract M createMode();

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {
        this.view = null;
    }

    public V getView() {
        return view;
    }

    public M getMode() {
        return mode;
    }

    public Activity getActivity(){
        if(view instanceof Activity){
            return (Activity) view;
        }
        return null;
    }

}
