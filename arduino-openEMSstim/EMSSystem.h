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
 * EMSSystem.h
 *
 *  Created on: 26.05.2014
 *      Author: Tim Duente
 */

#ifndef EMSSYSTEM_H_
#define EMSSYSTEM_H_

#include "EMSChannel.h"

#define ACTION 'G'
#define CHANNEL 'C'
#define INTENSITY 'I'
#define TIME 'T'
#define OPTION 'O'

class EMSSystem {
public:
	EMSSystem(int channels);
	virtual ~EMSSystem();

	virtual void addChannelToSystem(EMSChannel *emsChannel);
	virtual void doCommand(String *command);
	void shutDown();
	virtual int check();
	static void start();

protected:
	virtual void doActionCommand(String *command);
	virtual void setOption(String *option);
	virtual bool getChannelAndValue(String *option, int *channel, int *value);
	virtual int getNextNumberOfSting(String *command, int startIndex);

private:
	EMSChannel **emsChannels;
	int channelCount;
	int size;
	bool isInRange(int channel);
};

#endif /* EMSSYSTEM_H_ */
