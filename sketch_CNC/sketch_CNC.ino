#include <SoftwareSerial.h>
#include <SPI.h>
#include <SD.h>

/*
Arduino MEGA 2560
#define LUNGHEZZA_X 450 //lunghezza asse x in millimetri
#define GIRI_MM_X 640 //numero di giri per fare un millimetro
#define ENA_MOT_X 2 //define Enable Pin
#define DIR_MOT_X 3 //define Direction
#define PUL_MOT_X 4 //define Pulse pin
#define SENS_MOT_X 1 //define sensor pin
#define LUNGHEZZA_Y 309 //lunghezza asse y in millimetri
#define GIRI_MM_Y 640 //numero di giri per fare un millimetro
#define ENA_MOT_Y 5 //define Enable Pin
#define DIR_MOT_Y 6 //define Direction
#define PUL_MOT_Y 7 //define Pulse pin
#define SENS_MOT_Y 2 //define sensor pin
#define LUNGHEZZA_Z 135 //lunghezza asse z in millimetri
#define GIRI_MM_Z 430 //numero di giri per fare un millimetro
#define ENA_MOT_Z 8 //define Enable Pin
#define DIR_MOT_Z 9 //define Direction
#define PUL_MOT_Z 10 //define Pulse pin
#define SENS_MOT_Z 12 //define sensor pin
#define MANDRINO 23 // define mandrino pin
#define BLUETOOTH_TX 18 //define bluetooth tx pin
#define BLUETOOTH_RX 19 //define bluetooth rx pin
Sostituire bluetooth_seriale con Serial1
rimuovere SoftwareSerial
abilitare reset all all'avvio
abilitare while per reset
*/
#define LUNGHEZZA_X 450 //lunghezza asse x in millimetri
#define GIRI_MM_X 640 //numero di giri per fare un millimetro
#define ENA_MOT_X 3 //define Enable Pin
#define DIR_MOT_X 3 //define Direction
#define PUL_MOT_X 3 //define Pulse pin
#define SENS_MOT_X 3 //define sensor pin
#define BLUETOOTH_TX  6//define bluetooth tx pin
#define BLUETOOTH_RX 7 //define bluetooth rx pin
#define BLUETOOTH_AT 9 //define bluetooth at pin
#define LUNGHEZZA_Y 309 //lunghezza asse y in millimetri
#define GIRI_MM_Y 640 //numero di giri per fare un millimetro
#define ENA_MOT_Y 3 //define Enable Pin
#define DIR_MOT_Y 3 //define Direction
#define PUL_MOT_Y 3 //define Pulse pin
#define SENS_MOT_Y 3 //define sensor pin
#define LUNGHEZZA_Z 135 //lunghezza asse z in millimetri
#define GIRI_MM_Z 430 //numero di giri per fare un millimetro
#define ENA_MOT_Z 3 //define Enable Pin
#define DIR_MOT_Z 3 //define Direction
#define PUL_MOT_Z 3 //define Pulse pin
#define SENS_MOT_Z 3 //define sensor pin
#define MANDRINO 3 // define mandrino pin

struct movimento {
  String motore;
  boolean direzione;
  uint32_t giri;
  uint16_t velocita;
};

File myFile;
SoftwareSerial bluetooth_seriale(BLUETOOTH_TX, BLUETOOTH_RX);
float millimetri_totali[3] = {0,0,0};
float giri_millimetro[3] = {GIRI_MM_X, GIRI_MM_Y, GIRI_MM_Z};
uint16_t lunghezze[3] = {LUNGHEZZA_X, LUNGHEZZA_Y, LUNGHEZZA_Z};
String spostamenti[6] = {"xs", "xg", "ys", "yg", "zs", "zg"};
boolean seriale = true;
boolean ultimo = false;

