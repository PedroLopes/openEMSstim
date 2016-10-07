var SerialPort = require('serialport');// include the library
   // get port name from the command line:
var portName = "/dev/tty.wchusbserial1410";

var openEMSstim = new SerialPort(portName, {
   baudRate: 19200,
   parser: SerialPort.parsers.readline("\n")
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

setTimeout(function(){
	setInterval(function(){
		open_channels(0);
	}, 2 * 1000);  
	setTimeout(function(){
		setInterval(function(){
			open_channels(1);
		}, 2 * 1000); 
	}, 1 * 1000);	
}, 20 * 1000);  

function open_channels(channel) {
	console.log("Will write to openEMSstim: C" + channel + "I100T1000G");
	openEMSstim.write("C" + channel + "I100T1000G");
}

