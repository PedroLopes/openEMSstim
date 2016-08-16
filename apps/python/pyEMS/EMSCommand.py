#!/usr/bin/python
# Filename: EMSCommand.py
"""
"""
version = '0.1'

def ems_command(channel, intensity, duration):
    if (not channel.isdigit()) or (not intensity.isdigit()) or (not duration.isdigit()):
        print("Malformatted input " + str(channel) + " " + str(intensity) + " " + str(duration))
        return None
    
    # forcing everything to be an int
    channel = int(channel)
    intensity = int(intensity)
    duration = int(duration)
    command = ""
    
    # check channel input validity
    if channel == 1 or channel == 2:
        command += str("C" + str(channel-1))
    else:
        print("Malformatted input at channel number:" + str(channel) + ". Your device supports only channels 1 or 2")
        return None
            
    # check intensity input validity
    if intensity>= 0 and intensity<= 100:
        command += str("I"+ str(intensity))
    else:
        print("Malformatted input at intensity number:" + str(intensity) + ". Intensity must be betwen 0-100 (integer)" )
        return None 
    
    # check duration input validity
    if duration >= 0:
        command += str("T"+ str(duration) + "G")
    else:
        print("Malformatted input at duration number:" + str(duration) + ". Duration in milliseconds must be a positive integer, e.g., 1000")
        return None
    return command
