package com.yannis.ledcard.ble;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.yannis.ledcard.ble.BleDevice.DEVICE_NAME;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

/**
 * 设备搜索类
 */
public class BLEScanner {

    private Application context = null;

    BluetoothManager mBManager;
    BluetoothAdapter mBAdapter;
    BluetoothLeScannerCompat mScanner;
    ScanSettings mScanSettings;
    ScanFilter mScanFilter;
    private OnDeviceScanListener listener = null;

    ScanCallback mScanCallback;
    boolean isScanning = false;

    private Handler handler = null;

    public static int SCAN_TIME = 5000;


    public static final class BLEScannerHolder {
        private static final BLEScanner INSTANCE = new BLEScanner();
    }

    public static BLEScanner getInstance() {
        return BLEScannerHolder.INSTANCE;
    }

    public void checkBluetooth(Activity activity) {
        if (!mBAdapter.isEnabled()) {
            openBluetooth(activity);
        }
    }

    public void initBLE(Application application) {
        mScanner = BluetoothLeScannerCompat.getScanner();
        handler = new Handler(Looper.getMainLooper());
        this.context = application;
        if (!context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "SORRY!BLE NOT SUPPORTED!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mBManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (null != mBManager) {
            mBAdapter = mBManager.getAdapter();
            if (mBAdapter == null) {
                Toast.makeText(context, "SORRY!YOU PHONE IS NOT SUPPORTED!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
//        if (!mBAdapter.isEnabled()) {
////            mBAdapter.enable();
//            openBluetooth(application);
//        }
        mScanSettings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(10)
                .setUseHardwareBatchingIfSupported(false)
                .build();
        mScanFilter = new ScanFilter.Builder()
//                .setDeviceName(DEVICE_NAME)
//                .setServiceUuid(new ParcelUuid(BleDevice.serviceUUID))
                .build();
    }

    private static int REQUEST_CODE = 520;

    /**
     * 判断蓝牙是否打开蓝牙设备
     *
     * @return
     */
    public boolean isBluetoothEnable(Activity activity) {
        if (!mBAdapter.isEnabled()) {
            if (!mBAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_CODE);
            }
            return true;
        }
        return false;
    }

//    /**
//     * 打开蓝牙返回函数
//     *
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     */
//    public void onRequestResult(int requestCode, int resultCode, Intent data) {
//        // User chose not to enable Bluetooth.
//        if (requestCode == REQUEST_CODE
//                && resultCode == Activity.RESULT_CANCELED) {
//            ((Activity) this.context).finish();
//            return;
//        }
//    }

    private ArrayList<BluetoothDevice> scanBlueDeviceArray = new ArrayList<BluetoothDevice>();

    private List<ScanDevice> scanDevices = new ArrayList<>();

    public void setListener(OnDeviceScanListener listener) {
        this.listener = listener;
    }

    public void startScanBluetoothDevice() {
        if (scanBlueDeviceArray == null) {
            scanBlueDeviceArray = new ArrayList<>();
        }
        scanBlueDeviceArray.clear();
        scanDevices.clear();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanBluetoothDevice();
            }
        }, SCAN_TIME);
        isScanning = true;

        if (mScanCallback != null) {
            mScanCallback = null;
        }
        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, @NonNull ScanResult result) {
                super.onScanResult(callbackType, result);

            }

            @Override
            public void onBatchScanResults(@NonNull List<ScanResult> results) {
                super.onBatchScanResults(results);
                for (ScanResult result : results) {
                    String deviceName = result.getDevice().getName();
                    if (!TextUtils.isEmpty(deviceName) && deviceName.contains(DEVICE_NAME)) {
                        scanDevices.add(new ScanDevice(result.getRssi(), result.getDevice()));
                    }
                }
                StringBuilder sb = new StringBuilder();
                Collections.sort(scanDevices);
                for (int i = 0; i < scanDevices.size(); i++) {
                    Log.e("*********>>>", "" + scanDevices.get(i).toString());
                    sb.append(scanDevices.get(i).getName() + scanDevices.get(i).getRssi() + ",");
                }
                if (scanDevices.size() > 0) {
                    handler.post(() -> {
                        if (listener != null) {
                            listener.onDeviceScan(scanDevices);
                        }
                    });
                    if (listener != null) {
                        listener.logInfo(sb.toString());
                    }
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder()/*.setDeviceName("LSLED")*/.build());
        if (listener != null) {
            listener.onStartScan();
        }
        Log.e("*********", "开始搜索............");
        mScanner.startScan(filters, mScanSettings, mScanCallback);
    }

    public void stopScanBluetoothDevice() {
        Log.e("*********", "停止搜索............");
        isScanning = false;
        if (mScanner != null && mScanCallback != null) {
            mScanner.stopScan(mScanCallback);
        }
        if (listener != null) {
            listener.onStopScan(scanDevices);
        }
    }

    public interface OnDeviceScanListener {

        void onDeviceScan(List<ScanDevice> scanDevices);

        void onStartScan();

        void onStopScan(List<ScanDevice> scanDevices);

        void logInfo(String msg);
    }

    public static void openBluetooth(Activity activity) {
        BluetoothManager manager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        if (adapter != null && !adapter.isEnabled()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                bluetoothAdapter.enable();
            } else {
                boolean isGetBTScanPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN)
                        == PERMISSION_GRANTED; //android 12 需要BLUETOOTH_SCAN新权限
                if (isGetBTScanPermission) {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//                    bluetoothAdapter.enable();
                    activity.startActivity(new Intent(ACTION_REQUEST_ENABLE));
                } else {

                    boolean isNeedExplanation = ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.BLUETOOTH_SCAN);

                    if (isNeedExplanation) {
                        Toast.makeText(activity, "BLUETOOTH_SCAN permission is denied", Toast.LENGTH_SHORT).show();
                    } else {
                        List<String> permissionList = new ArrayList<>();
                        permissionList.add(Manifest.permission.BLUETOOTH_SCAN);
                        permissionList.add(Manifest.permission.BLUETOOTH_ADVERTISE);
                        permissionList.add(Manifest.permission.BLUETOOTH_CONNECT);
                        Dexter.withContext(activity)
                                .withPermissions(permissionList)
                                .withListener(new MultiplePermissionsListener() {
                                    @Override
                                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                                        for (PermissionGrantedResponse grantedResponse : multiplePermissionsReport.getGrantedPermissionResponses()) {
//                                            Logger.t(TAG).e("onPermissionsChecked:Granted::::" + grantedResponse.getRequestedPermission().toString());
//                                        }
//                                        for (PermissionDeniedResponse deniedResponse : multiplePermissionsReport.getDeniedPermissionResponses()) {
//                                            Logger.t(TAG).e("onPermissionsChecked:Denied::::" + deniedResponse.getRequestedPermission().toString());
//                                        }
                                        if (multiplePermissionsReport.getGrantedPermissionResponses().size() == 3) {
                                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                            bluetoothAdapter.enable();
                                        } else {
                                        }
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                                        for (PermissionRequest permissionRequest : list) {
//                                            Logger.t("TAG").e("onPermissionRationaleShouldBeShown::::" + permissionRequest.toString());
//                                        }
                                    }
                                }).check();
                    }
                }
            }
        }
    }

}
