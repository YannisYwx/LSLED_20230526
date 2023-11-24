package com.yannis.ledcard.presenter;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import com.yannis.ledcard.LedBleApplication;
import com.yannis.ledcard.R;
import com.yannis.ledcard.base.BasePresenter;
import com.yannis.ledcard.bean.SendContent;
import com.yannis.ledcard.ble.BLEManage;
import com.yannis.ledcard.ble.BLEScanner;
import com.yannis.ledcard.ble.BLEService;
import com.yannis.ledcard.ble.BleDevice;
import com.yannis.ledcard.ble.ScanDevice;
import com.yannis.ledcard.contract.MainContract;
import com.yannis.ledcard.mode.MainMode;
import com.yannis.ledcard.thread.HBThreadPools;
import com.yannis.ledcard.util.DataUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import static android.os.Looper.getMainLooper;
import static com.yannis.ledcard.activity.MainActivity.SDF;

/**
 * File:com.ls.yannis.presenter.MainPresenter.java
 *
 * @version V1.0 <描述当前版本功能>
 * Email:923080261@qq.com
 * @Description: ${todo}
 * Author Yannis
 * Create on: 2017-07-27 14:12
 */
public class MainPresenter extends BasePresenter<MainContract.View, MainContract.Mode> implements MainContract.Presenter,
        BLEScanner.OnDeviceScanListener {
    private static final String TAG = "MainPresenter";
    private BLEScanner bleScanner;
    private int count = 0;
    private boolean isSendFinished = true;
    private boolean mReceiverTag = false; //广播接受者标识位

    /**
     * 是否是点击了发送按钮发送
     */
    private boolean isBtnScanAndSendData = false;
    private boolean isScanDeviceSuccess = false;
    /**
     * 需要发送的数据
     */
    private List<SendContent> sendContentList;
    private HashMap<Integer, byte[]> byteArrays = new HashMap<>();
    private int sendPackageSizeCount = 0;
    /**
     * 是否正在发送
     */
    private boolean isSending = false;
    /**
     * 需要发送的点阵
     */
    private int matrix;

    @Override
    public void onAttach() {
        super.onAttach();
        initBle();
        registerBroadcastReceiver();
    }

    @Override
    public void onDetach() {
        unregisterReceiver();
        super.onDetach();
    }

    private boolean isCalculateFinished = false;

    public MainPresenter(MainContract.View view) {
        super(view);
    }

    @Override
    protected MainContract.Mode createMode() {
        return new MainMode();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sendData(List<SendContent> sendContentList, int matrix) {
        isCalculateFinished = false;
        HBThreadPools.getInstance().execute(() -> {
            isCalculateFinished = false;
            Date start = new Date();
            Log.e(TAG, "发送计算:" + "开始计算 = " + SDF.format(new Date()));
            try {
                byteArrays.clear();
                byteArrays.putAll(mode.getSendLedData(sendContentList, matrix));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Date end = new Date();
            long time = end.getTime() - start.getTime();
            view.logTv("计算耗时 = " + time);
            Log.e(TAG, "发送计算:" + "结束计算 = " + SDF.format(new Date()) + "计算耗时 = " + time);
            isCalculateFinished = true;
        });
        view.setSendBtnIsEnable(false);
        bleScanner.isBluetoothEnable(getActivity());
//        bleManage.isBluetoothEnable(getActivity());
        this.sendContentList = sendContentList;
        this.matrix = matrix;
        LedBleApplication.instance.disconnectDevice();
        if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 31) {
            //校验是否已具有模糊定位权限
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                view.showMsg(getString(R.string.open_coarse_location));
                return;
            }
        } else if (Build.VERSION.SDK_INT >= 31) {
            if (getActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                view.showMsg("Please enable bluetooth permissions manually");
                return;
            }
        }

        boolean isSelect = false;
        for (SendContent sendContent : sendContentList) {
            if (sendContent.isSelect()) {
                isSelect = true;
                break;
            }
        }
        if (isSelect) {
            view.startScan();
            view.showMsg(getString(R.string.searching));
            bleScanner.startScanBluetoothDevice();
            isBtnScanAndSendData = true;
        } else {
            view.showMsg(getActivity().getString(R.string.cannot_send_empty));
        }

    }

    @Override
    public void registerBroadcastReceiver() {
        if (!mReceiverTag) {
            mReceiverTag = true;//设置广播标识位为true
            getActivity().registerReceiver(receiver, BLEService.getRegisterIntentFilter());
        }
    }

    @Override
    public void unregisterReceiver() {
        if (mReceiverTag) {
            if (receiver != null) {
                try {
                    mReceiverTag = false;//设置广播标识位为false
                    getActivity().unregisterReceiver(receiver);
                } catch (IllegalArgumentException e) {
                    if (e.getMessage().contains("Receiver not registered")) {
                        // Ignore this exception. This is exactly what is desired
                    } else {
                        // unexpected, re-throw
                        throw e;
                    }
                }
            }
        }
    }

    @Override
    public void startScanDevice() {
        bleScanner.startScanBluetoothDevice();
    }

    @Override
    public void testParseData(List<SendContent> sendContentList, int matrix) {
        mode.getSendLedData(sendContentList, matrix);
    }

    /**
     * 初始化ble搜索
     */
    private void initBle() {
        bleScanner = BLEScanner.getInstance();
        bleScanner.setListener(this);
    }

    @Override
    public void onDeviceScan(List<ScanDevice> scanDevices) {
        Log.e(TAG, "===================================>onDeviceScan size = " + scanDevices.size());
        BluetoothDevice device = scanDevices.get(0).getDevice();
        if (!TextUtils.isEmpty(device.getName())) {
            if (device.getName().contains(BleDevice.DEVICE_NAME)) {
                LedBleApplication.instance.setDevice(device);
                LedBleApplication.instance.isConnected = false;
                isScanDeviceSuccess = true;
                bleScanner.stopScanBluetoothDevice();
                if (isBtnScanAndSendData) {
                    //开始连接设备
                    connectDevice();
                }
            }
        }
    }

    @Override
    public void logInfo(String msg) {
        view.logTv(msg);
    }

    @Override
    public void onStartScan() {
        Log.e(TAG, "===================================>onStartScan");
        view.showMsg(getString(R.string.searching));
    }

    @Override
    public void onStopScan(List<ScanDevice> scanDevices) {
        Log.e(TAG, "===================================>onStopScan size = " + scanDevices.size());
        isScanDeviceSuccess = scanDevices.size() > 0;
        if (!isScanDeviceSuccess) {
            view.setSendBtnIsEnable(true);
            view.showMsg(getString(R.string.no_device_found));
        }
    }

    /**
     * 开始连接设备
     */
    private void connectDevice() {
        isSendFinished = false;
        if (LedBleApplication.instance.isConnected) {
            LedBleApplication.instance.disconnectDevice();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.showMsg(getString(R.string.connecting));
                    LedBleApplication.instance.connectDevice();
                }
            }, 100);
        } else {
            view.showMsg(getString(R.string.connecting));
            LedBleApplication.instance.connectDevice();
        }
    }

    /**
     * 发送数据
     */
    private void sendDataWithResponse() {
        HBThreadPools.getInstance().execute(() -> {
            while (!isCalculateFinished) {
                SystemClock.sleep(30);
            }
            isSending = true;
            view.setSendBtnIsEnable(false);
            startNewTimer();
            count = 0;
            if (byteArrays.size() > 0) {
                sendPackageSizeCount = mode.getSendPackageSize();
                Log.e(TAG, "发送数据---------> No." + count + "---" + Thread.currentThread().getName());
                view.showMsg(getString(R.string.sending_data));
                LedBleApplication.instance.write(byteArrays.get(count));
            } else {
                view.showMsg("发送内容有误，无法识别");
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // 每次发送都是在连接成功后才开始的
    ///////////////////////////////////////////////////////////////////////////
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        String macAddress;
        String action;
        String uuid = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            macAddress = intent.getStringExtra(BLEService.BROAD_MAC);
            switch (action) {
                case BLEService.GATT_CONNECTED:
                case BLEService.GATT_SERVICE_DISCOVERED_FAIL:
                    LedBleApplication.instance.isConnected = false;
                    break;
                case BLEService.GATT_DISCONNECTED:
                    LedBleApplication.instance.isConnected = false;
//                    if (!isSendFinished) {
//                        //如果是发送成功 断开就不做任何事 反之isSendFinished=false 则是在发送中
//                        LedBleApplication.instance.connectDevice();
//                    }
//                    view.showMsg("连接断开");
                    isSendFinished = true;
                    view.setSendBtnIsEnable(true);
                    break;
                case BLEService.GATT_SERVICE_DISCOVERED_SUCCESS:
                    LedBleApplication.instance.isConnected = true;
                    view.showMsg(getString(R.string.connect_success));
                    view.scanSuccess();
                    LedBleApplication.instance.initDevice();
                    sendDataWithResponse();
                    break;
                case BLEService.GATT_WRITE_FAIL:
                    view.showMsg(getString(R.string.send_failed));
                    uuid = intent.getStringExtra(BLEService.BROAD_UUID);
                    stopTimer();
                    view.setSendBtnIsEnable(true);
                    break;
                case BLEService.GATT_WRITE_SUCCESS:
                    uuid = intent.getStringExtra(BLEService.BROAD_UUID);
                    if (count == 0) {
                        view.startSend();
                    }
                    if (count == (sendPackageSizeCount - 1)) {
                        stopTimer();//
                        view.showMsg(getString(R.string.send_data_over));
                        count = 0;
                        sendPackageSizeCount = 0;
                        isSending = false;
                        new Handler(getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.setSendBtnIsEnable(true);
                                isSendFinished = true;
                                LedBleApplication.instance.disconnectDevice();
                                view.sendFinished();
                            }
                        }, 500); //发送结束
                    } else {
                        view.showMsg(getString(R.string.sending_data));
                        if (count == 3) {
                            count++;
                            SystemClock.sleep(300);
                            startNewTimer();
                            Log.e(TAG, "发送数据---------> No." + count + "---" + Thread.currentThread().getName());
                            LedBleApplication.instance.write(byteArrays.get(count));
                        } else {
                            count++;
                            startNewTimer();
                            SystemClock.sleep(25);
                            Log.e(TAG, "发送数据---------> No." + count + "---" + Thread.currentThread().getName());
                            LedBleApplication.instance.write(byteArrays.get(count));
                        }
                    }
                    break;
            }
        }
    };

    public static String getString(@StringRes int id) {
        return LedBleApplication.getContext().getResources().getString(id);
    }

    private Timer timer;

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //超时了
                    view.showMsg(getString(R.string.send_timeout));
                    count = 0;
                    sendPackageSizeCount = 0;
                    view.setSendBtnIsEnable(true);
                }
            }, 3 * 1000);
        }
    }


    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void startNewTimer() {
        stopTimer();
        startTimer();
    }
}
