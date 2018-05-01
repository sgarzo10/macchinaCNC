package com.prova.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.util.Set;

public class Bluetooth {

    private BluetoothAdapter bluetoothAdapter;

    public BluetoothAdapter getBluetoothAdapter() { return bluetoothAdapter; }

    public Bluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void spegni(){
        bluetoothAdapter.disable();
    }

    public void annullaRicerca(){
        bluetoothAdapter.cancelDiscovery();
    }

    public Set<BluetoothDevice> dispositivi_associati(){
        return bluetoothAdapter.getBondedDevices();
    }

    public void cerca(){
        bluetoothAdapter.startDiscovery();
    }

    public BluetoothDevice getDevice(String mac){
        return bluetoothAdapter.getRemoteDevice(mac);
    }
}
