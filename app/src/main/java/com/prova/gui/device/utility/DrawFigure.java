package com.prova.gui.device.utility;

import com.prova.gui.device.activity.AscoltatoreConnectionActivity;
import java.util.ArrayList;

//Do sempre sngoli esterni
public class DrawFigure {

    private AscoltatoreConnectionActivity ascoltatore;

    public DrawFigure(AscoltatoreConnectionActivity ascoltatore){
        this.ascoltatore = ascoltatore;
    }

    public void disegnaRettangolo(int larghezza, int altezza, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        int giri = 1;
        if (riempi)
            giri = Math.min(altezza, larghezza);
        for (int i = 0; i < giri; i = i + 2) {
            if (i > 0) {
                messaggi.add("mxs1");
                messaggi.add("mys1");
            }
            messaggi.addAll(disegnaLinea(larghezza - i, 0));
            messaggi.addAll(disegnaLinea(altezza - i, 90));
            messaggi.addAll(disegnaLinea(larghezza - i, 180));
            messaggi.addAll(disegnaLinea(altezza - i, 270));
        }
        ascoltatore.addMex(messaggi);
    }

    public void disegnaTriangolo(int base, int lato1, int lato2, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        int giri = 1;
        long angolo1 = Math.round(Math.toDegrees(Math.acos((lato1 * lato1 + lato2 * lato2 - base * base) / (2d * lato1 * lato2))));
        long angolo2 = Math.round(Math.toDegrees(Math.acos((base * base + lato2 * lato2 - lato1 * lato1) / (2d * base * lato2))));
        long angolo3 = 180 - angolo1 - angolo2;
        if (!(angolo1 == 0 || angolo2 == 0 || angolo3 == 0)) {
            if (riempi)
                giri = Math.min(Math.min(base, lato1), lato2);
            for (int i = 0; i < giri; i = i + 2) {
                if (i > 0){
                    messaggi.add("mxs1");
                    messaggi.add("mys1");
                }
                messaggi.addAll(disegnaLinea(base - i, 0));
                messaggi.addAll(disegnaLinea(lato1 - i, 180 - angolo3));
                messaggi.addAll(disegnaLinea(lato2 - i, 270 - (angolo1 - (90 - angolo3))));
            }
            ascoltatore.addMex(messaggi);
        }
    }

    public void disegnaParallelo(int base, double diagonale, double altezza, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        double giri = 1;
        if (diagonale >= altezza) {
            long angolo = Math.round(90 - Math.toDegrees(Math.acos(altezza / diagonale)));
            if (riempi)
                giri = Math.min(base, diagonale);
            for (int i = 0; i < giri; i = i + 2) {
                if (i > 0) {
                    messaggi.add("mxs1");
                    messaggi.add("mys1");
                }
                messaggi.addAll(disegnaLinea(base - i, 0));
                messaggi.addAll(disegnaLinea((int) diagonale - i, angolo));
                messaggi.addAll(disegnaLinea(base - i, 180));
                messaggi.addAll(disegnaLinea((int) diagonale - i, 180 + angolo));
            }
            ascoltatore.addMex(messaggi);
        }
    }

    public void disegnaTrapezio(int baseMaggiore, double lato1, int baseMinore, double lato2, double altezza, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        double giri = 1;
        if (lato2 >= altezza && lato1 >= altezza && baseMinore <= baseMaggiore) {
            double angolo1 = Math.toDegrees(Math.acos(altezza / lato1));
            double angolo2 = Math.toDegrees(Math.acos(altezza / lato2));
            long tot = baseMinore + Math.round(lato1 * Math.sin(Math.toRadians(angolo1))) + Math.round(lato2 * Math.sin(Math.toRadians(angolo2)));
            if (tot == baseMaggiore) {
                if (riempi)
                    giri = Math.min(Math.min(Math.min(baseMaggiore, lato1), baseMinore), lato2);
                for (int i = 0; i < giri; i = i + 2) {
                    if (i > 0) {
                        messaggi.add("mxs1");
                        messaggi.add("mys1");
                    }
                    messaggi.addAll(disegnaLinea(baseMaggiore - 1, 0));
                    messaggi.addAll(disegnaLinea((int) lato1- 1, Math.round(90 + angolo1)));
                    messaggi.addAll(disegnaLinea(baseMinore - 1, 180));
                    messaggi.addAll(disegnaLinea((int) lato2 - 1, Math.round(270 - angolo2)));
                }
                ascoltatore.addMex(messaggi);
            }
        }
    }

    public void disegnaCerchio(int raggio, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        int giri = 1;
        if (riempi)
            giri = raggio;
        for (int i = raggio; i > raggio - giri; i--) {
            if (i < raggio)
                messaggi.add("myg1");
            messaggi.addAll(disegnaSemiCerchio(i, "mxs1", "myg1"));
            messaggi.addAll(disegnaSemiCerchio(i, "myg1", "mxg1"));
            messaggi.addAll(disegnaSemiCerchio(i, "mxg1", "mys1"));
            messaggi.addAll(disegnaSemiCerchio(i, "mys1", "mxs1"));
        }
        ascoltatore.addMex(messaggi);
    }

    private ArrayList<String> disegnaSemiCerchio(int raggio, String primo, String secondo){
        ArrayList<String> messaggi = new ArrayList<>();
        int myRaggio = raggio;
        for(int i = 1; i <= raggio; i++){
            long y = Math.round(Math.sqrt(raggio*raggio - i*i ));
            if (y == myRaggio)
                messaggi.add(primo);
            if (y < myRaggio) {
                for (int j = 0; j < myRaggio - y; j++)
                    messaggi.add(secondo);
                messaggi.add(primo);
                myRaggio = myRaggio - (myRaggio - (int)y);
            }
        }
        for (int j = 0; j < myRaggio; j++)
            messaggi.add(secondo);
        return messaggi;
    }

    private ArrayList<String> disegnaLinea(int lunghezza, long angolo) {
        ArrayList<String> messaggi = new ArrayList<>();
        if (angolo == 0 || angolo == 90 || angolo == 180 || angolo == 270) {
            String messaggio = "mxs1";
            if (angolo == 90)
                messaggio = "mys1";
            if (angolo == 180)
                messaggio = "mxg1";
            if (angolo == 270)
                messaggio = "myg1";
            for (int i = 0; i < lunghezza; i++)
                messaggi.add(messaggio);
        } else {
            double base = 0;
            double coeff = Math.abs(Math.tan(Math.toRadians(angolo)));
            String messaggioX = "";
            String messaggioY = "";
            if (angolo > 0 && angolo < 90){
                messaggioX = "mxs1";
                messaggioY = "mys1";
                base = lunghezza*Math.cos(Math.toRadians(angolo));
            }
            if (angolo > 90 && angolo < 180){
                messaggioX = "mxg1";
                messaggioY = "mys1";
                base = lunghezza*Math.cos(Math.toRadians(180 - angolo));
            }
            if (angolo > 180 && angolo < 270){
                messaggioX = "mxg1";
                messaggioY = "myg1";
                base = lunghezza*Math.cos(Math.toRadians(angolo - 180));
            }
            if (angolo > 270 && angolo < 359){
                messaggioX = "mxs1";
                messaggioY = "myg1";
                base = lunghezza*Math.cos(Math.toRadians(360 - angolo));
            }
            long currentY = 0;
            for(int i = 1; i <= Math.round(base); i++){
                long y = Math.round(coeff * i);
                for (int j = 0; j < y - currentY; j++)
                    messaggi.add(messaggioY);
                currentY = y;
                messaggi.add(messaggioX);
            }
        }
        return messaggi;
    }
}
