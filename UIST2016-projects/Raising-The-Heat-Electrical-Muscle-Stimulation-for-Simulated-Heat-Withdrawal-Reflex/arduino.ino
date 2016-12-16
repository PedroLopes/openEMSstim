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

#define DISABLE_ALL_SAFETY_CHECKS   false  //for when nothing is connected to arduino, for testing only
#define DISABLE_EXTRA_SAFETY_CHECKS true   //enable these checks when testing new peltier code, to avoid voltage reversals, etc.

#define USE_SMOOTHED_TEMPERATURE false     //if temperature readings are 

#define PELTIER_INITIAL_TEMPERATURE 20.0
#define MAX_TEMP 50.0 //safety maximum temperature - won't allow requests for temperatures above this.
#define SHUTDOWN_MAX_TEMP 55.0 //exits the app if ever sees a temperature this high on a peltier
#define SHUTDOWN_MIN_TEMP 15.0 //if gets too cold, either temp sensor disconnected, or maybe peltier voltage is reversed!

#define MIN_MS_BETWEEN_SHOCKS 3000 //only allow a shock once every this many ms, to avoid repeated shocks (switch debouncing)
#define HOTTER_SWITCHES_TIMEOUT 5000   //wait this number of ms before allowing another switch press (debounce)
#define MS_AFTER_SWITCH_BEFORE_SHOCK 15 // delay a bit after user touches pad so they perceive that they really "felt" the pad
#define MAX_SMOOTHED_TEMP_DELTA 2.0
#define MINIMUM_PELTIER_ON_OFF_MS 100

#define MIN_DEGREES_PER_SECOND 0.3 // safety check - if peltier not changing at least this fast, there is a problem
#define MAX_STABLE_TEMPERATURE_FLUCTUATION 2.0 //when peltier reaches its temperature, what is the max expected fluctuation?


//note that if all are disabled, app will run self test then exit
#define ENABLE_TENS     1
#define ENABLE_PELTIER0 1
#define ENABLE_PELTIER1 1

#define TENS_STIMULUS_MS 80 //how many ms should each shock last?

//Define all the arduino pins

#define PIN_ONBOARD_LED               13

#define PIN_TENS_MOSFET               2
#define PIN_USER_TOUCHED_SWITCH_STATE 6 //the physical switch below the peltier
#define PIN_USER_CHOOSE_LEFT          A0
#define PIN_USER_CHOOSE_RIGHT         A1

#define PIN_PELTIER_0_TEMPERATURE     A3
#define PIN_PELTIER_0_MOSFET          7
#define PIN_PELTIER_1_TEMPERATURE     A4
#define PIN_PELTIER_1_MOSFET          4

//What should the onboard arduino LED light up for? (choose ONE of the below)
#define LED_TENS            0   //whenever the TENS is on, light the LED
#define LED_PELTIER0        0
#define LED_PELTIER1        0
#define LED_EITHER_PELTIER  1

char buf[32]; //small character buffer for sprintf
unsigned long lastUserSideChoiceTime = 0; //for debouncing hotter side choice switches
boolean guiAllowShock = false; //did the GUI say it is ok to shock?

struct Peltier {
  double targetTemp;
  int pinPeltierTemperature, pinPeltierMOSFET;

  //for safety, track rate of change and whether it makes sense. If not, shut down for hardware issue?
  unsigned long lastSetTempRequestTime; //when was the peltier last requested to change its temperature?
  double initialTemperature, curTemp, curTempSmoothed;
  boolean isHeating;
};
Peltier peltier[2];

unsigned long lastTensSwitchTriggerTime = 0; //time when TENS last triggered, for computing timeout

//Turn all pins off to make sure everything is shut down
void everythingOff() {
  digitalWrite(PIN_TENS_MOSFET, LOW);
  digitalWrite(PIN_PELTIER_0_MOSFET, LOW);
  digitalWrite(PIN_PELTIER_1_MOSFET, LOW);
  digitalWrite(PIN_ONBOARD_LED, LOW);
}

void emergencyStop() {
  everythingOff();
  exit(-1); //leaves pins in previous state according to
  //   http://arduino.stackexchange.com/questions/188/what-happens-when-i-call-exit-from-my-program
}

//quit with a message, usually due to some parameter going out of line
void quit(const char *msg) {
  everythingOff(); //yes, this gets called again in emergencyStop, but we want this to happen asap.
  Serial.println(msg);
  Serial.flush();
  emergencyStop();
}

