# ping-EMS-pong by Sujeath
## Based on openEMSstim by Pedro Lopes

This submission consists of two components:

1. An openEMS board with minor firmware changes
2. An android app communicating with 1 over Bluetooth

The demo adds a fun twist to traditional ping pong by augmenting players with 
EMS based 'special weapons' that hinder an opponent's movements or gameplay.

#### Firmware changes to board:

The basic openEMS software expects string commands over a serial/bluetooth connection.
String commands can activate one channel for a specified duration and intensity.
While adequate for most purposes, the high latency of bluetooth connections renders this approach
problematic for advanced stimulation needed for arm paralysis or wrist vibration. It makes much 
more sense for the board to be 'smarter', keeping track of it's state and being able to take higher
level instructions such as "Turn on channels alternately for 3 seconds, switching one on for 0.3 seconds 
before turning it off".

*	Phone -> Board ['v' = switch on magnet special effect]
*	Phone -> Board ['d' = switch on shaky hands effect]

#### Android App:

Fairly basic app that sends character commands over bluetooth to the board.
Some key changes from the bluetooth library in Pedro's repository.
Firstly, BLE search happens using device mac address instead of device name. 
This seems to be less error prone.
Secondly, I abandoned the EMSModule layer of code in the provided example since I am only
sending single characters over. 