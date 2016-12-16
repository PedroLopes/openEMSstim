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
 * EMSChannel.cpp
 *
 *  Created on: 06.05.2014
 *  Author: Tim Duente
 *  Edit by: Max Pfeiffer 15.06.2015
 */
#include "EMSChannel.h"

//---------- constructor ----------------------------------------------------

EMSChannel::EMSChannel(uint8_t channel_to_Pads, uint8_t channel_to_Pads_2,
		uint8_t led_active_pin, AD5252* digitalPoti, uint8_t wiperIndex) {
	intensity = 0;
	activated = false;

	maxIntensity = 215; // Die differnez ist gleich dem max Wert
	minIntensity = 55; //

	this->channel_to_Pads = channel_to_Pads;
	this->channel_to_Pads_2 = channel_to_Pads_2;
	this->led_active_pin = led_active_pin;

	endTime = 1;
	onTimeChannel = 1;

	this->digitalPoti = digitalPoti;
	this->wiperIndex = wiperIndex;

	pinMode(channel_to_Pads, OUTPUT);
	pinMode(channel_to_Pads_2, OUTPUT);
	pinMode(led_active_pin, OUTPUT);

	digitalWrite(channel_to_Pads_2, LOW);
	digitalWrite(channel_to_Pads, LOW);
	digitalWrite(led_active_pin, LOW);
}

EMSChannel::~EMSChannel() {
}

//---------- public ----------------------------------------------------
/*
 * Starts the communication with the digital Poti. Must be called for initialization
 */
void EMSChannel::start() {
	Wire.begin();
}

/*
 * Schaltet das EMS-Signal auf die Pads.
 */
void EMSChannel::activate() {
	digitalWrite(channel_to_Pads, HIGH);
	digitalWrite(channel_to_Pads_2, HIGH);
	activated = true;
	digitalWrite(led_active_pin, HIGH);
}
/*
 * Schaltet das EMS-Signal auf den MosFet.
 */
void EMSChannel::deactivate() {
	digitalPoti->setPosition(wiperIndex, 255);
	delay(50);
	digitalWrite(led_active_pin, LOW);
	digitalWrite(channel_to_Pads_2, LOW);
	digitalWrite(channel_to_Pads, LOW);
	activated = false;
	endTime = 0;
}

/* EN: Proofs if the channel is active
 */
bool EMSChannel::isActivated() {
	return activated;
}

/* Sets the intensity from 0-100
 *
 */
void EMSChannel::setIntensity(int intensity) {
	this->intensity = int(
			((maxIntensity - minIntensity) * intensity) * 0.01f + 0.5f)
			+ minIntensity;

	if (this->intensity > POTI_STEPS_UP) {
		this->intensity = POTI_STEPS_UP;
	} else if (this->intensity < POTI_STEPS_DOWN) {
		this->intensity = POTI_STEPS_DOWN;
	}

	int resistorLevel = POTI_STEPS_UP - this->intensity;

	digitalPoti->setPosition(wiperIndex, resistorLevel);
}

/* Return the intensity in a range of 0-100

 */
int EMSChannel::getIntensity() {
	return intensity;
}

void EMSChannel::setSignalLength(int signalLength) {
	onTimeChannel = signalLength;

}

int EMSChannel::getSignalLength() {
	return onTimeChannel;

}

void EMSChannel::applySignal() {
	endTime = millis() + onTimeChannel;
}


int EMSChannel::check() {
	if (endTime && endTime <= millis()) {
		deactivate();
		return 1;
	}
	return 0;
}

//maxIntensity in percent
void EMSChannel::setMaxIntensity(int maxIntensity) {
	this->maxIntensity = int(POTI_STEPS_UP * maxIntensity * 0.01f + 0.5f);
}

//minIntensity in percent
void EMSChannel::setMinIntensity(int minIntensity) {
	this->minIntensity = int(POTI_STEPS_UP * minIntensity * 0.01f + 0.5f);
}

//---------- private ----------------------------------------------------
