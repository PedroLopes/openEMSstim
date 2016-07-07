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

import java.util.Observer;

public interface IEMSBluetoothLEService {

    public boolean isConnected();

    public void connectTo(String deviceName);

    public void sendMessageToEMSDevice(String message);

    public void disconnect();

    public void addObserver(Observer observer);
}
