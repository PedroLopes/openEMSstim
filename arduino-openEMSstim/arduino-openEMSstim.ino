/**
 * arduino-openEMSstim
 * a mod of the original [1] by Pedro Lopes, see 
 * [1] <https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home/License>
 * @license "The MIT License (MIT) – military use of this product is forbidden – V 0.2"
 */ 
 
// Necessary files (AltSoftSerial.h, AD5252.h, Rn4020BTLe.h, EMSSystem.h, EMSChannel.h) and dependencies (Wire.h, Arduino.h)
#include "Arduino.h"
#include "AltSoftSerial.h"
#include "Wire.h"
#include "AD5252.h"
#include "Rn4020BTLe.h"
#include "EMSSystem.h"
#include "EMSChannel.h"
#include "avr/pgmspace.h"

//the string below is how your EMS module will show up for other BLE devices
#define EMS_BLUETOOTH_ID "openEMSstim1"

//setup for verbose mode (prints debug messages if DEBUG_ON is 1)
#define DEBUG_ON 1

//setup for accepting commands also via USB (accepts USB commands if ACCEPT_USB_COMMANDS is 1)
#define ACCEPT_USB_COMMANDS 1

//helper print function that handles the DEBUG_ON flag automatically
void printer(String msg, boolean force = false) {
  if (DEBUG_ON || force) {
    Serial.println(msg);
  }
}

//Initialization of control objects
AltSoftSerial softSerial;
AD5252 digitalPot(0);
Rn4020BTLe bluetoothModule(2, &softSerial);
EMSChannel emsChannel1(5, 4, A2, &digitalPot, 1);
EMSChannel emsChannel2(6, 7, A3, &digitalPot, 3);
EMSSystem emsSystem(2);

void setup() {
	Serial.begin(19200);
	softSerial.setTimeout(100);
	printer("\nSETUP:");
	Serial.flush();

	//Reset and Initialize the Bluetooth module
	printer("\tBT: RESETTING");
	bluetoothModule.reset();
	printer("\tBT: RESET DONE");
	printer("\tBT: INITIALIZING");
	bluetoothModule.init(EMS_BLUETOOTH_ID);
	printer("\tBT: INITIALIZED");

	//Add the EMS channels and start the control
	printer("\tEMS: INITIALIZING CHANNELS");
	emsSystem.addChannelToSystem(&emsChannel1);
	emsSystem.addChannelToSystem(&emsChannel2);
	EMSSystem::start();
	printer("\tEMS: INITIALIZED");
	printer("\tEMS: STARTED");
	printer("SETUP DONE (LED 13 WILL BE ON)");
	pinMode(13, OUTPUT);
	digitalWrite(13, HIGH);
}

String command = "";
String hexCommandString;
const String BTLE_DISCONNECT = "Connection End";

void loop() {

	if (softSerial.available() > 0) {

		String notification = softSerial.readStringUntil('\n');
		notification.trim();
		printer("\tBT: received message: " + String(notification));
		Serial.flush(); //was on DEBUG, I think it should be all the time
		//Converting
		if (notification.charAt(0) == 'W' && notification.charAt(1) == 'V') {
			int lastIndexOfComma = notification.lastIndexOf(',');
			hexCommandString = notification.substring(lastIndexOfComma + 1,
					notification.length() - 1);
			command = "";
			printer("\tEMS_CMD: Notification length: ");
			printer(String(hexCommandString.length()));
			printer(hexCommandString);
			Serial.flush(); //was on DEBUG, I think it should be all the time
			for (unsigned int i = 0; i < hexCommandString.length(); i = i + 2) {
				char nextChar = convertToHexCharsToOneByte(
						hexCommandString.charAt(i),
						hexCommandString.charAt(i + 1));
				command = command + nextChar;
			}
			printer("\tEMS_CMD: Converted command: ");
			printer(command);
			Serial.flush(); //was on DEBUG, I think it should be all the time
			emsSystem.doCommand(&command);
		} else if (notification.equals(BTLE_DISCONNECT)) {
			printer("\tBT: Disconnected");
			Serial.flush(); //was on DEBUG, I think it should be all the time
			emsSystem.shutDown();
		}

	}

	//Checks whether a signal has to be stoped
	if (emsSystem.check() > 0) {

	}

	//Communicate to the EMS-module over USB
	if (ACCEPT_USB_COMMANDS) {
		if (Serial.available() > 0) {
			char c = Serial.read();
			doCommand(c);
		}
	}
}

