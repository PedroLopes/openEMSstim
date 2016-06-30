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
 * EMSSystem.cpp
 *
 *  Created on: 26.05.2014
 *  Author: Tim Duente
 *  Edit by: Max Pfeiffer - 13.06.2015
 */

#include "EMSSystem.h"

EMSSystem::EMSSystem(int channels) {
	emsChannels = (EMSChannel**) malloc(channels * sizeof(EMSChannel*));
	channelCount = channels;
	size = 0;
}

EMSSystem::~EMSSystem() {
	free(emsChannels);
}

void EMSSystem::addChannelToSystem(EMSChannel *emsChannel) {
	if (size < channelCount) {
		emsChannels[size] = emsChannel;
		size++;
	}
}

// get the next number out of a String object and return it
int EMSSystem::getNextNumberOfSting(String *command, int startIndex) {
	int number = -1;

	int endIndex = -1;

	// Select the number in the string
	for (int i = startIndex + 1; i < (int) command->length(); i++) {
		char tmp = command->charAt(i);
		if (tmp >= '0' && tmp <= '9') {
			if (startIndex == -1) {
				startIndex = i;
			}
			endIndex = i + 1;
		} else {
			break;
		}

		if (endIndex > startIndex && endIndex >= 0 && startIndex >= 0) {
			String temS = command->substring((unsigned int) startIndex + 1,
					(unsigned int) endIndex);
			number = (int) temS.toInt();

		} else {
			Serial.println(
					"not in if ERROR: endIndex>startIndex && endIndex>=0 &&  startIndex>=0");
		}
	}
	return number;
}

void EMSSystem::doActionCommand(String *command) {

	if (command->length() != 0) {
		// Channel
		int seperatorChannel = command->indexOf(CHANNEL);
		int currentChannel = -1;

		if (seperatorChannel != -1) {

			currentChannel = getNextNumberOfSting(command, seperatorChannel);
		}

		// Signal length onTime
		int seperatorSignalLength = command->indexOf(TIME);
		int signalLength = -1;
		if (seperatorSignalLength != -1) {
			signalLength = getNextNumberOfSting(command, seperatorSignalLength);
			if (signalLength > 5000) {
				//signaleLength max 5000ms
				signalLength = 5000;
			}
			emsChannels[currentChannel]->setSignalLength(signalLength);
		}

		// Signal Intensity
		int seperatorSignalIntensity = command->indexOf(INTENSITY);
		int signalIntensity = -1;
		if (seperatorSignalIntensity != -1) {
			signalIntensity = getNextNumberOfSting(command,
					seperatorSignalIntensity);
			emsChannels[currentChannel]->setIntensity(signalIntensity - 1);
		}

		// Apply the command
		int seperatAction = command->indexOf(ACTION);
		bool action = false;
		if (seperatAction != -1) {
			action = true;
		}

		if (currentChannel >= 0 && currentChannel < size) {
			emsChannels[currentChannel]->activate();
			emsChannels[currentChannel]->applySignal();
		} else {
			//deactivate all channels if channelNumber is wrong
			shutDown();
		}

	} else {

		Serial.print("Command = null!!!:");
	}

}

void EMSSystem::shutDown() {
	for (int i = 0; i < size; i++) {
		emsChannels[i]->deactivate();
	}
}

/* TODO change to set commands */

void EMSSystem::setOption(String *option) {
	char secChar = option->charAt(2);
	int channel = -1;
	int value = -1;
	switch (option->charAt(1)) {
	case 'C':
		if (secChar == 'T' && getChannelAndValue(option, &channel, &value)) {
			//set changeTime
			//emsChannels[channel]->setIncreaseDecreaseTime(value);
		}
		break;
	case 'M':
		if (secChar == 'A' && getChannelAndValue(option, &channel, &value)) {
			//Maxixum value for the calibration
			emsChannels[channel]->setMaxIntensity(value);
		} else if (secChar == 'I'
				&& getChannelAndValue(option, &channel, &value)) {
			//Minimum value for the calibration
			emsChannels[channel]->setMinIntensity(value);
		}
		break;

	default:
		break;
	}

}

bool EMSSystem::getChannelAndValue(String *option, int *channel, int *value) {
	int left = option->indexOf('[');
	int right = option->lastIndexOf(']');
	int seperator = option->indexOf(',', left + 1);

	if (left < seperator && seperator < right && left != -1 && right != -1
			&& seperator != -1) {
		String help = option->substring(left + 1, seperator);
		(*channel) = help.toInt();
		help = option->substring(seperator + 1, right);
		(*value) = help.toInt();

		//Parsing successful
		//Check whether channel exists
		return isInRange((*channel));
	}
	//Parsing not successful
	return false;
}

bool EMSSystem::isInRange(int channel) {
	return (channel >= 0 && channel < size);
}

int EMSSystem::check() {
	int stopCount = 0;
	for (int i = 0; i < size; i++) {
		stopCount = stopCount + emsChannels[i]->check();
	}
	return stopCount;
}

void EMSSystem::doCommand(String *command) {
	if (command->length() > 0) {
		if (command->indexOf(ACTION) != -1) {
			doActionCommand(command);
		} else if (command->charAt(0) == OPTION) {
			setOption(command);
		} else {
			Serial.print("Unknown command: ");
			Serial.println((*command));
			Serial.flush();
		}
	}
}

void EMSSystem::start() {
	EMSChannel::start();
}
