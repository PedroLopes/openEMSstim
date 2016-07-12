#!/usr/bin/python
# Filename: openEMSstim.py
"""
    openems is a python module that handles
    the creation of EMS stimulation commands
    and connection via USB to an openEMSstim
"""

import serial
from time import sleep
import EMSCommand
version = '0.1'

class openEMSstim():

    def __init__(self, serial_port,baudrate=9600):
            self.serial_port = serial_port
            self.baudrate = baudrate
            self.sleep_wait = 10
            self.ems_device = None
            
            #open the connection
            try:
                self.ems_device = serial.Serial(self.serial_port, self.baudrate) 
            except serial.serialutil.SerialException: 
                print("Exception Raised: serial device: " + str(self.serial_port) + " was not found")
                return None

            # wait for n seconds
            print("Waiting " + str(self.sleep_wait) + " seconds for the board to setup...\n")
            sleep(self.sleep_wait)
            print("Ready.\n")

    def send(self,ems_stimulation_command):
            if self.ems_device and ems_stimulation_command:      
                self.ems_device.write(str(ems_stimulation_command))
            else:
                print("Problem creating EMS device on: " + str(self.serial_port) + " with command: " + ems_stimulation_command)

    def shutdown(self):
        self.ems_device.close()

# End of openEMSstim.py
