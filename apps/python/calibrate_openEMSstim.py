# This program enables to send commands via USB to the openEMSstim board
# NOTE: To use it you will need to adjust the serial_port to whatever your operating system reported for the arduino connection (Windows will be something like COM3, check on Device Manager, while unix will be /dev/tty.usbsomething)
serial_port = "/dev/tty.wchusbserial1420"

import sys
from time import sleep
from pyEMS import openEMSstim
from pyEMS.EMSCommand import ems_command
from pyEMS.channel import Channel
from pyEMS.preset import Preset

my_ems_board = openEMSstim.openEMSstim(serial_port,19200) #adjust serial_port above, do not change baudrate if you don't know why
not_ended = True

command_list = sys.argv[0] + "choose of of the commands below:\n"
command_list += "\t[1|2] [0-100] [0-2000] e.g., 1 100 1000 (stimulates on channel 1, with 100% for 1000 ms)\n"
command_list += "\t[1|2][+|-] increases(+)/decreases(-) the intensity of this channel by 1%\n"
command_list += "\t[r] repeates the previous command\n"
command_list += "\t[d] to display current EMS parameters\n"
command_list += "\t[s] to save current EMS parameters to file (file: <date>_ems.params)\n"
command_list += "\t[p] [name] to save current EMS parameters as preset with [name]\n"
command_list += "\t[q] to quit\n"

# starting defaults
channels = []
channels.append(Channel(1,0,"right_hand_flexor"))
channels.append(Channel(2,0,"right_hand_extensor"))
max_intensity = Preset(100, "max")
min_intensity = Preset(0, "max")
channels[0].add_preset(max_intensity)
channels[0].add_preset(min_intensity)
channels[1].add_preset(max_intensity)
channels[1].add_preset(min_intensity)

def cli_clear():
    if os.type != "Windows":
        print(chr(27) + "[2J")

while (not_ended):
    cli_clear()
    command = raw_input(command_list)
    command_tokens = command.split(" ")
    if len(command_tokens) == 3: #direct command mode
        #command_history.append(command)
        #my_ems_board.send(ems_command(2,100,1000))
        print("Direct command mode") 
    elif len(command_tokens) == 2: #save preset command
        if command_tokens[0] == "p":
            #p = Preset(command_history[-1])
            print("save preset mode")

    elif len(command_tokens) == 1: #repeat command or string commands
        if command_tokens[0] == "r":
            print("repeat mode")

        elif command_tokens[0] == "d":
            print("display mode")
            for c in channels:
                print(c.name)
                print(c.intensity)
        elif command_tokens[0] == "s":
            print("save mode")
        elif command_tokens[0] == "q":
            not_ended = False
        elif len(command_tokens[0]) == 2: #does this work?
            print("repeat command with +-")

