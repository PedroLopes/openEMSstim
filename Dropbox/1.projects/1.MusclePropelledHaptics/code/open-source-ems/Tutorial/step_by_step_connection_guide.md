# How should the connection look like
![How to connect your board](/Tutorial/connecting_to_the_board.png)

# getting started: what is what?

So what do we have here:


1. the openEMS control board (the open source device that Max and Tim designed, and I gently modded)
2. a off the shelve muscle stimulator (for instance a TENS / massage device you bought legally in your country/doctor/etc)
3. cables to connect the openEMS to the electrodes (which attacht to your skin and deliver impulses)
4. pre-gelled EMS/TENS electodes (again, off the shelve, medically compliant)
5. cables from your EMS/TENS device to the openEMS
6. A bluetooth 4.0 device for controlling the openEMS, a simple smartphone.


## 1. Testing the EMS signal generator first!

## 2. Testing the bluetooth connection. 

## 3. Connecting the EMS control module to an Android App.

## 4. Setting all levels to low, before any test.

## 5. Attaching electrodes to the muscles.

## 6. Plugging the EMS signal generator (TENS/EMS) to the board/module.

## 7. Test and iteratively calibrate yourself, step by step.

# Debbugging potential issues

Before debugging ANYTHING, TURN THE MACHINE OFF AND TAKE THE ELECTRODES OUT OF YOUR SKIN. Now we can start debugging. 

## I don't feel any EMS impulses…

Okay, take it easy. This can be cause by multiple seeting in the signal path. So let's break it down into sub-questions:

### The EMS device is off.

Make sure the EMS device is on -- i.e., the signal generator that sends the impulses, a TENS machine, or a massage device. Most devices have a LED that shows you that pulses are coming out (they blink for each pulse, make sure to check that).

Important: some EMS devices (mostly the digital ones) shut off if the electrodes are not connected or if they detect a broken signal path (they are ttrying to help you, and proect the patient, so never circuinvent these protections). If this is happening it can be due to: 
1. openEMS board is off (so signal path is broken) 
2. the openEMS is on but the channels are closed and our EMS machine won't alow for operation, the safest is to select another EMS machine that continiously send pulses.  

### The openEMS board is off.

**Turn it on by safely connecting it to a 9V battery. **Please (triple) check the polarity if the battery before connecting. The modded boards have a safety diode to prevent inverse polarity but the awesome original design by Max and Tim does not and might break on inverted polarity.

If the board does not power after the 9V battery has been correctly inserted:
1. **Is the arduino in there?** openEMS uses a arduino nano on top, please inser the arduino correctly onto the vertical (black) pin headers. To inser the arduino correctly, check the pictures above, note that the USB port should face to <>. 
2. Is the 9V connector properly screwed to the board? Double check the power header with a simple screwdriver (and without the battery connected). 

### The openEMS board does not show LEDs when I send bluetooth messages.

### I'm not using the bluetooth (or I don't have a phone). 

### The bluetooth app does not see the openEMS board. 

### The openEMS responds to bluetooth and EMS/TENS is sending signals but I don't feel anything.





