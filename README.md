# Arduino-Parking-Assistance
A small Arduino project using distance sensor, LED display screen and potentiometer

This project uses distance sensor to detect the distance between obstacle and the system. The result is displayed on a LED display screen. A red warning LED will light up if the obstacle is too close to the system. User is also able to adjust the lightness of warning LED via a potentiometer.

We also implemented a Java program to talk to arduino via serial connection. Readings from arduino is streaming to the Java program through the connection which is then directed and stored in database by using JDBC.

Please note the Java code uses code template from: 
  http://playground.arduino.cc/Interfacing/Java