//convert arduino pin value to degrees for the temperature sensor
float pinToDegrees(int pinVal) {
  //float volts = pinVal * (5.0 / 1023.0); //5.0 is without the analogReference(INTERNAL) in setup, which is more coarse
  float volts = pinVal * (1.1 / 1023.0);
  return (volts * 1000 / 10); // 10mV/degree
}

//return the temperature of the specified peltier, depending on whether smoothed readings are being used.
float getCurTemp(int id) {
  if (USE_SMOOTHED_TEMPERATURE) return peltier[id].curTempSmoothed;
  else return peltier[id].curTemp;
}

//read the temperature pin to find out the current temperature of specified peltier
float updateTemperature(int id) {
  float newTemp = pinToDegrees(analogRead(peltier[id].pinPeltierTemperature));

  //compute smoothed temperature
  float tempDelta = newTemp - peltier[id].curTempSmoothed; //positive if temp is rising
  if (fabs(tempDelta) > MAX_SMOOTHED_TEMP_DELTA) {
    if (tempDelta > 0) peltier[id].curTempSmoothed += MAX_SMOOTHED_TEMP_DELTA;
    else peltier[id].curTempSmoothed -= MAX_SMOOTHED_TEMP_DELTA;
  }
  else {
    peltier[id].curTempSmoothed = newTemp;
  }

  peltier[id].curTemp = newTemp;

  return getCurTemp(id);
}

/*
  //unused - convert degrees to a pinvalue
  int degreesToPinval(float degreesC) {
  float volts = degreesC/100.0;
  return (int)((volts/(5.0 / 1023.0)) + 0.5); //0.5 for rounding since int cast floors
  }
*/

//run some initial self tests to make sure everything makes sense
int selfTest() {
  //if(fabs(pinToDegrees(degreesToPinval(30.0))-30.0) > 0.25) quit("pinToDegrees test failed");
  //if(degreesToPinval(pinToDegrees(55)) != 55) quit("pinToDegrees test failed");

  if (ENABLE_PELTIER0) peltierSafetyCheck(0);
  if (ENABLE_PELTIER1) peltierSafetyCheck(1);

  Serial.println("self_test status ok");
  if (!ENABLE_TENS && !ENABLE_PELTIER0 && !ENABLE_PELTIER1) {
    quit("self_test_only_exiting");
  }
}

//send current temperatures via serial
void printTemps() {
  // yuk no %f in sprintf - http://yaab-arduino.blogspot.ca/2015/12/how-to-sprintf-float-with-arduino.html
  sprintf(buf, "t p0 %d.%02d p1 %d.%02d", (int)getCurTemp(0), (int)(getCurTemp(0) * 100) % 100,  (int)getCurTemp(1), (int)(getCurTemp(1) * 100) % 100);
  Serial.println(buf);
}

//make sure peltier is within reasonable parameters for safety (of peltier and human!)
void peltierSafetyCheck(int id) {
  float degC = getCurTemp(id);

  if (DISABLE_ALL_SAFETY_CHECKS) return;

  //Check for min/max temps
  if (degC > SHUTDOWN_MAX_TEMP) {
    quit("SHUTDOWN_MAX_TEMP reached! Panic!");
  }
  if (degC < SHUTDOWN_MIN_TEMP) {
    quit("SHUTDOWN_MIN_TEMP reached! Panic!");
  }

  //the remaining checks are useful for debugging, when changing the peltier code, to be extra careful
  if (DISABLE_EXTRA_SAFETY_CHECKS) return;

  // now do checks for whether the peltiers are doing reasonable things
  float tempDelta = degC - peltier[id].initialTemperature;
  if (fabs(degC - peltier[id].targetTemp) > MAX_STABLE_TEMPERATURE_FLUCTUATION) { // if we're more than max fluctuation away from target temperature
    // first check if it is moving in the wrong direction from the initial value
    //  (note this will not cause a fail if it overshoots in the right direction)
    //  should catch if voltage on peltier is reversed, which could otherwise blow the peltier
    if (fabs(tempDelta) > 1.0) { //only if it has actually started moving off its baseline...
      if ((tempDelta < 0) != (peltier[id].initialTemperature > peltier[id].targetTemp)) { //if cooling when should be heating (or opposite)
        if (tempDelta < 0) { //ignore falling temperature since we can't reverse voltage anyway
          quit("Peltier temperature moving in wrong direction!");
        }
      }
    }

    //Now see how fast the temperature is changing
    // If it is not responding to the temperature control, maybe the temperature sensor is borked, and not updating!
    // Worst case, we don't want to drive the peltiers to meltdown since we think they still need to be heated up...
    unsigned long elapsedMS = millis() - peltier[id].lastSetTempRequestTime;
    float degPerSec = tempDelta / (elapsedMS / 1000.0);

    if (elapsedMS > 3000 && fabs(degPerSec) < MIN_DEGREES_PER_SECOND) { //give it a few seconds to try, then expect results...
      quit("Peltier not changing temperature fast enough. Hardware fault?");
    }
  }
}

