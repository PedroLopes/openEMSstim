from time import sleep
import subprocess
from sys import exit

#setting the deploy environment
arduino_port = None
search_results = -1
code_filename =  "../arduino-openEMSstim/arduino-openEMSstim.ino" 

#from pyEMS import openEMSstim
#from pyEMS.EMSCommand import ems_command

# edit the code, add a ID

def find_arduino_port():
    find_usb = subprocess.Popen(["ls","/dev/"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    search_results, err = find_usb.communicate()
    search_results = str.split(search_results)
    if search_results is None:
        return None, None
    for a in search_results:
        if a.find("tty.") > -1: 
            if a.find("tty.usb") > -1: 
                return a, search_results
            # uncomment the lines below if you need to add more special cases
            #elif a.find("tty.w") > -1: 
            #    print("Found arduino with non-FTDI driver")
            #    arduino_port = a
            #    return a, search_results
    return None, None

print("openEMSstim Firmware loader for multiple boards.")
board_no = int(raw_input("How many boards are we loading? "))
first_id = int(raw_input("What is the first ID number? "))
print("Ready.")
print("Connect the first board now.")
while arduino_port is None:
    arduino_port = find_arduino_port()
    arduino_port, search_results = find_arduino_port()
    print arduino_port
    if arduino_port is None:
        print("Could not find a arduino, here is what we found:") 
        print(search_results)
        choice = raw_input("Try again? (enter to continue, q to exit)")
        if choice == 'q':
            exit(0)
print("Board pluged to " + str(arduino_port))
ready = raw_input("Confirm? (just press enter to continue)")

if ready:
    print("Altering software to include ID:" + str(first_id))

    print("Burning with ID:" + str(first_id))

# also outputs a log file with time and stamps of when each arduino got logged and maybe some info?

#ask if wants to test:
    #then goes into testing mode. 
    #instantiate a python program, that does it

#my_ems_board = openEMSstim.openEMSstim("/dev/tty.wchusbserial14220",19200)
#while 1:
#    my_ems_board.send(ems_command(choice,100,2000))

def change_line_to(filename,line, text):
    with open(filename, 'r') as file:
        data = file.readlines()
    data[line-1] = text + '\n'
    with open(filename, 'w') as file:
        file.writelines( data )
