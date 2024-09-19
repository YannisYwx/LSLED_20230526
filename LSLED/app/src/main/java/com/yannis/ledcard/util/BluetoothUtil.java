package com.yannis.ledcard.util;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hjq.toast.ToastUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yannis.ledcard.LedBleApplication;

import java.util.ArrayList;
import java.util.List;

public class BluetoothUtil {

    private static final String TAG = BluetoothUtil.class.getSimpleName();

    public interface OnBluetoothScanPermissionRefuseListener {
        void onBluetoothScanPermissionRefuse();
    }

    public static void openBluetooth(Activity activity, OnBluetoothScanPermissionRefuseListener listener) {
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
                        ToastUtils.show("您已多次拒绝了，请手动打开android12 蓝牙搜索权限");
                        if (listener != null) {
                            listener.onBluetoothScanPermissionRefuse();
                        }
                    } else {
                        List<String> permissionList = new ArrayList<>();
                        permissionList.add(Manifest.permission.BLUETOOTH_SCAN);
                        permissionList.add(Manifest.permission.BLUETOOTH_ADVERTISE);
                        permissionList.add(Manifest.permission.BLUETOOTH_CONNECT);
                        Dexter.withContext(LedBleApplication.instance)
                                .withPermissions(permissionList)
                                .withListener(new MultiplePermissionsListener() {
                                    @Override
                                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                        for (PermissionGrantedResponse grantedResponse : multiplePermissionsReport.getGrantedPermissionResponses()) {
                                        }
                                        for (PermissionDeniedResponse deniedResponse : multiplePermissionsReport.getDeniedPermissionResponses()) {
                                        }
                                        if (multiplePermissionsReport.getGrantedPermissionResponses().size() == 3) {
                                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                            bluetoothAdapter.enable();
                                        } else {
                                        }
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                        for (PermissionRequest permissionRequest : list) {
                                        }
                                    }
                                }).check();
                    }
                }
            }
        }
    }

    /**
     * BluetoothAdapter权限是否可用
     *
     * @param context 上下文
     * @return true:可用 false:不可用
     */
    public static boolean isBluetoothAdapterPermissionEnable(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return true;
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
                    == PERMISSION_GRANTED;
        }
    }

    public static boolean isBluetoothPermissionEnable(Context context) {
        boolean isScanPermissionGranted;
        if (Build.VERSION.SDK_INT <= 22) {
            isScanPermissionGranted = true; //android 6.0 以下直接通过
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isScanPermissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
                    == PERMISSION_GRANTED; //android 12 需要BLUETOOTH_SCAN新权限
        } else {
            isScanPermissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PERMISSION_GRANTED;
        }
        return isScanPermissionGranted;
    }

    public static void restartBluetooth(Context context) {
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ToastUtils.show("蓝牙开关操作需要相关权限");
                return;
            }
        }
        adapter.disable();
        LedBleApplication.instance.uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.enable();
            }
        }, 1000);

    }

    // 检查经典蓝牙是否已连接
    public static boolean isClassicBluetoothConnected(String macAddress) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false; // 蓝牙未启用或设备不支持蓝牙
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        if (device == null) {
            return false; // 指定的蓝牙设备不存在
        }

        // 检查指定的蓝牙设备是否已连接
        for (BluetoothDevice connectedDevice : bluetoothAdapter.getBondedDevices()) {
            if (connectedDevice.getAddress().equals(macAddress)) {
                // 检查连接状态
                int connectionState = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
                if (connectionState == BluetoothProfile.STATE_CONNECTED) {
                    return true; // 设备已连接
                }
            }
        }

        return false; // 设备未连接
    }
}
