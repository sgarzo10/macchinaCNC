package com.prova.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class BluetoothConnection extends Thread {
    private BluetoothSocket mmSocket;
    private OutputStream outStream;
    private InputStream input;
    private BluetoothAdapter bluetoothAdapter;
    private MyHandler h;

    public MyHandler getH() {
        return h;
    }

    public BluetoothConnection (){
        h = new MyHandler();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mmSocket = null;
        outStream = null;
        input = null;
    }

    public boolean connetti(String mac) {
        BluetoothDevice mmDevice = bluetoothAdapter.getRemoteDevice(mac);
        try {
            mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
        } catch (Exception e) {
            return false;
        }
        try {
            mmSocket.connect();
            outStream = mmSocket.getOutputStream();
            input = mmSocket.getInputStream();
            return true;
        } catch (Exception e) {
            try {
                mmSocket.close();
                return false;
            } catch (Exception e1) {
                return false;
            }
        }
    }

    public boolean disconnetti() {
        try {
            mmSocket.close();
            outStream = null;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean invia(String message) {
        byte[] msgBuffer = message.getBytes();
        try {
            outStream.write(msgBuffer);
            Log.i("SEND",message);
            return true;
        } catch (IOException e) {
            Log.w("SEND","Messaggio non inviato");
            return false;
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                // Read from the InputStream
                if (input != null) {
                    bytes = input.available();
                    if (bytes != 0) {
                        bytes = input.read(buffer, 0, bytes);
                        h.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                    }
                }
            } catch (IOException ignored) {
                Log.e("ERROR CONNECTION", ignored.getMessage());
            }
        }
    }
}

