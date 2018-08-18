#include <SoftwareSerial.h>

#define BLUETOOTH_TX  10//define bluetooth tx pin
#define BLUETOOTH_RX 11 //define bluetooth rx pin
#define BLUETOOTH_AT 9 //define bluetooth at pin

SoftwareSerial bluetooth_seriale(BLUETOOTH_TX, BLUETOOTH_RX);

void setup() {
  Serial.begin(9600);
  pinMode(9,OUTPUT); 
  digitalWrite(9,HIGH);
  bluetooth_seriale.begin(38400);
}

void loop() {
  if (bluetooth_seriale.available())
    Serial.write(bluetooth_seriale.read());
  if (Serial.available())
    bluetooth_seriale.write(Serial.read());
}
