/*

MIT License

Copyright (c) 2016 Jeffrey R. Blum

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

import processing.serial.*;
import controlP5.*;

Serial myPort;

static boolean LOG_DATA_TO_FILE = true;

static int tempDelta = 6; //how many degrees should the two pads be apart?

static float MAX_TEMP_DELTA_YELLOW = 0.7;
static float MAX_TEMP_DELTA_RED = MAX_TEMP_DELTA_YELLOW * 2.0;

int temp_peltier_0 = 25;
int temp_peltier_1 = 25;

PrintWriter logfile;

String line;
boolean firstContact=false;
boolean tempChanged0=true; //send the initial values!
boolean tempChanged1=true; //send the initial values!
boolean zapChanged=true;

ControlP5 cp5;
int myColorBackground = color(0, 0, 0);

Slider p0slider, p1slider;
Button zapButton;
RadioButton trialRadio;
Textlabel P0_temp, P1_temp;
Button quit_button;

static int TEMP_BUF_SIZE = 3;
float[][] temp_buf = new float[2][TEMP_BUF_SIZE];
int[] temp_buf_curindex = new int[2];

float tempBufAddSample(int id, float val) {
  temp_buf[id][temp_buf_curindex[id]] = val;
  temp_buf_curindex[id] = (temp_buf_curindex[id]+1) % TEMP_BUF_SIZE;
  return tempBufAvg(id);
}
float tempBufAvg(int id) {
  float val=0.0;
  for(int i=0; i<TEMP_BUF_SIZE; i++) val += temp_buf[id][i];
  return val / float(TEMP_BUF_SIZE);
}

String curTime() {
  return "" + year() + String.format("%02d", month()) + String.format("%02d", day()) + "@" + String.format("%02d", hour()) + String.format("%02d", minute()) + String.format("%02d", second()) ;
}
void log(String msg, boolean printToConsole) {
  String line = curTime() + " " + msg;
  if(LOG_DATA_TO_FILE) logfile.println(line);
  if(printToConsole) println(line);
}

void lockControls(boolean state) {
  p0slider.setLock(state);
  p1slider.setLock(state);
  zapButton.setLock(state);
}

void startTrialNow(int trialNum, int t0, int t1, boolean zap){
  String sideString;
  if(t0>t1) sideString = "left";
  else sideString = "right";
  
  log("trial num " + trialNum + " t0 " + t0 + " t1 " + t1 + " zap " + zap + " hotter_side " + sideString, true);
  
  p0slider.setValue(t0);
  p1slider.setValue(t1);
  
  if(zap) zapButton.setOn();
  else zapButton.setOff();
  
  tempChanged0=true;
  tempChanged1=true;
  zapChanged=true;
}

void quit() {
  myPort.write(byte(102)); //tell the arduino to power down all the pins
  log("quit_gui", true);
  logfile.flush();
  exit();
}

String curCorrectSide = "none";
void startTrial(int trialNum) {
  lockControls(true); //will be reset to false if on manual control...
  int lowtemp;
  switch(trialNum) {
    case 0:
      lockControls(false);
      //switch to manual control
      startTrialNow(0, 20, 20, false);
      curCorrectSide = "none";
      break;
    case 1:
      lowtemp = 30;
      startTrialNow(1, lowtemp,               lowtemp+tempDelta, false);
      curCorrectSide = "right";
      break;
    case 2:
      lowtemp = 34;
      startTrialNow(2, lowtemp+tempDelta,     lowtemp, false);
      curCorrectSide = "left";
      break;
    case 3:
      lowtemp = 36;
      startTrialNow(3, lowtemp+tempDelta,     lowtemp, false);
      curCorrectSide = "left";
      break;
    case 4:
      lowtemp = 39; //40
      startTrialNow(4, lowtemp,               lowtemp+tempDelta, false);
      curCorrectSide = "right";
      break;
    case 5:
      lowtemp = 42; //44
      startTrialNow(5, lowtemp+tempDelta,     lowtemp, false);
      curCorrectSide = "left";
      break;
    case 6:
      lowtemp = 42; //44
      startTrialNow(6, lowtemp+tempDelta,     lowtemp, true);
      curCorrectSide = "left";
      break;
  }
}

void startNextTrial() {
  int curTrial = getCurTrialNum();
  if(curTrial == 0) return; //if on manual, don't move into trials!
  if(curTrial == 6) return; //curTrial=0; //if at last trial, don't jump off the end, revert to manual.
  else curTrial++;
  
  trialRadio.activate(curTrial); //only way it is set outside of clicking it yourself...
  //startTrial(curTrial);
}

void settings() { 
  size(600, 500); //Defines the screen size
}

// https://learn.sparkfun.com/tutorials/connecting-arduino-to-processing
void setup()
{
  logfile = createWriter("logs/log" + curTime() + ".log");
  
  String portName;
  try{
    portName = "/dev/ttyUSB0";
    myPort = new Serial(this, portName, 115200);
  }
  catch(Exception e) {
    portName = "/dev/ttyUSB1";
    myPort = new Serial(this, portName, 115200);
  }
  
  myPort.bufferUntil('\n'); 

  cp5 = new ControlP5(this);
  //controlP5.addSlider("P0",0,100,50,50,50,10,100);
  p0slider = cp5.addSlider("P0")
    .setPosition(100,100)
    .setSize(20, 300)
    .setRange(20, 55)
    .setValue(temp_peltier_0)
    .setNumberOfTickMarks(36)
    //.setSliderMode(Slider.FLEXIBLE) 
    ;
  P0_temp = cp5.addTextlabel("P0_temp")
    .setPosition(70, 30)
    .setSize(50, 30)
    .setText("??.??")
    .setBroadcast(false)
    .setFont(createFont("Courier", 30))
    .setColorValue(color(255,0,0))
    ;
  p1slider = cp5.addSlider("P1")
    .setPosition(200,100)
    .setSize(20, 300)
    .setRange(20, 55)
    .setValue(temp_peltier_1)
    .setNumberOfTickMarks(36)
    //.setSliderMode(Slider.FLEXIBLE) 
    ;
  P1_temp = cp5.addTextlabel("P1_temp")
    .setPosition(170,30)
    .setSize(50, 30)
    .setText("??.??")
    .setBroadcast(false)
    .setFont(createFont("Courier", 30))
    .setColorValue(color(255,0,0))
    ;

  zapButton = cp5.addButton("zap")
    .setPosition(300,50)
    .setSize(50,25)
    .setSwitch(true)
    .setOff()
    ;
    
  trialRadio = cp5.addRadioButton("trials")
    .setPosition(300,100)
    .setSize(50,40)
    .setNoneSelectedAllowed(false)
    .addItem("Manual", 0)
    .addItem("Trial1", 1)
    .addItem("Trial2", 2)
    .addItem("Trial3", 3)
    .addItem("Trial4", 4)
    .addItem("Trial5", 5)
    .addItem("Trial6", 6)
    .activate(0)
    ;
   
  quit_button = cp5.addButton("quit")
    .setPosition(10,10)
    .setSize(40,20)
    ;
}

//this is nuts... why can't 
int getCurTrialNum() {
  if     (trialRadio.getState(0)) return 0;
  else if(trialRadio.getState(1)) return 1;
  else if(trialRadio.getState(2)) return 2;
  else if(trialRadio.getState(3)) return 3;
  else if(trialRadio.getState(4)) return 4;
  else if(trialRadio.getState(5)) return 5;
  else if(trialRadio.getState(6)) return 6;
  else return 0; //error
}

void controlEvent(ControlEvent theEvent) {
  if(theEvent.isGroup()) {
    if(theEvent.isFrom(trialRadio)) {
      startTrial(getCurTrialNum());
    }
  }

  if (theEvent.isController()) {
    //println("got a control event from controller with id "+theEvent.getController().getId() + " val " + (int)(theEvent.getController().getValue()));
    
    if (theEvent.isFrom(cp5.getController("P0"))) {
      temp_peltier_0 = (int)(theEvent.getController().getValue());
      tempChanged0 = true;
    }
    if (theEvent.isFrom(cp5.getController("P1"))) {
      temp_peltier_1 = (int)(theEvent.getController().getValue());
      tempChanged1 = true;
    }
    if (theEvent.isFrom(zapButton)) {
      zapChanged = true;
    }
    if(theEvent.isFrom(quit_button)) quit();
  }
}

void draw()
{
  background(myColorBackground);
}

void serialEvent(Serial myPort) {
  //put the incoming data into a String - 
  //the '\n' is our end delimiter indicating the end of a complete packet
  line = myPort.readStringUntil('\n');
  //make sure our data isn't empty before continuing
  if (line != null) {
    //trim whitespace and formatting characters (like carriage return)
    line = trim(line);
    String[] vals = split(line, ' ');

    /*
     *    RECEIVING VALUES FROM ARDUINO
     */
    if (line.charAt(0)=='t' && line.charAt(1)==' ') { // new temperature values from arduino
      if (P0_temp != null) { //make sure controls have been created...
        float temp, tdiff, smoothedTemp;
        
        temp = float(vals[2]);
        smoothedTemp = tempBufAddSample(0, temp);
        tdiff = smoothedTemp-temp_peltier_0;
        P0_temp.setText(String.format("%.1f\n%+.1f", smoothedTemp, tdiff));
        tdiff = abs(tdiff);
        if(tdiff<MAX_TEMP_DELTA_YELLOW) P0_temp.setColorValue(color(0,255,0));
        else if(tdiff<MAX_TEMP_DELTA_RED) P0_temp.setColorValue(color(255,255,0));
        else P0_temp.setColorValue(color(255,0,0));
        
        temp = float(vals[4]);
        smoothedTemp = tempBufAddSample(1, temp);
        tdiff = smoothedTemp-temp_peltier_1;
        P1_temp.setText(String.format("%.1f\n%+.1f", smoothedTemp, tdiff));
        tdiff=abs(tdiff);
        if(tdiff<MAX_TEMP_DELTA_YELLOW) P1_temp.setColorValue(color(0,255,0));
        else if(tdiff<MAX_TEMP_DELTA_RED) P1_temp.setColorValue(color(255,255,0));
        else P1_temp.setColorValue(color(255,0,0));
      }
      log(line, false);
    }
    else if (vals[0].equals("button")) { // user pressed button to choose hotter side - move to next trial
      log(line + " trial_number " + getCurTrialNum() + " hotter_side_correct " + curCorrectSide, true);
      startNextTrial();
    } else {
      log(line, true);
    }

    /*
     *    SENDING VALUES TO ARDUINO
     */
    //look for our 'z' string to start the handshake
    //if it's there, clear the buffer, and send a request for data
    if (firstContact == false) {
      if (line.equals("z")) {
        myPort.clear();
        firstContact = true;
        myPort.write("z");
        log("contact", true);
      }
    } else { //if we've already established contact, keep getting and parsing data
      if (tempChanged0) {
        myPort.write(byte(temp_peltier_0));
        tempChanged0=false;
        log("request_temp P0 " + byte(temp_peltier_0), true);
      }
      if (tempChanged1) {
        myPort.write(byte(0-temp_peltier_1));
        tempChanged1=false;
        log("request_temp P1 " + byte(temp_peltier_1), true);
      }
      if (zapChanged) {
        boolean state = zapButton.getBooleanValue();
        if(state) myPort.write(byte(101));
        else myPort.write(byte(100));
        
        zapChanged=false;
        log("request_zap zap " + byte(state), true);
      }
    }
  }
}