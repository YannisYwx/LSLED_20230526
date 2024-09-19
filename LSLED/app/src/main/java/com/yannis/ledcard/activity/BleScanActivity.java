package com.yannis.ledcard.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yannis.ledcard.LedBleApplication;
import com.yannis.ledcard.R;
import com.yannis.ledcard.adapter.BleDeviceAdapter;
import com.yannis.ledcard.base.BaseActivity;
import com.yannis.ledcard.ble.BLEScanner;
import com.yannis.ledcard.ble.ScanDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class BleScanActivity extends BaseActivity implements BLEScanner.OnDeviceScanListener {

    private static final String TAG = "BleScanActivity";

    @BindView(R.id.tv_toolbar_center)
    public TextView tvContext;
    @BindView(R.id.tv_right)
    public TextView tvRight;
    @BindView(R.id.lvDevice)
    public ListView lvBleScan;
    @BindView(R.id.btnScan)
    public Button btnScan;
    @BindView(R.id.refreshView)
    public SwipeRefreshLayout refreshView;

    List<ScanDevice> deviceList = new ArrayList<>();
    BleDeviceAdapter adapter;
    private BLEScanner bleScanner;

    private String selectMacAddress = null;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void init() {
        initBle();
    }

    /**
     * 初始化ble搜索
     */
    private void initBle() {
        bleScanner = BLEScanner.getInstance();
        bleScanner.setListener(this);
    }

    @Override
    protected void initData() {
        adapter = new BleDeviceAdapter(deviceList, this);
        lvBleScan.setAdapter(adapter);
        tvContext.setText(R.string.scan_list);
        checkBluetoothAndStoragePermissionAndStartScan();
    }

    @Override
    protected void initEvent() {
        btnScan.setOnClickListener(v -> {
            checkBluetoothAndStoragePermissionAndStartScan();
        });
        lvBleScan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectMacAddress = deviceList.get(position).getAddress();
                LedBleApplication.instance.setDevice(deviceList.get(position).getDevice());
                LedBleApplication.instance.isConnected = false;
                Intent intent = new Intent();
                intent.putExtra(SELECT_ADDRESS, selectMacAddress);
                setResult(987, intent);
                finish();
            }
        });
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(TAG, "=====================onRefresh");
                bleScanner.startScanBluetoothDevice();
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (refreshView.isRefreshing()) {
                            refreshView.setRefreshing(false);
                        }
                    }
                }, 5000);
            }
        });
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_ble_scan;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDeviceScan(List<ScanDevice> scanDevices) {
        for (ScanDevice scanDevice : scanDevices) {
            boolean isContains = false;
            for (ScanDevice device : deviceList) {
                if (device.getAddress().equals(scanDevice.getAddress())) {
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                deviceList.add(scanDevice);
            }
        }
        Collections.sort(deviceList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStartScan() {
        deviceList.clear();
    }

    @Override
    public void onStopScan(List<ScanDevice> scanDevices) {
    }

    @Override
    public void logInfo(String msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("==================>", "---------------onDestroy");
        uiHandler.removeCallbacksAndMessages(null);
        bleScanner.stopScanBluetoothDevice();
        bleScanner.setListener(null);
    }

    public static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 110;
    public static final int PERMISSIONS_REQUEST_BLUETOOTH_SCAN_CONNECT = 111;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isPermissionGranted(String... permissions) {
        boolean isGranted = false;
        for (String permission : permissions) {
            isGranted = checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            if (!isGranted) return false;
        }
        return isGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (requestCode == PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //同意权限
                    startScan();
                } else {
                    // 权限拒绝
                    // 下面的方法最好写一个跳转，可以直接跳转到权限设置页面，方便用户
                    showToast(getString(R.string.permission_locaiton_content));
                }
            } else if (requestCode == PERMISSIONS_REQUEST_BLUETOOTH_SCAN_CONNECT) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //同意权限
                    startScan();
                } else {
                    // 权限拒绝
                    // 下面的方法最好写一个跳转，可以直接跳转到权限设置页面，方便用户
                    showToast(getString(R.string.bluetooth_scan_permission));
                }
            }
        }
    }

    /*
   校验蓝牙权限
  */
    private void checkBluetoothAndStoragePermissionAndStartScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                } else {
                    startScan();
                    Log.e(TAG, "=====================权限都已经获取");
                }
            } else {
                if (!isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                } else {
                    Log.e(TAG, "=====================权限都已经获取");
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!isPermissionGranted(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT},
                        PERMISSIONS_REQUEST_BLUETOOTH_SCAN_CONNECT);
            } else {
                startScan();
                Log.e(TAG, "=====================权限都已经获取");
            }
        } else {
            //系统不高于6.0
            startScan();
        }
    }

    private void startScan() {
        refreshView.setRefreshing(true);
        bleScanner.startScanBluetoothDevice();
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (refreshView.isRefreshing()) {
                    refreshView.setRefreshing(false);
                }
            }
        }, 5000);
    }

    public static final String SELECT_ADDRESS = "_SELECT_ADDRESS_";

    @OnClick(R.id.iv_back)
    public void onBack() {
        bleScanner.stopScanBluetoothDevice();
        bleScanner.setListener(null);
        Intent intent = new Intent();
        intent.putExtra(SELECT_ADDRESS, selectMacAddress);
        setResult(987, intent);
        finish();
    }
}
