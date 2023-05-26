package com.yannis.ledcard.ble;

import android.bluetooth.BluetoothDevice;

public class ScanDevice implements Comparable<Object> {
    private String name;
    private String address;
    private int rssi;
    private BluetoothDevice device;

    public ScanDevice(int rssi, BluetoothDevice device) {
        super();
        this.rssi = rssi;
        this.device = device;
        this.name = device.getName();
        this.address = device.getAddress();
    }

    public int getRssi() {
        return rssi;
    }


    public void setRssi(int rssi) {
        this.rssi = rssi;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "ScanDevice{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", rssi=" + rssi +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) {
            return -1;
        }
        if (this == o) {
            return 0;
        }
        if (o instanceof ScanDevice) {
            ScanDevice bluetooth = (ScanDevice) o;
            return bluetooth.rssi > this.rssi ? 1 : -1;
        }
        return 0;
    }

}