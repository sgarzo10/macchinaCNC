#include <SoftwareSerial.h>

SoftwareSerial bluetooth(6, 7); //BLUETOOTH: PIN TXD 6, PIN RXD 7

void setup() {
  Serial.begin(9600);
  bluetooth.begin(9600);
}

void loop() {
  String BLUETOOTH_BUFFER = BLUETOOTH_READ();
  if (BLUETOOTH_BUFFER != "")
  {
    Serial.print("MESSAGGIO RICEVUTO: "); 
    Serial.println(BLUETOOTH_BUFFER);
    bluetooth.println("ricevuto " + BLUETOOTH_BUFFER);
  }
}

String BLUETOOTH_READ(){
  String lettura = "";
  while (bluetooth.available()){
    lettura += (char)bluetooth.read();
    delay(100);
  }
  return lettura;
}
