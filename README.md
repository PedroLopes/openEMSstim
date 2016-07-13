# openEMSstim: open-hardware module to adjust the intensity of EMS/TENS stimulators. 

This is the openEMSstim, a **hardware board** based on an Arduino Nano that modulates the amplitude of Electrical Muscle Stimulation (EMS) signals. Here you also find the **software** that communicates with the board and controls it (android, unity, etc). This board is controllable via *Bluetooth* and compatible with any *BLE* device you have (such as your smartphone). It also is controllable via *Serial* (USB) by plugging in a USB cable from your computer to the *Arduino Nano* on the board. 

![How to connect your board](extra/openEMSstim-logo/openEMSstim-logo200px.png)

## Getting started (tutorial)

All the necessary information for getting started is compiled in a step-by-step tutorial, [start here](start-here-tutorials/1.getting_started_step_by_step.md). 

![How to connect your board](extra/images/getting-started/1-the-openemsstim-labels.png)

## Why this project?

The openEMSstim is a mod by [Pedro Lopes](plopes.org) of the awesome [EMS toolkit](https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home). See the License which acknoledges all the work from the original makers. This project is forked to (1) provide a simpler design with a few changes, (2) preserve the original design and credit without pulling all the changes to it and (3) be used in the [UIST Student Innovation Contest 2016](https://uist.acm.org/uist2016/contest) without needing to change the instructions of the original project which is meant for HCI researchers and not for a UIST student audience. 	
## Read before using
* Read the [LICENSE](https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home/License)
* Make sure you follow the safety instructions before using it and you are familiar with all the procedure of how to apply EMS safely.
* If you are not familiar with EMS: [start here (slides,papers,examples)](chi16.plopes.org)
* Please note that this board does not generate any EMS signal, it is a amplifier that can only reduce the power of the signal you input to it. You need a EMS signal generator too, this will be your input. For your safety, use only: an off-the-shelve, unmodded, original, regulation-approved, medically compliant device. 
* Please pair this board only with medically compliant EMS devices (check regulation in your country) and verify that you plug it in the INPUT port marked in the board. The OUTPUT port is connected to the electrode pads. 
* Please use EMS electrodes (wet, pre-gelled and aproved electrodes). 

## Supported platforms & APIs

openEMSstim can be interfaced on virtually anything as long as it has bluetooth (BT LE) or serial (USB). Here's the languages and libraries for which we have created examples and support code:

![How to connect your board](extra/images/other-logos/android.png)
![How to connect your board](extra/images/other-logos/unity.png)
![How to connect your board](extra/images/other-logos/python.png)
![How to connect your board](extra/images/other-logos/node.js.png)
![How to connect your board](extra/images/other-logos/processing.png)


### Copyright for this repo's software, casings & hardware
* Copyright 2016 by Pedro Lopes <plopesresearch@gmail.com> (Software, Cases, Hardware Remix)
* Copyright 2016 by Doga Yuksel <dogayuksel@gmail.com> (Cases)

### Copyright from the original board design (EMS Toolkit)
* Copyright 2016 by Tim Dünte <tim.duente@hci.uni-hannover.de>
* Copyright 2016 by Max Pfeiffer <max.pfeiffer@hci.uni-hannover.de>

### Original License 
Licensed under "The MIT License (MIT) – military use of this product is forbidden – V 0.2" by the makers Max Pfeiffer & Tim Dünte, all original designs fully credited to the makers. 
Some rights reserved. See [LICENSE](https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home/License>)

### Liability

Please refer to the liability waiver (in documentation/liability_waiver.md).

