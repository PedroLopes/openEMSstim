from time import sleep
import subprocess
from sys import exit
import sys
sys.path.append('../apps/python/')
from pyEMS import openEMSstim
from pyEMS.EMSCommand import ems_command

#setting the deploy environment
testing = True
arduino_port = None
search_results = -1
code_filename =  "../arduino-openEMSstim/arduino-openEMSstim.ino" 
text = "#define EMS_BLUETOOTH_ID "

# edit the code, add a ID

def deployOnBoard():
    clean = subprocess.call(["ino","clean"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    print(clean)
    build = subprocess.call(["ino","build"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    print(build)
    deploy = subprocess.call(["ino","upload"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    print(deploy)

def setupInoDevEnv():
    print("TODO: still need setup the ino env automatically w/ latest build and also double checks git pull")

def find_arduino_port():
    find_usb = subprocess.Popen(["ls","/dev/"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    search_results, err = find_usb.communicate()
    search_results = str.split(search_results)
    if search_results is None:
        return None, None
    for a in search_results:
        if a.find("tty.") > -1: 
            if a.find("tty.usb") > -1: 
                print("Found arduino with FTDI driver")
                arduino_port = "/dev/" + str(a)
                return a, search_results
            elif a.find("tty.wchusb") > -1: 
                print("Found arduino with non-FTDI driver")
                arduino_port = "/dev/" + str(a)
                return a, search_results
    return None, None

def change_line_to(filename,line, text):
    with open(filename, 'r') as file:
        data = file.readlines()
    data[line-1] = text + '\n'
    with open(filename, 'w') as file:
        file.writelines( data )

print("openEMSstim Firmware loader for multiple boards.")
board_no = int(raw_input("How many boards are we loading? "))
curr_id = int(raw_input("What is the first ID number? "))
setupInoDevEnv()
while board_no >= 0:
    print("Ready.")
    print("Connect the first board now.")
    arduino_port = True
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
    ready = raw_input("Confirm? (type enter to continue)")
    if ready == "":
        print("BUILDING: software to include ID:" + str(curr_id))
        change_line_to(code_filename, 20, text+"\"emsB"+str(curr_id)+"\"")
        print("UPLOAD: to board with ID:" + str(curr_id))
        #deployOnBoard()        
    if testing == True:
        print("TEST: executing python test on board ID: " + str(curr_id))
            
        #create a ems_device
        ems_device = openEMSstim.openEMSstim(arduino_port,19200)
        if ems_device: #check if was created properly
            print("Device Found")
            print("Will send: Channel 1, Intensity=1 (out of 100), Duration=1000 (1 second)")
            command_1 = ems_command(1,1,1000)
            ems_device.send(command_1)
            wait = None
            while wait == 'y' or wait == 'n':
                wait = raw_input("Did you see the LED on this channel go ON? [y/n/r for repeat]")
                if wait == 'r':
                    ems_device.send(command_1)
                elif wait == 'y':
                    print("TEST/LOG/B"+str(curr_id)+": LED1 OK") #should change to log
                elif wait == 'n':
                    print("TEST/LOG/B"+str(curr_id)+": LED1 FAIL") #should change to log
            print("Now will open channel at maximum (100) and you will open the EMS slowly for that channel")
            #send direct writes using short commands
            #command_1 = ems_command(1,1000,2000)
            #ems_device.send(command_1)
            wait = None
            while wait == 'y' or wait == 'n':
                wait = raw_input("Did you feel the EMS on this channel go ON? [y/n/r for repeat]")
                if wait == 'r':
                    pass
                    #ems_device.send(command_1)
                elif wait == 'y':
                    print("TEST/LOG/B"+str(curr_id)+": EMS1 OK") #should change to log
                elif wait == 'n':
                    print("TEST/LOG/B"+str(curr_id)+": EMS1 FAIL") #should change to log
            print("Will send: Channel 2, Intensity=1 (out of 100), Duration=1000 (1 second)")
            command_2 = ems_command(2,1,1000)
            ems_device.send(command_2)
            wait = None
            while wait == 'y' or wait == 'n':
                wait = raw_input("Did you see the LED on this channel go ON? [y/n/r for repeat]")
                if wait == 'r':
                    ems_device.send(command_2)
                elif wait == 'y':
                    print("TEST/LOG/B"+str(curr_id)+": LED2 OK") #should change to log
                elif wait == 'n':
                    print("TEST/LOG/B"+str(curr_id)+": LED2 FAIL") #should change to log
             
            #do EMS for channel 2
            
            #now do both channels
            print("Will send: Channel 1 + 2 (two commands) with Intensity=40 (out of 100), Duration=2000 (2 seconds)")
            command_1 = ems_command(1,40,2000)
            command_2 = ems_command(2,40,2000)
            ems_device.send(command_1)
            ems_device.send(command_2)
            # do wait and ask
            ems_device.shutdown()
            print("TEST:Done.")
    curr_id += 1
    board_no-=1

# also outputs a log file with time and stamps of when each arduino got logged and maybe some info?
