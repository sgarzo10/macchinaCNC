package com.prova.gui.device.utility;

import android.util.Log;

import com.prova.bluetooth.R;
import com.prova.gui.device.activity.ConnectionActivity;

import java.util.HashMap;
import java.util.Map;

public class MyHandler extends android.os.Handler{

    private StringBuilder sb;
    private ConnectionActivity app;
    private Map<String, Integer> map = new HashMap<String, Integer>();

    public MyHandler(ConnectionActivity app) {
        sb = new StringBuilder("");
        this.app = app;
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
                        app.addView(mex);
                        sb.delete(0, sb.length());
                        String[] mexSplit = mex.split(" ");
                        if (mexSplit[0].equals("TI") && mexSplit[1].equals("TROVI")) {
                            app.getAscoltatore().getPosizioni()[map.get(mexSplit[6])] = Integer.parseInt(mexSplit[3]);
                            Log.i("POSIZIONE", mexSplit[6] + " " + mexSplit[3]);
                            if (mex.equals(String.format(app.getResources().getString(R.string.mex_posizioni), Integer.parseInt(mexSplit[3]), mexSplit[6]))) {
                                Log.i("ESITO CHECK", "OK");
                                app.getQuadratoView().drawPoint(app.getAscoltatore().getPosizioni()[0],app.getAscoltatore().getPosizioni()[1]);
                            }
                            else
                                app.getBluetooth().invia("dove all");
                        }
                        if(mexSplit[0].equals("L'ASSE") && mexSplit[3].equals("LUNGO")) {
                            app.getAscoltatore().getLunghezze()[map.get(mexSplit[1])] = Integer.parseInt(mexSplit[4]);
                            Log.i("LUNGHEZZA", mexSplit[1] + " " + mexSplit[4]);
                            if (mex.equals(String.format(app.getResources().getString(R.string.mex_lunghezze), mexSplit[1], Integer.parseInt(mexSplit[4])))) {
                                Log.i("ESITO CHECK", "OK");
                                app.getQuadratoView().setMaxX(app.getAscoltatore().getLunghezze()[0]);
                                app.getQuadratoView().setMaxY(app.getAscoltatore().getLunghezze()[1]);
                            }
                            else
                                app.getBluetooth().invia("lung all");
                        }
                    }
                }catch (Exception e){
                    Log.e("EXCEPTION HAND RECIVE", e.getMessage());
                }
                break;
        }
    }
}
