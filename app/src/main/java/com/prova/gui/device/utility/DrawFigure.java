package com.prova.gui.device.utility;

import android.util.Log;

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
        messaggi.add(ascoltatore.getSpostamenti().get("mzs") + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        double giri = ascoltatore.getApp().getManageXml().getDiametro();
        float init_x = ascoltatore.getPosizioni()[0];
        float init_y = ascoltatore.getPosizioni()[1];
        ArrayList<String> messaggi2d = new ArrayList<>();
        altezza = altezza - giri;
        larghezza = larghezza - giri;
        if (riempi) {
            messaggi2d.addAll(disegnaLinea(larghezza, 0));
            messaggi2d.addAll(disegnaLinea(altezza, 90));
            messaggi2d.addAll(disegnaLinea(larghezza, 180));
            messaggi2d.addAll(disegnaLinea(altezza, 270));
            messaggi2d.add(ascoltatore.getSpostamenti().get("mxg") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[0])));
            messaggi2d.add(ascoltatore.getSpostamenti().get("mys") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
            double diff = altezza - giri * 2;
            for (int j = 0; diff > 0; j++) {
                if (j % 2 == 0)
                    messaggi2d.addAll(disegnaLinea(larghezza - giri * 2, 0));
                else
                    messaggi2d.addAll(disegnaLinea(larghezza - giri * 2, 180));
                messaggi2d.add(ascoltatore.getSpostamenti().get("mys") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                diff -= giri;
            }
        } else {
            /*
            giri = Math.min(altezza, larghezza);
            for (double i = 0; i < giri; i = i + ascoltatore.getApp().getManageXml().getDiametro() * 2) {
                if (i > 0) {
                    messaggi2d.add(ascoltatore.getSpostamenti().get("mxg") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[0])));
                    messaggi2d.add(ascoltatore.getSpostamenti().get("mys") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                }
                messaggi2d.addAll(disegnaLinea(larghezza - i, 0));
                messaggi2d.addAll(disegnaLinea(altezza - i, 90));
                messaggi2d.addAll(disegnaLinea(larghezza - i, 180));
                messaggi2d.addAll(disegnaLinea(altezza - i, 270));
            }*/
            messaggi2d.addAll(disegnaLinea(larghezza, 0));
            messaggi2d.addAll(disegnaLinea(altezza, 90));
            messaggi2d.addAll(disegnaLinea(larghezza, 180));
            messaggi2d.addAll(disegnaLinea(altezza, 270));
        }
        if (profondita > ascoltatore.getApp().getManageXml().getPrecisioni().get(2)) {
            messaggi.addAll(simulaAndPosiziona(messaggi2d, init_x, init_y));
            messaggi.add("*" + Integer.toString((int) (profondita / ascoltatore.getApp().getManageXml().getPrecisioni().get(2))) + "-" + Integer.toString(messaggi.size() - messaggi2d.size() - 1));
        } else
            messaggi.addAll(messaggi2d);
        messaggi.addAll(resetPostDraw());
        ascoltatore.addMex(messaggi);
    }

    public void disegnaTriangolo(double base, double lato1, double lato2, double profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add(ascoltatore.getSpostamenti().get("mzs") + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        double giri = ascoltatore.getApp().getManageXml().getDiametro();
        float init_x = ascoltatore.getPosizioni()[0];
        float init_y = ascoltatore.getPosizioni()[1];
        long angolo1 = Math.round(Math.toDegrees(Math.acos((lato1 * lato1 + lato2 * lato2 - base * base) / (2d * lato1 * lato2))));
        long angolo2 = Math.round(Math.toDegrees(Math.acos((base * base + lato2 * lato2 - lato1 * lato1) / (2d * base * lato2))));
        long angolo3 = 180 - angolo1 - angolo2;
        ArrayList<String> messaggi2d = new ArrayList<>();
        if (!(angolo1 == 0 || angolo2 == 0 || angolo3 == 0)) {
            base = base - giri;
            lato1 = lato1 - giri;
            lato2 = lato2 - giri;
            if (riempi) {
                giri = Math.min(Math.min(base, lato1), lato2);
                messaggi2d.addAll(disegnaLinea(base, 0));
                messaggi2d.addAll(disegnaLinea(lato1, 180 - angolo3));
                messaggi2d.addAll(disegnaLinea(lato2, 270 - (angolo1 - (90 - angolo3))));
                messaggi2d.add(ascoltatore.getSpostamenti().get("mxg") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[0])));
                messaggi2d.add(ascoltatore.getSpostamenti().get("mys") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                double diff = ascoltatore.getApp().getManageXml().getDiametro() * 2;
                for (int j = 0; diff < giri; j++) {
                    if (j % 2 == 0)
                        messaggi2d.addAll(disegnaLinea(base - diff, 0));
                    else
                        messaggi2d.addAll(disegnaLinea(base - diff, 180));
                    messaggi2d.add(ascoltatore.getSpostamenti().get("mys") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                    diff += ascoltatore.getApp().getManageXml().getDiametro() * 2;
                }
            } else {
                /*for (double i = 0; i < giri; i = i + ascoltatore.getApp().getManageXml().getDiametro() * 2) {
                    if (i > 0) {
                        messaggi2d.add(ascoltatore.getSpostamenti().get("mxg") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[0])));
                        messaggi2d.add(ascoltatore.getSpostamenti().get("mys") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                    }
                    messaggi2d.addAll(disegnaLinea(base - i, 0));
                    messaggi2d.addAll(disegnaLinea(lato1 - i, 180 - angolo3));
                    messaggi2d.addAll(disegnaLinea(lato2 - i, 270 - (angolo1 - (90 - angolo3))));
                }*/
                messaggi2d.addAll(disegnaLinea(base, 0));
                messaggi2d.addAll(disegnaLinea(lato1, 180 - angolo3));
                messaggi2d.addAll(disegnaLinea(lato2, 270 - (angolo1 - (90 - angolo3))));
            }
            if (profondita > ascoltatore.getApp().getManageXml().getPrecisioni().get(2)) {
                messaggi.addAll(simulaAndPosiziona(messaggi2d, init_x, init_y));
                messaggi.add("*" + Integer.toString((int) (profondita / ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
            } else
                messaggi.addAll(messaggi2d);
            messaggi.addAll(resetPostDraw());
            ascoltatore.addMex(messaggi);
        }
    }

    public void disegnaParallelo(double base, double diagonale, double altezza, double profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add(ascoltatore.getSpostamenti().get("mzs") + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        double giri = ascoltatore.getApp().getManageXml().getDiametro();
        float init_x = ascoltatore.getPosizioni()[0];
        float init_y = ascoltatore.getPosizioni()[1];
        if (diagonale >= altezza) {
            long angolo = Math.round(90 - Math.toDegrees(Math.acos(altezza / diagonale)));
            base = base - giri;
            diagonale = diagonale - giri;
            if (riempi)
                giri = Math.min(base, diagonale);
            ArrayList<String> messaggi2d = new ArrayList<>();
            for (double i = 0; i < giri; i = i + ascoltatore.getApp().getManageXml().getDiametro() * 2) {
                if (i > 0) {
                    messaggi2d.add(ascoltatore.getSpostamenti().get("mxg") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[0])));
                    messaggi2d.add(ascoltatore.getSpostamenti().get("mys") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                }
                messaggi2d.addAll(disegnaLinea(base - i, 0));
                messaggi2d.addAll(disegnaLinea((int) diagonale - i, angolo));
                messaggi2d.addAll(disegnaLinea(base - i, 180));
                messaggi2d.addAll(disegnaLinea((int) diagonale - i, 180 + angolo));
            }
            if (profondita > ascoltatore.getApp().getManageXml().getPrecisioni().get(2)) {
                messaggi.addAll(simulaAndPosiziona(messaggi2d, init_x, init_y));
                messaggi.add("*" + Integer.toString((int) (profondita / ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
            } else
                messaggi.addAll(messaggi2d);
            messaggi.addAll(resetPostDraw());
            ascoltatore.addMex(messaggi);
        }
    }

    public void disegnaTrapezio(double baseMaggiore, double lato1, double baseMinore, double lato2, double altezza, double profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add(ascoltatore.getSpostamenti().get("mzs") + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
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
                ArrayList<String> messaggi2d = new ArrayList<>();
                for (double i = 0; i < giri; i = i + ascoltatore.getApp().getManageXml().getDiametro() * 2) {
                    if (i > 0) {
                        messaggi2d.add(ascoltatore.getSpostamenti().get("mxg") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[0])));
                        messaggi2d.add(ascoltatore.getSpostamenti().get("mys") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                    }
                    messaggi2d.addAll(disegnaLinea(baseMaggiore - 1, 0));
                    messaggi2d.addAll(disegnaLinea((int) lato1 - 1, Math.round(90 + angolo1)));
                    messaggi2d.addAll(disegnaLinea(baseMinore - 1, 180));
                    messaggi2d.addAll(disegnaLinea((int) lato2 - 1, Math.round(270 - angolo2)));
                }
                if (profondita > ascoltatore.getApp().getManageXml().getPrecisioni().get(2)) {
                    messaggi.addAll(simulaAndPosiziona(messaggi2d, init_x, init_y));
                    messaggi.add("*" + Integer.toString((int) (profondita / ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
                } else
                    messaggi.addAll(messaggi2d);
                messaggi.addAll(resetPostDraw());
                ascoltatore.addMex(messaggi);
            }
        }
    }

    public void disegnaCerchio(double raggio, double profondita, boolean riempi){
        ArrayList<String> messaggi = new ArrayList<>();
        messaggi.add(ascoltatore.getSpostamenti().get("mzs") + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        double giri = ascoltatore.getApp().getManageXml().getDiametro();
        float init_x = ascoltatore.getPosizioni()[0];
        float init_y = ascoltatore.getPosizioni()[1];
        if (riempi)
            giri = raggio;
        ArrayList<String> messaggi2d = new ArrayList<>();
        for (double i = raggio; i > raggio - giri; i = i - ascoltatore.getApp().getManageXml().getDiametro()) {
            if (i < raggio)
                messaggi2d.add(ascoltatore.getSpostamenti().get("myg") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
            messaggi2d.addAll(disegnaSemiCerchio(i, "mxg", "myg"));
            messaggi2d.addAll(disegnaSemiCerchio(i, "myg", "mxs"));
            messaggi2d.addAll(disegnaSemiCerchio(i, "mxs", "mys"));
            messaggi2d.addAll(disegnaSemiCerchio(i, "mys", "mxg"));
        }
        if (profondita > ascoltatore.getApp().getManageXml().getPrecisioni().get(2)) {
            messaggi.addAll(simulaAndPosiziona(messaggi2d, init_x, init_y));
            messaggi.add("*" + Integer.toString((int) (profondita / ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        } else
            messaggi.addAll(messaggi2d);
        messaggi.addAll(resetPostDraw());
        ascoltatore.addMex(messaggi);
    }

    public ArrayList<String> disegnaLineaProfonda(double lunghezza, long angolo, double profondita) {
        ArrayList<String> messaggi = new ArrayList<>();
        double avanzamento = 0;
        lunghezza = lunghezza - ascoltatore.getApp().getManageXml().getDiametro();
        for (int j = 0; avanzamento < profondita; j++){
            messaggi.add(ascoltatore.getSpostamenti().get("mzs") + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
            if (j % 2 == 0)
                messaggi.addAll(disegnaLinea(lunghezza, angolo));
            else
                messaggi.addAll(disegnaLinea(lunghezza, angolo + 180));
            avanzamento += ascoltatore.getApp().getManageXml().getPrecisioni().get(2);
        }
        messaggi.addAll(resetPostDraw());
        return messaggi;
    }

    public ArrayList<String> disegnaSemiCerchioProfondo(double raggio, String mex1, String mex2, double profondita) {
        ArrayList<String> messaggi = new ArrayList<>();
        float init_x = ascoltatore.getPosizioni()[0];
        float init_y = ascoltatore.getPosizioni()[1];
        messaggi.add(ascoltatore.getSpostamenti().get("mzs") + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[2]*ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        ArrayList<String> messaggi2d = disegnaSemiCerchio(raggio, mex1, mex2);
        if (profondita > ascoltatore.getApp().getManageXml().getPrecisioni().get(2)) {
            messaggi.addAll(simulaAndPosiziona(messaggi2d, init_x, init_y));
            messaggi.add("*" + Integer.toString((int) (profondita / ascoltatore.getApp().getManageXml().getPrecisioni().get(2))));
        } else
            messaggi.addAll(messaggi2d);
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
                messaggi.add(aggiungiVelocita(ascoltatore.getSpostamenti().get(secondo) + Long.toString(Math.round(giri))));
                myRaggio = myRaggio - (giri / giriMmY);
            }
            messaggi.add(ascoltatore.getSpostamenti().get(primo) + Long.toString(Math.round(giriMmX*precisione)));
        }
        if (myRaggio > 0)
            messaggi.add(aggiungiVelocita(ascoltatore.getSpostamenti().get(secondo) + Long.toString(Math.round(giriMmY * myRaggio))));
        return messaggi;
    }

    private ArrayList<String> disegnaLinea(double lunghezza, long angolo) {
        Log.i("LINEA",Double.toString(lunghezza) + "-" + Long.toString(angolo));
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
                messaggi.add(aggiungiVelocita(ascoltatore.getSpostamenti().get(messaggio) + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[0] * lunghezza))));
            else
                messaggi.add(aggiungiVelocita(ascoltatore.getSpostamenti().get(messaggio) + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[1] * lunghezza))));
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
            double maxGiri = 0;
            double giri;
            double y;
            for(double i = precisione; i <= base; i = i + precisione){
                y = coeff * i;
                giri = Math.round(ascoltatore.getGiriMillimetro()[1]*(y - currentY));
                if ( giri > 0) {
                    if (giri > maxGiri)
                        maxGiri = giri;
                    currentY = currentY + (maxGiri / ascoltatore.getGiriMillimetro()[1]);
                }
            }
            messaggi.add(aggiungiVelocita(ascoltatore.getSpostamenti().get(messaggioY) + Long.toString(Math.round(maxGiri))) + "-" + ascoltatore.getSpostamenti().get(messaggioX) + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[0]*precisione)) + "#" + Integer.toString((int)(base / precisione)));
        }
        return messaggi;
    }

    private ArrayList<String> simulaAndPosiziona(ArrayList<String> messaggi2d, float x, float y){
        ArrayList<String> messaggi = new ArrayList<>(messaggi2d);
        ascoltatore.simulaDisegno(messaggi2d);
        ArrayList<String> posiziona = new ArrayList<>(ascoltatore.posiziona(x, y, ascoltatore.getPosizioni()[2]));
        ascoltatore.simulaDisegno(posiziona);
        messaggi.addAll(posiziona);
        return messaggi;
    }

    private String aggiungiVelocita(String message){
        if (ascoltatore.getApp().getManageXml().getVelocita() > 32)
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
