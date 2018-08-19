package com.prova.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.prova.gui.device.activity.ConnectionActivity;
import com.prova.gui.device.utility.MyHandler;
import java.io.InputStream;
import java.io.OutputStream;


public class BluetoothConnection extends Thread {
    private BluetoothSocket mmSocket;
    private OutputStream outStream;
    private InputStream input;
    private BluetoothAdapter bluetoothAdapter;
    private MyHandler h;
    private ConnectionActivity app;
    private int progresso;
    private String message;

    public MyHandler getH() { return h; }
    public void setProgresso() {this.progresso = 0;}

    public BluetoothConnection (MyHandler h, ConnectionActivity app){
        this.h = h;
        this.app = app;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mmSocket = null;
        outStream = null;
        input = null;
        progresso = 0;
    }

    public boolean connetti(String mac) {
        BluetoothDevice mmDevice = bluetoothAdapter.getRemoteDevice(mac);
        try {
            mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
            mmSocket.connect();
            outStream = mmSocket.getOutputStream();
            input = mmSocket.getInputStream();
            return true;
        } catch (Exception e) {
            Log.e("EXCEPTION CONNECTION",e.getMessage());
            return disconnetti();
        }
    }

    public boolean disconnetti() {
        try {
            mmSocket.close();
            outStream = null;
            input = null;
            return true;
        } catch (Exception e) {
            Log.e("EXCEPTION DISCONNECT",e.getMessage());
            return false;
        }
    }

    public void invia(String toSend) {
        message = toSend + "!";
        new Thread(new Runnable() {

            int size = 63, i;
            String mex;

            public void run() {
                try {
                    app.getNumMex().setText(String.format(app.getResources().getString(R.string.numero_messaggi), message.length()));
                    if (message.length() > size) {
                        for (i = 0; i < message.length(); i = i + size) {
                            if (i + size < message.length()) {
                                mex = message.substring(i, i + size);
                                progresso = ((i + size) * 100) / message.length();
                            } else
                                mex = message.substring(i, message.length());
                            outStream.write(mex.getBytes());
                            if (i == 0)
                                sleep(450);
                            else
                                sleep(320);
                        }
                    } else
                        outStream.write(message.getBytes());
                    progresso = 100;
                } catch (Exception e) {
                    Log.e("EXCEPTION SEND", e.getMessage());
                    app.addView(app.getResources().getString(R.string.error));
                }
            }
        }).start();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[4096];
        int bytes;
        while (true) {
            try {
                if (input != null) {
                    bytes = input.available();
                    if (bytes != 0) {
                        bytes = input.read(buffer, 0, bytes);
                        h.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                    }
                }
                app.getProgressBar().setProgress(progresso);
            } catch (Exception ignored) {
                Log.e("EXCEPTION CONNEC RECIVE", ignored.getMessage());
            }
        }
    }
}

