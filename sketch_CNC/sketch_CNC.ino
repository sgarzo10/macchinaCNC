#include <SPI.h>
#include <SD.h>

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
#define SENS_MOT_Y 5 //define sensor pin
#define LUNGHEZZA_Z 135 //lunghezza asse z in millimetri
#define GIRI_MM_Z 430 //numero di giri per fare un millimetro
#define ENA_MOT_Z 8 //define Enable Pin
#define DIR_MOT_Z 9 //define Direction
#define PUL_MOT_Z 10 //define Pulse pin
#define SENS_MOT_Z 11 //define sensor pin
#define MANDRINO 23 // define mandrino pin
#define BLUETOOTH_RX 18 //define bluetooth tx pin
#define BLUETOOTH_TX 19 //define bluetooth rx pin
#define PIN_SD 53 //define SD pin
#define BUFFER_SIZE 128 //dimensione massima del mex non compresso con il quale posso lavorare
#define BAUD_RATE 38400 //velocita seriale
/*
Abilitare reset all all'avvio
Abilitare while per reset
Abilitare ruota disegno

Disabilitare reset all all'avvio
Disabilitare while per reset
Disabilitare ruota disegno
*/
struct movimento {
  String motore;
  boolean direzione;
  uint32_t giri;
  uint16_t velocita;
};

struct coppia {
  char chiave;
  String valore;
};

File myFile;
float millimetri_totali[3] = {0,0,0};
float giri_millimetro[3] = {GIRI_MM_X, GIRI_MM_Y, GIRI_MM_Z};
uint16_t lunghezze[3] = {LUNGHEZZA_X, LUNGHEZZA_Y, LUNGHEZZA_Z};
String spostamenti[6] = {"xs", "xg", "ys", "yg", "zs", "zg"};
coppia dizionario[10];
boolean seriale = true;
boolean ultimo = false;
boolean diz = false;
boolean sd = false;
uint8_t dimDiz = 0;
uint16_t dimMexNoComp = 0;
String lettura = "";

void setup() {
  if (seriale)
    Serial.begin(BAUD_RATE);
  if (!SD.begin(PIN_SD)) {
    Serial.println("INIZIALIZZAZIONE SD FALLITA!");
  }
  Serial1.begin(BAUD_RATE);
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
  pulisci();
  Serial.println("---------------  START  ---------------");
  //reset("a");
}

void loop() {
  bluetooth_read();
}

void bluetooth_read(){
  char c = 'w';
  if (Serial1.available() > 0){
    while (c != 'L'){
      c = Serial1.read();
      if ((int)c > -1)
        lettura += c;
    }
    dimMexNoComp = lettura.substring(0, lettura.length() - 1).toInt();
    if (dimMexNoComp > BUFFER_SIZE){
      sd = true;
      /*Serial.print("I");
      Serial.println(millis());*/
      if (SD.exists("last.txt"))
        SD.remove("last.txt");
      myFile = SD.open("last.txt", FILE_WRITE);
      /*Serial.print("F");
      Serial.println(millis());*/
      while (c != '!'){
        /*Serial.print("I");
        Serial.println(millis());*/
        c = Serial1.read();
        if ((int)c > -1){
          myFile.print(c);
          /*Serial.print("F");
          Serial.println(millis());*/
        }
      }
    } else {
       while (c != '!'){
        /*Serial.print("I");
        Serial.println(millis());*/
        c = Serial1.read();
        if ((int)c > -1){
          lettura += c;
          /*Serial.print("F");
          Serial.println(millis());*/
        }
      }
    }
  }
  if (c != 'w'){
    if (sd)
      myFile.close();
    analyzeResponse();
  }
  return;
}

