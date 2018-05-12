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
    public void setFine(boolean fine) { this.fine = fine; }

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
                    //Log.i("RECIVE", strIncom);
                    sb.append(strIncom);
                    int endOfLineIndex = sb.indexOf("\r\n");
                    if (endOfLineIndex > 0) {
                        String mex = sb.substring(0, endOfLineIndex);
                        sb.delete(0, sb.length());
                        String[] mexSplit = mex.split(" ");
                        if (app.getAscoltatore().getMessaggio().equals("ra") || app.getAscoltatore().getMessaggio().equals("rs"))
                            app.addView(mex);
                        if (app.getAscoltatore().getMessaggio().equals("ssb"))
                            fine = true;
                        if (app.getAscoltatore().getMessaggio().equals("da")){
                            ricevuti = ricevuti + 1;
                            if (mexSplit.length == 4 && mex.equals(String.format(app.getResources().getString(R.string.mex_posizioni), Integer.parseInt(mexSplit[1]), mexSplit[3]))) {
                                ok = ok +1;
                                Log.i("POSIZIONE", mexSplit[3] + " " + mexSplit[1]);
                                app.getAscoltatore().getPosizioni()[map.get(mexSplit[3])] = Integer.parseInt(mexSplit[1]);
                            } else
                                ok = 0;
                            if (ricevuti == 3) {
                                ricevuti = 0;
                                if (ok == 3) {
                                    app.addView("POSIZIONI OTTENUTE CORRETTAMENTE");
                                    app.getPosizioni().setText(String.format(app.getResources().getString(R.string.output_posizioni), app.getAscoltatore().getPosizioni()[0], app.getAscoltatore().getPosizioni()[1], app.getAscoltatore().getPosizioni()[2]));
                                    app.getQuadratoView().drawPoint(app.getAscoltatore().getPosizioni()[0], app.getAscoltatore().getPosizioni()[1]);
                                    fine = true;
                                }
                                else
                                    app.getBluetooth().invia("da");
                                ok = 0;
                            }
                        }
                        if (app.getAscoltatore().getMessaggio().equals("la")){
                            ricevuti = ricevuti + 1;
                            if(mexSplit.length == 4 && mex.equals(String.format(app.getResources().getString(R.string.mex_lunghezze), mexSplit[1], Integer.parseInt(mexSplit[3])))) {
                                ok = ok +1;
                                Log.i("LUNGHEZZA", mexSplit[1] + " " + mexSplit[3]);
                                app.getAscoltatore().getLunghezze()[map.get(mexSplit[1])] = Integer.parseInt(mexSplit[3]);
                            } else
                                ok = 0;
                            if (ricevuti == 3) {
                                ricevuti = 0;
                                if (ok == 3) {
                                    app.addView("LUNGHEZZE OTTENUTE CORRETTAMENTE");
                                    app.getTextLunghezze().setText(String.format(app.getResources().getString(R.string.output_lunghezze), app.getAscoltatore().getLunghezze()[0], app.getAscoltatore().getLunghezze()[1], app.getAscoltatore().getLunghezze()[2]));
                                    app.getQuadratoView().setMaxX(app.getAscoltatore().getLunghezze()[0]);
                                    app.getQuadratoView().setMaxY(app.getAscoltatore().getLunghezze()[1]);
                                    fine = true;
                                }
                                else
                                    app.getBluetooth().invia("la");
                                ok = 0;
                            }
                        }
                        if (app.getAscoltatore().getMessaggio().equals("mzg1") || app.getAscoltatore().getMessaggio().equals("mzs1")){
                            ricevuti = ricevuti + 1;
                            if (mexSplit.length == 4 && mex.equals(String.format(app.getResources().getString(R.string.mex_posizioni), Integer.parseInt(mexSplit[1]), mexSplit[3]))) {
                                ok = ok +1;
                                Log.i("POSIZIONE", mexSplit[3] + " " + mexSplit[1]);
                                app.getAscoltatore().getPosizioni()[map.get(mexSplit[3])] = Integer.parseInt(mexSplit[1]);
                            } else
                                ok = 0;
                            if (ricevuti == 1) {
                                ricevuti = 0;
                                if (ok == 1) {
                                    app.addView("POSIZIONE OTTENUTA CORRETTAMENTE");
                                    app.getPosizioni().setText(String.format(app.getResources().getString(R.string.output_posizioni), app.getAscoltatore().getPosizioni()[0], app.getAscoltatore().getPosizioni()[1], app.getAscoltatore().getPosizioni()[2]));
                                }
                                else
                                    app.getBluetooth().invia("dz");
                                ok = 0;
                            }
                        }
                    }
                }catch (Exception e){
                    Log.e("EXCEPTION HAND RECIVE", e.getMessage());
                }
                break;
        }
    }
}
