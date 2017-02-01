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

/**
 * Minor Revisions to this file by Pedro Lopes <plopesresearch@gmail.com>, all credit remains with the original authors (see above).
 */

/*
 * Rn4020BTLe.cpp
 *
 *  Created on: 19.03.2015
 *      Author: Tim Dünte
 */

#include "Rn4020BTLe.h"

Rn4020BTLe::Rn4020BTLe(uint8_t HW_Wake_Up, AltSoftSerial *serial) {
	this->HW_Wake_Up = HW_Wake_Up;
	pinMode(HW_Wake_Up, OUTPUT);
	digitalWrite(HW_Wake_Up, HIGH);
	this->serial = serial;
}

Rn4020BTLe::~Rn4020BTLe() {
	// TODO Auto-generated destructor stub
}

void Rn4020BTLe::reset() {
	//Nach Stromversorgung in den ersten 5 Sekunden 3 mal von High auf Low wechseln. F�hrt zu vollst�ndigem Reset.
	digitalWrite(HW_Wake_Up, HIGH);
	delay(200);
	digitalWrite(HW_Wake_Up, LOW);
	delay(200);
	digitalWrite(HW_Wake_Up, HIGH);
	delay(200);
	digitalWrite(HW_Wake_Up, LOW);
	delay(200);
	digitalWrite(HW_Wake_Up, HIGH);
	delay(200);
	digitalWrite(HW_Wake_Up, LOW);
	delay(200);
	digitalWrite(HW_Wake_Up, HIGH);
	delay(3000);
}

void Rn4020BTLe::init(String bluetoothName) {
  char buffer[68];

	String notification = "";
	serial->begin(115200);
	delay(800);
	serial->println("SB,0");
	delay(200);
  strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble[9])));
	Serial.print(buffer); //"\t\tSet Baudrate: "
	if (serial->available() > 0) {
		notification = serial->readStringUntil('\n');
		Serial.print(notification);
	}
	Serial.println();
	serial->println("R,1"); //Reboot
	delay(200);
  strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble[0])));
	Serial.print(buffer); //"\t\tReboot?:
	if (serial->available() > 0) {
		notification = serial->readStringUntil('\n');
		Serial.print(notification);
	}
	Serial.println();
	delay(2000);
	if ( notification.length()>0){
        	serial->begin(2400);
	}else{
		serial->begin(19200);
	}


	//Device Name = bluetoothName

	bluetoothName = "SN,"+ bluetoothName;
	//Send a dummy command. Might fail. Next command should work properly
	serial->println("V");
	delay(200);
  strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble[1])));
	Serial.print(buffer); //"\t\tVersion: "
	if (serial->available() > 0) {
		notification = serial->readStringUntil('\n');
		Serial.print(notification);
	}
	Serial.println();
	serial->println(bluetoothName);
		delay(200);
    strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble[2])));
		Serial.print(buffer); //"\t\tSet Name: "
		if (serial->available() > 0) {
			notification = serial->readStringUntil('\n');
			Serial.print(notification);
		}
		Serial.println();

	// Set the RN4020 to Apple Bluetooth Accessory Design Guidelines mode RS 0x00004000

		/*
		serial->println("SR,00004000");
		delay(200);
    strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble[3])));
		Serial.print(buffer); //"Set peripheral mode Apple Bluetooth Accessory Design Guidelines : "
		if (serial->available() > 0) {
			notification = serial->readStringUntil('\n');
			Serial.print(notification);
		}
		Serial.println();
*/

	//Sets RN4020 in peripheral Mode with Auto Advertising
	serial->println("SR,20000000");
	delay(200);
  strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble[4])));
	Serial.print(buffer); //"\t\tSet peripheral mode: "
	if (serial->available() > 0) {
		notification = serial->readStringUntil('\n');
		Serial.print(notification);
	}
	Serial.println();


	//Enables private services.
	serial->println("SS,C0000001");
	delay(200);
  strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble[5])));
	Serial.print(buffer); //"\t\tEnable private services: "
	if (serial->available() > 0) {
		notification = serial->readStringUntil('\n');
		Serial.print(notification);
	}
	Serial.println();

	serial->println("PZ"); // Clear the current private service and characteristics
	delay(200);
  strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble[6])));
	Serial.print(buffer); //"\t\tClear private services:"
	if (serial->available() > 0) {
		notification = serial->readStringUntil('\n');
		Serial.print(notification);
	}
	Serial.println();


  strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble_commands[0])));
	serial->println(buffer); //  "PS,454d532d536572766963652d424c4531" in ASCII "EMS-Service-BLE1"
	delay(200);
  strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble[7])));
	Serial.print(buffer); //"\t\tSet new private service: "
	if (serial->available() > 0) {
		notification = serial->readStringUntil('\n');
		Serial.print(notification);
	}
	Serial.println();
  strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble_commands[1])));
	serial->println(buffer); // "PC,454d532d537465756572756e672d4348,18,20" in ASCII "EMS-SteuerungCH1" gets Handle 001C (proof with LS)
	delay(200);
  strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble[8])));
	Serial.print(buffer); // "\t\tSet new private service value: "
	if (serial->available() > 0) {
		notification = serial->readStringUntil('\n');
		Serial.print(notification);
	}
	Serial.println();
	serial->println("SB,2");
	delay(200);
  strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble[9])));
	Serial.print(buffer); //"\t\tSet Baudrate: "
	if (serial->available() > 0) {
		notification = serial->readStringUntil('\n');
		Serial.print(notification);
	}
	Serial.println();

	serial->println("R,1");
	delay(200);
  strcpy_P(buffer, (char*)pgm_read_word(&(string_table_ble[0])));
	Serial.print(buffer); //"\t\tReboot?: "
	if (serial->available() > 0) {
		notification = serial->readStringUntil('\n');
		Serial.print(notification);
	}
	Serial.println();
	serial->begin(19200);
	delay(2000);
}
