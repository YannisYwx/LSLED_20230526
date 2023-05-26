package com.yannis.ledcard.ble;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class BLEService extends Service {

    public static final String GATT_CONNECTED = "com.yannis.ledcard.ble.GATT_CONNECTED";
    public static final String GATT_CONNECTING = "com.yannis.ledcard.ble.GATT_CONNECTING";
    public static final String GATT_DISCONNECTED = "com.yannis.ledcard.ble.GATT_DISCONNECTED";
    public static final String GATT_READ_FAIL = "com.yannis.ledcard.ble.GATT_READ_FAIL";
    public static final String GATT_READ_SUCCESS = "com.yannis.ledcard.ble.GATT_READ_SUCCESS";
    public static final String GATT_WRITE_FAIL = "com.yannis.ledcard.ble.GATT_WRITE_FAIL";
    public static final String GATT_WRITE_FAIL_133 = "com.yannis.ledcard.ble.GATT_WRITE_FAIL_133";
    public static final String GATT_WRITE_SUCCESS = "com.yannis.ledcard.ble.GATT_WRITE_SUCCESS";
    public static final String GATT_SERVICE_DISCOVERED_FAIL = "com.yannis.ledcard.ble.GATT_SERVICE_DISCOVERED_FAIL";
    public static final String GATT_SERVICE_DISCOVERED_SUCCESS = "com.yannis.ledcard.ble.GATT_SERVICE_DISCOVERED_SUCCESS";
    public static final String GATT_CHARACTERISTIC_CHANGED = "com.yannis.ledcard.ble.GATT_CHARACTERISTIC_CHANGED";
    public static final String GATT_DESCRIPTOR_WRITE = "com.yannis.ledcard.ble.GATT_DESCRIPTOR_WRITE";

    public static final String tag = "led-card";
    public static final String BROAD_MAC = "broad_mac";
    public static final String BROAD_VALUE = "broad_value";
    public static final String BROAD_UUID = "broad_uuid";

    private BluetoothGatt mBluetoothGatt;
    private final LocalBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public BLEService getService() {
            return BLEService.this;
        }
    }

    private final BluetoothGattCallback callback = new BluetoothGattCallback() {
        private String mac = "";
        private String action = "";
        private String uuid = "";

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            // TODO Auto-generated method stub
            super.onConnectionStateChange(gatt, status, newState);
            mac = gatt.getDevice().getAddress();
            Log.d(tag, "onConnectionStateChange mac:" + mac + "  status:" + status + " newState:" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(tag, mac + "gatt 连接成功。。。正前往发现服务");
                gatt.discoverServices();
                action = GATT_CONNECTED;
            } else {
                Log.d(tag, mac + "gatt 连接失败。。。关闭服务");
                action = GATT_DISCONNECTED;
                destroyGatt(gatt);
            }
            sendBroadCastToUI(action, mac, null, null);

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // TODO Auto-generated method stub
            super.onServicesDiscovered(gatt, status);
            Log.d(tag, "++++++++++++++++++++++++onServicesDiscovered");

            mac = gatt.getDevice().getAddress();
            Log.d(tag, "onServicesDiscovered mac" + mac + "  status:" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                action = GATT_SERVICE_DISCOVERED_SUCCESS;
                mBluetoothGatt = gatt;
                Log.d(tag, "onServicesDiscovered 服务连接成功！");
            } else {
                action = GATT_SERVICE_DISCOVERED_FAIL;
                destroyGatt(gatt);
            }
            sendBroadCastToUI(action, mac, null, null);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            super.onCharacteristicRead(gatt, characteristic, status);
            mac = gatt.getDevice().getAddress();
            uuid = characteristic.getUuid() + "";
            byte[] value = null;
            Log.d(tag, "onCharacteristicRead 读特征值  mac:" + mac);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                action = GATT_READ_SUCCESS;
                value = characteristic.getValue();
            } else {
                action = GATT_READ_FAIL;
            }
            sendBroadCastToUI(action, mac, uuid, value);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            // TODO Auto-generated method stub
            super.onCharacteristicWrite(gatt, characteristic, status);
            mac = gatt.getDevice().getAddress();
            uuid = characteristic.getUuid() + "";
            Log.d(tag, "onCharacteristicWrite mac:" + mac + " status:" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                action = GATT_WRITE_SUCCESS;
            } else {
                if (status == 133) {
                    destroyGatt(mBluetoothGatt);
                }
                action = GATT_WRITE_FAIL;
            }
            sendBroadCastToUI(action, mac, uuid, null);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            mac = gatt.getDevice().getAddress();
            uuid = characteristic.getUuid() + "";
            byte[] value = null;
            value = characteristic.getValue();
            action = GATT_CHARACTERISTIC_CHANGED;
            sendBroadCastToUI(action, mac, uuid, value);

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            mac = gatt.getDevice().getAddress();
            uuid = descriptor.getUuid() + "";
            action = GATT_DESCRIPTOR_WRITE;
            sendBroadCastToUI(action, mac, uuid, null);
        }
    };

    /**
     * 连接服务
     *
     * @param device
     * @return
     */
    public boolean connect(BluetoothDevice device) {
        if (mBluetoothGatt != null) {
            refreshDeviceCache(mBluetoothGatt);
            destroyGatt(mBluetoothGatt);
        }
        mBluetoothGatt = device.connectGatt(this, false, callback);
        refreshDeviceCache(mBluetoothGatt);
        return true;
    }


    public boolean refreshDeviceCache(BluetoothGatt gatt) {
        final BluetoothGatt bluetoothGatt = gatt;
        try {
            Method method = bluetoothGatt.getClass().getMethod("refresh");
            if(method!=null){
                return (boolean) method.invoke(bluetoothGatt);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (mBluetoothGatt == null) {
            Log.e("yannis", "gatt = null");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * 获得支持的所有服务
     *
     * @return
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;
        return mBluetoothGatt.getServices();
    }

    /**
     * 摧毁gatt
     *
     * @param gatt
     */
    public void destroyGatt(BluetoothGatt gatt) {
        if (gatt == null)
            return;
        gatt.disconnect();
        gatt.close();
        gatt = null;
        System.gc();
    }

    /**
     * 根据Mac获得对应的特征值
     *
     * @param device
     * @param name
     * @return
     */
    public BluetoothGattCharacteristic getCharacteristicByMacAndName(
            BluetoothDevice device, String name) {

        if (mBluetoothGatt != null) {
            if (mBluetoothGatt.getDevice().getAddress().equals(device.getAddress())) {
                for (BluetoothGattService service : mBluetoothGatt.getServices()) {
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        if (characteristic.getUuid().toString().contains(name)) {
                            return characteristic;
                        }
                    }
                }
            }
        }
        return null;
    }


    public BluetoothGattCharacteristic getCharacteristicByMacAndName(BluetoothDevice device, String serviceUuid, String characteristicsUuid) {

        if (mBluetoothGatt != null) {
            if (mBluetoothGatt.getDevice().getAddress().equals(device.getAddress())) {
                for (BluetoothGattService service : mBluetoothGatt.getServices()) {
                    if (service.getUuid().toString().contains(serviceUuid)) {
                        for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                            Log.e("yannis", "sid = " + service.getUuid().toString() + " uuid = " + characteristic.getUuid().toString());
                            if (characteristic.getUuid().toString().contains(characteristicsUuid)) {
                                Log.e("yannis", "找到---- uid = " + characteristicsUuid + " uuid = " + characteristic.getUuid().toString());
                                return characteristic;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }


    /**
     * 写特征值
     *
     * @param characteristic
     */
    public void writValue(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.writeCharacteristic(characteristic);
        } else
            Log.d(tag, "the gatt is null,you cann't writer the Characteristic");
    }

    /**
     * 读特征值
     *
     * @param characteristic
     */
    public void readValue(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.readCharacteristic(characteristic);
        } else
            Log.d(tag, "the gatt is null,you cann't read the Characteristic");
    }

    /**
     * 发送广播更新UI
     *
     * @param action
     * @param mac
     * @param value
     */
    public void sendBroadCastToUI(String action, String mac, String uuid, byte[] value) {
        Intent broadIntent = new Intent(action);
        broadIntent.putExtra(BROAD_MAC, mac);
        if (value != null) {
            broadIntent.putExtra(BROAD_VALUE, value);
        }
        if (uuid != null) {
            broadIntent.putExtra(BROAD_UUID, uuid);
        }
        sendBroadcast(broadIntent);
    }


    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     *                       If true, enable notification. False otherwise.
     */
    public boolean setCharacteristicNotification(BluetoothDevice device, BluetoothGattCharacteristic characteristic, boolean enable) {
        if (mBluetoothGatt != null) {
            if (!mBluetoothGatt.setCharacteristicNotification(characteristic, enable)) {
                return false;
            }
        }

        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if (clientConfig == null)
            return false;

        if (enable) {
            clientConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            clientConfig.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }

        return mBluetoothGatt.writeDescriptor(clientConfig);
    }

    public boolean isNotificationEnabled(BluetoothGattCharacteristic characteristic) {


        BluetoothGattDescriptor clientConfig = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if (clientConfig == null)
            return false;

        return clientConfig.getValue() == BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    }


    public static IntentFilter getRegisterIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BLEService.GATT_CONNECTED);
        filter.addAction(BLEService.GATT_CONNECTING);
        filter.addAction(BLEService.GATT_DISCONNECTED);
        filter.addAction(BLEService.GATT_READ_FAIL);
        filter.addAction(BLEService.GATT_READ_SUCCESS);
        filter.addAction(BLEService.GATT_WRITE_FAIL);
        filter.addAction(BLEService.GATT_WRITE_SUCCESS);
        filter.addAction(BLEService.GATT_SERVICE_DISCOVERED_FAIL);
        filter.addAction(BLEService.GATT_SERVICE_DISCOVERED_SUCCESS);
        filter.addAction(BLEService.GATT_CHARACTERISTIC_CHANGED);
        filter.addAction(BLEService.GATT_DESCRIPTOR_WRITE);
        return filter;
    }

}
