# 1. android using processing: openEMSstim-control (see below for processing + USB)

This is a simple processing app (mainly made to be invoked inside Android, the APK is there too). 
You can use it as the basis for your projects because you can clone it (copy paste it with a different name) and edit to create a new app that does much more exciting and interesting things. 

## I dont want to build the code... where do I find the pre-build/apk/binary apps?
No worries, the pre-build/binary apps are here too. Navigate into each app folder and you will find a file called "appname".apk, this is a android binary. You can simply copy it to your phone (even email it to yourself) and tap on it to install it (will invoke the android installer). Note you have to disable "install from trusted sources only" on your preferences (also know as "Developer Options" on the android preferences). 

## How to mod this app to do what I want?

1. Get Processing (at least 3.0)  
2. Download the Libraries: controlP5 and (we also included them in /processing-libraries-for-dependencies)
3. Copy those libraries to your processing Library folder
4. Open processing, and switch it to ANDROID MODE
5. Open the project (for example: "OnOff-basics.pde") and execute it (will launch and install in a phone if connected or an emulator)

# 2. Processing + USB (for example in your laptop)

Check out the **USB_Pong** example, this provides a simple game that connects to your stimulator over USB. Check the USB_Pong.pde file for instructions on how to play and connect.


### License and Liability

Please refer to the liability waiver (in documentation/liability_waiver.md).

Please refer to the license (in /license.md)