unsigned long lastPeltierSwitchTime = 0;

//decide whether to turn peltier on, and do so if necessary
void peltierHeatDecide(int id) {
  double curTemp = getCurTemp(id);
  if (curTemp < peltier[id].targetTemp) {
    peltierHeat(id, true);
  }
  else {
    peltierHeat(id, false);
  }
}

// set peltier to heating or not (depends on state parameter)
void peltierHeat(int id, boolean state) {
  peltier[id].isHeating = state;

  if (state) {
    digitalWrite(peltier[id].pinPeltierMOSFET, HIGH);

    if (LED_EITHER_PELTIER)           digitalWrite(PIN_ONBOARD_LED, HIGH);
    else if (LED_PELTIER0 && id == 0) digitalWrite(PIN_ONBOARD_LED, HIGH);
    else if (LED_PELTIER1 && id == 1) digitalWrite(PIN_ONBOARD_LED, HIGH);
  }
  else {
    digitalWrite(peltier[id].pinPeltierMOSFET, LOW);

    if (LED_EITHER_PELTIER && (!peltier[0].isHeating && !peltier[1].isHeating)) digitalWrite(PIN_ONBOARD_LED, LOW);
    else if (LED_PELTIER0 && id == 0) digitalWrite(PIN_ONBOARD_LED, LOW);
    else if (LED_PELTIER1 && id == 1) digitalWrite(PIN_ONBOARD_LED, LOW);
  }
}

//each cycle, see if we are done with one on/off cycle, and if so, read the current temperature.
//Note that temperature is only read when neither peltier is heating, to avoid noise issues.
void peltierLoop() {
  unsigned long now = millis();
  unsigned long elapsedMS = now - lastPeltierSwitchTime;

  if (elapsedMS > MINIMUM_PELTIER_ON_OFF_MS) {
    lastPeltierSwitchTime = millis();

    //turn off peltiers so can get clean temperature readings
    peltierHeat(0, false);
    peltierHeat(1, false);

    delay(1); //let mosfets settle, to make sure they are not pulling current, which would cause noise
    updateTemperature(0);
    updateTemperature(1);
    printTemps();

    //now that we know current temperature, safety check peltiers each time we get a new temperature reading...
    peltierSafetyCheck(0);
    peltierSafetyCheck(1);

    if (ENABLE_PELTIER0) peltierHeatDecide(0);
    if (ENABLE_PELTIER1) peltierHeatDecide(1);
  }
}
 
void setPeltierTemperature(int id, int degreesC) {
  if (degreesC > MAX_TEMP) {
    //Serial.print("Requested temperature %d too high. Setting to MAX_TEMP of %d.", degreesC, MAX_TEMP);
    Serial.println("error_requested_temp_too_high");
    degreesC = MAX_TEMP;
  }
  peltier[id].targetTemp = degreesC;

  //set safety check variables
  peltier[id].lastSetTempRequestTime = millis();
  peltier[id].initialTemperature = getCurTemp(id);
}

//we don't want to continuously shock or allow multiple too fast, so there is a timeout between shocks
bool okToShock() {
  if (!guiAllowShock) return false;

  if (millis() - lastTensSwitchTriggerTime > MIN_MS_BETWEEN_SHOCKS) return true;
  else return false;
}

//keep checking to see if user pressed peltier down, activating the TENS switch
void tensLoop() {
  int switchState = !digitalRead(PIN_USER_TOUCHED_SWITCH_STATE);
  if ( switchState == HIGH ) {
    //Serial.println("tens_switch_triggered");
    if(okToShock()) {
      Serial.println("shock");
      delay(MS_AFTER_SWITCH_BEFORE_SHOCK);
      if (LED_TENS) digitalWrite(PIN_ONBOARD_LED, HIGH);
      digitalWrite(PIN_TENS_MOSFET, HIGH);
      delay(TENS_STIMULUS_MS);
      digitalWrite(PIN_TENS_MOSFET, LOW);
      if (LED_TENS) digitalWrite(PIN_ONBOARD_LED, LOW);
    }
    lastTensSwitchTriggerTime = millis(); //just in case button press makes switch connect, this avoids an extra shock
  }
}