void analyzeResponse(){
  String mex = "";
  int8_t old_index = -1;
  uint8_t ripetizioni = 1;
  uint16_t ripetizioniCoppia = 0;
  String mexX = "";
  String mexY = "";
  uint8_t mexPos = 0;
  uint16_t numeroMex = countMessages();
  uint16_t currentMex = 0;
  boolean riparti = false;
  Serial.println("----------------------------");
  Serial.print("DIMENSIONE COMPRESSO: ");
  if (sd)
    Serial.print(myFile.size() + lettura.length());
  else
    Serial.print(lettura.length());
  Serial.print(" DIMENSIONE NON COMPRESSO: ");
  Serial.print(dimMexNoComp);
  Serial.print(" NUMERO MESSAGGI: ");
  Serial.println(numeroMex);
  if (sd){
    if (diz)
      myFile.seek(dimDiz);
    else
      myFile.seek(0);
  } else {
    if (diz)
      lettura = lettura.substring(lettura.indexOf("|") + 1, lettura.length());
    else
      lettura = lettura.substring(lettura.indexOf("L") + 1, lettura.length());
  }
  for(uint8_t j = 0; j < ripetizioni; j++){
    for (uint16_t i = 0; i < dimMexNoComp && !riparti; i = i + lettura.length()){
      lettura = readMessages();
      Serial.println(lettura);
      old_index = -1;
      for (int16_t index = lettura.indexOf("&"); index > 0 && !riparti; index = lettura.indexOf("&", old_index + 1)){
        currentMex = currentMex + 1;
        mex = lettura.substring(old_index + 1, index);
        if(mex.indexOf("#") > -1){
          mexY = mex.substring(0, mex.indexOf("-"));
          mexX = mex.substring(mex.indexOf("-") + 1, mex.indexOf("#"));
          ripetizioniCoppia = mex.substring(mex.indexOf("#") + 1, mex.length()).toInt();
          for(uint16_t k = 0; k < ripetizioniCoppia; k++){
            bluetooth_command(mexY);
            bluetooth_command(mexX);
          }
        } else {
          if (mex.indexOf("*") > -1){
            ripetizioni = mex.substring(1, mex.indexOf("-")).toInt();
            if (j < ripetizioni - 1){
              if (sd){
                if(diz)
                  myFile.seek(dimDiz);
                else
                  myFile.seek(0);
              }
              riparti = true;
              currentMex = 0;
            }
            if (j == ripetizioni - 2)
              mexPos = mex.substring(mex.indexOf("-") + 1, mex.length()).toInt();
          }
          if (!(currentMex >= numeroMex - mexPos - 3 && currentMex < numeroMex - 3))
            bluetooth_command(mex);
        }
        old_index = index;
      }
      if (!riparti){
        mex = lettura.substring(old_index + 1, lettura.length() - 1);
        if (mex != "" && mex != "&"){
          ultimo = true;
          bluetooth_command(mex);
          if (sd)
            Serial.println("HO ANALIZZATO TUTTO IL FILE");
          else
            Serial.println("HO ANALIZZATO TUTTO IL MESSAGGIO");
        }
      }
    }
    riparti = false;
  }
  pulisci();
}

String readMessages(){
  uint16_t caratteri = 0;
  uint8_t dimLast = 0;
  String myLettura = "";
  char c = 'w';
  boolean inDict = false;
  boolean esci = false;
  while (myLettura.length() <= BUFFER_SIZE && !esci && myLettura.length() < dimMexNoComp){
    if (sd){
      if (myFile.available())
        c = myFile.read();
      else
        break;
    } else
      c = lettura.charAt(caratteri);
    if (diz){
      for (uint8_t k = 0; k < 10; k++){
        if (dizionario[k].chiave == c){
          if (myLettura.length() + dizionario[k].valore.length() + 1 <= BUFFER_SIZE)
            myLettura += dizionario[k].valore + "&";
          else {
            esci = true;
            myFile.seek(myFile.position() - 1);
          }
          k = 10;
          inDict = true;
        }
      }
    }
    if (!inDict){
      if (myLettura.length() + 1 <= BUFFER_SIZE){
        myLettura += c;
        dimLast++;
      } else {
        esci = true;
        myFile.seek(myFile.position() - dimLast - 1);
        myLettura = myLettura.substring(0, myLettura.length() - dimLast);
      }
    } else
      inDict = false;
    if (c == '&' || c == '!' || inDict)
      dimLast = 0;
    caratteri++;
  }
  return myLettura;
}

uint16_t countMessages(){
  uint16_t count = 0;
  char c = 'w';
  boolean inDict = false;
  if (sd){
    myFile = SD.open("last.txt");
    c = myFile.read();
    if (c == 'D')
      readDictionary();
    while (myFile.available()){
      c = myFile.read();
      if (diz){
        for (uint8_t k = 0; k < 10; k++){
          if (dizionario[k].chiave == c){
            k = 10;
            inDict = true;
          }
        }
      }
      if (c == '&' || c == '!' || inDict){
        count = count + 1;
        inDict = false;
      }
    }
  } else {
    c = lettura.charAt(lettura.indexOf("L") + 1);
    if (c == 'D')
      readDictionary();
    for (uint16_t i = dimDiz; i < lettura.length(); i++){
      c = lettura.charAt(i);
      if (diz){
        for (uint8_t k = 0; k < 10; k++){
          if (dizionario[k].chiave == c){
            k = 10;
            inDict = true;
          }
        }
      }
      if (c == '&' || c == '!' || inDict){
        count = count + 1;
        inDict = false;
      }
    }
  }
  return count;
}

