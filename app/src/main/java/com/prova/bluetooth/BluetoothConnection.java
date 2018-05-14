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

    public MyHandler getH() { return h; }

    public BluetoothConnection (MyHandler h){
        this.h = h;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mmSocket = null;
        outStream = null;
        input = null;
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

    public boolean invia(String message) {
        try {
            byte[] msgBuffer = message.getBytes();
            outStream.write(msgBuffer);
            return true;
        } catch (Exception e) {
            Log.e("EXCEPTION SEND",e.getMessage());
            return false;
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[2048];
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
            } catch (Exception ignored) {
                Log.e("EXCEPTION CONNEC RECIVE", ignored.getMessage());
            }
        }
    }
}

