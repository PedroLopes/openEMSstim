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
 * AD5252.h
 *
 *  Created on: 27.05.2015
 *      Author: Tim Duente
 */

#include <Arduino.h>
#include "Wire.h"

#ifndef AD5252_AD5252_H_
#define AD5252_AD5252_H_

class AD5252 {
public:
	AD5252(uint8_t address);
	virtual ~AD5252();
	void setPosition(uint8_t wiperIndex, uint8_t whiperPosition);
	uint8_t getPosition(uint8_t wiperIndex);

	void decrement(uint8_t wiperIndex);
	void increment(uint8_t wiperIndex);

	void increment(uint8_t wiperIndex, int steps, int stepDelay);
	void decrement(uint8_t wiperIndex, int steps, int stepDelay);

private:
	static const uint8_t poti_manufactur_address = B0101100;
	uint8_t address;

};

#endif /* AD5252_AD5252_H_ */
