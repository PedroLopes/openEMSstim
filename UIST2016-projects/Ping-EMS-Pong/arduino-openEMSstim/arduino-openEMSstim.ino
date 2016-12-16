/**
 * Minor modifications to below by Sujeath Pareddy for UIST 2016 Student Innovation Contest
 * arduino-openEMSstim: https://github.com/PedroLopes/openEMSstim
 * a mod of the original [1] by Pedro Lopes, see 
 * [1] <https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home/License>
 * @license "The MIT License (MIT) – military use of this product is forbidden – V 0.2"
 */ 

// Necessary files (AltSoftSerial.h, AD5252.h, Rn4020BTLe.h, EMSSystem.h, EMSChannel.h) and dependencies (Wire.h, Arduino.h)
//#include "Arduino_Software.h"
#include "Arduino.h"
#include "AltSoftSerial.h"
#include "Wire.h"
#include "AD5252.h"
#include "Rn4020BTLe.h"
#include "EMSSystem.h"
#include "EMSChannel.h"
#include "avr/pgmspace.h"

//BT: the string below is how your EMS module will show up for other BLE devices
#define EMS_BLUETOOTH_ID "openEMS_pongfury"

//DEBUG: setup for verbose mode (prints debug messages if DEBUG_ON is 1)
#define DEBUG_ON 1

//USB: allows commands using the full protocol (refer to https://github.com/PedroLopes/openEMSstim) (by default this is active)
#define USB_FULL_COMMANDS_ACTIVE 1 

//USB: allows to send simplified test commands (one char each, refer to https://github.com/PedroLopes/openEMSstim) to the board via USB (by default this is inactive)
#define USB_TEST_COMMANDS_ACTIVE 0

#define SHAKE_TIME 200l
#define MAGNET_TIME 3000l

#define SHAKY_GARBLED_STRING "WV,0018,64."
#define MAGNET_GARBLED_STRING "WV,0018,76."

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

class PongFury{
public:

	bool channel_active = false;
	unsigned long tic = 0l; // time at beginning of command
	unsigned long toc = 0l; // latest time
	unsigned long tho = 0l; // time at last channel switch (shaky hands only)
	
	int status = 0;// 0 is normal, 1 is magnet/viscous, 2 is shaky hands
	int active_channel = 0; // indicates which channel is active (used for shaky hands)
	
	bool message_due = false;
	
	PongFury(){
		tic = millis();
	}
	
	void feed_message(const String message){
		Serial.println(message);
		if(message == MAGNET_GARBLED_STRING){ // Magnet: Switch channel 1 on for 3 seconds
			Serial.println("Got v");
			status = 1;
			message_due = true;
			tic = millis();
		}
		else if(message == SHAKY_GARBLED_STRING){ // Shaky Hands: Need to switch channels every 0.3 seconds for a total duration of 3 seconds 
			Serial.println("Got d");
			status = 2;
			active_channel = 0;
			message_due = true;
			tho = tic = millis();
		}
		else{
			
		}
	}
	
	void update(){ //update state of machine; Makes no functional difference in Magnet state only in Shaky Hands state
		toc = millis();
		
		if(toc - tic > MAGNET_TIME && status != 0){
			tic = millis();
			Serial.println("Cooldown");
			status = 0;		
			message_due = false;	
			return;
		}
		if(status == 2){
			if(toc - tho > SHAKE_TIME){ // check if channels need to be switched
				tho = millis();
				active_channel = (active_channel==0)?1:0;
				message_due = true;
			}
		}
	}
	
	String get_message(){
		message_due = false;
		if(status == 1){
			Serial.println("Sending command");
			return String("C0I100T3000G"); // Make channel 1 high for 3 seconds
		}
		else if(status == 2){
			if(active_channel == 0){
				Serial.println("Sending command");
				return String("C0I100T300G");// Make channel 1 high for 0.3 seconds
			}
			else{
				Serial.println("Sending command");
				return String("C1I100T300G");// Make channel 2 high for 0.3 seconds
			}
		}
		else{
			return String("");
		}
	}
};

PongFury pongfury;

void setup() {
	Serial.begin(19200);
	softSerial.setTimeout(100);
	Serial.setTimeout(50);
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
		String message = softSerial.readStringUntil('\n');
		printer("\tBT: received command: " + String(message));
		message.trim();
		pongfury.feed_message(message);
        softSerial.flush(); //never tested
	}

	//Checks whether a signal has to be stoped
	if (emsSystem.check() > 0) {

	}

	//Communicate to the EMS-module over USB
	if (Serial.available() > 0) {
	   if (USB_FULL_COMMANDS_ACTIVE) {
		 String message = Serial.readStringUntil('\n');
		 printer("\tUSB: received command: " + String(message));
		 message.trim();
		 pongfury.feed_message(message);
	   }
	Serial.flush(); 
	}
	pongfury.update();
	if(pongfury.message_due){
		String msg = pongfury.get_message();
		processMessage(msg);
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


//process a command message (according to protocol, check https://bitbucket.org/MaxPfeiffer/letyourbodymove/)
void processMessage(String message) {
  if (message.charAt(0) == 'W' && message.charAt(1) == 'V') {
    int lastIndexOfComma = message.lastIndexOf(',');
    hexCommandString = message.substring(lastIndexOfComma + 1,
    message.length() - 1);
    command = "";
    printer("\tEMS_CMD: HEX command length: ");
    printer(String(hexCommandString.length()));
    printer(hexCommandString);
    for (unsigned int i = 0; i < hexCommandString.length(); i = i + 2) {
      char nextChar = convertToHexCharsToOneByte(hexCommandString.charAt(i),hexCommandString.charAt(i + 1));
      command = command + nextChar;
    }
    printer("\tEMS_CMD: Converted HEX command: ");
    printer(command);
    emsSystem.doCommand(&command);
  } else if (message.equals(BTLE_DISCONNECT)) {
    printer("\tBT: Disconnected");
    emsSystem.shutDown();
  }
  else {
    printer("\tCommand NON HEX:");
    printer(message);
    emsSystem.doCommand(&message);
    //printer("\tERROR: HEX Command Unknown");
  }
}



//For testing
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
		printer(
				buffer + String(digitalPot.getPosition(1))); //"\tEMS: Intensity Channel 1: "
	} else if (c == 'w') {
    strcpy_P(buffer, (char*)pgm_read_word(&(string_table_outputs[4])));
		digitalPot.setPosition(1, digitalPot.getPosition(1) - 1);
		printer(
				buffer + String(digitalPot.getPosition(1))); //"\tEMS: Intensity Channel 1: "
	} else if (c == 'e') {
		//Note that this is channel 3 on Digipot but EMS channel 2
		digitalPot.setPosition(3, digitalPot.getPosition(3) + 1);
   strcpy_P(buffer, (char*)pgm_read_word(&(string_table_outputs[5])));
		printer(
				buffer + String(digitalPot.getPosition(3))); //"\tEMS: Intensity Channel 2: "
	} else if (c == 'r') {
		//Note that this is channel 3 on Digipot but EMS channel 2
		digitalPot.setPosition(3, digitalPot.getPosition(3) - 1);
   strcpy_P(buffer, (char*)pgm_read_word(&(string_table_outputs[5])));
		printer(
				buffer + String(digitalPot.getPosition(3))); //"\tEMS: Intensity Channel 2: "
	}
}
