/**
 * openEMSstim is a mod by Pedro Lopes of the EMSTookit by Max Pfeiffer & Tim Dünte.
 * code, samples, examples and etc on openEMSstim are at: plopes.org/ems
 *
 * ---- original license below -----
 *
 *  Copyright 2016 by Tim Dünte <tim.duente@hci.uni-hannover.de>
 *  Copyright 2016 by Max Pfeiffer <max.pfeiffer@hci.uni-hannover.de>
 *
 *  Licensed under "The MIT License (MIT) – military use of this product is forbidden – V 0.2".
 *  Some rights reserved. See LICENSE.
 *
 * @license "The MIT License (MIT) – military use of this product is forbidden – V 0.2"
 * <https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home/License>
 */

package openEMSstim.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.UUID;

public class EMSBluetoothLEService extends BluetoothGattCallback implements IEMSBluetoothLEService {

    private Handler mHandler = new Handler();

    private String currentDeviceName;
    private ArrayList<BluetoothDevice> moduleBTList = new ArrayList<BluetoothDevice>();
    private boolean writeDone = true;

    //Observer variables
    private boolean connected;
    private boolean changed;
    private ArrayList<Observer> observers = new ArrayList<Observer>();

    //Bluetooth
    private BluetoothGattCallback mGattCallback;
    private BluetoothDevice mDevice; //currently scan is not being performed.
    private BluetoothAdapter blAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic characteristic;

    private static final String READABLE_UUID_EMS_SERVICE = "EMS-Service-BLE1";
    private static final String TAG = "EMSBluetoothLEService";

    //converts an UUID to a readable String
    public static String uuidToReadableString(UUID uuid) {
        byte[] bytes1 = ByteBuffer.allocate(8)
                .putLong(uuid.getMostSignificantBits()).array();
        byte[] bytes2 = ByteBuffer.allocate(8)
                .putLong(uuid.getLeastSignificantBits()).array();
        return new String(bytes1) + new String(bytes2);
    }

    public EMSBluetoothLEService(BluetoothAdapter blAdapter) {
        this.blAdapter = blAdapter;
        mGattCallback = this;
        changed = false;
        connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void connectTo(String deviceName) {
        this.currentDeviceName = deviceName;
        scanLeDevice(true);
    }

    @Override
    public synchronized void sendMessageToEMSDevice(String message) {

        //waits until last message is written
        long start = System.currentTimeMillis();
        while (!writeDone && start + 500 > System.currentTimeMillis()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (characteristic != null) {
            writeDone = false;
            characteristic.setValue(message);
            Log.e(TAG, "MESSAGE AS CHAR:" + message);
            Log.e(TAG, "CHAR:" +  characteristic.toString());
            Log.e(TAG, "STRING CHAR:" +  characteristic.getStringValue(0));
            mBluetoothGatt.writeCharacteristic(characteristic);
        } else {
            Log.e(TAG, "Missing characteristic on EMS Device.");
        }
    }

    @Override
    public void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            connected = false;
            setChanged();
            notifyObservers();
        }
    }

    // Various callback methods defined by the BLE API.
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i(TAG, "Connected.");
            mBluetoothGatt.discoverServices();
            connected = true;
            setChanged();
            notifyObservers();

        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i(TAG, "Connection lost. Try to reconnect. ");
            connected = false;
            setChanged();
            notifyObservers();
            //scanLeDevice(true);
        }
    }

    @Override
    // New services discovered
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        List<BluetoothGattService> serviceList = gatt.getServices();
        UUID uuid;
        BluetoothGattService bleService;
        for (int i = 0; i < serviceList.size(); i++) {
            bleService = serviceList.get(i);
            uuid = bleService.getUuid();
            String readableUUID = uuidToReadableString(uuid);
            Log.i(TAG, "Service found: " + readableUUID);

            //Check if there is an EMS Service
            if (readableUUID.equals(READABLE_UUID_EMS_SERVICE) && bleService.getCharacteristics().size() > 0) {
                //The first Characteristic is the characteristic we need.
                try {
                    characteristic = bleService.getCharacteristics().get(0);
                    characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

                } catch (Error e) {

                    Log.w(TAG, "Unable to get the EMS characteristic.");

                }


            }
        }
    }

    @Override
    // Result of a characteristic read operation
    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic, int status) {
        //not used
    }

    @Override
    // Result of a characteristic write operation
    public void onCharacteristicWrite(BluetoothGatt gatt,
                                      BluetoothGattCharacteristic characteristic, int status) {
        Log.i(TAG, "Write of  "
                + uuidToReadableString(characteristic.getUuid())
                + " was successful: "
                + (status == BluetoothGatt.GATT_SUCCESS)
                + " value was: " + characteristic.getStringValue(0));
        writeDone = true;
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.e(TAG, "Write of " + uuidToReadableString(characteristic.getUuid())
                    + " was not successful. Gatt status: " + status);
        }
    }

    public void notifyObservers() {
        if (changed) {
            for (Observer observer : observers) {
                observer.update(null, this);
            }
            changed = false;
        }
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    private void setChanged() {
        changed = true;
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Device found: " + device.getName());

                    if (device != null && device.getName() != null) {

                        if (device.getName().equals(currentDeviceName)) {
                            mDevice = device;
                            mBluetoothGatt = device.connectGatt(null, false,
                                    mGattCallback);
                        } else {
                            moduleBTList.add(device);
                        }
                    }
                }
            });
        }
    };

    // Stops scanning after 2 seconds.
    private static final long SCAN_PERIOD = 2000;

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    blAdapter.stopLeScan(mScanCallback);
                }
            }, SCAN_PERIOD);
            blAdapter.startLeScan(mScanCallback);
        } else {
            blAdapter.stopLeScan(mScanCallback);
        }

    }
}