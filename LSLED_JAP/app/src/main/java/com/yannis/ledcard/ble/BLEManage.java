package com.yannis.ledcard.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;

/**
 * @author yannis-ywx
 * BLE管理类：
 * 功能：判断蓝牙是否打开、打开蓝牙、扫描BLE设备
 */
public class BLEManage {
    public static int SCAN_TIME = 5000;
    private static int REQUEST_CODE = 520;
    private Context context = null;
    private static BluetoothAdapter bleAdapter = null;
    private Handler handler = null;
    private boolean isScanning = false;
    private BLEManageListener listener = null;
    private ArrayList<BluetoothDevice> scanBlueDeviceArray = new ArrayList<BluetoothDevice>();

    public BLEManage(Context context) {
        this.context = context;
        handler = new Handler();
        if (!context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "SORRY!BLE NOT SUPPORTED!",
                    Toast.LENGTH_SHORT).show();
            ((Activity) context).finish();
        }
        this.context = context;
        BluetoothManager manager = (BluetoothManager) this.context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = manager.getAdapter();
        if (bleAdapter == null) {
            Toast.makeText(context, "SORRY!YOU PHONE IS NOT SUPPORTED!",
                    Toast.LENGTH_SHORT).show();
            ((Activity) context).finish();
        }
    }

    /**
     * 开始搜索蓝牙设备
     */
    public void startScanBluetoothDevice() {
        if (scanBlueDeviceArray != null) {
            scanBlueDeviceArray = null;
        }
        scanBlueDeviceArray = new ArrayList<>();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanBluetoothDevice();
            }
        }, SCAN_TIME);
        isScanning = true;
        bleAdapter.startLeScan(leScanCallback);
    }

    public void stopScanBluetoothDevice() {
        if (isScanning) {
            isScanning = false;
            bleAdapter.stopLeScan(leScanCallback);
            listener.BLEManageStopScan();
        }
    }

    private LeScanCallback leScanCallback = new LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] bytes) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!scanBlueDeviceArray.contains(device)) {
                        scanBlueDeviceArray.add(device);
                        listener.BLEDeviceScanListener(device, Math.abs(rssi), null, 0);
                    }
                }
            });
        }
    };


    /**
     * 判断蓝牙是否打开蓝牙设备
     *
     * @return
     */
    public boolean isBluetoothEnable(Activity activity) {
        if (!bleAdapter.isEnabled()) {
            if (!bleAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_CODE);
            }
            return true;
        }
        return false;
    }

    /**
     * 打开蓝牙返回函数
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onRequestResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_CODE
                && resultCode == Activity.RESULT_CANCELED) {
            ((Activity) this.context).finish();
            return;
        }
    }

    public static void setScanTime(int scantime) {
        SCAN_TIME = scantime;
    }

    public boolean getScanningState() {
        return isScanning;
    }

    public void setListener(BLEManageListener listener) {
        this.listener = listener;
    }

    public interface BLEManageListener {
        void BLEDeviceScanListener(BluetoothDevice device, int rssi, byte[] scanRecord, int lampType);

        void BLEManageStarScan();

        void BLEManageStopScan();
    }

    /**
     * 字节转十六进制 为相应的字符串显示
     *
     * @param data
     * @return
     */
    public static String byte2Hex(byte data[]) {
        if (data != null && data.length > 0) {
            StringBuilder sb = new StringBuilder(data.length);
            for (byte tmp : data) {
                sb.append(String.format("%02X ", tmp));
            }
            return sb.toString();
        }
        return "no data";
    }
    BluetoothLeScannerCompat mScanner;

    ScanCallback mScanCallback;

    public void initNewScanner() {
        mScanner = BluetoothLeScannerCompat.getScanner();

    }

}
