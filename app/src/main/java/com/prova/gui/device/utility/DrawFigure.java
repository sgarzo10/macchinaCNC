package com.prova.gui.device.utility;

import com.prova.gui.device.activity.AscoltatoreConnectionActivity;
import java.util.ArrayList;

//Do sempre angoli esterni
public class DrawFigure {

    private AscoltatoreConnectionActivity ascoltatore;

    public DrawFigure(AscoltatoreConnectionActivity ascoltatore){
        this.ascoltatore = ascoltatore;
    }

    public void disegnaRettangolo(double larghezza, double altezza, double profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add("mzs" + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        double giri = ascoltatore.getApp().getManageXml().getDiametro();
        float init_x = ascoltatore.getPosizioni()[0];
        float init_y = ascoltatore.getPosizioni()[1];
        altezza = altezza - giri;
        larghezza = larghezza - giri;
        if (riempi)
            giri = Math.min(altezza, larghezza);
        for (double j = 0; j < profondita; j = j + ascoltatore.getApp().getManageXml().getPrecisioni().get(2)) {
            ArrayList<String> messaggi2d = new ArrayList<>();
            for (double i = 0; i < giri; i = i + ascoltatore.getApp().getManageXml().getDiametro() * 2) {
                if (i > 0) {
                    messaggi2d.add("mxg" + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[0])));
                    messaggi2d.add("mys" + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                }
                messaggi2d.addAll(disegnaLinea(larghezza - i, 0));
                messaggi2d.addAll(disegnaLinea(altezza - i, 90));
                messaggi2d.addAll(disegnaLinea(larghezza - i, 180));
                messaggi2d.addAll(disegnaLinea(altezza - i, 270));
            }
            messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
        }
        messaggi.addAll(resetPostDraw());
        ascoltatore.addMex(messaggi);
    }

    public void disegnaTriangolo(double base, double lato1, double lato2, double profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add("mzs" + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        double giri = ascoltatore.getApp().getManageXml().getDiametro();
        float init_x = ascoltatore.getPosizioni()[0];
        float init_y = ascoltatore.getPosizioni()[1];
        long angolo1 = Math.round(Math.toDegrees(Math.acos((lato1 * lato1 + lato2 * lato2 - base * base) / (2d * lato1 * lato2))));
        long angolo2 = Math.round(Math.toDegrees(Math.acos((base * base + lato2 * lato2 - lato1 * lato1) / (2d * base * lato2))));
        long angolo3 = 180 - angolo1 - angolo2;
        if (!(angolo1 == 0 || angolo2 == 0 || angolo3 == 0)) {
            base = base - giri;
            lato1 = lato1 - giri;
            lato2 = lato2 - giri;
            if (riempi)
                giri = Math.min(Math.min(base, lato1), lato2);
            for (double j = 0; j < profondita; j = j + ascoltatore.getApp().getManageXml().getPrecisioni().get(2)) {
                ArrayList<String> messaggi2d = new ArrayList<>();
                for (double i = 0; i < giri; i = i + ascoltatore.getApp().getManageXml().getDiametro() * 2) {
                    if (i > 0) {
                        messaggi2d.add("mxg" + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[0])));
                        messaggi2d.add("mys" + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                    }
                    messaggi2d.addAll(disegnaLinea(base - i, 0));
                    messaggi2d.addAll(disegnaLinea(lato1 - i, 180 - angolo3));
                    messaggi2d.addAll(disegnaLinea(lato2 - i, 270 - (angolo1 - (90 - angolo3))));
                }
                messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
            }
            messaggi.addAll(resetPostDraw());
            ascoltatore.addMex(messaggi);
        }
    }

    public void disegnaParallelo(double base, double diagonale, double altezza, double profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add("mzs" + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        double giri = ascoltatore.getApp().getManageXml().getDiametro();
        float init_x = ascoltatore.getPosizioni()[0];
        float init_y = ascoltatore.getPosizioni()[1];
        if (diagonale >= altezza) {
            long angolo = Math.round(90 - Math.toDegrees(Math.acos(altezza / diagonale)));
            base = base - giri;
            diagonale = diagonale - giri;
            if (riempi)
                giri = Math.min(base, diagonale);
            for (double j = 0; j < profondita; j = j + ascoltatore.getApp().getManageXml().getPrecisioni().get(2)) {
                ArrayList<String> messaggi2d = new ArrayList<>();
                for (double i = 0; i < giri; i = i + ascoltatore.getApp().getManageXml().getDiametro() * 2) {
                    if (i > 0) {
                        messaggi2d.add("mxg" + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[0])));
                        messaggi2d.add("mys" + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                    }
                    messaggi2d.addAll(disegnaLinea(base - i, 0));
                    messaggi2d.addAll(disegnaLinea((int) diagonale - i, angolo));
                    messaggi2d.addAll(disegnaLinea(base - i, 180));
                    messaggi2d.addAll(disegnaLinea((int) diagonale - i, 180 + angolo));
                }
                messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
            }
            messaggi.addAll(resetPostDraw());
            ascoltatore.addMex(messaggi);
        }
    }

