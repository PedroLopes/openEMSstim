# using openEMSstim via python

This is a collection of python examples of how to interface with openEMSstim (via USB):

1. **pyEMS**: is the python module that you need to send commands to openEMSstim 
	1. **openEMSstim.py**: is the main module, allows to connect to the board
	2. **EMSCommand.py**: contains the function to generate EMS commands
2. **send_single_command.py**: is a simple script to send one command via USB to openEMSstim
3. pong-in-python: is an example of a simple game using EMS and Python
4. more-detailed-python-examples: has some extra things you might want to explore
5. tests: run ``python test_openems_module.py`` to check if your python module works
	

## I dont want to build the code... where do I find the pre-build/apk/binary apps?
No worries, the pre-build/binary apps are here too. Navigate into each app folder and you will find a file called "appname".apk, this is a android binary. You can simply copy it to your phone (even email it to yourself) and tap on it to install it (will invoke the android installer). Note you have to disable "install from trusted sources only" on your preferences (also know as "Developer Options" on the android preferences). 

## How to mod any python app to do what I want?

1. Make sure you have python
2. Install any dependency by doing: pip -r requirements.txt
3. Copy paste an app, give it a new name and start working
4. To access the openEMSstim you can use the pyEMS module, which abstracts everything for you.

### License and Liability

Please refer to the liability waiver (in documentation/liability_waiver.md).

Please refer to the license (in /license.md)


