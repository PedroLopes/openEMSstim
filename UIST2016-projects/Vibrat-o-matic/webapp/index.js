var express = require('express')
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
var SerialPort = require('serialport');
var osc = require('node-osc');

var defaultSerialPort = "/dev/tty.usbserial-A9O3R1XL"
var portName = process.argv.length > 2 ? process.argv[2] : defaultSerialPort;
var updateInterval = 30 // [msec]
var lastEmission = 0;

var oscServer = new osc.Server(3001, '0.0.0.0');
console.log("listening OSC on *:3001");
oscServer.on("message", function (msg, rinfo) {
      console.log("OSC message:", msg);
      if(msg[0][1] == 'on'){
      	console.log("OSC vibrato On")
		io.sockets.emit('remoteUI', {strength: 80});      	
      }else{
      	console.log("OSC vibrato Off")
		io.sockets.emit('remoteUI', {strength: 0}); 	
      }
});

// ensure f(0) = f(1) = 0
var envelopeFunction = {
	square: function(x) { return 1 },
	sin: function(x) { return Math.sin(x*Math.PI) },
	sin3: function(x) { return Math.pow(Math.sin(x*Math.PI), 3) },
	sinr3: function(x) { return Math.pow(Math.sin(x*Math.PI), 1/3) },
	sina: function(x) { return Math.sin(x*Math.PI)*1/2 + 1/2 },
}

app.use(express.static('static'));

app.get('/', function(req, res){
  res.sendFile('static/index.html');
});

io.on('connection', function(socket){
	console.log('a user connected');
	var updateIntervalTimer;
	var channelNo = 3;

	socket.on('remoteUI', function (data) {	
		console.log('remoteUI', data);
		socket.broadcast.emit('remoteUI', data);
	});

	socket.on('test', function (data) {
	var startDate = +(new Date())
	if (data.updateInterval) updateInterval = data.updateInterval

	if (data.envelopeFunction == 'default') {
		// default ならば
		send_command(channelNo, data.strength, data.length)
	}else{
		if (!envelopeFunction[data.envelopeFunction]){
			console.log("no such envelope function:", data.envelopeFunction)
			return
		}
		if (updateIntervalTimer){
			clearInterval(updateIntervalTimer)
		}
		setInterval(function (arguments) {
			var currentDate = +(new Date())
			var elapsedRatio = (currentDate - startDate) / data.length
			if (elapsedRatio < 0 || elapsedRatio > 1){
				clearInterval(updateIntervalTimer)
				return
			}
			var intensity = Math.round(envelopeFunction[data.envelopeFunction](elapsedRatio) * data.strength)
			send_command(channelNo, intensity, updateInterval)
		}, updateInterval)
	}

  });
});

http.listen(3000, function(){
  console.log('listening on *:3000');
});

var openEMSstim = new SerialPort(portName, {
   baudRate: 57600,
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
	if (!openEMSstim.isOpen()){
		console.log("openEMSstim: port is not open")
	}
	if (+new Date() - lastEmission < 80) {
		console.log("openEMSstim: emission rate is to high")
		return;
	}
	lastEmission = +new Date();
	var command = ""
	console.log("||||||||||||||||||||||||||||||".substring(0, Math.round(intensity / 3)), +new Date())
	if (is_numeric(channel) && is_numeric(intensity) && is_numeric(duration)) {
		if (channel == 1 || channel == 2 || channel == 3) {
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
		command += "\n"
		// console.log("sending: " + command);
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

