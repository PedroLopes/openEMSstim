var express   =     require("express");
var bodyParser  =    require("body-parser");
var app       =     express();
app.use(bodyParser.urlencoded({ extended: false }));
var serialport = require('serialport');// include the library
   SerialPort = serialport.SerialPort; // make a local instance of it
   // get port name from the command line:
   portName = "/dev/tty.wchusbserial1410";


app.get('/',function(req,res){
  res.sendfile("index.html");
});

app.post('/ems',function(req,res){
  var channel=req.body.channel;
  var intensity=req.body.intensity;
  var duration=req.body.duration;
  console.log("From Client POST request: channel = "+ channel +", intensity = " + intensity + " duration = " + duration);
  send_command(channel,intensity,duration);
  res.end("yes");
});

app.listen(3000,function(){
  console.log("Started server on PORT 3000, please open in your browser: http://localhost:3000/");
})

var openEMSstim = new SerialPort(portName, {
   baudRate: 19200,
   parser: serialport.parsers.readline("\n")
 });

openEMSstim.on('open', showPortOpen);
openEMSstim.on('data', sendSerialData);
openEMSstim.on('close', showPortClose);
openEMSstim.on('error', showError);

function showPortOpen() {
	   console.log('port open. Data rate: ' + openEMSstim.options.baudRate);
}
 
function sendSerialData(data) {
	   console.log(data);
}
 
function showPortClose() {
	   console.log('port closed.');
}
 
function showError(error) {
	   console.log('Serial port error: ' + error);
}

function send_command(channel, intensity, duration) {
	var command = ""
	if (is_numeric(channel) && is_numeric(intensity) && is_numeric(duration)) {
		if (channel == 1 || channel == 2) {
			command = "C" + (channel -1);
		} else {
			console.log("Command malformatted");
			return null;
		}
		if (intensity >= 0 && intensity <= 100) {
			command += "I" + intensity; 
		} else {
			console.log("Command malformatted");
			return null;
		}
		if (duration >= 0) {
			command += "T" + duration + "G";
		} else {
			console.log("Command malformatted");
			return null;
		}
		console.log("sending: " + command);
		openEMSstim.write(command);
	} else {
		console.log("Command malformatted");
		return null;
	}
}


function is_numeric(str){
	if (!isNaN(parseInt(str, 10))) return true;
	else return false;
}
