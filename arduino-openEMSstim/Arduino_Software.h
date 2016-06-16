/**
 * ArduinoSoftware_Arduino_IDE
 *
 *  Copyright 2016 by Tim Dünte <tim.duente@hci.uni-hannover.de>
 *  Copyright 2016 by Max Pfeiffer <max.pfeiffer@hci.uni-hannover.de>
 *
 *  Licensed under "The MIT License (MIT) – military use of this product is forbidden – V 0.2".
 *  Some rights reserved. See LICENSE.
 *
 * @license "The MIT License (MIT) – military use of this product is forbidden – V 0.2"
 * <https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home/License>
 */

// Only modify this file to include
// - function definitions (prototypes)
// - include files
// - extern variable definitions
// In the appropriate section

#ifndef _Arduino_Software_H_
#define _Arduino_Software_H_
#include <Arduino.h>

//add your includes for the project Arduino_Workshop_Software_2_MAC here


//end of add your includes here
#ifdef __cplusplus
extern "C" {
#endif



void doCommand(char c);
void loop();
void setup();

char convertToHexCharsToOneByte(char one, char two);
char convertHexCharToByte(char hexChar);
#ifdef __cplusplus
} // extern "C"
#endif

//add your function definitions for the project Arduino_Workshop_Software_2_MAC here




//Do not add code below this line
#endif /* _Arduino_Workshop_Software_2_MAC_H_ */
