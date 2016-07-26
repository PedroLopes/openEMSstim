/*
  Pong demo for openEMSstim connected via USB using Processing (this is not a processing for android + bluetooth demo)
  
  before playing / setting up:
  1. Make sure you have your openEMSstim board connected via USB
  2. Start game using Processing and pressing "Play", and check the output on the Processing window. Find which number is your serial port (where your arduino shows up, should be something like dev/tty.usbserial-XXXX or dev/cu.usbserial-XXXX).  
  2. Change the line 32 "int index_of_serial_port = 8;" to point to your index (for instance change it to "int index_of_serial_port = 2;" if you found it on position number "2" of the log list)  
  3. Start game using Processing and pressing "Play" and now it will work just fine. 
  
  How to play: (use your keyboard)
  Player 1: 
  e : player 1 up
  d : player 1 down
  c : player 1 release ball
  Player 2: 
  up : player 2 up
  down : player 2 down
  left : player 2 release ball
  
  Extras: 
  p : switch player 2 : computer / human
  r : reset game
  + : computer level ++
  - : computer level --
  
*/

import processing.serial.*;

//configure Serial Port
Serial openEMSstim;
int index_of_serial_port = 17;

//game UI
Message message;
PFont scoreFont;
Ground ground;
Player player1;
Player player2;
Ball ball;
boolean start = true;
int UPCODE = 1;
int DOWNCODE = 2;
int moveIncrements = 10; 
float ball_speed = 1; 
float speed_up_on_attack = 0.5; 

void setup() {
  printArray(Serial.list());
  openEMSstim = new Serial(this, Serial.list()[index_of_serial_port], 9600);
  delay(10000); //waiting to configure bluetooth on board
  size(600, 450);
  ground = new Ground();
  player1 = new Player(1, 20, height/2, false,moveIncrements);
  player2 = new Player(2, width - 20, height/2, true,moveIncrements);
  ball = new Ball(0,0,1,openEMSstim);
  ball.setPos(player1.posX + player1.size/9 + ball.size/2, player1.posY);
  message = new Message();
  scoreFont = createFont("Arial", 40);
  textFont(scoreFont);
  ellipseMode(CENTER);
  frameRate(50);
}

void draw() {
  background(0);
  stroke(255); fill(255);
  ground.draw();
  player1.draw();
  player2.draw();
  ball.draw();
  message.draw();
  if (start) {
    player1.movY = 4;
    ball.stickRelease(1);
    start = false;
  }
}

void keyPressed() {
  if (key == '1') { // normal mode, player against computer
    player2.setMoveIncrements(12);
    player1.setMoveIncrements(12);
  } else if (key == '2') { // tracking pitch (up/down) and envelope (speed of ball)
    player2.setMoveIncrements(1);
    player1.setMoveIncrements(1);
  } else if (key == 'r' || key == 'R') {
    resetGame();
  } else if (key == 'p' || key == 'P') {
    player2.switchComputerDriven();
  } else if (key == 'e' || key == 'E') {
    player1.up();
  } else if (key == 'd' || key == 'D') {
    player1.down();
  } else if (key == 'c' || key == 'C') {
    player1.releaseBall(); 
  } else if (key == '+') {
    player2.setDifficulty(1);
    message.set("computer level " + (int)player2.getDifficulty());
  } else if (key == '-') {
    player2.setDifficulty(-1);
    message.set("computer level " + (int)player2.getDifficulty());
  } else if (key == CODED) {
    if (keyCode == UP) {
      player2.up();
    } else if (keyCode == DOWN) {
      player2.down();
    } else if (keyCode == LEFT) {
      player2.releaseBall();
    }
  }
}

void resetGame() {
  player1.setPos(20, height/2);
  player2.setPos(width - 20, height/2);
  player1.resetScore();
  player2.resetScore();
  ball.setSticky(1);
  player1.movY = 4;
  ball.stickRelease(1);
  message.set("new game");
  ball.setBallSpeed(1);
}