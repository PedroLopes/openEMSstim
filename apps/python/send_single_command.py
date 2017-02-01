# This program enables to send commands via USB to the openEMSstim board
# NOTE: To use it you will need to adjust the COM_port to whatever your operating system reported for the arduino connection (Windows will be something like COM3, check on Device Manager, while unix will be /dev/tty.usbsomething)

from time import sleep
from pyEMS import openEMSstim
from pyEMS.EMSCommand import ems_command

my_ems_board = openEMSstim.openEMSstim("/dev/tty.usbserial-A98VFLTL",19200)
while 1:
    choice = int(raw_input("1 or 2? EMS with full intensity on that channel for 2 seconds"))
    my_ems_board.send(ems_command(choice,100,9000))