void setup() {
  if (seriale)
    Serial.begin(38400);
  if (!SD.begin(4)) {
    Serial.println("INIZIALIZZAZIONE SD FALLITA!");
    return;
  }
  bluetooth_seriale.begin(38400);
  pinMode (PUL_MOT_X, OUTPUT);
  pinMode (DIR_MOT_X, OUTPUT);
  pinMode (ENA_MOT_X, OUTPUT);
  pinMode (PUL_MOT_Y, OUTPUT);
  pinMode (DIR_MOT_Y, OUTPUT);
  pinMode (ENA_MOT_Y, OUTPUT);
  pinMode (PUL_MOT_Z, OUTPUT);
  pinMode (DIR_MOT_Z, OUTPUT);
  pinMode (ENA_MOT_Z, OUTPUT);
  pinMode (MANDRINO, OUTPUT);
  Serial.println("---------------  START  ---------------");
  //reset("a");
}

void loop() {
  bluetooth_read();
}

void bluetooth_read(){
  char c = '#';
  if (bluetooth_seriale.available() > 0){
    if (SD.exists("last.txt"))
      SD.remove("last.txt");
    myFile = SD.open("last.txt", FILE_WRITE);
    while (c != '!'){
      c = bluetooth_seriale.read();
      if ((int)c > -1){
        myFile.print(c);
      }
    }
  }
  if (c != '#'){
    myFile.close();
    analyzeResponse();
  }
  return;
}

void analyzeResponse(){
  String lettura = "";
  String mex = "";
  int8_t old_index = -1;
  uint8_t dim = 10;
  uint16_t numeroMex = countMessages();
  uint32_t posizione = 0;
  Serial.println("----------------------------");
  Serial.print("CARATTERI: ");
  Serial.print(myFile.size());
  Serial.print(" MESSAGGI: ");
  Serial.println(numeroMex);
  myFile.close();
  for (uint16_t i = 0; i < numeroMex; i = i + dim){
    lettura = readMessages(posizione, dim);
    posizione = posizione + lettura.length();
    old_index = -1;
    for (int8_t index = lettura.indexOf("&"); index > 0; index = lettura.indexOf("&", old_index + 1)){
      mex = lettura.substring(old_index + 1, index);
      bluetooth_command(mex);
      old_index = index;
    }
    mex = lettura.substring(old_index + 1, lettura.length() - 1);
    if (mex != "" && mex != "&"){
      ultimo = true;
      bluetooth_command(mex);
      Serial.println("HO ANALIZZATO TUTTO IL FILE");
    }
  }
}

String readMessages(uint32_t posizione, uint16_t numero){
  uint16_t nMessages = 0;
  String lettura = "";
  char c = 'w';
  myFile = SD.open("last.txt");
  myFile.seek(posizione);
  while (nMessages < numero && myFile.available()){
    c = myFile.read();
    if (c == '&' || c == '!')
      nMessages = nMessages + 1;
    lettura += c;
  }
  myFile.close();
  return lettura;
}

uint16_t countMessages(){
  uint16_t count = 0;
  char c = 'w';
  myFile = SD.open("last.txt");
  while (myFile.available()){
    c = myFile.read();
    if (c == '&' || c == '!')
      count = count + 1;
  }
  return count;
}

//direzione: true orario(sale), false antiorario (scende)
//velocita: 20 velocissimo, 1000 lentissimo
void ruota(String motore, boolean direzione, uint16_t velocita){
  uint8_t pin_dir = 0;
  uint8_t pin_pul = 0;
  if (motore == "x"){
    pin_dir = DIR_MOT_X;
    pin_pul = PUL_MOT_X;
  }
  if (motore == "y"){
    pin_dir = DIR_MOT_Y;
    pin_pul = PUL_MOT_Y;
  }
  if (motore == "z"){
    pin_dir = DIR_MOT_Z;
    pin_pul = PUL_MOT_Z;
  }
  digitalWrite(pin_dir,direzione);
  digitalWrite(pin_pul,HIGH);
  delayMicroseconds(velocita);
  digitalWrite(pin_pul,LOW);
  delayMicroseconds(velocita);
  return;
}

//3200 pulsazioni giro completo 5mm - 640 pulsazioni 1mm 
void sposta_millimetro(String motore, uint32_t giri, boolean direzione, uint16_t velocita){
  uint8_t pin = 0;
  if (motore == "x")
    pin = ENA_MOT_X;
  if (motore == "y")
    pin = ENA_MOT_Y;
  if (motore == "z")
    pin = ENA_MOT_Z;
  digitalWrite(pin,true);
  for(uint32_t i=0;i<giri;i++)
    ruota(motore, direzione, velocita);
  return;
}

