class Message {
  String say;
  int time;
  int maxTime = 50;
  
  Message() {
    say = "";
    time = 0;
  }
  
  void set(String _say) {
    time = maxTime;
    say = _say;
  }
  
  void draw() {
    if ((say != "") && (time > 0)) {
      float a = 255 - ((maxTime - time) * 5);
      stroke(255,255,255,a); fill(255,255,255,a);
      text(say, width/2 - (say.length() * 18)/2, height/2);
      time--;
    } else {
      say = "";
    }
    stroke(255,255,255,255); fill(255,255,255,255);
  }
}