#define ENA 8 //define Enable Pin
#define DIR 9 //define Direction
#define PUL 10 //define Pulse pin

struct movimento {
  boolean direzione;
  int millimetri;
  int velocita;
};

//LUNGHEZZA TOTALE: 309 millimetri
int millimetri_totali=0;

void setup() {
  Serial.begin(9600);
  pinMode (PUL, OUTPUT);
  pinMode (DIR, OUTPUT);
  pinMode (ENA, OUTPUT);
}

void loop() {
  movimento m = lettura_parametri();
  if (m.millimetri != 0){
    for(int i=0;i<m.millimetri;i++)
      sposta_millimetro(m.direzione, m.velocita);
    if (m.direzione)
      millimetri_totali = millimetri_totali + m.millimetri;
    else
      millimetri_totali = millimetri_totali - m.millimetri;
    Serial.print("TI TROVI A: ");
    Serial.print(millimetri_totali);
    Serial.println(" MILLIMETRI");
  }
}

//direzione: true orario(sale), false antiorario (scende)
//velocita: 20 velocissimo, 1000 lentissimo
void ruota(boolean direzione, int velocita){
  digitalWrite(DIR,direzione);
  digitalWrite(PUL,HIGH);
  delayMicroseconds(velocita);
  digitalWrite(PUL,LOW);
  delayMicroseconds(velocita);
}

//3200 pulsazioni giro completo 5mm - 640 pulsazioni 1mm 
void sposta_millimetro(boolean direzione, int velocita){
  digitalWrite(ENA,true);
  for(int i=0;i<427;i++)
    ruota(direzione, velocita);
}

movimento lettura_parametri(){
  String direzione;
  String millimetri;
  String velocita;
  movimento m;
  m.velocita=0;
  m.millimetri=0;
  if (Serial.available() > 0) {
        String s = Serial.readString();
        int ind = s.indexOf(" ");
        direzione = s.substring(0, ind);
        int ind1 = s.indexOf(" ", ind+1);
        millimetri = s.substring(ind+1,ind1);
        velocita = s.substring(ind1+1,s.length());
        Serial.print("Direzione: ");
        Serial.println(direzione);
        Serial.print("Millimetri: ");
        Serial.println(millimetri);
        Serial.print("Velocita: ");
        Serial.println(velocita);
        if (direzione == "su")
          m.direzione = true;
        if (direzione == "giu")
          m.direzione = false;
        if (m.direzione){
           int mancante = 309 - millimetri_totali;
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
           if (millimetri.toInt() > millimetri_totali){
              Serial.print("ERRORE!!! PER ARRIVARE A INIZIO CORSA CI SONO SOLO ");
              Serial.print(millimetri_totali);
              Serial.println(" MILLIMETRI");
              Serial.println("TI PORTO A INIZIO CORSA!!");
              m.millimetri = millimetri_totali;   
           }
           else
              m.millimetri = millimetri.toInt();
        }
        if (velocita.toInt() < 32){
          Serial.println("ERRORE!! VELOCITA TROPPO BASSA, IMPOSTO 32 CHE È IL MININMO"); 
          m.velocita = 32;
        }
        else
          m.velocita = velocita.toInt();
  }
  return m;
}
