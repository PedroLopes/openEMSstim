from time import sleep
import subprocess
import sys
sys.path.append('../apps/python/')
from pyEMS import openEMSstim
from pyEMS.EMSCommand import ems_command
from glob import glob
import ConfigParser #later change because of py3

#setting the deploy environment
testing = True
arduino_port = None
search_results = -1
code_filename =  "../arduino-openEMSstim/arduino-openEMSstim.ino" 
text = "#define EMS_BLUETOOTH_ID "
Config = ConfigParser.ConfigParser()

# edit the code, add a ID

def deployOnBoard(usb_port):
    global Config
    clean = subprocess.call(["ino","clean"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    print(clean)
    ino = open("ino.ini",'w')
    Config.set('upload','serial-port', usb_port)
    Config.set('serial','serial-port', usb_port)
    Config.write(ino)
    ino.close()
    build = subprocess.call(["ino","build"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    print(build)
    deploy = subprocess.call(["ino","upload"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    print(deploy)

def newInoConfig():
    global Config
    ino = open("ino.ini",'w')
    Config.add_section('build')
    Config.set('build','board-model', "nano328")
    Config.add_section('upload')
    Config.set('upload','board-model', "nano328")
    #Config.set('upload','serial-port', "None")
    Config.add_section('serial')
    #Config.set('serial','serial-port', "None")
    Config.write(ino)
    ino.close()

def listdir():
    lsdir = subprocess.Popen(["ls", "-la"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    results, err = lsdir.communicate()
    print(results)

def setupInoDevEnv():
    print("TODO: still need setup the ino env automatically w/ latest build and also double checks git pull")
    #ask for git pull?
    gitpull = raw_input("Do you want to \">git pull\" the latest verson of this repository? [y/n]")
    if gitpull == 'y':
        git = subprocess.call(["git","pull"])
        print(git)
    print(sys.argv[0])
    mv = subprocess.call(["mv",str(sys.argv[0]),".."])
    print(mv)
    listdir()
    rm = raw_input("Will delete all files (as above) in this dir (except this script). OK? [y/n]")
    if rm == 'y':
        files_to_delete = glob("*")
        for file_rm in files_to_delete:
            rm = subprocess.call(["rm","-rf", file_rm])
        #rm = subprocess.call(["rm","-r", ".build"])
        print(rm)
    init = subprocess.call(["ino","init"])
    print(init)
    rm = subprocess.call(["rm","src/sketch.ino"])
    print(rm)
    newInoConfig()
    mv = subprocess.call(["mv","../"+str(sys.argv[0]),"."])
    print(mv)
    files_to_copy = glob("../arduino-openEMSstim/*")
    for file_cp in files_to_copy:
        cp = subprocess.call(["cp","../arduino-openEMSstim/" + file_cp, "src/"])
        print(cp)
    print("STATUS: build environment ready")

def find_arduino_port():
    find_usb = subprocess.Popen(["ls","/dev/"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    search_results, err = find_usb.communicate()
    search_results = str.split(search_results)
    if search_results is None:
        return None, None
    for a in search_results:
        if a.find("tty.") > -1: 
            if a.find("tty.usb") > -1: 
                print("STATUS: Found arduino with FTDI driver")
                arduino_port = "/dev/" + str(a)
                return a, search_results
            elif a.find("tty.wchusb") > -1: 
                print("STATUS: Found arduino with non-FTDI driver")
                arduino_port = "/dev/" + str(a)
                return a, search_results
    return None, None

def change_line_to(filename,line, text):
    with open(filename, 'r') as file:
        data = file.readlines()
    data[line-1] = text + '\n'
    with open(filename, 'w') as file:
        file.writelines( data )

print("MENU: openEMSstim Firmware loader for multiple boards.")
board_no = int(raw_input("MENU: How many boards are we loading? "))
curr_id = int(raw_input("MENU: What is the first ID number? "))
setupInoDevEnv()
while board_no >= 0:
    print("STATUS: Ready.")
    print("ACTION: Connect the first board now.")
    arduino_port = True
    while arduino_port is None:
        arduino_port = find_arduino_port()
        arduino_port, search_results = find_arduino_port()
        print arduino_port
        if arduino_port is None:
            print("STATUS: Could not find a arduino, here is what we found:") 
            print(search_results)
            choice = raw_input("Try again? (enter to continue, q to exit)")
            if choice == 'q':
                exit(0)
    print("STATUS: Board pluged to " + str(arduino_port))
    ready = raw_input("ACTION: Confirm? (type enter to continue)")
    if ready == "":
        print("BUILDING: software to include ID:" + str(curr_id))
        change_line_to(code_filename, 20, text+"\"emsB"+str(curr_id)+"\"")
        print("UPLOADING: to board with ID:" + str(curr_id))
        deployOnBoard(arduino_port)        
    if testing == True:
        print("TESTING: executing python test on board ID: " + str(curr_id))
            
        #create a ems_device
        ems_device = openEMSstim.openEMSstim(arduino_port,19200)
        if ems_device: #check if was created properly
            print("STATUS: Device Found")
            print("TEST: stimulation to Channel 1, Intensity=1 (out of 100), Duration=1000 (1 second)")
            command_1 = ems_command(1,1,1000)
            ems_device.send(command_1)
            wait = None
            while wait == 'y' or wait == 'n':
                wait = raw_input("ACTION: Did you see the LED on this channel go ON? [y/n/r for repeat]")
                if wait == 'r':
                    ems_device.send(command_1)
                elif wait == 'y':
                    print("TEST/LOG/B"+str(curr_id)+": LED1 OK") #should change to log
                elif wait == 'n':
                    print("TEST/LOG/B"+str(curr_id)+": LED1 FAIL") #should change to log
            print("TEST: stimulation to channel 1 at maximum (100) and you will open the EMS slowly for that channel")
            #send direct writes using short commands
            #command_1 = ems_command(1,1000,2000)
            #ems_device.send(command_1)
            wait = None
            while wait == 'y' or wait == 'n':
                wait = raw_input("ACTION: Did you feel the EMS on channel 1? [y/n/r for repeat]")
                if wait == 'r':
                    pass
                    #ems_device.send(command_1)
                elif wait == 'y':
                    print("TEST/LOG/B"+str(curr_id)+": EMS1 OK") #should change to log
                elif wait == 'n':
                    print("TEST/LOG/B"+str(curr_id)+": EMS1 FAIL") #should change to log
            print("TEST: stimulation to Channel 2, Intensity=1 (out of 100), Duration=1000 (1 second)")
            command_2 = ems_command(2,1,1000)
            ems_device.send(command_2)
            wait = None
            while wait == 'y' or wait == 'n':
                wait = raw_input("ACTION: Did you see the LED on this channel go ON? [y/n/r for repeat]")
                if wait == 'r':
                    ems_device.send(command_2)
                elif wait == 'y':
                    print("TEST/LOG/B"+str(curr_id)+": LED2 OK") #should change to log
                elif wait == 'n':
                    print("TEST/LOG/B"+str(curr_id)+": LED2 FAIL") #should change to log
             
            #do EMS for channel 2
            
            #now do both channels
            print("TEST: stimulation to Channel 1 + 2 (two commands) with Intensity=40 (out of 100), Duration=2000 (2 seconds)")
            command_1 = ems_command(1,40,2000)
            command_2 = ems_command(2,40,2000)
            ems_device.send(command_1)
            ems_device.send(command_2)
            # do wait and ask
            ems_device.shutdown()
            print("TEST:Done.")
            #write test results
    curr_id += 1
    board_no-=1
