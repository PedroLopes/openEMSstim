"use strict";
var socket = io();
var youtubePlayer
var onYouTubeIframeAPIReady;
var onPlayerReady;
var onPlayerStateChange;
var youtubeRecording = false;
var youtubePlaying = false;
var lastParamUpdated = 0;
var currentVideoId = ""

// setting
var youtubeSettings = {};
// output JSON to textarea
var parameters = [];
// iterator
var parametersSeekIndex = 0;

var signalArraySize = 200;
var signalArray = [];
for(var i = 0; i < signalArraySize; i++){
	signalArray.push(0)
}
var startDate = +new Date();
var visualizeFrequency = 7.0 //[Hz]

$(function () {
	/** test interface */
	var testEmitTimer;
	socket.on('remoteUI', function (data) {
		console.log('remoteUI', data);
		$("sing-strength").val(data.strength);
		targetStrength = data.strength;
		$("#sing-target-strength-label").text(data.strength);
	})
	$("#test-length").change(function () {
		$("#test-length-label").text($("#test-length").val())
	});
	$("#test-strength").change(function () {
		$("#test-strength-label").text($("#test-strength").val())
	})
	$("#test-submit").click(function () {
		console.log("submit")
		var afn = function () {
			socket.emit('test', {
			// add + for numeric value
				length: +$("#test-length").val(),
				strength: +$("#test-strength").val(),
				envelopeFunction: $("#test-envelope").val(),
				updateInterval: +$("#test-update-interval").val()
			});
	 	}
		if($("#test-repeat").prop('checked')){
			if(testEmitTimer) clearInterval(testEmitTimer);
			testEmitTimer = setInterval(afn, +Math.max($("#test-interval").val(), 50))
		}
		afn();
	});

	$("#test-repeat").change(function () {
		if($("#test-repeat").prop('checked')){
			// この状態でsubmitボタンを押すと繰り返し実行される
		}else{
			if(testEmitTimer) clearInterval(testEmitTimer);
		}
	})

	 /** sing interface */
	var singEmitTimer;
	var lastRecordedStrength = 0;

	(function (interval) {
		var emit = function () {
		 	// 録画
			if (youtubeRecording && youtubePlayer.getPlayerState() == 1 && lastRecordedStrength != currentStrength){
				parameters.push({
					timeStamp: Math.floor(youtubePlayer.getCurrentTime()*1000)/1000,
					event: "updateParam",
					strength: Math.round(currentStrength)
				})
				updateSettingJSON();
				lastRecordedStrength = currentStrength;
			}
			if (currentStrength > 0.5){
				socket.emit('test', {
					// add + for numeric value
					length: interval*2,
					strength: +Math.round(currentStrength),
					updateInterval: interval,
					envelopeFunction: "default",
				});
			}
		}
		setInterval(emit, interval);
	}(100));

	function vibratoOn(){
		console.log("vibratoOn")
	 	// 録画
		if (youtubeRecording && youtubePlayer.getPlayerState() == 1){
			parameters.push({
				timeStamp: Math.floor(youtubePlayer.getCurrentTime()*1000)/1000,
				event: "vibratoOn",
				strength: currentStrength
			})
			updateSettingJSON();
		}
	}
	function vibratoOff(){
		console.log("vibratoOff")
	 	// 録画
		if (youtubeRecording && youtubePlayer.getPlayerState() == 1){
			parameters.push({
				timeStamp: Math.floor(youtubePlayer.getCurrentTime()*1000)/1000,
				event: "vibratoOff"
			})
			updateSettingJSON();
		}
		targetStrength = 0;
	}
	
	$("#sing-vibrato").mousedown(vibratoOn).mouseup(vibratoOff)
	$("#sing-vibrato-toggle").click(function () {
		if(singEmitTimer) {
			vibratoOff();
		}else{
			vibratoOn();
		}
	})

	function updateParam(evt) {
		if (evt.type == "mousemove" && +new Date() - lastParamUpdated < 50){
			return
		}
		lastParamUpdated = +new Date();
	 	var strength = Math.round(evt.offsetX / $(evt.currentTarget).width() * 100)
	 	targetStrength = strength
	}
	var padMouseDownFlag = false
	
	$("#sing-target-strength").change(function () {
		targetStrength = $(this).val()
		$("#sing-target-strength-label").text(targetStrength)
	}).mousemove(function () {
		targetStrength = $(this).val()
		$("#sing-target-strength-label").text(targetStrength)
	});

	$("#sing-pad").mousedown(updateParam).mousedown(function (evt) {
		padMouseDownFlag = true
		updateParam(evt);
		vibratoOn(evt);
	}).mouseup(function (evt) {
		padMouseDownFlag = false
		updateParam(evt);
		vibratoOff(evt);
	}).mousemove(function (evt) {
		if(padMouseDownFlag){
			updateParam(evt);
		}
	}).mouseout(function (evt) {
		if(padMouseDownFlag){
			padMouseDownFlag = false;
			vibratoOff();
		}
	})

	/*
	 * +++++++++++++++++++++++++++++
	 * Youtube song interface
	 * +++++++++++++++++++++++++++++
	 */
    var playStartTimestamp = 0
    onYouTubeIframeAPIReady = function() {
    	youtubePlayer = new YT.Player('youtube-player', {
          videoId: '',
          events: {
            'onReady': onPlayerReady,
            'onStateChange': onPlayerStateChange
          }
        });
        console.log("Youtube iframe API Loaded");
    }
    onPlayerReady = function() {
    	console.log("Youtube iframe API Player Ready")
    }
	onPlayerStateChange = function() {
		if (youtubePlayer.getPlayerState() == 1){
			parametersSeekIndex = 0
			$("#youtube-record").attr("disabled", true);
			$("#youtube-play").attr("disabled", true);
		} else {
			parametersSeekIndex = 0
			$("#youtube-record").attr("disabled", false);
			$("#youtube-play").attr("disabled", false);			
		}
		saveParameters();
	};
    console.log("Youtube iframe API Loading");
    var tag = document.createElement('script');
    tag.src = "https://www.youtube.com/iframe_api";
    var firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
    var youtubePlayer = null;


    function updateSettingJSON(){
    	if(currentVideoId != ""){
	    	$("#youtube-settings").val(JSON.stringify(parameters));
    		youtubeSettings[currentVideoId] = parameters;
    	}
    }
    /*
     localStorageから設定を読み込み
     */
    function loadSettingsFromLocalStorage(){
	    if(window.localStorage && window.localStorage.youtubeSettings){
	    	youtubeSettings = JSON.parse(window.localStorage.youtubeSettings);
	    }
	    if(youtubeSettings.length){
	    	console.log("invalid settings: erased");
	    	youtubeSettings = {};
	    }
	    console.log("loadSettingsFromLocalStorage", localStorage.youtubeSettings, youtubeSettings)
	    updateSettingJSON();
    }
    loadSettingsFromLocalStorage();

	function UIUpdate(){
		// called every 50ms
		// var diff = 0
		// if(targetStrength > currentStrength){
		// 	if(currentStrength < 50){
		// 		diff = Math.min(15, targetStrength - currentStrength)
		// 	} else {
		// 		diff = Math.min(3, targetStrength - currentStrength)
		// 	}
		// }else if(targetStrength < currentStrength){ 
			// if(currentStrength > 50){
			// 	diff = -Math.min(3, currentStrength - targetStrength)
			// } else {
			// 	diff = -Math.min(15, currentStrength - targetStrength)
			// }
		// }
		var diff = targetStrength > currentStrength ? (targetStrength - currentStrength) / 2 : (targetStrength - currentStrength) / 4;
		currentStrength += Math.floor(diff);
		// $("#sing-strength").val(Math.round(currentStrength));
		$("#sing-strength-bar").css("width", Math.round(currentStrength) + "%");
	 	$("#sing-strength-label").text(Math.round(currentStrength));
	 	if(currentStrength < 70){
		 	$("#sing-strength-bar").removeClass("progress-bar-warning")
		 	$("#sing-strength-bar").removeClass("progress-bar-danger")
	 	}else if(currentStrength < 90){
		 	$("#sing-strength-bar").addClass("progress-bar-warning")
		 	$("#sing-strength-bar").removeClass("progress-bar-danger")
	 	}else{
		 	$("#sing-strength-bar").removeClass("progress-bar-warning")
		 	$("#sing-strength-bar").addClass("progress-bar-danger")
	 	}
		loadCurrentParameter();
	}
	// draw a plot of signal
	var canvas = document.getElementById('waveform-canvas');
	var context = canvas.getContext('2d');
	var width = canvas.width;
	var height = canvas.height;
	context.lineWidth = 6
	setInterval(function () {
		signalArray.pop();
		signalArray.unshift(Math.sin(Math.PI*2*(+new Date()-startDate)/200)*currentStrength/200);
	}, 10);
	setInterval(function() {
		context.clearRect(0, 0, width, height);
		context.strokeStyle = "#98AFC7"
		context.beginPath();     // パスを開始
		context.moveTo(0, signalArray[0]*height/2+height/2); // ペンを紙に付けずに移動
		for(var i = 0; i < signalArraySize; i++){
			context.lineTo(i/(signalArraySize-1)*width, signalArray[i]*height/2+height/2); // ペンを紙に付けて移動
		}
		context.stroke();        // パスに沿ってインクを流し込む（デフォルトでは1ピクセル幅）		
	}, 50)

	function loadCurrentParameter(){
		if (targetStrength > 0){
			$("#sing-pad").css("background-color", "lightgray");
		}else{
			$("#sing-pad").css("background-color", "white");
		}
		// update parameter if playing
		var setting;
		if (!youtubePlaying){
			// console.log("loadCurrentParameter: youtubeplaying flag is off");
			return
		}
		if (youtubePlayer.getPlayerState() != 1){
			// console.log("loadCurrentParameter: youtubePlayer.getPlayerState() is not playing state");
			return
		}
		if(setting = parameters[parametersSeekIndex]){
			if (setting.timeStamp > youtubePlayer.getCurrentTime()){
				// do nothing
				// console.log("loadCurrentParameter: no op");
				return
			}
			switch (setting.event) {
				case "vibratoOn":
					vibratoOn();
					break;
				case "vibratoOff":
					vibratoOff();
					break;
				case "updateParam":
					targetStrength = setting.strength
					currentStrength = setting.strength
					break;
				default:
					break;
			}
			// console.log("loadCurrentParameter:", parametersSeekIndex);
			parametersSeekIndex ++;
			loadCurrentParameter();
		}else{
			// console.log("loadCurrentParameter: Seekindex out of range", parametersSeekIndex)
		}
	}
	var loadParameterTimer = setInterval(UIUpdate, 70);

	$("#youtube-switch").children().each(function(e){
		console.log("each")
		$(this).click(function() {
			saveParameters();
			currentVideoId = $(this).attr("x-youtube-id");
			youtubePlayer.cueVideoById(currentVideoId, $(this).attr("x-youtube-playAt"));
			playStartTimestamp = +$(this).attr("x-youtube-playAt");
			if(youtubeSettings[currentVideoId]){
				parameters = youtubeSettings[currentVideoId];				
			}else{
				parameters = [];
			}
			updateSettingJSON();
		})
	});
	$("#youtube-new-song").click(function () {
		currentVideoId = prompt("input videoId");
		youtubePlayer.cueVideoById(currentVideoId);
		parameters = youtubeSettings[currentVideoId];
	})
	$("#youtube-record").click(function() {
		youtubePlayer.playVideo();
		youtubePlayer.seekTo(playStartTimestamp, true);
		youtubeRecording = true;
		youtubePlaying = false;
		lastRecordedStrength = 0;
	});
	$("#youtube-play").click(function() {
		youtubeRecording = false;		
		youtubePlaying = true;
		youtubePlayer.playVideo();
		youtubePlayer.seekTo(playStartTimestamp, true);
	});
	$("#youtube-stop").click(function() {
		youtubeRecording = false;		
		youtubePlaying = false;
		parametersSeekIndex = 0;
		youtubePlayer.stopVideo()
		vibratoOff();
	});
	$("#youtube-clear-settings").click(function(){
		if(confirm("Clear All Settings")){
			parameters = [];
			$("#youtube-settings").val("[]")
			localStorage.parameters = []
		}
	});
	$("#youtube-settings").change(function () {
		try{
			parameters = JSON.parse($("#youtube-settings").val());
		}catch(e){
			parameters = []
			alert("JSON parse failed")
		}
	})
	function saveParameters(){
		console.log(youtubeSettings);
		if(currentVideoId != ""){
			youtubeSettings[currentVideoId] = parameters
		}
		localStorage.youtubeSettings = JSON.stringify(youtubeSettings);
	}
	$(window).bind('beforeunload', function(event) {
		saveParameters();		
	});

	/*
	 * +++++++++++++++++++++++++++++
	 * Import / Export Settings
	 * +++++++++++++++++++++++++++++
	 */
	function saveFile(fileName, content) {
		console.log(fileName);
		if (window.navigator.msSaveBlob) {
			window.navigator.msSaveBlob(new Blob([content], { type: "text/plain" }), fileName);
		} else {
			var a = document.createElement("a");
			a.href = URL.createObjectURL(new Blob([content], { type: "text/plain" }));
			//a.target   = '_blank';
			a.download = fileName;
			document.body.appendChild(a) //  FireFox specification
			a.click();
			document.body.removeChild(a) //  FireFox specification
		}
	}
	$("#youtube-import-settings").click(function () {
		var settings = prompt("Paste settings file content:");
		try{
			var settings = JSON.parse(settings);
			youtubeSettings = settings;
			alert("Successfully Imported!")
		}catch(e){
			alert("invalid JSON")
		}

	});
	$("#youtube-export-settings").click(function () {
		saveFile("VibratomaticSettings.json", JSON.stringify(youtubeSettings));
	});
	/*
	 * +++++++++++++++++++++++++++++
	 * Force Vibrato
	 * +++++++++++++++++++++++++++++
	 */

	var targetStrength = 0
	var currentStrength = 0
	$('#sing-vibrato-force-button').pressure({
	  start: function(event){
	  	vibratoOn();
	  	targetStrength = 0
	  	$("#vibrato-force-bar").css("width", "0%")
	  },
	  end: function(){
	  	vibratoOff();
	  	targetStrength = 0
	  	$("#vibrato-force-bar").css("width", "0%")
	  },
	  startDeepPress: function(event){
	  },
	  endDeepPress: function(){
	  },
	  change: function(force, event){
	  	if (force > 0.5){
		  	targetStrength = Pressure.map(force, 0.5, 1, 95, 100);
	  	}else if(force > 0.1){
		  	targetStrength = Pressure.map(force, 0.1, 0.5, 50, 95);
	  	}else{
		  	targetStrength = Pressure.map(force, 0, 0.1, 0, 50);
	  	}
	  	$("#vibrato-force-bar").css("width", Math.round(targetStrength) + "%")
	  	$("#vibrato-force-bar").text(Math.round(targetStrength) + "%")
	    // this is called every time there is a change in pressure
	    // force will always be a value from 0 to 1 on mobile and desktop
	  },
	  unsupported: function(){
	    // this is called once there is a touch on the element and the device or browser does not support Force or 3D touch
	    $("#sing-force-pad").hide();
	  }
	});


})