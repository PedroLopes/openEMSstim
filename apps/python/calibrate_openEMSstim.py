# This program enables to send commands via USB to the openEMSstim board
# NOTE: To use it you will need to adjust the COM_port to whatever your operating system reported for the arduino connection (Windows will be something like COM3, check on Device Manager, while unix will be /dev/tty.usbsomething)

from time import sleep
from pyEMS import openEMSstim
from pyEMS.EMSCommand import ems_command

my_ems_board = openEMSstim.openEMSstim("/dev/tty.wchusbserial1420",19200)
not_ended = True

while (not_ended):
    choice = raw_input("Select [1] or [2] to send impulse through channel 1 or 2 respectively (and [q] to quit):")
    if int(choice) == 1:
        my_ems_board.send(ems_command(1,100,1000))
    elif int(choice) == 2:
        my_ems_board.send(ems_command(2,100,1000))
    elif choice == "q":
        not_ended = False

