#include <SoftwareSerial.h>

#define LUNGHEZZA_X 309 //lunghezza asse x in millimetri
#define ENA_MOT_X 2 //define Enable Pin
#define DIR_MOT_X 3 //define Direction
#define PUL_MOT_X 4 //define Pulse pin
#define SENS_MOT_X 5 //define sensor pin
#define BLUETOOTH_TX 6 //define bluetooth tx pin
#define BLUETOOTH_RX 7 //define bluetooth rx pin
#define LUNGHEZZA_Y 309 //lunghezza asse y in millimetri
#define ENA_MOT_Y 8 //define Enable Pin
#define DIR_MOT_Y 9 //define Direction
#define PUL_MOT_Y 10 //define Pulse pin
#define SENS_MOT_Y 11 //define sensor pin
#define LUNGHEZZA_Z 200 //lunghezza asse z in millimetri
#define ENA_MOT_Z 12 //define Enable Pin
#define DIR_MOT_Z 13 //define Direction
#define PUL_MOT_Z 14 //define Pulse pin
#define SENS_MOT_Z 15 //define sensor pin
#define MANDRINO 16 // define mandrino pin
#define LED 17 // define led pin

struct movimento {
  String motore;
  boolean direzione;
  int millimetri;
  int velocita;
};

SoftwareSerial bluetooth(BLUETOOTH_TX, BLUETOOTH_RX);
//x y z
int millimetri_totali[3] = {0,0,0};
int lunghezze[3] = {LUNGHEZZA_X, LUNGHEZZA_Y, LUNGHEZZA_Z};

void setup() {
  Serial.begin(9600);
  bluetooth.begin(9600);
  pinMode (PUL_MOT_X, OUTPUT);
  pinMode (DIR_MOT_X, OUTPUT);
  pinMode (ENA_MOT_X, OUTPUT);
  pinMode (SENS_MOT_X, INPUT);
  pinMode (PUL_MOT_Y, OUTPUT);
  pinMode (DIR_MOT_Y, OUTPUT);
  pinMode (ENA_MOT_Y, OUTPUT);
  pinMode (SENS_MOT_Y, INPUT);
  pinMode (PUL_MOT_Z, OUTPUT);
  pinMode (DIR_MOT_Z, OUTPUT);
  pinMode (ENA_MOT_Z, OUTPUT);
  pinMode (SENS_MOT_Z, INPUT);
  pinMode (MANDRINO, OUTPUT);
  pinMode (LED, OUTPUT);
  digitalWrite(LED,HIGH);
  Serial.println("---------------  START  ---------------");
  reset();
}

void loop() {
  String bluetooth_buffer = bluetooth_read();
  if (bluetooth_buffer != "")
  {
    bluetooth.println("ricevuto " + bluetooth_buffer);
    bluetooth_command(bluetooth_buffer);
  }
}

