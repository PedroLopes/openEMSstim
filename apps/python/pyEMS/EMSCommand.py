#!/usr/bin/python
# Filename: EMSCommand.py
"""
"""
version = '0.1'

def ems_command(_channel, _intensity, _duration):
    # forcing everything to be an int
    channel = int(_channel)
    intensity = int(_intensity)
    duration = int(_duration)
    command = ""
    
    # check channel input validity
    if channel == 1 or channel == 2:
        command += str("C" + str(channel-1))
    else:
        print("Malformatted input at channel number:" + str(channel))
        return None
            
    # check intensity input validity
    if intensity>= 0 and intensity<= 100:
        command += str("I"+ str(intensity))
    else:
        print("Malformatted input at intensity number:" + str(intensity))
        return None 
    
    # check duration input validity
    if duration >= 0:
        command += str("T"+ str(duration) + "G")
    else:
        print("Malformatted input at duration number:" + str(duration))
        return None
    return command
