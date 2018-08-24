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
        messaggi2d.add(disegnaLinea(larghezza, 0));
        messaggi2d.add(disegnaLinea(altezza, 90));
        messaggi2d.add(disegnaLinea(larghezza, 180));
        messaggi2d.add(disegnaLinea(altezza, 270));
        if (riempi) {
            messaggi2d.add(ascoltatore.getSpostamenti().get("mxg") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[0])));
            messaggi2d.add(ascoltatore.getSpostamenti().get("mys") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
            double diff = altezza - giri * 2;
            for (int j = 0; diff > 0; j++) {
                if (j % 2 == 0)
                    messaggi2d.add(disegnaLinea(larghezza - giri * 2, 0));
                else
                    messaggi2d.add(disegnaLinea(larghezza - giri * 2, 180));
                messaggi2d.add(ascoltatore.getSpostamenti().get("mys") + Long.toString(Math.round(ascoltatore.getApp().getManageXml().getDiametro() * ascoltatore.getGiriMillimetro()[1])));
                diff -= giri;
            }
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
            messaggi2d.add(disegnaLinea(base, 0));
            messaggi2d.add(disegnaLinea(lato1, 180 - angolo3));
            messaggi2d.add(disegnaLinea(lato2, 270 - (angolo1 - (90 - angolo3))));
            if (riempi) {
                double altezza = 0, diffDiag = 0, incAltezza;
                double xl1, xl2;
                String temp;
                for (int j = 0; diffDiag < lato1; j++) {
                    if (j % 2 == 0) {
                        temp = disegnaLinea(giri, 90 - (angolo1 - (90 - angolo3)));
                        incAltezza = (Double.parseDouble(temp.split("-")[0].substring(1)) / ascoltatore.getGiriMillimetro()[1]) * Integer.parseInt(temp.split("#")[1]);
                        altezza += incAltezza;
                        xl1 = calcolaXdaY(lato1, 180 - angolo3, altezza);
                        xl2 = calcolaXdaY(lato2, 270 - (angolo1 - (90 - angolo3)), altezza);
                        messaggi2d.add(temp);
                        if (xl1 + xl2 > 0)
                            messaggi2d.add(disegnaLinea(xl1 + xl2, 0));
                    } else {
                        temp = disegnaLinea(giri, 180 - angolo3);
                        incAltezza = (Double.parseDouble(temp.split("-")[0].substring(1)) / ascoltatore.getGiriMillimetro()[1]) * Integer.parseInt(temp.split("#")[1]);
                        altezza += incAltezza;
                        xl1 = calcolaXdaY(lato1, 180 - angolo3, altezza);
                        xl2 = calcolaXdaY(lato2, 270 - (angolo1 - (90 - angolo3)), altezza);
                        messaggi2d.add(temp);
                        if (xl1 + xl2 > 0)
                            messaggi2d.add(disegnaLinea(xl1 + xl2, 180));
                    }
                    diffDiag += giri;
                }
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
            ArrayList<String> messaggi2d = new ArrayList<>();
            long angolo = Math.round(90 - Math.toDegrees(Math.acos(altezza / diagonale)));
            base = base - giri;
            diagonale = diagonale - giri;
            messaggi2d.add(disegnaLinea(base, 0));
            messaggi2d.add(disegnaLinea(diagonale, angolo));
            messaggi2d.add(disegnaLinea(base, 180));
            messaggi2d.add(disegnaLinea(diagonale, 180 + angolo));
            if (riempi) {
                double diff = 0;
                for (int j = 0; diff < diagonale; j++){
                    messaggi2d.add(disegnaLinea(giri , angolo));
                    if (j % 2 == 0)
                        messaggi2d.add(disegnaLinea(base, 0));
                    else
                        messaggi2d.add(disegnaLinea(base, 180));
                    diff += giri;
                }
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
            long angolo1 = Math.round(Math.toDegrees(Math.acos(altezza / lato1)));
            long angolo2 = Math.round(Math.toDegrees(Math.acos(altezza / lato2)));
            double tot = baseMinore + Math.round(lato1 * Math.sin(Math.toRadians(angolo1))) + Math.round(lato2 * Math.sin(Math.toRadians(angolo2)));
            if (tot == baseMaggiore) {
                ArrayList<String> messaggi2d = new ArrayList<>();
                baseMaggiore = baseMaggiore - giri;
                lato1 = lato1 - giri;
                baseMinore = baseMinore - giri;
                lato2 = lato2 - giri;
                messaggi2d.add(disegnaLinea(baseMaggiore, 0));
                messaggi2d.add(disegnaLinea(lato1, 90 + angolo1));
                messaggi2d.add(disegnaLinea(baseMinore, 180));
                messaggi2d.add(disegnaLinea(lato2, 270 - angolo2));
                if (riempi) {
                    double  diffDiag = 0, incAltezza;
                    double xl1, xl2;
                    String temp;
                    altezza = 0;
                    for (int j = 0; diffDiag < lato1; j++) {
                        if (j % 2 == 0) {
                            temp = disegnaLinea(giri, Math.round(90 - angolo2));
                            incAltezza = (Double.parseDouble(temp.split("-")[0].substring(1)) / ascoltatore.getGiriMillimetro()[1]) * Integer.parseInt(temp.split("#")[1]);
                            altezza += incAltezza;
                            xl1 = calcolaXdaY(lato1, 90 + angolo1, altezza);
                            xl2 = calcolaXdaY(lato2, 270 - angolo2, altezza);
                            messaggi2d.add(temp);
                            if (xl1 + xl2 + baseMinore > baseMinore)
                                messaggi2d.add(disegnaLinea(xl1 + xl2 + baseMinore, 0));
                        } else {
                            temp = disegnaLinea(giri, 90 + angolo1);
                            incAltezza = (Double.parseDouble(temp.split("-")[0].substring(1)) / ascoltatore.getGiriMillimetro()[1]) * Integer.parseInt(temp.split("#")[1]);
                            altezza += incAltezza;
                            xl1 = calcolaXdaY(lato1, 90 + angolo1, altezza);
                            xl2 = calcolaXdaY(lato2, 270 - angolo2, altezza);
                            messaggi2d.add(temp);
                            if (xl1 + xl2 + baseMinore > baseMinore)
                                messaggi2d.add(disegnaLinea(xl1 + xl2 + baseMinore, 180));
                        }
                        diffDiag += giri;
                    }
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
        ArrayList<String> messaggi2d = new ArrayList<>();
        messaggi2d.addAll(disegnaSemiCerchio(raggio, "mxg", "myg"));
        messaggi2d.addAll(disegnaSemiCerchio(raggio, "myg", "mxs"));
        messaggi2d.addAll(disegnaSemiCerchio(raggio, "mxs", "mys"));
        messaggi2d.addAll(disegnaSemiCerchio(raggio, "mys", "mxg"));
        if (riempi) {
            int quadrante = 4;
            int j;
            for (j = 0; giri * ( ( j + 1) * 2) < raggio * 2; j++){
                if (quadrante == 1)
                    messaggi2d.addAll(disegnaSemiCerchio(giri, "mxg", "myg"));
                if (quadrante == 4)
                    messaggi2d.addAll(disegnaSemiCerchio(giri, "mxs", "myg"));
                if (j % 2 == 0) {
                    messaggi2d.add(disegnaLinea(giri * ( ( j + 1) * 2), 0));
                    quadrante = 1;
                } else {
                    messaggi2d.add(disegnaLinea(giri * ( ( j + 1) * 2), 180));
                    quadrante = 4;
                }
            }
            if (quadrante == 1)
                quadrante = 2;
            else
                quadrante = 3;
            for (; j > -1; j--){
                if (quadrante == 2)
                    messaggi2d.addAll(disegnaSemiCerchio(giri, "myg", "mxs"));
                if (quadrante == 3)
                    messaggi2d.addAll(disegnaSemiCerchio(giri, "myg", "mxg"));
                if (quadrante == 3) {
                    messaggi2d.add(disegnaLinea(giri * ( ( j + 1) * 2), 0));
                    quadrante = 2;
                } else {
                    messaggi2d.add(disegnaLinea(giri * ( ( j + 1) * 2), 180));
                    quadrante = 3;
                }
            }
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
                messaggi.add(disegnaLinea(lunghezza, angolo));
            else
                messaggi.add(disegnaLinea(lunghezza, angolo + 180));
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

    private String disegnaLinea(double lunghezza, long angolo) {
        Log.i("LINEA",Double.toString(lunghezza) + "-" + Long.toString(angolo));
        String toReturn;
        if (angolo == 0 || angolo == 90 || angolo == 180 || angolo == 270) {
            String messaggio = "mxg";
            if (angolo == 90)
                messaggio = "mys";
            if (angolo == 180)
                messaggio = "mxs";
            if (angolo == 270)
                messaggio = "myg";
            if (messaggio.contains("x"))
                toReturn = aggiungiVelocita(ascoltatore.getSpostamenti().get(messaggio) + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[0] * lunghezza)));
            else
                toReturn = aggiungiVelocita(ascoltatore.getSpostamenti().get(messaggio) + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[1] * lunghezza)));
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
            float precisione = ascoltatore.getApp().getManageXml().getPrecisioni().get(0);
            double maxGiri = 0;
            double giri;
            double y;
            int count = 0;
            for(float i = precisione; i <= base; i = i + precisione){
                y = coeff * i;
                giri = Math.round(ascoltatore.getGiriMillimetro()[1]*(y - currentY));
                if ( giri > 0) {
                    if (giri > maxGiri)
                        maxGiri = giri;
                    currentY = currentY + (maxGiri / ascoltatore.getGiriMillimetro()[1]);
                    count++;
                }
            }
            toReturn = aggiungiVelocita(ascoltatore.getSpostamenti().get(messaggioY) + Long.toString(Math.round(maxGiri))) + "-" + ascoltatore.getSpostamenti().get(messaggioX) + Long.toString(Math.round(ascoltatore.getGiriMillimetro()[0]*precisione)) + "#" + Integer.toString(count);
        }
        return toReturn;
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

    private double calcolaXdaY(double lunghezza, long angolo, double y){
        double coeff = Math.abs(Math.tan(Math.toRadians(angolo)));
        double base = 0;
        if (angolo > 0 && angolo < 90)
            base = lunghezza*Math.cos(Math.toRadians(angolo));
        if (angolo > 90 && angolo < 180)
            base = lunghezza*Math.cos(Math.toRadians(180 - angolo));
        if (angolo > 180 && angolo < 270)
            base = lunghezza*Math.cos(Math.toRadians(angolo - 180));
        if (angolo > 270 && angolo < 359)
            base = lunghezza*Math.cos(Math.toRadians(360 - angolo));
        return base - y / coeff;
    }
}
