#include <SoftwareSerial.h>
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
#define ENA_MOT_X 2 //define Enable Pin
#define DIR_MOT_X 3 //define Direction
#define PUL_MOT_X 4 //define Pulse pin
#define SENS_MOT_X 5 //define sensor pin
#define BLUETOOTH_TX 6 //define bluetooth tx pin
#define BLUETOOTH_RX 7 //define bluetooth rx pin
#define LUNGHEZZA_Y 309 //lunghezza asse y in millimetri
#define GIRI_MM_Y 640 //numero di giri per fare un millimetro
#define ENA_MOT_Y 8 //define Enable Pin
#define DIR_MOT_Y 9 //define Direction
#define PUL_MOT_Y 10 //define Pulse pin
#define SENS_MOT_Y 11 //define sensor pin
#define LUNGHEZZA_Z 135 //lunghezza asse z in millimetri
#define GIRI_MM_Z 430 //numero di giri per fare un millimetro
#define ENA_MOT_Z 12 //define Enable Pin
#define DIR_MOT_Z 13 //define Direction
#define PUL_MOT_Z 14 //define Pulse pin
#define SENS_MOT_Z 15 //define sensor pin
#define MANDRINO 16 // define mandrino pin

struct movimento {
  String motore;
  boolean direzione;
  uint32_t giri;
  int velocita;
};

SoftwareSerial bluetooth_seriale(BLUETOOTH_TX, BLUETOOTH_RX);
float millimetri_totali[3] = {0,0,0};
float giri_millimetro[3] = {GIRI_MM_X, GIRI_MM_Y, GIRI_MM_Z};
int lunghezze[3] = {LUNGHEZZA_X, LUNGHEZZA_Y, LUNGHEZZA_Z};
String spostamenti[6] = {"xs", "xg", "ys", "yg", "zs", "zg"};
boolean seriale = true;
boolean ultimo = false;

void setup() {
  if (seriale)
    Serial.begin(9600);
  bluetooth_seriale.begin(9600);
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
  String lettura = "";
  char c = 's';
  uint32_t numero = 0; 
  while (bluetooth_seriale.available() && c != '!'){
    c = (char)bluetooth_seriale.read();
    //if (lettura. length() < 5)
    lettura += c;
    numero = numero + 1;
    /*if (numero % 500 == 0)
      Serial.println(numero);*/
    delay(10);
  }
  if (lettura != ""){
    Serial.println(numero);
    int old_index = -1;
    String mex = "";
    for (int index = lettura.indexOf("&"); index > 0; index = lettura.indexOf("&", old_index + 1)){
      mex = lettura.substring(old_index + 1, index);
      bluetooth_command(mex);
      old_index = index;
    }
    mex = lettura.substring(old_index + 1, lettura.length() - 1);
    if (mex != ""){
      ultimo = true;
      bluetooth_command(mex);
    }
  }
  return;
}

//direzione: true orario(sale), false antiorario (scende)
//velocita: 20 velocissimo, 1000 lentissimo
void ruota(String motore, boolean direzione, int velocita){
  int pin_dir = 0;
  int pin_pul = 0;
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
}

//3200 pulsazioni giro completo 5mm - 640 pulsazioni 1mm 
void sposta_millimetro(String motore, uint32_t giri, boolean direzione, int velocita){
  int pin = 0;
  if (motore == "x")
    pin = ENA_MOT_X;
  if (motore == "y")
    pin = ENA_MOT_Y;
  if (motore == "z")
    pin = ENA_MOT_Z;
  digitalWrite(pin,true);
  for(uint32_t i=0;i<giri;i++)
    ruota(motore, direzione, velocita);
}

void aggiorna_misura(movimento m){
  int indice = motoreToIndice(m.motore);
  float totali = millimetri_totali[indice];
  if (m.direzione)
    totali = totali + (m.giri / giri_millimetro[indice]);
  else
    totali = totali - (m.giri / giri_millimetro[indice]);
  millimetri_totali[indice] = totali;
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
}

void dove_sono(String command){
  if (command == "a"){
    dove_print("x", false);
    dove_print("y", false);
    dove_print("z", true);
  }
  else
    my_print("e", true); 
}

void dove_print(String asse, boolean new_line){
  int indice = motoreToIndice(asse);
  my_print(asse, false);
  my_print(String(millimetri_totali[indice], 7), new_line);
  if (!new_line)
    my_print("&", new_line);
}

void dimmi_lunghezze(String command){
  if (command == "a"){
    print_lunghezze("x");
    print_lunghezze("y");
    print_lunghezze("z");
  }
  else
    my_print("e", true);
}

void print_lunghezze(String asse){
  int indice = motoreToIndice(asse);
  my_print(asse, false);
  if (asse == "z")
    my_print(String(lunghezze[indice]), true);
  else{
    my_print(String(lunghezze[indice]), false);
    my_print("&", false);
  }
}

void dimmi_giri(String command){
  if (command == "a"){
    print_giri("x");
    print_giri("y");
    print_giri("z");
  }
  else
    my_print("e", true);
}

void print_giri(String asse){
  int indice = motoreToIndice(asse);
  my_print(asse, false);
  if (asse == "z")
    my_print(String(giri_millimetro[indice]), true);
  else{
    my_print(String(giri_millimetro[indice]), false);
    my_print("&", false);
  }
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
}

void bluetooth_command(String command){
  Serial.print("MESSAGGIO RICEVUTO: "); 
  Serial.println(command);
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
}

movimento lettura_parametri(String command, boolean reset){
  String direzione;
  String giri;
  String velocita;
  String motore;
  boolean vel = false;
  boolean ok = true;
  int indice = -1;
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
}

void reset_motore(String asse){
  int pin = 0;
  int indice = motoreToIndice(asse);
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
        delay(100);
      }
      else
        bluetooth_seriale.print(s);
    }
  }
}

int motoreToIndice(String motore){
  int indice = -1;
  if (motore == "x")
    indice = 0;
  if (motore == "y")
    indice = 1;
  if (motore == "z")
    indice = 2;
  return indice;
}
