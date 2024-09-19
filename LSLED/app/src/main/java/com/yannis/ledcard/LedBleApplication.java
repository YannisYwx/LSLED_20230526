package com.yannis.ledcard;

import static com.yannis.ledcard.activity.MainActivity.TAG;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.hjq.toast.ToastUtils;
import com.yannis.ledcard.bean.LEDBmp;
import com.yannis.ledcard.ble.BLEScanner;
import com.yannis.ledcard.ble.BLEService;
import com.yannis.ledcard.ble.BleDevice;
import com.yannis.ledcard.thread.HBThreadPools;

import org.litepal.LitePalApplication;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.activity.DefaultErrorActivity;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class LedBleApplication extends LitePalApplication {
    public BleDevice bleDevice;

    public static LedBleApplication instance;

    public BLEService bleService;

    public boolean isConnected = false;

    public String address;

    private List<LEDBmp> dbLEDBmpList = new ArrayList<>();

    public static float _TEXT_SIZE = 0F;

    public Handler uiHandler = new Handler(Looper.getMainLooper());

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();
        boundService();
        bleDevice = new BleDevice();
        instance = this;
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                .enabled(true)//这阻止了对崩溃的拦截,false表示阻止。用它来禁用customactivityoncrash框架
                .minTimeBetweenCrashesMs(2000)      //定义应用程序崩溃之间的最短时间，以确定我们不在崩溃循环中。比如：在规定的时间内再次崩溃，框架将不处理，让系统处理！
                .errorActivity(DefaultErrorActivity.class) //程序崩溃后显示的页面
                .apply();
        //如果没有任何配置，程序崩溃显示的是默认的设置
        CustomActivityOnCrash.install(this);
        loadLEDBmpFromDB();
        BLEScanner.getInstance().initBLE(this);
        ToastUtils.init(this);
    }


    public void boundService() {
        Intent serviceIntent = new Intent(this, BLEService.class);
        bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE);
    }

    public void unBoundService() {
        unbindService(conn);
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Log.d("App-ble", "：服务绑定成功");
            try {
                bleService = ((BLEService.LocalBinder) iBinder).getService();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("App-ble", "：服务绑定失败");
            bleService = null;
        }
    };

    public void initBleDevice(BluetoothDevice device) {
        if (bleService != null) {
            isConnected = true;
            bleDevice.setDevice(device);
//            bleDevice.setBleNotification(bleService.getCharacteristicByMacAndName(device,BleDevice.UUID_SERVICE,BleDevice.UUID_CHARACTERISTICS_NOTIFICATION));
            bleDevice.setBleWrite(bleService.getCharacteristicByMacAndName(device, BleDevice.UUID_SERVICE, BleDevice.UUID_CHARACTERISTICS_WRITE));
        } else {
            Log.d("yannis", "服务未开启！或设备为连接！");
        }
    }

    public void setDevice(BluetoothDevice device) {
        bleDevice.setDevice(device);
    }


    public void initDevice() {
        Log.e(TAG, "===================>.................initDevice");
        if (bleDevice.getDevice() != null) {
            if (bleService != null && isConnected) {
                Log.e(TAG, "===================>.................setBleWrite");
                bleDevice.setBleWrite(bleService.getCharacteristicByMacAndName(bleDevice.getDevice(), BleDevice.UUID_SERVICE, BleDevice.UUID_CHARACTERISTICS_WRITE));
            }
        }

    }

    public void write(byte[] data) {
        Log.e("yannis", "is Null = " + (bleService == null) + "-------------- isConnected = " + isConnected);
        if (bleService != null && isConnected) {
            if (bleDevice.getBleWrite() != null) {
                bleDevice.getBleWrite().setValue(data);
                bleService.writValue(bleDevice.getBleWrite());
            } else {
                Log.d("yannis", "write-----------------特征值为空？ " + (bleDevice.getBleWrite() == null));
                bleService.disconnect();
                Log.d("yannis", "write-----------------特征值为空");
            }
        }
    }

    public void startNotification() {
        if (bleService != null && isConnected) {
            if (bleDevice.getBleNotification() != null) {
                bleService.setCharacteristicNotification(bleDevice.getDevice(), bleDevice.getBleNotification(), true);
            } else {
                Log.d("yannis", "startNotification-----------------特征值为空");
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (bleService != null && isConnected) {
            bleService.disconnect();
        }
    }


    public void connectDevice() {
        if (bleService != null && !isConnected) {
            Log.d("yannis", "connectDevice---------------------开始连接设备....");
            bleService.connect(bleDevice.getDevice());
        } else {
            Log.d("yannis", "设备已连接---isConnected：" + isConnected);
        }
    }

    public void disconnectDevice() {
        if (bleService != null && isConnected) {
            Log.d("yannis", "disconnectDevice---------------------准备断开连接....");
            bleService.disconnect();
        } else {
            Log.d("yannis", "设备已断开---isConnected：" + isConnected);
        }
    }

    public void loadLEDBmpFromDB() {
        HBThreadPools.getInstance().execute(() -> {
            dbLEDBmpList.clear();
            dbLEDBmpList.addAll(DataSupport.findAll(LEDBmp.class));
        });
    }

    public LEDBmp getLEDBmpById(int id, int matrix) {
        //1-16   17-32  33-48
        if (id < 48 && id > 0) {
            int realId = id % 16;
            if (realId == 0) {
                realId = 16;
            }
            if (matrix == 11) {
                id = realId;
            }
            if (matrix == 12) {
                id = 16 + realId;
            }
            if (matrix == 16) {
                id = 32 + realId;
            }
        }
        for (LEDBmp ledBmp : dbLEDBmpList) {
            if (ledBmp.getId() == id) {
                return ledBmp;
            }
        }
        return null;
    }

    /**
     * 是否是日本APP
     */
    public boolean isJapanApp(){
        String packageName = getPackageName();
        return packageName.equals("com.yannis.flexsign");
    }
}