//Convert-functions for HEX-Strings "4D"->"M"
char convertToHexCharsToOneByte(char one, char two) {
	char byteOne = convertHexCharToByte(one);
	char byteTwo = convertHexCharToByte(two);
	if (byteOne != -1 && byteTwo != -1)
		return byteOne * 16 + byteTwo;
	else {
		return -1;
	}
}

char convertHexCharToByte(char hexChar) {
	if (hexChar >= 'A' && hexChar <= 'F') {
		return hexChar - 'A';
	} else if (hexChar >= '0' && hexChar <= '9') {
		return hexChar - '0';
	} else {
		return -1;
	}
}

const char ems_channel_1_active[] PROGMEM =    "\tEMS: Channel 1 active";
const char ems_channel_1_inactive[]  PROGMEM = "\tEMS: Channel 1 inactive";
const char ems_channel_2_active[] PROGMEM =    "\tEMS: Channel 2 active";
const char ems_channel_2_inactive[] PROGMEM =  "\tEMS: Channel 2 inactive";
const char ems_channel_1_intensity[] PROGMEM = "\tEMS: Intensity Channel 1: ";
const char ems_channel_2_intensity[] PROGMEM = "\tEMS: Intensity Channel 2: ";

const char* const string_table_outputs[] PROGMEM = {ems_channel_1_active, ems_channel_1_inactive, ems_channel_2_active, ems_channel_2_inactive, ems_channel_1_intensity, ems_channel_2_intensity};

char buffer[32];

//For testing purposes only
void doCommand(char c) {
	if (c == '1') {
		if (emsChannel1.isActivated()) {
			emsChannel1.deactivate();
                        strcpy_P(buffer, (char*)pgm_read_word(&(string_table_outputs[1])));
			printer(buffer); //"\tEMS: Channel 1 inactive"
		} else {
			emsChannel1.activate();
                        strcpy_P(buffer, (char*)pgm_read_word(&(string_table_outputs[0])));
			printer(buffer); //"\tEMS: Channel 1 active"
		}
	} else if (c == '2') {
		if (emsChannel2.isActivated()) {
			emsChannel2.deactivate();
                        strcpy_P(buffer, (char*)pgm_read_word(&(string_table_outputs[3])));
			printer(buffer); //"\tEMS: Channel 2 inactive"
		} else {
			emsChannel2.activate();
                        strcpy_P(buffer, (char*)pgm_read_word(&(string_table_outputs[2])));
			printer(buffer);  //"\tEMS: Channel 2 inactive"
		}
	} else if (c == 'q') {
		digitalPot.setPosition(1, digitalPot.getPosition(1) + 1);
                strcpy_P(buffer, (char*)pgm_read_word(&(string_table_outputs[4])));
		printer(buffer + String(digitalPot.getPosition(1))); //"\tEMS: Intensity Channel 1: "
	} else if (c == 'w') {
                strcpy_P(buffer, (char*)pgm_read_word(&(string_table_outputs[4])));
		digitalPot.setPosition(1, digitalPot.getPosition(1) - 1);
		printer(buffer + String(digitalPot.getPosition(1))); //"\tEMS: Intensity Channel 1: "
	} else if (c == 'e') {
		//Note that this is channel 3 on Digipot but EMS channel 2
		digitalPot.setPosition(3, digitalPot.getPosition(3) + 1);
                strcpy_P(buffer, (char*)pgm_read_word(&(string_table_outputs[5])));
		printer(buffer + String(digitalPot.getPosition(3))); //"\tEMS: Intensity Channel 2: "
	} else if (c == 'r') {
		//Note that this is channel 3 on Digipot but EMS channel 2
		digitalPot.setPosition(3, digitalPot.getPosition(3) - 1);
                strcpy_P(buffer, (char*)pgm_read_word(&(string_table_outputs[5])));
		printer(buffer + String(digitalPot.getPosition(3))); //"\tEMS: Intensity Channel 2: "
	}
}
