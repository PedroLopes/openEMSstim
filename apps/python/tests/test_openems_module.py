import sys
sys.path.append("../")
from pyEMS import openems

#create a ems_device
ems_device = openems.create_usb_device("/dev/tty.usb",19200)

#
