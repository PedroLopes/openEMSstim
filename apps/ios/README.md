# Open EMS Connector 
OpenEmsConnector is a project that helps you connect the openEMSstim-device with your iOS device or Mac.

It is currently implemented with Swift 3, so make sure to have XCode 8 or newer.

# how to compile
1. Check out the repo
2. in the __OpenEmsConnector/DeviceConnection/OpenEmsConstants.swift__ enter the information for the following fields
    * deviceName
    * deviceId
    * dispatchQueueUID


## How to find the device-ID?
On iOS the standard approach to connect to a BTLE device is to search for the service. However, the UUID of the service of the openEMSstim-device is not considered a valid UUID (UUID / CBUUID or NSUUID) by Swift / Cocoa. 
Thus it seems not that easy do discover the service. Therefore I decided to connect to the device using the UUID of the device itself. The disadvantage is, that this approach is not very flexible. You can use an app like __'BLExplr'__ to retrieve the UUID of the device on iOS.
This UUID you should enter into the '_deviceId_' field mentioned above.


## Troubleshooting
OpenEmsHelper has a methods to construct a message to send to the device. As a safer measure it method assures that the EMS intensities (channelNumber, intensity and impulse length) stay within a specific range. However, as always, no warranties are taken over, use and modify at your own risk!


## Author of iOS code
Steffen Norbert Franz Blümm <steffen-norbert-franz.bluemm@stud.uni-bamberg.de>, 2016

## Liability

Please refer to the liability waiver (in documentation/liability_waiver.md).


