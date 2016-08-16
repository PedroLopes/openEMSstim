# This program enables to send commands via USB to the openEMSstim board
# NOTE: To use it you will need to adjust the serial_port to whatever your operating system reported for the arduino connection (Windows will be something like COM3, check on Device Manager, while unix will be /dev/tty.usbsomething)
serial_port = "/dev/tty.usbserial-A9Q51NVR"
import os
import sys
from time import sleep
from pyEMS import openEMSstim
from pyEMS.EMSCommand import ems_command
from pyEMS.channel import Channel
from pyEMS.preset import Preset

# create a new openEMSstim board
my_ems_board = openEMSstim.openEMSstim(serial_port,19200) #adjust serial_port above, do not change baudrate if you don't know why

# settings for commands
not_ended = True
print_channel_config_next_time = False
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
command_history = []

def cli_clear():
    #if os.type != "Windows":
    #    print(chr(27) + "[2J")
    pass

def print_configuration(print_channel_config_next_time):
    for c in channels:
        print("-----------")
        print(c.name)
        print(c.intensity)
    print("-----------")
    print("Command history (last 5)")
    for c in command_history[len(command_history-5):-1]:
        print c
    print_channel_config_next_time = False

def stimulate(ems_board, command, command_history, save_command_to_history): 
    if command:
        if save_command_to_history:
            command_history.append(command)
        else:
            command = command_history[-1]
        print("openEMSstim: stimulates now (raw command is:" + str(command) + ")")  
        ems_board.send(command)
    else:
        print("Error: command invalid, hence not sent")

while (not_ended):
    cli_clear()
    print(print_channel_config_next_time)
    if print_channel_config_next_time:
        print_configuration(print_channel_config_next_time)
    command = raw_input(command_list)
    command_tokens = command.split(" ")
    
    # direct command mode
    if len(command_tokens) == 3: 
        command = ems_command(command_tokens[0],command_tokens[1],command_tokens[2])
        stimulate(my_ems_board, command, command_history, True)

   
    
    # save preset mode
    elif len(command_tokens) == 2: 
        if command_tokens[0] == "p":
            #p = Preset(command_history[-1])
            print("save preset mode")

    # repeat command mode or all single "string" commands
    elif len(command_tokens) == 1: 

        # repeat last command mode
        if command_tokens[0] == "r":
            if len(command_history) >= 1:
                stimulate(my_ems_board, command, command_history, False)
            else:
                print("Warning: there is no last command to repeat (history empty)")

        # display channel configurations mode
        elif command_tokens[0] == "d":
            print_channel_config_next_time = True  

        # save preset mode
        elif command_tokens[0] == "s":
            print("save mode")

        # quit program
        elif command_tokens[0] == "q":
            not_ended = False

        # repeat command with de/in-crease in amplitude
        elif len(command_tokens[0]) == 2: #does this work?
            print("repeat command with +-")

