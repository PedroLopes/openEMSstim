import blepdroid.*;
import blepdroid.BlepdroidDevice;
import com.lannbox.rfduinotest.*;
//import android.os.Bundle;
import android.os.*;
//import android.content.Context;
import android.content.*;
import java.util.UUID;
import java.util.Arrays;

// here's some RFDuino values
/*public static UUID RFDUINO_UUID_SERVICE = BluetoothHelper.sixteenBitUuid(0x2220);
public static UUID RFDUINO_UUID_RECEIVE = BluetoothHelper.sixteenBitUuid(0x2221); //this was being used. let's see if it works witout it.
public static UUID RFDUINO_UUID_SEND = BluetoothHelper.sixteenBitUuid(0x2222);
public static UUID RFDUINO_UUID_DISCONNECT = BluetoothHelper.sixteenBitUuid(0x2223);
public static UUID RFDUINO_UUID_CLIENT_CONFIGURATION = BluetoothHelper.sixteenBitUuid(0x2902);
*/

//values for EMS UUID, you need to find this out, the code does it for you but you will need to copy paste it here if you want. 
public static UUID EMS_UUID_SERVICE = UUID.fromString("454d532d-5374-6575-6572-756e672d4348");

BlepdroidDevice device1;
BlepdroidDevice device2;

public static String openEMSstim_device_1_name = "EMS40LJ";
public static String openEMSstim_device_2_name = "EMS42LJ";

boolean allSetUp = false;

void setup() {
  size(800, 800); //can this be auto
  smooth();
  println("started.");
  Blepdroid.initialize(this);
  delay(1000);
  Blepdroid.getInstance().scanDevices();
}

void draw() {
  background(20);
  //make a line here
  fill(255);
}

void mousePressed()
{ 
  //down and up
  
  int channel = 0;
  int[] intensities = {100,100};
  int[] signalLengths = {1000,1000};
  
  //make message
  String msg =    "C0I0T0G";
  //String msg = new String("C" + str(channel) + "I" + intensities[channel] + "T" + signalLengths[channel] + "G");
  
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
  
  if (device.name != null && device.name.equals(openEMSstim_device_2_name))
  {
    if (Blepdroid.getInstance().connectDevice(device))
    {
      println(" connected device 2 (a.k.a." + openEMSstim_device_2_name + ")");
      device2 = device;
    } else println(" couldn't connect device 2 (a.k.a." + openEMSstim_device_2_name + ")");
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
  
  // we want to set this for whatever device we just connected to
  //Blepdroid.getInstance().setCharacteristicToListen(device, RFDUINO_UUID_RECEIVE); //wow.. this was set like this?
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