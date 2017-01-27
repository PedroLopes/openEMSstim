from time import sleep
import subprocess
import sys
sys.path.append('../apps/python/')
import time
from pyEMS import openEMSstim
from pyEMS.EMSCommand import ems_command
from glob import glob
import logging
if sys.version_info >= (3,0):
    import configparser
else:
    import ConfigParser 

# setting the build & deploy environment (using ino)
testing = True
arduino_port = None
search_results = -1
code_filename =  "../arduino-openEMSstim/arduino-openEMSstim.ino" 
text = "#define EMS_BLUETOOTH_ID "
Config = ConfigParser.ConfigParser()

# setting the logger 
logger = logging.getLogger('openEMSstim')
hdlr = logging.FileHandler('deploy' + "_" + str(time.time()) + '.log')
formatter = logging.Formatter('%(asctime)s:%(levelname)s:%(message)s"')
hdlr.setFormatter(formatter)
logger.addHandler(hdlr)
logger.setLevel(logging.WARNING)
logger.setLevel(logging.INFO)
logger.info( "ID","serial-port", "LED1", "channel 1 EMS", "LED2", "channel 2 EMS", "Bluetooth", "observations", "case", "9v")
tests = [None] * 10
curr_test = 0

def testCommandReturn(value, name):
    if value == 0:
        return
    else:
        logging.warning("shell command: " + name + " failed with incorrect status" )
        exit(0)

