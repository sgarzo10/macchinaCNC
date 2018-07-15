package com.prova.gui.device.utility;

import android.util.Log;
import com.prova.bluetooth.R;
import com.prova.gui.device.activity.ConnectionActivity;
import java.util.HashMap;
import java.util.Map;

public class MyHandler extends android.os.Handler{

    private StringBuilder sb;
    private ConnectionActivity app;
    private int ok;
    private int ricevuti;
    private boolean fine;
    private Map<String, Integer> map = new HashMap<>();

    public boolean isFine() { return fine; }
    public void setFine() { this.fine = false; }

    public MyHandler(ConnectionActivity app) {
        sb = new StringBuilder("");
        this.app = app;
        ok = 0;
        ricevuti = 0;
        fine = false;
        map.put("x", 0);
        map.put("y", 1);
        map.put("z", 2);
    }

    @Override
    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case 1:
                try {
                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf, 0, msg.arg1);
                    sb.append(strIncom);
                    int endOfLineIndex = sb.indexOf("\r\n");
                    if (endOfLineIndex > 0) {
                        String mex = sb.substring(0, endOfLineIndex);
                        sb.delete(0, sb.length());
                        Log.i("RECIVE", mex);
                        if (app.getAscoltatore().getMessaggi().get(0).equals("ra")) {
                            if (checkOtherMessagge(mex)) {
                                app.addView("MANDRINO ATTIVATO");
                                app.getAscoltatore().shiftMessaggi();
                            } else
                                app.getBluetooth().invia("ra");
                        }
                        if (app.getAscoltatore().getMessaggi().get(0).equals("rs")){
                            if (checkOtherMessagge(mex)) {
                                app.addView("MANDRINO DISATTIVATO");
                                app.getAscoltatore().shiftMessaggi();
                            } else
                                app.getBluetooth().invia("rs");
                        }
                        if (app.getAscoltatore().getMessaggi().get(0).equals("ssb")){
                            if (checkOtherMessagge(mex)) {
                                app.getAscoltatore().shiftMessaggi();
                                fine = true;
                            } else
                                app.getBluetooth().invia("ssb");
                        }
                        if (app.getAscoltatore().getMessaggi().get(0).contains("slx") || app.getAscoltatore().getMessaggi().get(0).contains("sly") || app.getAscoltatore().getMessaggi().get(0).contains("slz")){
                            if (checkOtherMessagge(mex)) {
                                app.addView("LUNGHEZZA ASSE " + app.getAscoltatore().getMessaggi().get(0).substring(2,3).toUpperCase() + " SETTATA");
                                app.getAscoltatore().shiftMessaggi();
                            } else
                                app.getBluetooth().invia(app.getAscoltatore().getMessaggi().get(0));
                        }
                        if (app.getAscoltatore().getMessaggi().get(0).equals("da")){
                            ricevuti = ricevuti + 1;
                            if (checkMessaggePosLung(mex)) {
                                ok = ok +1;
                                app.getAscoltatore().getPosizioni()[map.get(mex.substring(0,1))] = Integer.parseInt(mex.substring(1,mex.length()));
                            } else
                                ok = 0;
                            if (ricevuti == 3) {
                                ricevuti = 0;
                                if (ok == 3) {
                                    app.addView("POSIZIONI OTTENUTE CORRETTAMENTE");
                                    app.getPosizioni().setText(String.format(app.getResources().getString(R.string.output_posizioni), app.getAscoltatore().getPosizioni()[0], app.getAscoltatore().getPosizioni()[1], app.getAscoltatore().getPosizioni()[2]));
                                    app.getQuadratoView().drawPoint(app.getAscoltatore().getPosizioni()[0], app.getAscoltatore().getPosizioni()[1]);
                                    app.getAscoltatore().shiftMessaggi();
                                    fine = true;
                                }
                                else
                                    app.getBluetooth().invia("da");
                                ok = 0;
                            }
                        }
                        if (app.getAscoltatore().getMessaggi().get(0).equals("la")){
                            ricevuti = ricevuti + 1;
                            if(checkMessaggePosLung(mex)) {
                                ok = ok +1;
                                app.getAscoltatore().getLunghezze()[map.get(mex.substring(0,1))] = Integer.parseInt(mex.substring(1,mex.length()));
                            } else
                                ok = 0;
                            if (ricevuti == 3) {
                                ricevuti = 0;
                                if (ok == 3) {
                                    app.addView("LUNGHEZZE OTTENUTE CORRETTAMENTE");
                                    app.getTextLunghezze().setText(String.format(app.getResources().getString(R.string.output_lunghezze), app.getAscoltatore().getLunghezze()[0], app.getAscoltatore().getLunghezze()[1], app.getAscoltatore().getLunghezze()[2]));
                                    app.getQuadratoView().setMaxX(app.getAscoltatore().getLunghezze()[0]);
                                    app.getQuadratoView().setMaxY(app.getAscoltatore().getLunghezze()[1]);
                                    app.getAscoltatore().shiftMessaggi();
                                    fine = true;
                                }
                                else
                                    app.getBluetooth().invia("la");
                                ok = 0;
                            }
                        }
                        if (app.getAscoltatore().getMessaggi().get(0).equals("rex") || app.getAscoltatore().getMessaggi().get(0).equals("rey") || app.getAscoltatore().getMessaggi().get(0).equals("rez")){
                            if (checkOtherMessagge(mex)) {
                                ok = ok + 1;
                                app.getAscoltatore().getPosizioni()[map.get(mex.substring(1,2))] = 0;
                                app.addView("RESET ASSE " + mex.substring(1,2).toUpperCase() + " EFFETTUATO");
                            } else
                                ok = 0;
                            if (ok == 1) {
                                app.getPosizioni().setText(String.format(app.getResources().getString(R.string.output_posizioni), app.getAscoltatore().getPosizioni()[0], app.getAscoltatore().getPosizioni()[1], app.getAscoltatore().getPosizioni()[2]));
                                if (mex.substring(1,2).equals("x") || mex.substring(1,2).equals("y"))
                                    app.getQuadratoView().drawPoint(app.getAscoltatore().getPosizioni()[0], app.getAscoltatore().getPosizioni()[1]);
                                app.getQuadratoView().pulisci();
                                app.getAscoltatore().shiftMessaggi();
                            } else
                                app.getBluetooth().invia(app.getAscoltatore().getMessaggi().get(0));
                            ok = 0;
                        }
                        if (app.getAscoltatore().getMessaggi().get(0).contains("mzg") || app.getAscoltatore().getMessaggi().get(0).contains("mzs") || app.getAscoltatore().getMessaggi().get(0).contains("mxg") || app.getAscoltatore().getMessaggi().get(0).contains("mxs") || app.getAscoltatore().getMessaggi().get(0).contains("myg") || app.getAscoltatore().getMessaggi().get(0).contains("mys")){
                            int old = app.getAscoltatore().getPosizioni()[map.get(app.getAscoltatore().getMessaggi().get(0).substring(1,2))];
                            if (checkMessaggePosLung(mex)) {
                                ok = ok +1;
                                app.getAscoltatore().getPosizioni()[map.get(mex.substring(0,1))] = Integer.parseInt(mex.substring(1,mex.length()));
                            } else
                                ok = 0;
                            if (ok == 1) {
                                app.getPosizioni().setText(String.format(app.getResources().getString(R.string.output_posizioni), app.getAscoltatore().getPosizioni()[0], app.getAscoltatore().getPosizioni()[1], app.getAscoltatore().getPosizioni()[2]));
                                if (mex.substring(0,1).equals("x") || mex.substring(0,1).equals("y"))
                                    app.getQuadratoView().drawLine(mex.substring(0,1), old, map.get(mex.substring(0,1)));
                                else
                                    app.getQuadratoView().pulisci();
                            }
                            else
                                app.getAscoltatore().simulaAvanzamento(app.getAscoltatore().getMessaggi().get(0), true, old);
                            app.getAscoltatore().shiftMessaggi();
                            ok = 0;
                        }
                    }
                }catch (Exception e){
                    Log.e("EXCEPTION HAND RECIVE", e.getMessage());
                }
                break;
        }
    }

    private boolean checkMessaggePosLung(String mex){
        boolean ok = true;
        if (mex.substring(0, 1).equals("x") || mex.substring(0, 1).equals("y") || mex.substring(0, 1).equals("z")){
                try {
                    if (!(Integer.parseInt(mex.substring(1, mex.length())) >= 0))
                        ok = false;
                } catch (NumberFormatException e) {
                    ok = false;
                }
            }
        else
            ok = false;
        return ok;
    }

    private boolean checkOtherMessagge(String mex){
        boolean ok = false;
        if (app.getAscoltatore().getMessaggi().get(0).substring(0,2).equals("re") && mex.equals("r" + app.getAscoltatore().getMessaggi().get(0).substring(2,3)))
            ok = true;
        if (app.getAscoltatore().getMessaggi().get(0).equals("ra") && mex.equals("am"))
            ok = true;
        if (app.getAscoltatore().getMessaggi().get(0).equals("rs") && mex.equals("sm"))
            ok = true;
        if ((app.getAscoltatore().getMessaggi().get(0).equals("ssb") || app.getAscoltatore().getMessaggi().get(0).contains("slx") || app.getAscoltatore().getMessaggi().get(0).contains("sly") || app.getAscoltatore().getMessaggi().get(0).contains("slz")) && mex.equals("o"))
            ok = true;
        return ok;
    }
}