void aggiorna_misura(movimento m){
  uint8_t indice = motoreToIndice(m.motore);
  float totali = millimetri_totali[indice];
  if (m.direzione)
    totali = totali + (m.giri / giri_millimetro[indice]);
  else
    totali = totali - (m.giri / giri_millimetro[indice]);
  millimetri_totali[indice] = totali;
  return;
}

void sposta(String command, boolean aggiorna, boolean reset){
  movimento m = lettura_parametri(spostamenti[command.substring(0,1).toInt() - 1] + command.substring(1, command.length()), reset);
  if (m.giri != 0){
    sposta_millimetro(m.motore, m.giri, m.direzione, m.velocita);
    if (aggiorna)
      aggiorna_misura(m);
  }
  if (aggiorna)
    dove_print(m.motore, true);
  return;
}

void attiva_mandrino(String command){
  if (command == "a" || command == "s"){
    if(command == "a")
      digitalWrite(MANDRINO, true);
    else
      digitalWrite(MANDRINO, false);
    my_print("o", true);
  }
  else
    my_print("e", true);
  return;
}

void dove_sono(String command){
  if (command == "a"){
    dove_print("x", false);
    dove_print("y", false);
    dove_print("z", true);
  }
  else
    my_print("e", true);
  return;
}

void dove_print(String asse, boolean new_line){
  uint8_t indice = motoreToIndice(asse);
  my_print(asse, false);
  my_print(String(millimetri_totali[indice], 7), new_line);
  if (!new_line)
    my_print("&", new_line);
  return;
}

void dimmi_lunghezze(String command){
  if (command == "a"){
    print_lunghezze("x");
    print_lunghezze("y");
    print_lunghezze("z");
  }
  else
    my_print("e", true);
  return;
}

void print_lunghezze(String asse){
  uint8_t indice = motoreToIndice(asse);
  my_print(asse, false);
  if (asse == "z")
    my_print(String(lunghezze[indice]), true);
  else{
    my_print(String(lunghezze[indice]), false);
    my_print("&", false);
  }
  return;
}

void dimmi_giri(String command){
  if (command == "a"){
    print_giri("x");
    print_giri("y");
    print_giri("z");
  }
  else
    my_print("e", true);
  return;
}

void print_giri(String asse){
  uint8_t indice = motoreToIndice(asse);
  my_print(asse, false);
  if (asse == "z")
    my_print(String(giri_millimetro[indice]), true);
  else{
    my_print(String(giri_millimetro[indice]), false);
    my_print("&", false);
  }
  return;
}

void setta_lunghezze(String command){
  boolean ok = true;
  String motore = command.substring(0, 1);
  String lunghezza = command.substring(1, command.length());
  int indice = motoreToIndice(motore);
  if (!(motore == "x" || motore == "y" || motore == "z")){
    my_print("e", true);
    ok = false;
  }
  if (lunghezza.toInt() == 0 && ok){
    my_print("e", true);
    ok = false;
  }
  if (ok){
    lunghezze[indice] = lunghezza.toInt();
    my_print("o", true);
  }
  return;
}

void setta_giri(String command){
  boolean ok = true;
  String motore = command.substring(0, 1);
  String giri = command.substring(1, command.length());
  int indice = motoreToIndice(motore);
  if (!(motore == "x" || motore == "y" || motore == "z")){
    my_print("e", true);
    ok = false;
  }
  if (giri.toInt() == 0 && ok){
    my_print("e", true);
    ok = false;
  }
  if (ok){
    giri_millimetro[indice] = giri.toInt();
    my_print("o", true);
  }
  return;
}

void setta_seriale(String command){
  if (command == "b" || command == "s"){
    if (command == "b")
      seriale = false;
    else
      seriale = true;
    my_print("o", true);
  }
  else
    my_print("e", true);
  return;
}

