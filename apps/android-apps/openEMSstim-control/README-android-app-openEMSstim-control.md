# android: openEMSstim-control

![How to connect your board](../extra/images/getting-started/1-the-openemsstim-labels.png)

This is a simple Android app. You can use it as the basis for your projects because you can clone it (copy paste it with a different name) and edit to create a new app that does much more exciting and interesting things. 

## How to mod this app to do what I want?

1. Get AndroidStudio (or setup your own environment). 
2. Check the app/src/main/java/openEMSstim/OpenEMSstim.java file, this is the main one. This file sends all the commands to the EMS board, to do so, it invokes the sender functions from
3. pp/src/main/java/openEMSstim/ems/EMSModule.java, this file connects to the device and sends the simple protocol. You can easily see how to change and send your own values. 

### License and Liability

Please refer to the liability waiver (in documentation/liability_waiver.md).

Please refer to the license (in /license.md)


