# using openEMSstim via python

This is a collection of python examples of how to interface with openEMSstim (via USB):

1. **pyEMS**: is the python module that you need to send commands to openEMSstim 
	1. **openEMSstim.py**: is the main module, allows to connect to the board
	2. **EMSCommand.py**: contains the function to generate EMS commands
2. **send_single_command.py**: is a simple script to send one command via USB to openEMSstim
3. pong-in-python: is an example of a simple game using EMS and Python
4. more-detailed-python-examples: has some extra things you might want to explore
5. tests: run ``python test_openems_module.py`` to check if your python module works
	
## How to run the python apps

1. Make sure you have python installed
2. Install our dependency by doing: pip -r requirements.txt
3. Run an app by typing ``python nameofapp.py`` (do not forget to configure serial port)

## More info about pong-in-python game



### License and Liability

Please refer to the liability waiver (in documentation/liability_waiver.md).

Please refer to the license (in /license.md)