String bluetooth_read(){
  String lettura = "";
  while (bluetooth.available()){
    lettura += (char)bluetooth.read();
    delay(100);
  }
  return lettura;
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
void sposta_millimetro(String motore, boolean direzione, int velocita){
  int pin = 0;
  if (motore == "x")
    pin = ENA_MOT_X;
  if (motore == "y")
    pin = ENA_MOT_Y;
  if (motore == "z")
    pin = ENA_MOT_Z;
  digitalWrite(pin,true);
  for(int i=0;i<640;i++)
    ruota(motore, direzione, velocita);
}

void aggiorna_misura(movimento m){
  int totali = 0;
  int indice = -1;
  if (m.motore == "x")
    indice = 0;
  if (m.motore == "y")
    indice = 1;
  if (m.motore == "z")
    indice = 2;
  totali = millimetri_totali[indice];
  if (m.direzione)
    totali = totali + m.millimetri;
  else
    totali = totali - m.millimetri;
  millimetri_totali[indice] = totali;
  dove_print(totali, m.motore);
}

void sposta(String command){
  movimento m = lettura_parametri(command);
  if (m.millimetri != 0){
    for(int i=0;i<m.millimetri;i++)
      sposta_millimetro(m.motore, m.direzione, m.velocita);
    aggiorna_misura(m);
  }
}

void attiva_mandrino(String command){
  if (command == "on" || command == "off"){
    if(command == "on"){
      digitalWrite(MANDRINO, true);
      Serial.println("ATTIVO MANDRINO!");
    }
    else{
      digitalWrite(MANDRINO, false);
      Serial.println("SPENGO MANDRINO!");
    }
  }
  else{
    Serial.print("I POSSIBILI VALORI PER QUESTO COMANDO SONO ON O OFF, TU HAI SCRITTO: "); 
    Serial.println(command);
  }
}

void dove_sono(String command){
  if (command == "x" || command == "y" || command == "z" || command == "all"){
    if (command == "x" || command == "all")
      dove_print(0, "x");
    if (command == "y" || command == "all")
      dove_print(1, "y");
    if (command == "z" || command == "all")
      dove_print(2, "z");
  }
  else{
    Serial.print("I POSSIBILI VALORI PER QUESTO COMANDO SONO X Y Z ALL, TU HAI SCRITTO: "); 
    Serial.println(command);
  }
}

void dove_print(int indice, String asse){
  Serial.print("TI TROVI A: ");
  Serial.print(millimetri_totali[indice]);
  Serial.print(" MILLIMETRI SULL'ASSE ");
  Serial.println(asse);
}

void dimmi_lunghezze(String command){
  if (command == "x" || command == "y" || command == "z" || command == "all"){
    if (command == "x" || command == "all")
      print_lunghezze(0, "x");
    if (command == "y" || command == "all")
      print_lunghezze(1, "y");
    if (command == "z" || command == "all")
      print_lunghezze(2, "z");
  }
  else{
    Serial.print("I POSSIBILI VALORI PER QUESTO COMANDO SONO X Y Z ALL, TU HAI SCRITTO: "); 
    Serial.println(command);
  }
}

void print_lunghezze(int indice, String asse){
  Serial.print("L'ASSE ");
  Serial.print(asse);
  Serial.print(" È LUNGO ");
  Serial.print(lunghezze[indice]);
  Serial.println(" MILLIMETRI");
}

void bluetooth_command(String command){
  Serial.print("MESSAGGIO RICEVUTO: "); 
  Serial.println(command);
  if (command.substring(0,6) == "muovi ")
    sposta(command.substring(6,command.length()));
  if (command.substring(0,6) == "ruota ")
    attiva_mandrino(command.substring(6,command.length()));
  if (command.substring(0,5) == "dove ")
    dove_sono(command.substring(5,command.length()));
  if (command.substring(0,5) == "lung ")
    dimmi_lunghezze(command.substring(5,command.length())); 
}

movimento lettura_parametri(String command){
  String direzione;
  String millimetri;
  String velocita;
  String motore;
  boolean ok = true;
  int indice = -1;
  movimento m;
  m.velocita=0;
  m.millimetri=0;
  m.motore="";
  m.direzione=NULL;
  int ind = command.indexOf(" ");
  if (ind > 0){
    motore = command.substring(0, ind);
    int ind1 = command.indexOf(" ", ind+1);
    if (ind1 > 0){
      direzione = command.substring(ind+1, ind1);
      int ind2 = command.indexOf(" ", ind1+1);
      if (ind2 > 0){
        millimetri = command.substring(ind1+1,ind2);
        velocita = command.substring(ind2+1,command.length());
      }
    }
  }
  Serial.print("Motore: ");
  Serial.println(motore);
  Serial.print("Direzione: ");
  Serial.println(direzione);
  Serial.print("Millimetri: ");
  Serial.println(millimetri);
  Serial.print("Velocita: ");
  Serial.println(velocita);
  if (motore == "x" || motore == "y" || motore == "z"){
    m.motore = motore;
    if (motore == "x")
     indice = 0;
    if (motore == "y")
     indice = 1;
    if (motore == "z")
     indice = 2;
  }
  else{
    Serial.print("I POSSIBILI MOTORI SONO X Y Z, NON ESISTE NESSUN MOTORE CON LA SIGLA: ");
    Serial.println(motore);
    ok = false;
  }
  if (direzione == "su" || direzione == "giu"){
    if (direzione == "su")  
      m.direzione = true;
    if (direzione == "giu")
      m.direzione = false;
  }
  else{
    Serial.print("LE POSSIBILI DIREZIONI SONO SU O GIU, TU HAI SCRITTO: ");
    Serial.println(direzione);
    ok = false;
  }
  if (millimetri.toInt() == 0){
    Serial.print("I MILLIMETRI DEVONO ESSERE UN NUMERO MAGGIORE DI 0, HAI INSERITO: ");
    Serial.println(millimetri);
    ok = false;
    }
  if (velocita.toInt() == 0){
    Serial.print("LA VELOCITA DEVE ESSERE UN NUMERO MAGGIORE DI 0, HAI INSERITO: ");
    Serial.println(velocita);
    ok = false;
    }
  if (ok){
    if (m.direzione){
       int mancante = lunghezze[indice] - millimetri_totali[indice];
       if (millimetri.toInt() > mancante){
          Serial.print("ERRORE!!! PER ARRIVARE A FINE CORSA CI SONO SOLO ");
          Serial.print(mancante);
          Serial.println(" MILLIMETRI");
          Serial.println("TI PORTO A FINE CORSA!!");
          m.millimetri = mancante;   
       }
       else
          m.millimetri = millimetri.toInt();
    }
    else{
       if (millimetri.toInt() > millimetri_totali[indice]){
          Serial.print("ERRORE!!! PER ARRIVARE A INIZIO CORSA CI SONO SOLO ");
          Serial.print(millimetri_totali[indice]);
          Serial.println(" MILLIMETRI");
          Serial.println("TI PORTO A INIZIO CORSA!!");
          m.millimetri = millimetri_totali[indice];   
       }
       else
          m.millimetri = millimetri.toInt();
    }
    if (velocita.toInt() < 32){
      Serial.println("ERRORE!! VELOCITA TROPPO BASSA, IMPOSTO 32"); 
      m.velocita = 32;
    }
    else
      m.velocita = velocita.toInt();
  }
  return m;
}

void reset(){
  Serial.println("EFFETTUO IL RESET DEI MOTORI!");
  Serial.println("ORA TUTTI I MOTORI SONO A 0");
}
