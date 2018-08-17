package com.prova.gui.device.utility;

import android.annotation.SuppressLint;
import android.util.Log;
import com.prova.bluetooth.R;
import com.prova.gui.device.activity.ConnectionActivity;
import java.util.HashMap;
import java.util.Map;

public class MyHandler extends android.os.Handler{

    private StringBuilder sb;
    private ConnectionActivity app;
    private int ok;
    private boolean fine;
    private Map<String, Integer> map = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, String> iMap = new HashMap<>();

    public boolean isFine() { return fine; }
    public void setFine() { this.fine = false; }

    public MyHandler(ConnectionActivity app) {
        sb = new StringBuilder("");
        this.app = app;
        ok = 0;
        fine = false;
        map.put("x", 0);
        map.put("y", 1);
        map.put("z", 2);
        iMap.put(0, "x");
        iMap.put(1, "y");
        iMap.put(2, "z");
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
                        if (app.getAscoltatore().getMessaggio().equals("ra") || app.getAscoltatore().getMessaggio().equals("rs")) {
                            if (mex.equals("o")) {
                                app.addView("MANDRINO");
                                app.getAscoltatore().setMessaggio();
                            } else
                                app.getBluetooth().invia(app.getAscoltatore().getMessaggio());
                            return;
                        }
                        if (app.getAscoltatore().getMessaggio().contains("da")){
                            if (mex.split("&").length == 3) {
                                String[] pos = mex.split("&");
                                for (int i = 0; i < 3; i++) {
                                    if (checkMessaggePosLung(pos[i], false)) {
                                        app.getAscoltatore().getPosizioni()[map.get(pos[i].substring(0, 1))] = Float.parseFloat(pos[i].substring(1, pos[i].length()));
                                        ok = ok + 1;
                                    }
                                }
                                if (ok == 3) {
                                    app.addView("POSIZIONI OTTENUTE CORRETTAMENTE");
                                    for (int i = 0; i < 3; i++)
                                        app.getTextPosizioni().get(i).setText(String.format(app.getResources().getString(R.string.output_posizione), iMap.get(i).toUpperCase(), app.getAscoltatore().getPosizioni()[i]));
                                    app.getAscoltatore().setMessaggio();
                                    fine = true;
                                } else
                                    app.getBluetooth().invia("da");
                                ok = 0;
                            } else
                                app.getBluetooth().invia("da");
                            return;
                        }
                        if (app.getAscoltatore().getMessaggio().equals("ga")){
                            if (mex.split("&").length == 3) {
                                String[] pos = mex.split("&");
                                for (int i = 0; i < 3; i++) {
                                    if (checkMessaggePosLung(pos[i], true)) {
                                        app.getAscoltatore().getGiriMillimetro()[map.get(pos[i].substring(0, 1))] = (int) Float.parseFloat(pos[i].substring(1, pos[i].length()));
                                        ok = ok + 1;
                                    }
                                }
                                if (ok == 3) {
                                    app.addView("GIRI OTTENUTI CORRETTAMENTE");
                                    app.getTextGiri().setText(String.format(app.getResources().getString(R.string.ouput_giri), app.getAscoltatore().getGiriMillimetro()[0], app.getAscoltatore().getGiriMillimetro()[1], app.getAscoltatore().getGiriMillimetro()[2]));
                                    app.getAscoltatore().setMessaggio();
                                    fine = true;
                                } else
                                    app.getBluetooth().invia("ga");
                                ok = 0 ;
                            } else
                                app.getBluetooth().invia("ga");
                            return;
                        }
                        if (app.getAscoltatore().getMessaggio().equals("la")){
                            if(mex.split("&").length == 3) {
                                String[] pos = mex.split("&");
                                for (int i = 0; i < 3; i++) {
                                    if (checkMessaggePosLung(pos[i], true)) {
                                        app.getAscoltatore().getLunghezze()[map.get(pos[i].substring(0, 1))] = (int) Float.parseFloat(pos[i].substring(1, pos[i].length()));
                                        ok = ok +1;
                                    }
                                }
                                if (ok == 3) {
                                    app.addView("LUNGHEZZE OTTENUTE CORRETTAMENTE");
                                    app.getTextLunghezze().setText(String.format(app.getResources().getString(R.string.output_lunghezze), app.getAscoltatore().getLunghezze()[0], app.getAscoltatore().getLunghezze()[1], app.getAscoltatore().getLunghezze()[2]));
                                    app.getAscoltatore().setMessaggio();
                                    fine = true;
                                } else
                                    app.getBluetooth().invia("la");
                                ok = 0;
                            } else
                                app.getBluetooth().invia("la");
                            return;
                        }
                        if (app.getAscoltatore().getMessaggio().contains("sl") || app.getAscoltatore().getMessaggio().contains("sg") || app.getAscoltatore().getMessaggio().equals("ssb")){
                            if (mex.equals("o")) {
                                app.getAscoltatore().setMessaggio();
                                fine = true;
                            } else
                                app.getBluetooth().invia(app.getAscoltatore().getMessaggio());
                            return;
                        }
                        if (app.getAscoltatore().getMessaggio().contains("mz") || app.getAscoltatore().getMessaggio().contains("mx") || app.getAscoltatore().getMessaggio().contains("my")){
                            if (checkMessaggePosLung(mex, false)) {
                                ok = 1;
                                app.getAscoltatore().getPosizioni()[map.get(mex.substring(0,1))] = Float.parseFloat(mex.substring(1,mex.length()));
                            } else
                                ok = 0;
                            if (ok == 1)
                                app.getTextPosizioni().get(map.get(mex.substring(0,1))).setText(String.format(app.getResources().getString(R.string.output_posizione), mex.substring(0,1).toUpperCase(), app.getAscoltatore().getPosizioni()[map.get(mex.substring(0,1))]));
                            else
                                app.getAscoltatore().simulaAvanzamento(app.getAscoltatore().getMessaggio(), true);
                            app.getAscoltatore().setMessaggio();
                            ok = 0;
                            return;
                        }
                    }
                }catch (Exception e){
                    Log.e("EXCEPTION HAND RECIVE", e.getMessage());
                }
                break;
        }
    }

    private boolean checkMessaggePosLung(String mex, boolean lunghezza){
        boolean ok = true;
        if (mex.substring(0, 1).equals("x") || mex.substring(0, 1).equals("y") || mex.substring(0, 1).equals("z")){
            if ((!lunghezza && mex.contains(".") && mex.substring(mex.indexOf("."),mex.length() - 1).length() == 7) || lunghezza) {
                try {
                    if (!(Float.parseFloat(mex.substring(1, mex.length())) >= 0))
                        ok = false;
                } catch (Exception e) {
                    ok = false;
                }
            } else
                ok = false;
        } else
            ok = false;
        return ok;
    }
}