void bluetooth_command(String command){
  if (command.substring(0,1).toInt() > 0)
    sposta(command, true, false);
  if (command.substring(0,1) == "r" && !(command.substring(1,2) == "e"))
    attiva_mandrino(command.substring(1,command.length()));
  if (command.substring(0,1) == "d")
    dove_sono(command.substring(1,command.length()));
  if (command.substring(0,1) == "l")
    dimmi_lunghezze(command.substring(1,command.length()));
  if (command.substring(0,1) == "g")
    dimmi_giri(command.substring(1,command.length()));
  if (command.substring(0,2) == "re")
    reset(command.substring(2,command.length()));
  if (command.substring(0,2) == "sl")
    setta_lunghezze(command.substring(2,command.length()));
  if (command.substring(0,2) == "sg")
    setta_giri(command.substring(2,command.length()));
  if (command.substring(0,2) == "ss")
    setta_seriale(command.substring(2,command.length()));
  return;
}

movimento lettura_parametri(String command, boolean reset){
  String direzione;
  String giri;
  String velocita;
  String motore;
  boolean vel = false;
  boolean ok = true;
  uint8_t indice = 0;
  movimento m;
  m.velocita=32;
  m.giri=0;
  m.motore="";
  m.direzione=NULL;
  motore = command.substring(0,1);
  direzione = command.substring(1,2);
  giri = command.substring(2,command.length());
  if (giri.indexOf(".") > 0){
    velocita = giri.substring(giri.indexOf(".") + 1,giri.length());
    giri = giri.substring(0, giri.indexOf("."));
    vel = true;
  }
  if (motore == "x" || motore == "y" || motore == "z"){
    m.motore = motore;
    indice = motoreToIndice(motore);
  }
  else{
    my_print("e", true);
    ok = false;
  }
  if ((direzione == "s" || direzione == "g") && ok){
    if (direzione == "s")  
      m.direzione = true;
    if (direzione == "g")
      m.direzione = false;
  }
  else{
    my_print("e", true);
    ok = false;
  }
  if (giri.toInt() == 0 && ok){
    my_print("e", true);
    ok = false;
    }
  if (vel && velocita.toInt() < 32 && ok){
    my_print("e", true);
    ok = false;
    }
  if (ok && !reset){
    if (m.direzione){
       float mancante = lunghezze[indice] - millimetri_totali[indice];
       if (giri.toInt() / giri_millimetro[indice] > mancante)
          m.giri = mancante * giri_millimetro[indice];   
       else
          m.giri = giri.toInt();
    }
    else{
       if (giri.toInt() / giri_millimetro[indice] > millimetri_totali[indice])
          m.giri = millimetri_totali[indice] * giri_millimetro[indice];   
       else
          m.giri = giri.toInt();
    }
    if (vel)
      m.velocita = velocita.toInt();
  } else {
    if (reset)
      m.giri = giri.toInt();
  }
  return m;
}

void reset(String command){
  if (command == "x" || command == "y" || command == "z" || command == "a"){
    if (command == "z" || command == "a")
      reset_motore("z");
    if (command == "y" || command == "a")
      reset_motore("y");
    if (command == "x" || command == "a")
      reset_motore("x");
  }
  else
    my_print("e", true);
  return;
}

void reset_motore(String asse){
  uint8_t pin = 0;
  uint8_t indice = motoreToIndice(asse);
  String dir = "";
  if (asse == "x"){
    pin = SENS_MOT_X;
    dir = "2";
  }
  if (asse == "y"){
    pin = SENS_MOT_Y;
    dir = "4";
  }
  if (asse == "z"){
    pin = SENS_MOT_Z;
    dir = "6";
  }
  /*while(analogRead(pin) < 1019)
    sposta(dir+String((int)giri_millimetro[indice]), false, true);*/
  millimetri_totali[indice] = 0;
  my_print("o", true);
  return;
}

void my_print(String s, boolean new_line){
  if (seriale){
    if (new_line)
      Serial.println(s);
    else
      Serial.print(s);
  }
  else{
    if (ultimo){
      if (new_line){
        bluetooth_seriale.println(s);
        ultimo = false;
      }
      else
        bluetooth_seriale.print(s);
    }
  }
  return;
}

uint8_t motoreToIndice(String motore){
  uint8_t indice = -1;
  if (motore == "x")
    indice = 0;
  if (motore == "y")
    indice = 1;
  if (motore == "z")
    indice = 2;
  return indice;
}
