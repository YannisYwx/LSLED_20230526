package com.yannis.ledcard.contract;

import android.util.Log;

import com.yannis.ledcard.base.inter.IMode;
import com.yannis.ledcard.base.inter.IPresenter;
import com.yannis.ledcard.base.inter.IView;
import com.yannis.ledcard.bean.SendContent;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * File:com.ls.yannis.contract.MainContract.java
 *
 * @version V1.0 <描述当前版本功能>
 * Email:923080261@qq.com
 * @Description: ${在这里规定main模块MVP的一些接口}
 * Author Yannis
 * Create on: 2017-07-27 12:49
 */
public interface MainContract {

    interface View extends IView {
        void logTv(String msg);

        void showMsg(String msg);

        void startScan();

        void scanSuccess();

        void startSend();

        void sendFinished();

        void setSendBtnIsEnable(boolean isEnable);
    }

    interface Mode extends IMode {
        byte[] getSendHeader(List<SendContent> sendContentList, int matrix);

        HashMap<Integer, byte[]> getSendLedData(List<SendContent> sendContentList, int matrix);

        int getSendPackageSize();
    }

    interface Presenter extends IPresenter {
        /**
         * 发送数据
         *
         * @param sendContentList 消息内容
         * @param matrix          点阵
         */
        void sendData(List<SendContent> sendContentList, int matrix);


        void registerBroadcastReceiver();

        void unregisterReceiver();

        void startScanDevice();
    }
}
