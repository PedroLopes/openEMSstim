var express   =     require("express");
var bodyParser  =    require("body-parser");
var app       =     express();
app.use(bodyParser.urlencoded({ extended: false }));

app.get('/',function(req,res){
  res.sendFile("index.html");
});

app.post('/login',function(req,res){
  var channel=req.body.channel;
  var intensity=req.body.intensity;
  var duration=req.body.duration;
  console.log("From Client POST request: channel = "+ channel +", intensity = " + intensity + " duration = " + duration);
  res.end("yes");
});

app.listen(3000,function(){
  console.log("Started server on PORT 3000, please open in your browser: http://localhost:3000/");
})
