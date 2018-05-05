package com.prova.bluetooth;

import android.util.Log;

public class MyHandler extends android.os.Handler{

    private StringBuilder sb;
    private String mexRicevuto;

    public String getMexRicevuto() {return mexRicevuto;}
    public void setMexRicevuto(String mexRicevuto){ this.mexRicevuto = mexRicevuto;}

    MyHandler() {
        sb = new StringBuilder("");
        mexRicevuto = "";
    }

    @Override
    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case 1:
                try {
                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf, 0, msg.arg1);
                    Log.i("RECIVE", strIncom);
                    sb.append(strIncom);
                    int endOfLineIndex = sb.indexOf("\r\n");
                    if (endOfLineIndex > 0) {
                        mexRicevuto = sb.substring(0, endOfLineIndex);
                        sb.delete(0, sb.length());
                    }
                }catch (Exception e){
                    Log.e("EXCEPTION HAND RECIVE", e.getMessage());
                    mexRicevuto = "ERROR";
                }
                break;
        }
    }
}
