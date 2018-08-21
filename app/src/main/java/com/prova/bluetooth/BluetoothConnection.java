package com.prova.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.prova.gui.device.activity.ConnectionActivity;
import com.prova.gui.device.utility.MyHandler;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;


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
        if (message.contains("&"))
            compress();
        Log.i("SEND", message);
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

    private void compress(){
        String newMex = message;
        StringBuilder dict = new StringBuilder("");
        String[] messages = message.split("&");
        List<String> chiavi = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "f", "g", "h", "i", "l", "m", "n"));
        HashMap<String, Integer> count = new HashMap<>();
        int n = 0;
        for (String message1 : messages) {
            int c = 0;
            for (String mex : messages) {
                if (Objects.equals(message1, mex))
                    c++;
            }
            if (!count.containsKey(message1))
                count.put(message1, c);
        }
        count = sortHashMapByValues(count);
        Iterator<HashMap.Entry<String, Integer>> it = count.entrySet().iterator();
        while (it.hasNext() && n < 10) {
            HashMap.Entry<String, Integer> e = it.next();
            if (e.getValue() > 1) {
                dict.append(chiavi.get(n)).append(":").append(e.getKey()).append("&");
                newMex = newMex.replace(e.getKey(), chiavi.get(n));
                n++;
            }
        }
        if (!dict.toString().equals(""))
            newMex = dict.toString().substring(0, dict.toString().length() - 1) + "|" + newMex;
        message = newMex;
    }

    private LinkedHashMap<String, Integer> sortHashMapByValues(HashMap<String, Integer> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        boolean continua = true;
        Collections.sort(mapValues, Collections.reverseOrder());
        Collections.sort(mapKeys);
        for (int val : mapValues) {
            Iterator<String> keyIt = mapKeys.iterator();
            while (keyIt.hasNext() && continua) {
                String key = keyIt.next();
                if (passedMap.get(key) == val) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    continua = false;
                }
            }
            continua = true;
        }
        return sortedMap;
    }
}

