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

import openEMSstim.ble.EMSBluetoothLEService;

public interface IEMSModule {

    //EMS functions

    public void setIntensity(int intensity, int channel);

    public void setMAX_INTENSITY(int progress, int channel);

    public void setSignalLength(int time, int channel);

    public void stopCommand(int channel);

    public void startCommand(int channel);

    public void setIntensityOnChannelForTime(int intensity, int channel, long time);

    public void sendMessageToBoard(String msg);

    //EMS Pattern functions

    public void setPattern(int pattern, int channel);

    public int getPattern(int channel);

    //Connection relevant functions

    public void setDeviceName(String deviceName);

    public EMSBluetoothLEService getBluetoothLEConnector();

    public boolean isConnected();

    public void connect();

    public void disconnect();

}
