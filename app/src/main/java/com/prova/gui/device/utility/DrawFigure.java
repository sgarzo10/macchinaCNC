package com.prova.gui.device.utility;

import com.prova.gui.device.activity.AscoltatoreConnectionActivity;
import java.util.ArrayList;

//Do sempre angoli esterni
public class DrawFigure {

    private AscoltatoreConnectionActivity ascoltatore;

    public DrawFigure(AscoltatoreConnectionActivity ascoltatore){
        this.ascoltatore = ascoltatore;
    }

    public void disegnaRettangolo(int larghezza, int altezza, int profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add("mzs1");
        int giri = 1;
        int init_x = ascoltatore.getPosizioni()[0];
        int init_y = ascoltatore.getPosizioni()[1];
        altezza = altezza - ascoltatore.getApp().getManageXml().getDiametro();
        larghezza = larghezza - ascoltatore.getApp().getManageXml().getDiametro();
        if (riempi)
            giri = Math.min(altezza, larghezza);
        for (int j = 0; j < profondita; j++) {
            ArrayList<String> messaggi2d = new ArrayList<>();
            for (int i = 0; i < giri; i = i + 2) {
                if (i > 0) {
                    messaggi2d.add("mxg1");
                    messaggi2d.add("mys1");
                }
                messaggi2d.addAll(disegnaLinea(larghezza - i, 0));
                messaggi2d.addAll(disegnaLinea(altezza - i, 90));
                messaggi2d.addAll(disegnaLinea(larghezza - i, 180));
                messaggi2d.addAll(disegnaLinea(altezza - i, 270));
            }
            messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
        }
        ascoltatore.addMex(messaggi);
    }

    public void disegnaTriangolo(int base, int lato1, int lato2, int profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add("mzs1");
        int giri = 1;
        int init_x = ascoltatore.getPosizioni()[0];
        int init_y = ascoltatore.getPosizioni()[1];
        long angolo1 = Math.round(Math.toDegrees(Math.acos((lato1 * lato1 + lato2 * lato2 - base * base) / (2d * lato1 * lato2))));
        long angolo2 = Math.round(Math.toDegrees(Math.acos((base * base + lato2 * lato2 - lato1 * lato1) / (2d * base * lato2))));
        long angolo3 = 180 - angolo1 - angolo2;
        if (!(angolo1 == 0 || angolo2 == 0 || angolo3 == 0)) {
            base = base - ascoltatore.getApp().getManageXml().getDiametro();
            lato1 = lato1 - ascoltatore.getApp().getManageXml().getDiametro();
            lato2 = lato2 - ascoltatore.getApp().getManageXml().getDiametro();
            if (riempi)
                giri = Math.min(Math.min(base, lato1), lato2);
            for (int j = 0; j < profondita; j++) {
                ArrayList<String> messaggi2d = new ArrayList<>();
                for (int i = 0; i < giri; i = i + 2) {
                    if (i > 0) {
                        messaggi2d.add("mxg1");
                        messaggi2d.add("mys1");
                    }
                    messaggi2d.addAll(disegnaLinea(base - i, 0));
                    messaggi2d.addAll(disegnaLinea(lato1 - i, 180 - angolo3));
                    messaggi2d.addAll(disegnaLinea(lato2 - i, 270 - (angolo1 - (90 - angolo3))));
                }
                messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
            }
            ascoltatore.addMex(messaggi);
        }
    }

    public void disegnaParallelo(int base, double diagonale, double altezza, int profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add("mzs1");
        double giri = 1;
        int init_x = ascoltatore.getPosizioni()[0];
        int init_y = ascoltatore.getPosizioni()[1];
        if (diagonale >= altezza) {
            long angolo = Math.round(90 - Math.toDegrees(Math.acos(altezza / diagonale)));
            base = base - ascoltatore.getApp().getManageXml().getDiametro();
            diagonale = diagonale - ascoltatore.getApp().getManageXml().getDiametro();
            if (riempi)
                giri = Math.min(base, diagonale);
            for (int j = 0; j < profondita; j++) {
                ArrayList<String> messaggi2d = new ArrayList<>();
                for (int i = 0; i < giri; i = i + 2) {
                    if (i > 0) {
                        messaggi2d.add("mxg1");
                        messaggi2d.add("mys1");
                    }
                    messaggi2d.addAll(disegnaLinea(base - i, 0));
                    messaggi2d.addAll(disegnaLinea((int) diagonale - i, angolo));
                    messaggi2d.addAll(disegnaLinea(base - i, 180));
                    messaggi2d.addAll(disegnaLinea((int) diagonale - i, 180 + angolo));
                }
                messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
            }
            ascoltatore.addMex(messaggi);
        }
    }

