#!/usr/bin/python
# Filename: openems.py
"""
    openems is a python module that handles
    the creation of EMS stimulation commands
    and connection via USB to an openEMSstim
"""

import serial
from time import sleep

def create_usb_device(self,serial_port,baudrate):
    self.seriali_port = serial_port
    self.baudrate = baudrate
    
    #open the connection
    this.serial = serial.Serial(COM_port, baud_rate) 
    
    # wait for n seconds
    sleep_wait = 10
    print("Waiting " + str(sleep_wait) + " seconds for the board to setup...\n")
    sleep(sleep_wait)
    print("Ready.\n")

def stimulation_command(channel, intensity, duratin):
    # check channel input validity
    if channel.isdigit():
        channel_number = int(channel)
        command = ""
        if channel_number == 1 or channel_number == 2:
            command += str("C" + str(channel_number-1))
        else:
            print("Malformatted input at channel number:" + str(channel))
            return None
            
        # check intensity input validity
        if intensity.isdigit():
            intensity_number = int(intensity)
        if intensity_number >= 0 and intensity_number <= 100:
            command += str("I"+ intensity)
        else:
            print("Malformatted input at intensity number:" + str(intensity))
            return None 
        
        # check duration input validity
        if duration.isdigit():
            if int(duration) >= 0:
                command += str("T"+ duration + "G")
            else:
                print("Malformatted input at duration number:" + str(duration))
                return None
        print("CMD="+str(command))
        return command
    
def send(self,ems_stimulation_command):
        ser.write(str(ems_stimulation_command))

version = '0.1'

# End of openems.py
