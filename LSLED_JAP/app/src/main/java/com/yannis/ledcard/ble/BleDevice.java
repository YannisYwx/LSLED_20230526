package com.yannis.ledcard.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.UUID;

/**
 * $DESC$
 *
 * @author yannis
 * Created on 2016/4/22 16:25
 * Email:923080261@qq.com
 */
public class BleDevice implements Comparable<Object> {
    public static final String DEVICE_NAME = "LS";
    public static final String UUID_SERVICE = "fee0";
    public static final UUID serviceUUID = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb");
    public static final String UUID_CHARACTERISTICS_WRITE = "fee1";
    public static final UUID characteristicsUUID = UUID.fromString("0000fee1-0000-1000-8000-00805f9b34fb");
    public static final String UUID_CHARACTERISTICS_NOTIFICATION = "2af0";


    private BluetoothGattCharacteristic bleWrite;
    private BluetoothGattCharacteristic bleNotification;
    private BluetoothDevice device;

    private int rssi;

    public BleDevice(BluetoothDevice device) {
        this.device = device;
    }

    public BleDevice() {

    }


    public BluetoothGattCharacteristic getBleWrite() {
        return bleWrite;
    }

    public void setBleWrite(BluetoothGattCharacteristic bleWrite) {
        this.bleWrite = bleWrite;
    }

    public BluetoothGattCharacteristic getBleNotification() {
        return bleNotification;
    }

    public void setBleNotification(BluetoothGattCharacteristic bleNotification) {
        this.bleNotification = bleNotification;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
