
// this is for LED screen
#include <LiquidCrystal.h>
int trigPin = 3;    //Trig - green Jumper
int echoPin = 2;    //Echo - yellow Jumper
long duration, cm, inches;
LiquidCrystal lcd(7, 8, 9, 10, 11 , 12);

void setup() {
  // for LED setup
  lcd.begin(16, 2);
  lcd.setCursor(0,1);

  // pin5 is for bulb
  pinMode(5,OUTPUT);

  //pin A1 is for detecting theometer
  pinMode(A1,INPUT);

  // pin 6: detecting button voltage
  pinMode(6,INPUT);

  // these are for LED screen
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  
  Serial.begin(9600);
}

void loop() {
  int screenValue =Serial.read();
  //Serial.println(screenValue);
  // the ultrosound part
  // The sensor is triggered by a HIGH pulse of 10 or more microseconds.
  // Give a short LOW pulse beforehand to ensure a clean HIGH pulse:

  // following are for emit signal
  digitalWrite(trigPin, LOW);
  delayMicroseconds(5);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
 
  // Read the signal from the sensor: a HIGH pulse whose
  // duration is the time (in microseconds) from the sending
  // of the ping to the reception of its echo off of an object.
  pinMode(echoPin, INPUT);
  duration = pulseIn(echoPin, HIGH);
 
  // convert the time into a distance
  cm = (duration/2) / 29.1;
  inches = (duration/2) / 74; 
  // put the distance into string

  //********************************** LED screen part
  // this is for clean the screen
  lcd.setCursor(0,1);
  lcd.write("                                           ");
  char str[15];
  sprintf(str, "%d", cm);
  char screenStr[15];
  sprintf(screenStr, "%d", screenValue);
  strcat(screenStr, "cm");

  // print out the distance
  lcd.setCursor(0,1);
  if(screenValue<100){
    lcd.write(screenStr);
  }

  int theremeterVal = analogRead(A1);
  char resisStr[30];
  sprintf(resisStr, "%d", theremeterVal);
  strcat(resisStr,"omega");

  // ****************** the lightness of the bulk********************
  int pwmValue = map(cm, 0, 10, 0, 255);
  pwmValue = 255 - pwmValue;
  analogWrite(5,0);
  if(cm<10){
    analogWrite(5,pwmValue);
  }

  char jsonStr[58];
  strcpy(jsonStr, "{\"resistance\":\"");
  strcat(jsonStr, resisStr);
  strcat(jsonStr, "\",");
  strcat(jsonStr, "\"distance\":\"");
  strcat(jsonStr, str);
  strcat(jsonStr, "\"}");
  Serial.println(jsonStr);

  delay(250);
}