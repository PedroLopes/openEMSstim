/**
 *  This is a basic processing Sketch that controls the openEMSstim. Ready to be ran on Android. 
 *  This is part of openEMSstim by Pedro Lopes. Get all code, schematics, etc at: http://plopes.org/ems
 *  
 *  How to use
 *  0. check the name of your openEMSstim device and edit the field openEMSstim_device_1_name = "YOURNAMEHERE"; (line 27)
 *  1. Run one time and check if device is found and if it connects (for troubleshooting check the videos at http://plopes.org/ems)  
 *  2. It will print the UUID of the discovered service, probably will be: 454d532d-5374-6575-6572-756e672d4348
 *  3. You can change: public static UUID EMS_UUID_SERVICE = UUID.fromString("YOURUUID"); (line 28)
 
 *  Dependencies
 *  0. Requires your device/phone/dongle to have bluetooth low energy (BT4.0).
 *  1. ControlP5 by Andreas Schlegel, www.sojamo.de/libraries/controlp5
 *  2. blepdroid by Andreas Schlegel, www.sojamo.de/libraries/controlp5  
 *  3. Compile with Processing with android mode enabled (tested on processing 3.0 & API 17 aka Android 4.2.2) hence requires the Android SDK setup. 
 **/

import blepdroid.*;
import blepdroid.BlepdroidDevice;
import android.os.*;
import android.content.*;
import java.util.UUID;
import java.util.Arrays;
import controlP5.*;

//EMS setup, connection information, service UUID, signal intensities
public static String openEMSstim_device_1_name = "EMS40LJ"; //your device name, check using your phone or an app such as BLEscanner (scan for devices, check name)
public static UUID EMS_UUID_SERVICE = UUID.fromString("454d532d-5374-6575-6572-756e672d4348"); //values for EMS UUID, you need to find this out, the code does it for you but you will need to copy paste it here if you want.
int channel = 0;
int[] stimulation_intensities = {100,100}; //i.e., {value_for_channel1,value_for_channel2} (in percentage 0-100)
int[] stimulation_durations = {1000, 1000}; //i.e., {value_for_channel1,value_for_channel2} (in milliseconds, usually around 200-2000)

//bluetooth device
BlepdroidDevice device1;
boolean allSetUp = false;

//UI
ControlP5 cp5;
public static int pad_x = 50;
public static int pad_y = 100;
public static int channel1 = 0;
public static int channel2 = 1;
CallbackListener cb;

void setup() { 
  fullScreen();
  print(width);
  print(height);
  noStroke();
  cp5 = new ControlP5(this);
 
  // create a new button with name 'buttonA'
  cp5.addButton("channel1")
     .setValue(0)
     .setId(channel1)
     .setPosition(pad_x/2,pad_y/2)
     .setSize((width/2)-pad_x,height/2)
     .setLabelVisible(false)
     ;
  
  cp5.addButton("channel2")
     .setValue(0)
     .setId(channel2) 
     .setPosition(width/2+pad_x/2,pad_y/2)
     .setSize((width/2)-pad_x,height/2)
     .setLabelVisible(false) 
     ;
     
  cp5.addTextlabel("Channel 1")
     .setText("Channel 1")
     .setPosition(pad_x/2+(width/6),pad_y/2+height/2)
     .setColorValue(0xffffffff)
     .setFont(createFont("Georgia",40))
     ;
     
  cp5.addTextlabel("Channel 2")
     .setText("Channel 2")
     .setPosition(width/2+(pad_x/2)+(width/6),pad_y/2+height/2)
     .setColorValue(0xffffffff)
     .setFont(createFont("Georgia",40))
     ;
  
  smooth();
  
  //BT initialization
  println("started processing sketch.");
  Blepdroid.initialize(this);
  delay(1000);
  Blepdroid.getInstance().scanDevices();
}

void draw() {
  background(20);
  //draw the two buttons
  fill(255);
}

// public void channel1(int theValue)
// function channel1 will execute when channel1 button is pressed
public void channel1(int theValue) {
  int channel = 0;
  println("a button event from channel" + str(channel) + ": " + theValue);
  sendMessageToEMS(channel,stimulation_intensities[channel],stimulation_intensities[channel]);
}

// public void channel2(int theValue)
// function channel2 will execute when channel1 button is pressed
public void channel2(int theValue) {
  int channel = 1;
  println("a button event from channel" + str(channel) + ": " + theValue);
  sendMessageToEMS(channel,stimulation_intensities[channel],stimulation_intensities[channel]);
}

// void sendMessageToEMS(int channel, int intensity, int duration_expiration)
// sends a message to EMS, args: channel_number (int), intensity (0-100, int) and time duration in milliseconds (usually 200-2000)
// note that the EMS command will only execute for that duration of time, if you want a continuous command, keep on sending commands before the expiration 
void sendMessageToEMS(int channel, int intensity, int duration_expiration) { 
  //make message
  String msg = new String("C" + str(channel) + "I" + stimulation_intensities[channel] + "T" + stimulation_durations[channel] + "G");
  //send message
  Blepdroid.getInstance().writeCharacteristic(device1, EMS_UUID_SERVICE, msg.getBytes());
  //print message
  println("SEND: " + msg);
  print(new String(msg.getBytes(),0));
}

void onDeviceDiscovered(BlepdroidDevice device)
{
  println("discovered device " + device.name + " address: " + device.address + " rssi: " + device.rssi );
  if (device.name != null && device.name.equals(openEMSstim_device_1_name))
  {
    if (Blepdroid.getInstance().connectDevice(device))
    {
      println(" connected device 1 (a.k.a." + openEMSstim_device_1_name + ")");
      device1 = device;
    } else println(" couldn't connect device (a.k.a." + openEMSstim_device_1_name + ")");
  }
}

void onServicesDiscovered(BlepdroidDevice device, int status)
{
  HashMap<String, ArrayList<String>> servicesAndCharas = Blepdroid.getInstance().findAllServicesCharacteristics(device);
  for( String service : servicesAndCharas.keySet())
  {
    print( service + " has " );
    // this will list the UUIDs of each service, in the future we're going to make
    // this tell you more about each characteristic, e.g. whether it's readable or writable
    println(servicesAndCharas.get(service));
  }
  allSetUp = true;
}

// these are all the BLE callbacks
void onBluetoothRSSI(BlepdroidDevice device, int rssi)
{
  println(" onBluetoothRSSI " + device.address + " " + Integer.toString(rssi));
}

void onBluetoothConnection( BlepdroidDevice device, int state)
{
  Blepdroid.getInstance().discoverServices(device);
}

void onCharacteristicChanged(BlepdroidDevice device, String characteristic, byte[] data)
{
  String dataString = new String(data);
  println(" onCharacteristicChanged " + characteristic + " " + dataString  );
}

void onDescriptorWrite(BlepdroidDevice device, String characteristic, String data)
{
  println(" onDescriptorWrite " + characteristic + " " + data);
}

void onDescriptorRead(BlepdroidDevice device, String characteristic, String data)
{
  println(" onDescriptorRead " + characteristic + " " + data);
}

void onCharacteristicRead(BlepdroidDevice device, String characteristic, byte[] data)
{
  println(" onCharacteristicRead " + characteristic + " " + data);
}

void onCharacteristicWrite(BlepdroidDevice device, String characteristic, byte[] data)
{
  println(" onCharacteristicWrite " + characteristic + " " + data);
}