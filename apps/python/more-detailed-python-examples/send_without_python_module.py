# This program enables to send commands via USB to the openEMSstim board
# NOTE: To use it you will need to adjust the COM_port to whatever your operating system reported for the arduino connection (Windows will be something like COM3, check on Device Manager, while unix will be /dev/tty.usbsomething)

from time import sleep
import serial

#setup the connection and its parameters (you can change this below)
COM_port = '/dev/tty.wchusbserial1410'
baud_rate = 19200

#open the connection
ser = serial.Serial(COM_port, baud_rate) 

# wait for n seconds
sleep_wait = 10
print("Waiting " + str(sleep_wait) + " seconds for the board to setup...\n")
sleep(sleep_wait)
print("Ready.\n")
notStopped = True
while notStopped:
    channel = ""
    intensity = ""
    duration = "1000"

    # read from user input
    command = raw_input("> Format your command like: channel (1-2) intensity(0-100) timeout(optional) [q to quit]")
    
    # parse command
    tokenized = command.split(" ")
    
    # test if number of arguments is correct (2 or 3)
    if len(tokenized) < 2 or len(tokenized) > 3:
        print("ERROR: Format your command like: channel (0-1) intensity(0-100) timeout(optional) [q to quit]")
        continue
    
    # parse channel
    channel = tokenized[0]
    print("channel: " + str(channel))
    if str(channel) == "q":
        notStopped = False
        print("USER: quited")
        continue
    else:
        # parse intensity
        intensity = tokenized[1]
        print("intensity: " + str(intensity))
        
        # get duration from user input if present
        if len(tokenized) == 3:
            duration = tokenized[2]
            print("duration: " + str(duration))

        # check channel input validity
        if channel.isdigit():
            channel_number = int(channel)
            command = ""
            if channel_number == 1 or channel_number == 2:
                command += str("C" + str(channel_number-1))
            else:
                print("Malformatted input at channel number:" + str(channel))
                continue
            
            # check intensity input validity
            if intensity.isdigit():
                intensity_number = int(intensity)
            if intensity_number >= 0 and intensity_number <= 100:
                command += str("I"+ intensity)
            else:
                print("Malformatted input at intensity number:" + str(intensity))
                continue
            
            # check duration input validity
            if duration.isdigit():
                if int(duration) >= 0:
                    command += str("T"+ duration + "G")
                else:
                    print("Malformatted input at duration number:" + str(duration))
                    continue
            print("CMD="+str(command))
            ser.write(str(command))
