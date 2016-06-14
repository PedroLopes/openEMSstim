# What is openEMS? An open source hardware module to control off-the-shelf EMS/TENS devices
This is the openEMS, a **hardware board** based on an Arduino Nano that modulates the amplitude of Electrical Muscle Stimulation (EMS) signals. Here you also find the **software** that communicates with the board and controls it. This board is controllable via *Bluetooth* and compatible with any *BLE* device you have (such as your smartphone). It also is controllable via *Serial* (USB) by plugging in a USB cable from your computer to the *Arduino Nano* on the board. 

The openEMS is a mod from <>. See the License which acknoledges all the work from the original makers. 

# How to connect this board
![How to connect your board](/Tutorial/connecting_to_the_board.png)
See the tutorial for a step by step guide to hardware and software deployment.  

# Read before using
* Read the [LICENSE](https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home/License)
* Make sure you follow the safety instructions before using it and you are familiar with all the procedure of how to apply EMS safely.
* If you are not familiar with EMS: [start here (slides,papers,examples)](chi16.plopes.org)
* Please note that this board does not generate any EMS signal, it is a amplifier that can only reduce the power of the signal you input to it. You need a EMS signal generator too, this will be your input. For your safety, use only: an off-the-shelve, unmodded, original, regulation-approved, medically compliant device. 
* Please pair this board only with medically compliant EMS devices (check regulation in your country) and verify that you plug it in the INPUT port marked in the board. The OUTPUT port is connected to the electrode pads. 
* Please use EMS electrodes (wet, pre-gelled and aproved electrodes). 

# Getting started

All the necessary information for getting started is compiled in a step-by-step tutorial, start here. 

# A guide to this repository
* Toolkit:
  * ArduinoSoftware_Arduino_IDE: open the software using the arduino IDE
  * ArduinoSoftware_Eclipse: open the software using the Eclipse IDE + Arduino Plugin
  * Hardware: the eagle schematics and BOM for making this board
* PrototypingSamples:
  * AndroidApps: applications written for Android that control the board via Bluetooth
* Casings:
  * 3d printed: 3d printable (stl) files for a case for the board
  * laser-cut: laser cuttable (svg) files for a case for the board
* Tutorials: 
  * hardware_guide.md: a description of the hardware design , read this if you want to understand how it works or make your own boards.
  * software_guide.md: a description of the software and mostly focusing on the communication protocol, read this if you want to understand how to talk to the board, for instance if you want to make a new app for controlling it.

### Copyright for this repo's software, casings & hardware
* Copyright 2016 by Pedro Lopes <plopesresearch@gmail.com> (Software, Cases, Hardware Remix)
* Copyright 2016 by Doga Yuksel <dogayuksel@gmail.com> (Cases)

### Copyright from the original design   
* Copyright 2016 by Tim Dünte <tim.duente@hci.uni-hannover.de>
* Copyright 2016 by Max Pfeiffer <max.pfeiffer@hci.uni-hannover.de>

### License
Licensed under "The MIT License (MIT) – military use of this product is forbidden – V 0.2" by the makers Max Pfeiffer & Tim Dünte, all original designs fully credited to the makers. 
Some rights reserved. See [LICENSE](https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home/License>)

### Liability

<include this in all files>