void readDictionary(){
  char c = 'w';
  String valore = "";
  boolean val = false;
  uint8_t i = 0;
  diz = true;
  while (c != '|'){
    if (sd)
      c = myFile.read();
    else
      c = lettura.charAt(dimDiz);
    dimDiz++;
    if (c == ':'){
      val = true;
      if (sd)
        c = myFile.read();
      else
        c = lettura.charAt(dimDiz);
      dimDiz++;
    }
    if (c == '&' || c == '|'){
      dizionario[i].valore = valore;
      i++;
      valore = "";
      if (c != '|'){
        if (sd)
          c = myFile.read();
        else
          c = lettura.charAt(dimDiz);
        dimDiz++;
        val = false;
      }
    }
    if (!val)
      dizionario[i].chiave = c;
    else
      valore += c;
  }
  dimDiz++;
  return;
}

/*
direzione: true orario(sale), false antiorario (scende)
velocita: 20 velocissimo, 1000 lentissimo
3200 giri rotazione completa 5mm - 640 giri 1mm*/
void sposta(String command, boolean aggiorna, boolean reset){
  movimento m = lettura_parametri(spostamenti[command.substring(0,1).toInt() - 1] + command.substring(1, command.length()), reset);
  uint8_t indice = motoreToIndice(m.motore);
  float totali = millimetri_totali[indice];
  uint8_t pin_ena = 0;
  uint8_t pin_dir = 0;
  uint8_t pin_pul = 0;
  if (m.giri != 0){
    if (m.motore == "x"){
      pin_ena = ENA_MOT_X;
      pin_dir = DIR_MOT_X;
      pin_pul = PUL_MOT_X;
    }
    if (m.motore == "y"){
      pin_ena = ENA_MOT_Y;
      pin_dir = DIR_MOT_Y;
      pin_pul = PUL_MOT_Y;
    }
    if (m.motore == "z"){
      pin_ena = ENA_MOT_Z;
      pin_dir = DIR_MOT_Z;
      pin_pul = PUL_MOT_Z;
    }
    digitalWrite(pin_ena,true);
    for(uint32_t i=0;i<m.giri;i++){
    /*digitalWrite(pin_dir,m.direzione);
      digitalWrite(pin_pul,HIGH);
      delayMicroseconds(m.velocita);
      digitalWrite(pin_pul,LOW);
      delayMicroseconds(m.velocita);*/
    }
    if (aggiorna){
      if (m.direzione)
        totali = totali + (m.giri / giri_millimetro[indice]);
      else
        totali = totali - (m.giri / giri_millimetro[indice]);
      millimetri_totali[indice] = totali;
    }
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

void dove_sono(){
  dove_print("x", false);
  dove_print("y", false);
  dove_print("z", true);
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

void dimmi_lunghezze(){
  print_lunghezze("x");
  print_lunghezze("y");
  print_lunghezze("z");
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

void dimmi_giri(){
  print_giri("x");
  print_giri("y");
  print_giri("z");
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
  uint8_t indice = motoreToIndice(motore);
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
  uint8_t indice = motoreToIndice(motore);
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

void bluetooth_command(String command){
  if (command.substring(0,1).toInt() > 0){
    sposta(command, true, false);
    return;
  }
  if (command == "da"){
    dove_sono();
    return;
  }
  if (command == "la"){
    dimmi_lunghezze();
    return;
  }
  if (command == "ga"){
    dimmi_giri();
    return;
  }
  if (command == "ra" || command == "rs"){
    attiva_mandrino(command.substring(1,command.length()));
    return;
  }
  if (command.substring(0,2) == "re"){
    reset(command.substring(2,command.length()));
    return;
  }
  if (command.substring(0,2) == "sl"){
    setta_lunghezze(command.substring(2,command.length()));
    return;
  }
  if (command.substring(0,2) == "sg"){
    setta_giri(command.substring(2,command.length()));
    return;
  }
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
  /*while(analogRead(pin) < 1008)
    sposta(dir+String((int)giri_millimetro[indice]), false, true);*/
  millimetri_totali[indice] = 0;
  my_print("o", true);
  return;
}

void my_print(String s, boolean new_line){
  if (ultimo){
    if (new_line){
      Serial1.println(s);
      ultimo = false;
    }
    else
      Serial1.print(s);
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

void pulisci(){
  if (sd)
    myFile.close();
  sd = false;
  lettura = "";
  diz = false;
  dimDiz = 0;
  for (uint8_t i = 0; i < 10; i++){
    dizionario[i].chiave = 'w';
    dizionario[i].valore = "";
  }
  return;
}
