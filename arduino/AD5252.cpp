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

/*
 * AD5252.cpp
 *
 *  Created on: 27.05.2015
 *      Author: Tim Duente
 */

#include "AD5252.h"

AD5252::AD5252(uint8_t address) {
	this->address = address;

}

AD5252::~AD5252() {
	// TODO Auto-generated destructor stub
}

//whiper index is 1 or 3
void AD5252::setPosition(uint8_t wiperIndex, uint8_t whiperPosition){
  Wire.beginTransmission(poti_manufactur_address);
  Wire.write(wiperIndex);
  Wire.write(whiperPosition);
  Wire.endTransmission(1);
}

//whiper is 1 or 3
uint8_t AD5252::getPosition(uint8_t wiperIndex){
  Wire.beginTransmission(poti_manufactur_address);
  Wire.write(wiperIndex);
  Wire.endTransmission();
  Wire.requestFrom(poti_manufactur_address, (uint8_t)1);
  uint8_t one = Wire.read();
  return one;
}

void AD5252::decrement(uint8_t wiperIndex) {
}

void AD5252::increment(uint8_t wiperIndex) {
}

void AD5252::increment(uint8_t wiperIndex, int steps, int stepDelay) {
}

void AD5252::decrement(uint8_t wiperIndex, int steps, int stepDelay) {
}
