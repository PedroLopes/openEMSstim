# This program enables to send commands via USB to the openEMSstim board
# NOTE: To use it you will need to adjust the COM_port to whatever your operating system reported for the arduino connection (Windows will be something like COM3, check on Device Manager, while unix will be /dev/tty.usbsomething)

from time import sleep
from pyEMS import openEMSstim
from pyEMS.EMSCommand import ems_command

my_ems_board = openEMSstim.openEMSstim("/dev/tty.wchusbserial1410",19200)
my_ems_board.send(ems_command(1,50,1000))