    public void disegnaTrapezio(int baseMaggiore, double lato1, int baseMinore, double lato2, double altezza, int profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add("mzs1");
        double giri = 1;
        int init_x = ascoltatore.getPosizioni()[0];
        int init_y = ascoltatore.getPosizioni()[1];
        if (lato2 >= altezza && lato1 >= altezza && baseMinore <= baseMaggiore) {
            double angolo1 = Math.toDegrees(Math.acos(altezza / lato1));
            double angolo2 = Math.toDegrees(Math.acos(altezza / lato2));
            long tot = baseMinore + Math.round(lato1 * Math.sin(Math.toRadians(angolo1))) + Math.round(lato2 * Math.sin(Math.toRadians(angolo2)));
            if (tot == baseMaggiore) {
                baseMaggiore = baseMaggiore - ascoltatore.getApp().getManageXml().getDiametro();
                lato1 = lato1 - ascoltatore.getApp().getManageXml().getDiametro();
                baseMinore = baseMinore - ascoltatore.getApp().getManageXml().getDiametro();
                lato2 = lato2 - ascoltatore.getApp().getManageXml().getDiametro();
                if (riempi)
                    giri = Math.min(Math.min(Math.min(baseMaggiore, lato1), baseMinore), lato2);
                for (int j = 0; j < profondita; j++) {
                    ArrayList<String> messaggi2d = new ArrayList<>();
                    for (int i = 0; i < giri; i = i + 2) {
                        if (i > 0) {
                            messaggi2d.add("mxg1");
                            messaggi2d.add("mys1");
                        }
                        messaggi2d.addAll(disegnaLinea(baseMaggiore - 1, 0));
                        messaggi2d.addAll(disegnaLinea((int) lato1 - 1, Math.round(90 + angolo1)));
                        messaggi2d.addAll(disegnaLinea(baseMinore - 1, 180));
                        messaggi2d.addAll(disegnaLinea((int) lato2 - 1, Math.round(270 - angolo2)));
                    }
                    messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
                }
                ascoltatore.addMex(messaggi);
            }
        }
    }

    public void disegnaCerchio(int raggio, int profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add("mzs1");
        int giri = 1;
        int init_x = ascoltatore.getPosizioni()[0];
        int init_y = ascoltatore.getPosizioni()[1];
        if (riempi)
            giri = raggio;
        for (int j = 0; j < profondita; j++) {
            ArrayList<String> messaggi2d = new ArrayList<>();
            for (int i = raggio; i > raggio - giri; i--) {
                if (i < raggio)
                    messaggi2d.add("myg1");
                messaggi2d.addAll(disegnaSemiCerchio(i, "mxg1", "myg1"));
                messaggi2d.addAll(disegnaSemiCerchio(i, "myg1", "mxs1"));
                messaggi2d.addAll(disegnaSemiCerchio(i, "mxs1", "mys1"));
                messaggi2d.addAll(disegnaSemiCerchio(i, "mys1", "mxg1"));
            }
            messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
        }
        ascoltatore.addMex(messaggi);
    }

    public ArrayList<String> disegnaLineaProfonda(int lunghezza, long angolo, int profondita) {
        ArrayList<String> messaggi = new ArrayList<>();
        int init_x = ascoltatore.getPosizioni()[0];
        int init_y = ascoltatore.getPosizioni()[1];
        messaggi.add("mzs1");
        lunghezza = lunghezza - ascoltatore.getApp().getManageXml().getDiametro();
        for (int j = 0; j < profondita; j++) {
            ArrayList<String> messaggi2d = disegnaLinea(lunghezza, angolo);
            messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
        }
        return messaggi;
    }

    public ArrayList<String> disegnaSemiCerchioProfondo(int raggio, String mex1, String mex2, int profondita) {
        ArrayList<String> messaggi = new ArrayList<>();
        int init_x = ascoltatore.getPosizioni()[0];
        int init_y = ascoltatore.getPosizioni()[1];
        messaggi.add("mzs1");
        for (int j = 0; j < profondita; j++) {
            ArrayList<String> messaggi2d = disegnaSemiCerchio(raggio, mex1, mex2);
            messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
        }
        return messaggi;
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
            String messaggio = "mxg1";
            if (angolo == 90)
                messaggio = "mys1";
            if (angolo == 180)
                messaggio = "mxs1";
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
                messaggioX = "mxg1";
                messaggioY = "mys1";
                base = lunghezza*Math.cos(Math.toRadians(angolo));
            }
            if (angolo > 90 && angolo < 180){
                messaggioX = "mxs1";
                messaggioY = "mys1";
                base = lunghezza*Math.cos(Math.toRadians(180 - angolo));
            }
            if (angolo > 180 && angolo < 270){
                messaggioX = "mxs1";
                messaggioY = "myg1";
                base = lunghezza*Math.cos(Math.toRadians(angolo - 180));
            }
            if (angolo > 270 && angolo < 359){
                messaggioX = "mxg1";
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

    private ArrayList<String> scendi(int j, int profondita, ArrayList<String> messaggi2d, int x, int y){
        ArrayList<String> messaggi = new ArrayList<>(messaggi2d);
        if (j + 1 < profondita) {
            ascoltatore.simulaDisegno(messaggi2d);
            ArrayList<String> posiziona = new ArrayList<>(ascoltatore.posiziona(x, y, ascoltatore.getPosizioni()[2] + 1));
            ascoltatore.simulaDisegno(posiziona);
            messaggi.addAll(posiziona);
        }
        return messaggi;
    }
}
