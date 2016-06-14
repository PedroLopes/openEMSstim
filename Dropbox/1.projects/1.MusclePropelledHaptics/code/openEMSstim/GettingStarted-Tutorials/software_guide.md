# openEMS Android Apps


# openEMS Unity3D Plugin/Scene/Demo/Asset

 				 		


# EMS Communication Protocol

To talk to the EMS device over bluetooth (or serial) you can use the following protocol. This is responsible for controlling the hardware inside the device (digital potentciometer, mosfets and optocouplers) to react to the commands you provide.

You can obviously create your own protocol, but we encourage you to re-use, re-mix, improve this one or see it as a starting point for something better.

## Communication protocol: how a message is structured

A serial command (sent via USB or bluetooth) must look like this: 

			
| Command       | Values   | Sample  	| Description | 
| ------------- |:--------:| ---------:|------------:|
| Channel 		|0-1		|C0 		|Set channel 0 or 1|
| Intensity		|0-100 		|I5			|6 Set intensity in| 
| On-Time 		|1-int32 	|T20000 	|Set the on time in ms|
| Activate/Go	|G 			|G			|Activate the command|

## Important facts about the protocol design

This protocol assumes you are always sending messages with a time in ms that the command is valid for. This allows some extra level of safety. Your host device (the ones that runs your application and talks via bluetooth to the EMS controller) can simply resend messages on a timely basis to make sure the commands are run all the time, appearing to be continuous. 

More detailed shown below at the example of one command. 

## Exemplary command

For example, when the message “C0I100T1000G” is sent to the service, channel 0 activates at full intensity for 1000 ms (1 second). Resending “G” activates the same channel with the previous parameters again. Each parameter can be changed individually. If a new “on-time” message is received while a channel is active, the new time will be updated even if it is shorter. For example, if the signal is on and the new time is set to 50 ms, it will deactivated after 50 ms.

## What's the easiest way for me to generate commands?

Simply use the android app supplied here in this repository and print out the generated commands to your console (e.g., while your android phone is connected to the computer for debug via USB).  This way you can iteratively see how the commands are generated. 

### License and Liability

<include this in all files>


