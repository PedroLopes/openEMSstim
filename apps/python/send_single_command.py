# This program enables you to test the openEMSstim board quickly via USB
# To use it you will need to adjust the COM_port to whatever your operating system reported for the arduino connection (Windows will be something like COM3, check on Device Manager, while unix will be /dev/tty.usbsomething)
# You can change the send mode, likely you want to keep it as is (STRING)

from time import sleep
import serial

def enum(**enums):
        return type('Enum', (), enums)
modes = enum(HEX=1, STRING=2)

#setup the connection and its parameters (you can change this below)
COM_port = '/dev/tty.wchusbserial1410'
baud_rate = 19200
send_mode = modes.STRING #available modes are: HEX (like BTLE does it internally) or STRING (easier to implement over USB connection)

#open the connection
ser = serial.Serial(COM_port, baud_rate) 
print("Will wait for the board to setup")
sleep(10)
print("wait completed")
notStopped = True
while notStopped:
    channel = raw_input("Test by sending a message to channel 1 or 2, choose by typing \"1\" or \"2\" (q to quit)")
    if channel.isdigit():
        channel_number = int(channel)
        if (channel_number == 1):
            if (send_mode == modes.HEX):
                print("sending HEX message to " + str(channel))
                ser.write(str("WV,0018,433049313030543130303047."))
            else: 
                print("sending STRING message to " + str(channel))
                ser.write(str("C0I100T1000G"))
        elif (channel_number == 2):
            if (send_mode == modes.HEX):
                print("sending HEX message to " + str(channel))
                ser.write(str("WV,0018,433149313030543130303047."))
            else: 
                print("sending STRING message to " + str(channel))
                ser.write(str("C1I100T1000G"))
        else:
            print("Malformatted input at " + str(channel))
    elif str(channel) == "q":
        notStopped = False
