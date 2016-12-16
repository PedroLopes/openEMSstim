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

package openEMSstim.ems;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import openEMSstim.ble.EMSBluetoothLEService;

public class EMSModule implements IEMSModule {

    private String deviceName = "";
    private EMSBluetoothLEService bleConnector;
    private BluetoothManager bluetoothManager;
    private int[] MAX_INTENSITY = {100, 100};
    private int[] intensities = {MAX_INTENSITY[0], MAX_INTENSITY[1]};
    private int[] signalLengths = {1000, 1000};
    private int[] currentPattern = {0, 0};
    private int[] patterns = {0, 1, 2, 3, 4};

    private int minResendTime = 50; //ms
    private int[] maxResendTime = {(int) ((double) signalLengths[0] * 0.75), (int) ((double) signalLengths[1] * 0.75)};
    private boolean[] updateDevice = {true, true};

    private static final String TAG = "EMSModule";

    public EMSModule(BluetoothManager btManger, String deviceName) {
        this.deviceName = deviceName;
        this.bluetoothManager = btManger;
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        bleConnector = new EMSBluetoothLEService(mBluetoothAdapter);
    }

    @Override
    public EMSBluetoothLEService getBluetoothLEConnector() {
        return bleConnector;
    }

    @Override
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        if (bleConnector.isConnected()) {
            disconnect();
        }
    }
    @Override
    public boolean isConnected() {
        return bleConnector.isConnected();
    }
    @Override
    public void connect() {

        if (bleConnector.isConnected()) {

            disconnect();
        }
        bleConnector.connectTo(deviceName);

    }
    @Override
    public void disconnect() {
        stopCommand(0);
        stopCommand(1);
        bleConnector.disconnect();
    }

    @Override
    public void sendMessageToBoard(String msg) {
        bleConnector.sendMessageToEMSDevice(msg);
    }


    @Override
    public void setIntensity(int intensity, int channel) {
        this.intensities[channel] = intensity;

        Log.w(TAG, "Intensity changed: Channel: " + channel + " Intensity: " + intensity);
        updateDevice[channel] = true;
    }

    @Override
    public void setMAX_INTENSITY(int intensity, int channel) {

        MAX_INTENSITY[channel] = intensity;
    }

    @Override
    public void setSignalLength(int time, int channel) {
        this.signalLengths[channel] = time;
        maxResendTime[channel] = (int) ((double) signalLengths[channel] * 0.75);
        updateDevice[channel] = true;
    }


    @Override
    public void stopCommand(int channel) {
        stopHandlerC(channel);
        setIntensity(0, channel);
        updateDevice[channel] = false;
        bleConnector.sendMessageToEMSDevice("C" + channel + "I0T0G");
    }

    @Override
    public void startCommand(int channel) {
        Log.w(TAG, "Command started on device: " + deviceName + " Channel: " + channel);
        stopHandlerC(channel);
        updateDevice[channel] = true;
        tickChannel(channel);

    }

    @Override
    public void setIntensityOnChannelForTime(int intensity, int channel, long time) {
    }
    @Override
    public void setPattern(int pattern, int channel) {
        currentPattern[channel] = pattern;
        switch (currentPattern[channel]) {

            case 0:
                intensities[channel] = MAX_INTENSITY[channel];
                break;
            case 1:
                intensities[channel] = 0;
                break;
            case 2:
                intensities[channel] = 0;
                break;
            case 3:
                intensities[channel] = 0;
                break;
            case 4:
                intensities[channel] = 0;
                break;
            default:
                break;
        }
    }
    @Override
    public int getPattern(int channel) {
        return currentPattern[channel];

    }

    /*
    Patterns
    Note by Pedro: will take this out, this app will not modulate patterns
     */
    // Pattern 1:
    //Increases the current from 0 to maximum intensity in 1000 ms then start form 0
    int scheduleNextTickPatternIncreaseT100Than0(int channel) {
        int increasingSteps = 8;
        int increasingSpeed = minResendTime + 70;
        if (intensities[channel] < MAX_INTENSITY[channel]) {
            if (intensities[channel] >= MAX_INTENSITY[channel]) {
                intensities[channel] = 1;
            }

            setIntensity(intensities[channel] + increasingSteps, channel);
        } else {

            setIntensity(1, channel);
        }
        return increasingSpeed;
    }

    // Pattern 2:
    // Toggle between high and low every second
    int scheduleNextTickPatternHighAndLow(int channel) {

        Log.w(TAG, "scheduleNextTickPattern " + channel);

        int increasingSteps = MAX_INTENSITY[channel];
        int increasingSpeed = minResendTime + 1000;
        if (intensities[channel] < MAX_INTENSITY[channel]) {
            setIntensity(increasingSteps, channel);
        } else {

            setIntensity(0, channel);
        }
        return increasingSpeed;

    }

    //Pattern 3
    // Linear increasing and decreasing
    boolean turnFromUmToDown = false;

    int scheduleNextTickPatternSinus(int channel) {
        int increasingSteps = 8;
        int increasingSpeed = minResendTime + 70;
        if (intensities[channel] < MAX_INTENSITY[channel] && !turnFromUmToDown) {
            if (intensities[channel] > MAX_INTENSITY[channel]) {
                setIntensity(MAX_INTENSITY[channel], channel);
            } else {
                setIntensity(intensities[channel] + increasingSteps, channel);
            }
        } else {
            turnFromUmToDown = true;
        }

        if (intensities[channel] - increasingSteps > 0 && turnFromUmToDown) {

            setIntensity(intensities[channel] - increasingSteps, channel);
        } else {

            turnFromUmToDown = false;
        }

        if (intensities[channel] < 0) intensities[channel] = 0;
        return increasingSpeed;
    }

    //Pattern 4
    // Increases the current from 0 to 100 in 3000 ms
    int scheduleNextTickPatternIncrease(int channel) {
        int increasingSteps = 4;
        int increasingSpeed = minResendTime + 70;
        if (intensities[channel] + increasingSteps <= MAX_INTENSITY[channel]) {

            setIntensity(intensities[channel] + increasingSteps, channel);
        } else {

            setIntensity(intensities[channel], channel);
            increasingSpeed = maxResendTime[channel];
        }
        return increasingSpeed;
    }


    private static final int TICK_C0 = 0;
    private static final int TICK_C1 = 1;

    private void tickChannel(int channel) {

        int nextTick = minResendTime;
        switch (currentPattern[channel]) {

            case 0:
                intensities[channel] = MAX_INTENSITY[channel];
                nextTick = maxResendTime[channel];
                break;
            case 1:
                nextTick = scheduleNextTickPatternIncreaseT100Than0(channel);
                break;
            case 2:
                nextTick = scheduleNextTickPatternHighAndLow(channel);
                break;
            case 3:
                nextTick = scheduleNextTickPatternSinus(channel);
                break;
            case 4:
                nextTick = scheduleNextTickPatternIncrease(channel);
                break;
            default:
                break;
        }
        scheduleNextTickC(nextTick, channel);

    }

    private void stopHandlerC(int channel) {
        if (channel == 0)
            tickHandler.removeMessages(TICK_C0);

        if (channel == 1)
            tickHandler.removeMessages(TICK_C1);
    }


    private void scheduleNextTickC(int delay, int channel) {
        if (channel == 0)
            tickHandler.sendMessageDelayed(tickHandler.obtainMessage(TICK_C0), delay);
        if (channel == 1)
            tickHandler.sendMessageDelayed(tickHandler.obtainMessage(TICK_C1), delay);
        Log.w(TAG, "UPDATE: Channel:" + channel + ", intensities[channel] " + intensities[channel] + ", signalLengths[channel]  " + signalLengths[channel] + ", delay " + delay);
        bleConnector.sendMessageToEMSDevice("C" + channel + "I" + intensities[channel] + "T" + signalLengths[channel] + "G");
    }

    private Handler tickHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TICK_C0:
                    tickChannel(0);
                    break;
                case TICK_C1:
                    tickChannel(1);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

}