class Ball {
  float ball_speed;
  float posX, posY, movX, movY, speedX, speedY, size;
  int stickOn;
  boolean sticky;
  int speed_size_compensate = 2;
  Serial openEMSstim;
  int player_1_intensity = 100; //adjust here the intensity of the openned stimulation chanel for player 1 (channel 0 of the hardware = channel 1)
  int player_2_intensity = 100; //adjust here the intensity of the openned stimulation chanel for player 2 (channel 1 of the hardware = channel 2)
  
  Ball(float _posX, float _posY, float _ball_speed, Serial _openEMSstim) {
    posX = _posX;
    posY = _posY;
    movX = 0;
    movY = 0;
    speedX = 0;
    speedY = 0;
    sticky = true;
    stickOn = 1;
    size = 6;
    ball_speed = _ball_speed;
    openEMSstim = _openEMSstim;
  }
  
  void setPos(float _posX, float _posY) {
    posX = _posX;
    posY = _posY;
  }
  
  void setBallSpeed(float _ball_speed ) {
   ball_speed = _ball_speed;
   //println("updated ball speed to " + ball_speed);
  }

  float getBallSpeed() {
   return ball_speed;
  }
  
  void update() {
    if (sticky) {
      if (stickOn == 1) {
        posX = player1.posX + player1.thickness/2 + ball.size/2;
        posY = player1.posY;
      } else if (stickOn == 2) {  
        posX = player2.posX - player2.thickness/2 - ball.size/2;
        posY = player2.posY;      
      }  
    } else {
      posX = posX + movX * ball_speed;
      posY = posY + movY * ball_speed;
    }
    
    
    if (posY < size) movY = -movY;        // top bounce
    if (posY > height-size) movY = -movY; // bottom bounce
    
    if (posX < 0) { // out left side
      player2.scorePlus();
      this.openEMSstim.write("C0"+"I"+str(player_1_intensity)+"T2000G");
      sticky = true;
      stickOn = 1; 
    }
    
    else if (posX > width) { // out right side
      player1.scorePlus();
      this.openEMSstim.write("C1"+"I"+str(player_2_intensity)+"T2000G");
      sticky = true;
      stickOn = 2;
    }
    
    else if (posX < (player1.posX + (player1.thickness / 2) + (size / 2))) {
      if (player1.testRacketCollision(posX, posY, size-speed_size_compensate)) {
        
        movX = -movX;
        movY += player1.getMovY() / 5;
      }
    }

    else if (posX > player2.posX) {  
      if (player2.testRacketCollision(posX, posY, size-speed_size_compensate)) {
        
        movX = -movX;
        movY += player2.getMovY() / 5;
      }
    }
  }
  
  void stickRelease(int idplayer) {
    sticky = false;
    if ((stickOn == 1) && (idplayer == 1)) {
      movY = player1.getMovY(); 
      movX = 5;
      stickOn = 0;
    } else if ((stickOn == 2) && (idplayer == 2)) {
      movY = player2.getMovY(); 
      movX = -5;
      stickOn = 0;
    }    
  }
  
  void setSticky(int _playerId) {
    sticky = true;
    stickOn = _playerId;  
  }
  
  float getPosY() {
    return posY;
  }
  
  void draw() {
    update();
    ellipse(posX, posY, size, size);
  }
}