    public void disegnaTrapezio(double baseMaggiore, double lato1, double baseMinore, double lato2, double altezza, double profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add("mzs" + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        double giri = ascoltatore.getApp().getManageXml().getDiametro();
        float init_x = ascoltatore.getPosizioni()[0];
        float init_y = ascoltatore.getPosizioni()[1];
        if (lato2 >= altezza && lato1 >= altezza && baseMinore <= baseMaggiore) {
            double angolo1 = Math.toDegrees(Math.acos(altezza / lato1));
            double angolo2 = Math.toDegrees(Math.acos(altezza / lato2));
            double tot = baseMinore + Math.round(lato1 * Math.sin(Math.toRadians(angolo1))) + Math.round(lato2 * Math.sin(Math.toRadians(angolo2)));
            if (tot == baseMaggiore) {
                baseMaggiore = baseMaggiore - giri;
                lato1 = lato1 - giri;
                baseMinore = baseMinore - giri;
                lato2 = lato2 - giri;
                if (riempi)
                    giri = Math.min(Math.min(Math.min(baseMaggiore, lato1), baseMinore), lato2);
                for (double j = 0; j < profondita; j = j + ascoltatore.getApp().getManageXml().getPrecisioni().get(2)) {
                    ArrayList<String> messaggi2d = new ArrayList<>();
                    for (double i = 0; i < giri; i = i + ascoltatore.getApp().getManageXml().getDiametro() * 2) {
                        if (i > 0) {
                            messaggi2d.add("mxg" + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[0])));
                            messaggi2d.add("mys" + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                        }
                        messaggi2d.addAll(disegnaLinea(baseMaggiore - 1, 0));
                        messaggi2d.addAll(disegnaLinea((int) lato1 - 1, Math.round(90 + angolo1)));
                        messaggi2d.addAll(disegnaLinea(baseMinore - 1, 180));
                        messaggi2d.addAll(disegnaLinea((int) lato2 - 1, Math.round(270 - angolo2)));
                    }
                    messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
                }
                messaggi.addAll(resetPostDraw());
                ascoltatore.addMex(messaggi);
            }
        }
    }

