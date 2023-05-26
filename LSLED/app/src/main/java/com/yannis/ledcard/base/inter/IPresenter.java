package com.yannis.ledcard.base.inter;

/**
 * Created by Yannis on 2017/7/27.
 *
 */

public interface IPresenter {

    /**
     * 绑定view
     */
    void onAttach();

    /**
     * 解绑view
     */
    void onDetach();
}
