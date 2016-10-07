var express = require("express");
var bodyParser = require("body-parser");
var SerialPort = require('serialport');
   portName = "/dev/tty.wchusbserial14210";
var app = express();
app.use(bodyParser.urlencoded({ extended: false }));

app.get('/',function(req,res){
  res.sendfile("index.html");
});

app.get('/advanced',function(req,res){
  res.sendfile("editor.html");
});

app.post('/ems',function(req,res){
  var channel=req.body.channel;
  var intensity=req.body.intensity;
  var duration=req.body.duration;
  if (!duration) duration = 1000;
  console.log("From Client POST request: channel = "+ channel +", intensity = " + intensity + " duration = " + duration);
  send_command(channel,intensity,duration);
  res.end("yes");
});

app.listen(3000,function(){
  console.log("Started server on PORT 3000, please open in your browser: http://localhost:3000/");
})

var openEMSstim = new SerialPort(portName, {
   baudRate: 19200,
   parser: SerialPort.parsers.readline("\n")
 });

// Register functions for handling the communication with openEMSstim
openEMSstim.on('open', showPortOpen);
openEMSstim.on('data', sendSerialData);
openEMSstim.on('close', showPortClose);
openEMSstim.on('error', showError);

function showPortOpen() {
	   console.log('openEMSstim connected to serial port at data rate: ' + openEMSstim.options.baudRate);
}
 
function sendSerialData(data) {
	   console.log(data);
}
 
function showPortClose() {
	   console.log('openEMSstim connection to serial port closed.');
}
 
function showError(error) {
	   console.log('Serial port error: ' + error);
}

//First validates a command, then if valid, sends it to the openEMSstim
function send_command(channel, intensity, duration) {
	var command = ""
	if (is_numeric(channel) && is_numeric(intensity) && is_numeric(duration)) {
		if (channel == 1 || channel == 2) {
			command = "C" + (channel -1);
		} else {
			console.log("ERROR: Command malformatted, will not send to openEMSstim");
			return null;
		}
		if (intensity >= 0 && intensity <= 100) {
			command += "I" + intensity; 
		} else {
			console.log("ERROR: Command malformatted, will not send to openEMSstim");
			return null;
		}
		if (duration >= 0) {
			command += "T" + duration + "G";
		} else {
			console.log("ERROR: Command malformatted, will not send to openEMSstim");
		}
		console.log("sending: " + command);
		openEMSstim.write(command);
	} else {
		console.log("ERROR: Command malformatted, will not send to openEMSstim");
		return null;
	}
}

function is_numeric(str){
	if (!isNaN(parseInt(str, 10))) return true;
	else return false;
}
