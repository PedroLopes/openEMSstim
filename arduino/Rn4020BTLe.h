/**
 * ArduinoSoftware_Arduino_IDE
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

/*
 * Rn4020BTLe.h
 *
 *  Created on: 19.03.2015
 *      Author: Tim D�nte
 */

#include "AltSoftSerial.h"
#include "avr/pgmspace.h"

#ifndef RN4020_RN4020BTLE_H_
#define RN4020_RN4020BTLE_H_

#include <Arduino.h>

  const char out_reboot[] PROGMEM =    "\t\tReboot?: ";
  const char out_version[]  PROGMEM = "\t\tVersion: ";
  const char out_name[] PROGMEM =    "\t\tSet Name: ";
  const char out_apple[] PROGMEM =  "Set peripheral mode Apple Bluetooth Accessory Design Guidelines : ";
  const char out_p_mode[] PROGMEM = "\t\tSet peripheral mode: ";
  const char out_enable_p_services[] PROGMEM = "\t\tEnable private services: ";
  const char out_clear_p_services[] PROGMEM = "\t\tClear private services:";
  const char out_new_p_services[] PROGMEM = "\t\tSet new private service: ";
  const char out_new_value_p_services[] PROGMEM = "\t\tSet new private service value: ";
  const char out_baudrate[] PROGMEM = "\t\tSet Baudrate: ";

  const char command_set_service_name[] PROGMEM = "PS,454d532d536572766963652d424c4531";
  const char command_set_handle[] PROGMEM = "PC,454d532d537465756572756e672d4348,18,20";

  const char* const string_table_ble[] PROGMEM = {out_reboot, out_version, out_name, out_apple, out_p_mode, out_enable_p_services, out_clear_p_services, out_new_p_services, out_new_value_p_services, out_baudrate};
  const char* const string_table_ble_commands[] PROGMEM = {command_set_service_name, command_set_handle};


class Rn4020BTLe {
public:
	Rn4020BTLe(uint8_t HW_Wake_Up, AltSoftSerial *serial);
	virtual ~Rn4020BTLe();
	void reset();
	void init(String bluetoothName);

private:
	uint8_t HW_Wake_Up;
	AltSoftSerial *serial;


};

#endif /* RN4020_RN4020BTLE_H_ */