//keep checking to see if the hotter side switches have been pushed
void switchesLoop() {
  if (millis() - lastUserSideChoiceTime > HOTTER_SWITCHES_TIMEOUT) {
    if ( !digitalRead(PIN_USER_CHOOSE_LEFT)  == HIGH ) {
      Serial.println("button hotter_side_selected left");
      lastUserSideChoiceTime = millis();
    }
    if ( !digitalRead(PIN_USER_CHOOSE_RIGHT) == HIGH ) {
      Serial.println("button hotter_side_selected right");
      lastUserSideChoiceTime = millis();
    }
  }
}

void setup() {
  Serial.begin(115200);
  establishContact(); //block until we hear from the GUI...we don't want to do anything until GUI is active!

  Serial.println("setup_start");

  if (DISABLE_ALL_SAFETY_CHECKS) {
    Serial.println("all_safety_disabled");
  }

  if (DISABLE_EXTRA_SAFETY_CHECKS) {
    Serial.println("extra_safety_disabled");
  }

  pinMode(PIN_ONBOARD_LED, OUTPUT);

  analogReference(INTERNAL);

  memset(&peltier[0], 0, sizeof(peltier[0])); //set everything to zero...
  peltier[0].pinPeltierTemperature = PIN_PELTIER_0_TEMPERATURE;
  peltier[0].pinPeltierMOSFET      = PIN_PELTIER_0_MOSFET;
  pinMode(PIN_PELTIER_0_MOSFET, OUTPUT);
  peltier[0].curTempSmoothed = pinToDegrees(analogRead(peltier[0].pinPeltierTemperature));
  updateTemperature(0);

  memset(&peltier[1], 0, sizeof(peltier[1])); //set everything to zero...
  peltier[1].pinPeltierTemperature = PIN_PELTIER_1_TEMPERATURE;
  peltier[1].pinPeltierMOSFET      = PIN_PELTIER_1_MOSFET;
  pinMode(PIN_PELTIER_1_MOSFET, OUTPUT);
  peltier[1].curTempSmoothed = pinToDegrees(analogRead(peltier[1].pinPeltierTemperature));
  updateTemperature(1);

  //Get the peltiers starting to warm up
  if (ENABLE_PELTIER0) {
    setPeltierTemperature(0, PELTIER_INITIAL_TEMPERATURE);
  }
  if (ENABLE_PELTIER1) {
    setPeltierTemperature(1, PELTIER_INITIAL_TEMPERATURE);
  }

  if (ENABLE_TENS) {
    //TENS control setup
    pinMode(PIN_TENS_MOSFET, OUTPUT);
    pinMode(PIN_USER_TOUCHED_SWITCH_STATE, INPUT_PULLUP);
  }

  // buttons for user to say which is hotter
  pinMode(PIN_USER_CHOOSE_LEFT,  INPUT_PULLUP);
  pinMode(PIN_USER_CHOOSE_RIGHT, INPUT_PULLUP);

  //just to be 100% sure, pull all output pins low to make sure nothing being passed
  everythingOff();

  selfTest(); //has to be after above setup since might rely on those variables...

  Serial.println("setup_done");
}


void loop() {
  //Do any serial communications...and clear them all before allowing anything else to happen
  if (Serial.available() > 0) { // If data is available to read,
    char val = Serial.read(); // read it and store it in val

    //sprintf(buf, "val received : %i", val);
    //Serial.println(buf);
    if      (val == 122) return; //'z' for handshake..don't try and set that as temperature
    else if (val == 100) guiAllowShock = false;
    else if (val == 101) guiAllowShock = true;
    else if (val == 102) quit("quit_from_gui");
    else if (val >=   0) setPeltierTemperature(0, val);
    else if (val <    0) setPeltierTemperature(1, 0 - val);
  }
  else {
    //trigger the TENS when user hits switch
    if (ENABLE_TENS) {
      tensLoop();
    }

    peltierLoop();

    switchesLoop();
  }
}

//initial handshake indicating the GUI is running, after which we can start doing other things.
void establishContact() {
  while (Serial.available() <= 0) {
    Serial.println('z');   // send a z for handshake
    delay(300);
  }
}


