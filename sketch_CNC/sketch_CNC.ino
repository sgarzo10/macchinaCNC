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
int millimetri_totali[3] = {0,0,0};
int lunghezze[3] = {LUNGHEZZA_X, LUNGHEZZA_Y, LUNGHEZZA_Z};
boolean seriale = true;

void setup() {
  if (seriale)
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
  my_print("---------------  START  ---------------", true);
  reset("a");
  digitalWrite(LED,HIGH);
}

void loop() {
  String bluetooth_buffer = bluetooth_read();
  if (bluetooth_buffer != "")
    bluetooth_command(bluetooth_buffer);
}

String bluetooth_read(){
  String lettura = "";
  while (bluetooth.available()){
    lettura += (char)bluetooth.read();
    delay(20);
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
  dove_print(indice, m.motore);
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
  if (command == "a" || command == "s"){
    if(command == "a"){
      digitalWrite(MANDRINO, true);
      my_print("ATTIVO MANDRINO!", true);
    }
    else{
      digitalWrite(MANDRINO, false);
      my_print("SPENGO MANDRINO!", true);
    }
  }
  else{
    my_print("I POSSIBILI VALORI PER QUESTO COMANDO SONO A O S, TU HAI SCRITTO: ", false); 
    my_print(command, true);
  }
}

void dove_sono(String command){
  if (command == "x" || command == "y" || command == "z" || command == "a"){
    if (command == "x" || command == "a")
      dove_print(0, "x");
    if (command == "y" || command == "a")
      dove_print(1, "y");
    if (command == "z" || command == "a")
      dove_print(2, "z");
  }
  else{
    my_print("I POSSIBILI VALORI PER QUESTO COMANDO SONO X Y Z A, TU HAI SCRITTO: ", false); 
    my_print(command, true);
  }
}

void dove_print(int indice, String asse){
  my_print("A ", false);
  my_print(String(millimetri_totali[indice]), false);
  my_print(" ASSE ", false);
  my_print(asse, true);
}

void dimmi_lunghezze(String command){
  if (command == "x" || command == "y" || command == "z" || command == "a"){
    if (command == "x" || command == "a")
      print_lunghezze(0, "x");
    if (command == "y" || command == "a")
      print_lunghezze(1, "y");
    if (command == "z" || command == "a")
      print_lunghezze(2, "z");
  }
  else{
    my_print("I POSSIBILI VALORI PER QUESTO COMANDO SONO X Y Z A, TU HAI SCRITTO: ", false); 
    my_print(command, true);
  }
}

void print_lunghezze(int indice, String asse){
  my_print("ASSE ", false);
  my_print(asse, false);
  my_print(" LUNGO ", false);
  my_print(String(lunghezze[indice]), true);
}

void setta_lunghezze(String command){
  String motore = "";
  String lunghezza = "0";
  int indice = -1;
  boolean ok = true;
  int ind = command.indexOf(" ");
  if (ind > 0){
    motore = command.substring(0, ind);
    lunghezza = command.substring(ind+1, command.length());
  }
  if (motore == "x" || motore == "y" || motore == "z"){
    if (motore == "x")
     indice = 0;
    if (motore == "y")
     indice = 1;
    if (motore == "z")
     indice = 2;
  }
  else{
    my_print("I POSSIBILI MOTORI SONO X Y Z, NON ESISTE NESSUN MOTORE CON LA SIGLA: ", false);
    my_print(motore, true);
    ok = false;
  }
  if (lunghezza.toInt() == 0){
    my_print("LA LUNGHEZZA DEVE ESSERE UN NUMERO MAGGIORE DI 0, HAI INSERITO: ", false);
    my_print(lunghezza, true);
    ok = false;
  }
  if (ok){
    lunghezze[indice] = lunghezza.toInt();
    my_print("SETTO LA LUNGHEZZA DELL'ASSE ", false);
    my_print(motore, false);
    my_print(" A ", false);
    my_print(lunghezza, false);
    my_print(" MILLIMETRI", true);
  }
}

void setta_seriale(String command){
  if (command == "b" || command == "s"){
    if (command == "b")
      seriale = false;
    else
      seriale = true;
    my_print("ORA L'OUTPUT VERRA MOSTRATO QUI", true);
  }
  else{
    my_print("I POSSIBILI VALORI PER QUESTO COMANDO SONO B O S, TU HAI SCRITTO: ", false); 
    my_print(command, true);
  }
}

void bluetooth_command(String command){
  Serial.print("MESSAGGIO RICEVUTO: "); 
  Serial.println(command);
  if (command.substring(0,1) == "m")
    sposta(command.substring(1,command.length()));
  if (command.substring(0,1) == "r")
    attiva_mandrino(command.substring(1,command.length()));
  if (command.substring(0,1) == "d")
    dove_sono(command.substring(1,command.length()));
  if (command.substring(0,1) == "l")
    dimmi_lunghezze(command.substring(1,command.length())); 
  if (command.substring(0,2) == "re")
    reset(command.substring(2,command.length()));
  if (command.substring(0,2) == "sl")
    setta_lunghezze(command.substring(2,command.length()));
  if (command.substring(0,2) == "ss")
    setta_seriale(command.substring(2,command.length()));
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
  /*int ind = command.indexOf(" ");
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
  }*/
  motore = command.substring(0,1);
  direzione = command.substring(1,2);
  millimetri = command.substring(2,command.length());
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
    my_print("I POSSIBILI MOTORI SONO X Y Z, NON ESISTE NESSUN MOTORE CON LA SIGLA: ", false);
    my_print(motore, true);
    ok = false;
  }
  if (direzione == "s" || direzione == "g"){
    if (direzione == "s")  
      m.direzione = true;
    if (direzione == "g")
      m.direzione = false;
  }
  else{
    my_print("LE POSSIBILI DIREZIONI SONO S O G, TU HAI SCRITTO: ", false);
    my_print(direzione, true);
    ok = false;
  }
  if (millimetri.toInt() == 0){
    my_print("I MILLIMETRI DEVONO ESSERE UN NUMERO MAGGIORE DI 0, HAI INSERITO: ", false);
    my_print(millimetri, true);
    ok = false;
    }
  /*  
  if (velocita.toInt() == 0){
    my_print("LA VELOCITA DEVE ESSERE UN NUMERO MAGGIORE DI 0, HAI INSERITO: ", false);
    my_print(velocita, true);
    ok = false;
    }*/
  if (ok){
    if (m.direzione){
       int mancante = lunghezze[indice] - millimetri_totali[indice];
       if (millimetri.toInt() > mancante){
          my_print("PER ARRIVARE A FINE CORSA CI SONO SOLO ", false);
          my_print(String(mancante), false);
          my_print(" MILLIMETRI", true);
          my_print("TI PORTO A FINE CORSA!!", true);
          m.millimetri = mancante;   
       }
       else
          m.millimetri = millimetri.toInt();
    }
    else{
       if (millimetri.toInt() > millimetri_totali[indice]){
          my_print("PER ARRIVARE A INIZIO CORSA CI SONO SOLO ", false);
          my_print(String(millimetri_totali[indice]), false);
          my_print(" MILLIMETRI", true);
          my_print("TI PORTO A INIZIO CORSA!!", true);
          m.millimetri = millimetri_totali[indice];   
       }
       else
          m.millimetri = millimetri.toInt();
    }
    /*if (velocita.toInt() < 32){
      my_print("VELOCITA TROPPO BASSA, IMPOSTO 32", true); 
      m.velocita = 32;
    }
    else
      m.velocita = velocita.toInt();*/
    m.velocita=32;
  }
  return m;
}

void reset(String command){
  if (command == "x" || command == "y" || command == "z" || command == "a"){
    if (command == "x" || command == "a")
      reset_motore("x");
    if (command == "y" || command == "a")
      reset_motore("y");
    if (command == "z" || command == "a")
      reset_motore("z");
  }
  else{
    my_print("I POSSIBILI VALORI PER QUESTO COMANDO SONO X Y Z A, TU HAI SCRITTO: ", false); 
    my_print(command, true);
  }
}

void reset_motore(String asse){
  my_print("EFFETTUO IL RESET DELL'ASSE ", false);
  my_print(asse, true);
  my_print("RESET DELL'ASSE ", false);
  my_print(asse, false);
  my_print(" TERMINATO", true);
}

void my_print(String s, boolean new_line){
  if (seriale){
    if (new_line)
      Serial.println(s);
    else
      Serial.print(s);
  }
  else{
    if (new_line){
      bluetooth.println(s);
      delay(100);
    }
    else
      bluetooth.print(s);
  }
}
