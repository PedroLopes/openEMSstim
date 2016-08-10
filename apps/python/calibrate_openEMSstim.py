# This program enables to send commands via USB to the openEMSstim board
# NOTE: To use it you will need to adjust the serial_port to whatever your operating system reported for the arduino connection (Windows will be something like COM3, check on Device Manager, while unix will be /dev/tty.usbsomething)
serial_port = "/dev/tty.wchusbserial1420"

from time import sleep
from pyEMS import openEMSstim
from pyEMS.EMSCommand import ems_command
import sys

my_ems_board = openEMSstim.openEMSstim(serial_port,19200) #adjust serial_port above, do not change baudrate if you don't know why
not_ended = True

command_list = sys.argv[0] + ", type: [1|2] [0-100] [0-2000]\n" 
command_list += "\tExample: 1 100 1000\n"
command_list += "\tHelp: channel (1 or 2), intensity (0 - 100%) duration (in milliseconds))\n"
command_list += "\tAlso: you can use [1|2][+|-]\n"
command_list += "\tHelp: repeates the previous command, on selected channel, and increases(+)/dercreases(-) the intensity by 1%\n"
command_list += "\t[q] to quit\n"

while (not_ended):
    #choice = raw_input("Select [1] or [2] to send impulse through channel 1 or 2 respectively (and [q] to quit):")
    choice = raw_input(command_list)
    if int(choice) == 1:
        my_ems_board.send(ems_command(1,100,1000))
    elif int(choice) == 2:
        my_ems_board.send(ems_command(2,100,1000))
    elif choice == "q":
        not_ended = False

