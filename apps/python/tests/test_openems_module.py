#native imports
import sys
from time import sleep 

#import openEMSstim modules
sys.path.append("../")
from pyEMS import openEMSstim
from pyEMS.EMSCommand import ems_command


#create a ems_device
ems_device = openEMSstim.openEMSstim("/dev/tty.wchusbserial1410",19200)
if ems_device: #check if was created properly
    print("Device Found")
    print("Will send: Channel 1, Intensity=1 (out of 100), Duration=1000 (1 second)")
    command_1 = ems_command(1,1,1000)
    ems_device.send(command_1)
    sleep(1)
    print("Will send: Channel 2, Intensity=1 (out of 100), Duration=1000 (1 second)")
    command_2 = ems_command(2,1,1000)
    ems_device.send(command_2)
    sleep(1)
    print("Will send: Channel 1 + 2 (two commands) with Intensity=40 (out of 100), Duration=2000 (2 seconds)")
    command_1 = ems_command(1,40,2000)
    command_2 = ems_command(2,40,2000)
    ems_device.send(command_1)
    ems_device.send(command_2)
    sleep(2)
    print("Done testing. Closing")
    ems_device.shutdown()
#