def deployOnBoard(usb_port):
    global Config
    clean = subprocess.call(["ino","clean"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    testCommandReturn(clean,"ino clean")
    ino = open("ino.ini",'w')
    Config.set('upload','serial-port',  usb_port)
    Config.set('serial','serial-port',  usb_port)
    Config.write(ino)
    ino.close()
    build = subprocess.call(["ino","build"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    testCommandReturn(build,"ino build")
    deploy = subprocess.call(["ino","upload"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    testCommandReturn(deploy,"ino upload")

def newInoConfig():
    global Config
    ino = open("ino.ini",'w')
    Config.add_section('build')
    Config.set('build','board-model', "nano328")
    Config.add_section('upload')
    Config.set('upload','board-model', "nano328")
    Config.add_section('serial')
    Config.write(ino)
    ino.close()

def listdir():
    lsdir = subprocess.Popen(["ls", "-la"], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    results, err = lsdir.communicate()
    print(results)

def setupInoDevEnv():
    gitpull = raw_input("Do you want to \">git pull\" the latest verson of this repository? [y/n]")
    if gitpull == 'y':
        git = subprocess.call(["git","pull"])
        testCommandReturn(git, "git pull")
    mv = subprocess.call(["mv",str(sys.argv[0]),".."])
    testCommandReturn(mv, "mv "+ str(sys.argv[0]) + " ..")
    listdir()
    rm = raw_input("Will delete all files (as above) in this dir (except this script). OK? [y/n]")
    if rm == 'y':
        files_to_delete = glob("*")
        for file_rm in files_to_delete:
            rm = subprocess.call(["rm","-rf", file_rm])
            testCommandReturn(rm, "rm -rf " +  file_rm)
        rm = subprocess.call(["rm","-r", ".build"])
    listdir()
    init = subprocess.call(["ino","init"])
    testCommandReturn(init, "ino init")
    rm = subprocess.call(["rm","src/sketch.ino"])
    testCommandReturn(rm, "rm src/sketch.ino")
    newInoConfig()
    mv = subprocess.call(["mv","../"+str(sys.argv[0]),"."])
    testCommandReturn(mv, "mv ../ " + str(sys.argv[0]) + " .")
    files_to_copy = glob("../arduino-openEMSstim/*")
    for file_cp in files_to_copy:
        cp = subprocess.call(["cp","../arduino-openEMSstim/" + file_cp, "src/"])
        testCommandReturn(cp, "cp ../arduino-openEMSstim/" + file_cp + " src/")
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
                return arduino_port, search_results
            elif a.find("tty.wchusb") > -1: 
                print("STATUS: Found arduino with non-FTDI driver")
                arduino_port = "/dev/" + str(a)
                return arduino_port, search_results
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
    curr_test = 0
    print("STATUS: Ready.")
    print("ACTION: Connect the first board now.")
    tests[curr_test] = str(curr_id)
    curr_test+=1
    while arduino_port is None:
        arduino_port, search_results = find_arduino_port()
        print arduino_port
        if arduino_port is None:
            print("STATUS: Could not find a arduino, here is what we found:") 
            print(search_results)
            choice = raw_input("Try again? (enter to continue, q to exit)")
            if choice == 'q':
                exit(0)
    print("STATUS: Board plugged to " + str(arduino_port))
    ready = raw_input("ACTION: Confirm? (type enter to continue)")
    if ready == "":
        print("BUILDING: software to include ID:" + str(curr_id))
        tests[curr_test] = arduino_port
        curr_test+=1
        change_line_to(code_filename, 20, text+"\"emsB"+str(curr_id)+"\"")
        print("UPLOADING: to board with ID:" + str(curr_id))
        deployOnBoard(arduino_port)       
    if testing == True:
        print("TESTING: executing python test on board ID: " + str(curr_id))
            
        #create a ems_device
        ems_device = openEMSstim.openEMSstim(arduino_port,19200)
        if ems_device: #check if was created properly
            print("STATUS: Device Found")
            print("TEST: stimulation to Channel 1, Intensity=0 (out of 100), Duration=2000 (2 seconds)")
            command_1 = ems_command(1,0,2000)
            ems_device.send(command_1)
            wait = None
            while wait != 'y' and wait != 'n':
                wait = raw_input("ACTION: Did you see the LED on this channel go ON? [y/n/r for repeat]")
                if wait == 'r':
                    ems_device.send(command_1)
                elif wait == 'y':
                    print("TEST/LOG/B"+str(curr_id)+": LED1 OK") #should change to log
                    tests[curr_test] = "OK"
                elif wait == 'n':
                    print("TEST/LOG/B"+str(curr_id)+": LED1 FAIL") #should change to log
                    tests[curr_test] = "FAIL"
            curr_test+=1
            print("TEST: stimulation to channel 1 at maximum (100) for 4 seconds and you will open the EMS slowly for that channel")
            #send direct writes using short commands
            command_1 = ems_command(1,100,4000)
            ems_device.send(command_1)
            wait = None
            while wait != 'y' and wait != 'n':
                wait = raw_input("ACTION: Did you feel the EMS on channel 1? [y/n/r for repeat]")
                if wait == 'r':
                    pass
                    ems_device.send(command_1)
                elif wait == 'y':
                    print("TEST/LOG/B"+str(curr_id)+": EMS1 OK") 
                    tests[curr_test] = "OK"
                elif wait == 'n':
                    print("TEST/LOG/B"+str(curr_id)+": EMS1 FAIL") 
                    tests[curr_test] = "FAIL"
            curr_test+=1

            print("TEST: LED & channel test: stimulation to Channel 2, Intensity=0 (out of 100), Duration=2000 (2 seconds)")
            command_2 = ems_command(1,0,2000)
            ems_device.send(command_2)
            wait = None
            while wait != 'y' and wait != 'n':
                wait = raw_input("ACTION: Did you see the LED on this channel go ON? [y/n/r for repeat]")
                if wait == 'r':
                    ems_device.send(command_2)
                elif wait == 'y':
                    print("TEST/LOG/B"+str(curr_id)+": LED2 OK") #should change to log
                    tests[curr_test] = "OK"
                elif wait == 'n':
                    print("TEST/LOG/B"+str(curr_id)+": LED2 FAIL") #should change to log
                    tests[curr_test] = "FAIL"
            curr_test+=1
            print("TEST: stimulation to channel 2 at maximum (100) for 4 seconds and you will open the EMS slowly for that channel")
            #send direct writes using short commands
            command_2 = ems_command(2,100,4000)
            ems_device.send(command_2)
            wait = None
            while wait != 'y' and wait != 'n':
                wait = raw_input("ACTION: Did you feel the EMS on channel 2? [y/n/r for repeat]")
                if wait == 'r':
                    pass
                    ems_device.send(command_2)
                elif wait == 'y':
                    print("TEST/LOG/B"+str(curr_id)+": EMS2 OK") 
                    tests[curr_test] = "OK"
                elif wait == 'n':
                    print("TEST/LOG/B"+str(curr_id)+": EMS2 FAIL") 
                    tests[curr_test] = "FAIL"
            curr_test+=1

            #now do both channels
            wait = raw_input("TEST: stimulation to Channel 1 + 2 (two commands) with Intensity=40 (out of 100), Duration=2000 (2 seconds). \"y\" to execute or \"n\" to pass ")
            if wait == 'y':
                command_1 = ems_command(1,40,2000)
                command_2 = ems_command(2,40,2000)
                ems_device.send(command_1)
                ems_device.send(command_2)
                sleep(2)
            ems_device.shutdown()

            #bluetooth
            bt = raw_input("Test the bluetooth now using the app. Did it work? [y/n]")
            if bt == 'y':
                print("TEST/LOG/B"+str(curr_id)+":BT OK") 
                tests[curr_test] = "OK"
            elif bt == 'n':
                print("TEST/LOG/B"+str(curr_id)+": BT FAIL") 
                tests[curr_test] = "FAIL"
            curr_test+=1

            case = raw_input("Does this board have a case for it? [y/n]")
            if case == 'y':
                print("TEST/LOG/B"+str(curr_id)+":BT OK") 
                tests[curr_test] = "OK"
            elif case == 'n':
                print("TEST/LOG/B"+str(curr_id)+": BT FAIL") 
                tests[curr_test] = "FAIL"
            curr_test+=1
            
            batt = raw_input("Test this board on battery + bluetooth app? [y/n]")
            if batt == 'y':
                print("TEST/LOG/B"+str(curr_id)+":BT OK") 
                tests[curr_test] = "OK"
            elif batt == 'n':
                print("TEST/LOG/B"+str(curr_id)+": BT FAIL") 
                tests[curr_test] = "FAIL"
            curr_test+=1
            obs = raw_input("Any observations about this board?")
            tests[curr_test] = str(obs)
            for a in tests:
                print a
            logger.info(','.join(tests))
            print("TEST:Done.")
    curr_id += 1
    board_no-=1