    public void disegnaCerchio(double raggio, double profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add("mzs" + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        double giri = ascoltatore.getApp().getManageXml().getDiametro();
        float init_x = ascoltatore.getPosizioni()[0];
        float init_y = ascoltatore.getPosizioni()[1];
        if (riempi)
            giri = raggio;
        for (double j = 0; j < profondita; j = j + ascoltatore.getApp().getManageXml().getPrecisioni().get(2)) {
            ArrayList<String> messaggi2d = new ArrayList<>();
            for (double i = raggio; i > raggio - giri; i = i - ascoltatore.getApp().getManageXml().getDiametro()) {
                if (i < raggio)
                    messaggi2d.add("myg" + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                messaggi2d.addAll(disegnaSemiCerchio(i, "mxg", "myg"));
                messaggi2d.addAll(disegnaSemiCerchio(i, "myg", "mxs"));
                messaggi2d.addAll(disegnaSemiCerchio(i, "mxs", "mys"));
                messaggi2d.addAll(disegnaSemiCerchio(i, "mys", "mxg"));
            }
            messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
        }
        messaggi.addAll(resetPostDraw());
        ascoltatore.addMex(messaggi);
    }

    public ArrayList<String> disegnaLineaProfonda(double lunghezza, long angolo, double profondita) {
        ArrayList<String> messaggi = new ArrayList<>();
        float init_x = ascoltatore.getPosizioni()[0];
        float init_y = ascoltatore.getPosizioni()[1];
        messaggi.add("mzs" + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        lunghezza = lunghezza - ascoltatore.getApp().getManageXml().getDiametro();
        for (int j = 0; j < profondita; j++) {
            ArrayList<String> messaggi2d = disegnaLinea(lunghezza, angolo);
            messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
        }
        messaggi.addAll(resetPostDraw());
        return messaggi;
    }

    public ArrayList<String> disegnaSemiCerchioProfondo(double raggio, String mex1, String mex2, double profondita) {
        ArrayList<String> messaggi = new ArrayList<>();
        float init_x = ascoltatore.getPosizioni()[0];
        float init_y = ascoltatore.getPosizioni()[1];
        messaggi.add("mzs" + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        for (int j = 0; j < profondita; j++) {
            ArrayList<String> messaggi2d = disegnaSemiCerchio(raggio, mex1, mex2);
            messaggi.addAll(scendi(j, profondita, messaggi2d, init_x, init_y));
        }
        messaggi.addAll(resetPostDraw());
        return messaggi;
    }

    private ArrayList<String> disegnaSemiCerchio(double raggio, String primo, String secondo){
        ArrayList<String> messaggi = new ArrayList<>();
        double myRaggio = raggio;
        double precisione;
        double giriMmX;
        double giriMmY;
        if (primo.contains("x")) {
            precisione = ascoltatore.getApp().getManageXml().getPrecisioni().get(0);
            giriMmX = ascoltatore.getGiriMillimetro()[0];
            giriMmY = ascoltatore.getGiriMillimetro()[1];
        } else {
            precisione = ascoltatore.getApp().getManageXml().getPrecisioni().get(1);
            giriMmX = ascoltatore.getGiriMillimetro()[1];
            giriMmY = ascoltatore.getGiriMillimetro()[0];
        }
        for(double i = precisione; i <= raggio; i = i + precisione){
            double y = Math.sqrt(raggio*raggio - i*i );
            double giri = Math.round(giriMmY*(myRaggio - y));
            if (giri > 0){
                messaggi.add(aggiungiVelocita(secondo + Long.toString(Math.round(giri))));
                myRaggio = myRaggio - (giri / giriMmY);
            }
            messaggi.add(primo + Long.toString(Math.round(giriMmX*precisione)));
        }
        if (myRaggio > 0)
            messaggi.add(aggiungiVelocita(secondo + Long.toString(Math.round(giriMmY * myRaggio))));
        return messaggi;
    }

    private ArrayList<String> disegnaLinea(double lunghezza, long angolo) {
        ArrayList<String> messaggi = new ArrayList<>();
        if (angolo == 0 || angolo == 90 || angolo == 180 || angolo == 270) {
            String messaggio = "mxg";
            if (angolo == 90)
                messaggio = "mys";
            if (angolo == 180)
                messaggio = "mxs";
            if (angolo == 270)
                messaggio = "myg";
            if (messaggio.contains("x"))
                messaggi.add(aggiungiVelocita(messaggio + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[0] * lunghezza))));
            else
                messaggi.add(aggiungiVelocita(messaggio + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[1] * lunghezza))));
        } else {
            double base = 0;
            double coeff = Math.abs(Math.tan(Math.toRadians(angolo)));
            String messaggioX = "";
            String messaggioY = "";
            if (angolo > 0 && angolo < 90){
                messaggioX = "mxg";
                messaggioY = "mys";
                base = lunghezza*Math.cos(Math.toRadians(angolo));
            }
            if (angolo > 90 && angolo < 180){
                messaggioX = "mxs";
                messaggioY = "mys";
                base = lunghezza*Math.cos(Math.toRadians(180 - angolo));
            }
            if (angolo > 180 && angolo < 270){
                messaggioX = "mxs";
                messaggioY = "myg";
                base = lunghezza*Math.cos(Math.toRadians(angolo - 180));
            }
            if (angolo > 270 && angolo < 359){
                messaggioX = "mxg";
                messaggioY = "myg";
                base = lunghezza*Math.cos(Math.toRadians(360 - angolo));
            }
            double currentY = 0;
            double precisione = ascoltatore.getApp().getManageXml().getPrecisioni().get(0);
            for(double i = precisione; i <= base; i = i + precisione){
                double y = coeff * i;
                double giri = Math.round(ascoltatore.getGiriMillimetro()[1]*(y - currentY));
                if ( giri > 0) {
                    messaggi.add(aggiungiVelocita(messaggioY + Long.toString(Math.round(giri))));
                    currentY = currentY + (giri / ascoltatore.getGiriMillimetro()[1]);
                }
                messaggi.add(messaggioX + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[0]*precisione)));
            }
        }
        return messaggi;
    }

    private ArrayList<String> scendi(double j, double profondita, ArrayList<String> messaggi2d, float x, float y){
        ArrayList<String> messaggi = new ArrayList<>(messaggi2d);
        if (j + ascoltatore.getApp().getManageXml().getPrecisioni().get(2) < profondita) {
            ascoltatore.simulaDisegno(messaggi2d);
            ArrayList<String> posiziona = new ArrayList<>(ascoltatore.posiziona(x, y, ascoltatore.getPosizioni()[2] + ascoltatore.getApp().getManageXml().getPrecisioni().get(2)));
            ascoltatore.simulaDisegno(posiziona);
            messaggi.addAll(posiziona);
        }
        return messaggi;
    }

    private String aggiungiVelocita(String message){
        if (Long.parseLong(message.substring(3, message.length())) > 1)
            message = message + "." + Integer.toString(ascoltatore.getApp().getManageXml().getVelocita());
        return message;
    }

    private ArrayList<String> resetPostDraw(){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add("rez");
        messaggi.add("rey");
        messaggi.add("da");
        return messaggi;
    }
}
