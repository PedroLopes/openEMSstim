import sys
sys.path.append("../")
from pyEMS import openEMSstim
from pyEMS.EMSCommand import ems_command

#create a ems_device
ems_device = openEMSstim.openEMSstim("/dev/tty.usb",19200)
if ems_device: #check if was created properly
    print("Device Found")
    print("Will send: Channel 1, Intensity=1 (out of 100), Duration=1000 (1 second)")
    command_1 = ems_command(1,1,1000)
    print(str(command_1))
    ems_device.send(command_1)


#
