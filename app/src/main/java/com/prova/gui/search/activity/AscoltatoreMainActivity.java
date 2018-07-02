package com.prova.gui.search.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;

import com.prova.bluetooth.R;
import com.prova.bluetooth.Bluetooth;
import com.prova.gui.device.activity.ConnectionActivity;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class AscoltatoreMainActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private MainActivity app;
    private boolean ricerca;
    private ArrayList<String> nomi;
    private ArrayList<String> mac;
    private Bluetooth bluetooth;

    public void setRicerca() {this.ricerca = false;}
    public ArrayList<String> getNomi() {return nomi;}
    public ArrayList<String> getMac() {return mac;}
    public Bluetooth getBluetooth() {return bluetooth;}

    AscoltatoreMainActivity(MainActivity app)
    {
        this.app=app;
        nomi = new ArrayList<>();
        mac = new ArrayList<>();
        bluetooth = new Bluetooth();
        ricerca= false;
    }

    void inRicerca()
    {
        if (ricerca) {
            try {
                bluetooth.annullaRicerca();
                app.getCerca().setImageDrawable(ContextCompat.getDrawable(app, R.drawable.lente));
                ricerca = false;
                app.getMessaggio().setText(R.string.search_finish);
            } catch (Exception e) {
                app.getMessaggio().setText(R.string.error);
            }
        }
    }

    private void dispositivi_associati()
    {
        inRicerca();
        try
        {
            Set<BluetoothDevice> pairedDevices;
            pairedDevices = bluetooth.dispositivi_associati();
            if (pairedDevices.size()==0)
                app.getMessaggio().setText(R.string.no_dispo);
            else
            {
                pulisci();
                for (BluetoothDevice bt : pairedDevices) nomi.add(bt.getName());
                for (BluetoothDevice bt : pairedDevices) mac.add(bt.getAddress());
                app.getMessaggio().setText(R.string.list);
                updateList();
                for (int i=0; i<nomi.size();i++)
                {
                    Button pr= new Button(app);
                    pr.setId(R.id.elimina);
                    pr.setText(R.string.delete);
                    pr.setTag(i);
                    pr.setTextSize(10);
                    pr.setOnClickListener(this);
                    app.getBottoni().addView(pr);
                }
            }
        }
        catch (Exception e)
        {app.getMessaggio().setText(R.string.no_bluetooth);}
    }

    private void accendi()
    {
        inRicerca();
        try {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            app.startActivityForResult(turnOn, 0);
            app.changeView();
        }
        catch (Exception e)
        {app.getMessaggio().setText(R.string.no_bluetooth);}
    }

    private void spegni()
    {
        inRicerca();
        try {
            bluetooth.spegni();
            app.getStato_bluetooth().setText(app.getResources().getString(R.string.off));
            app.getCerca().setVisibility(View.INVISIBLE);
            app.getAssociati().setVisibility(View.INVISIBLE);
            pulisci();
        }
        catch (Exception e)
        {app.getMessaggio().setText(R.string.no_bluetooth);}
    }

    private void cerca()
    {
        if (!ricerca)
        {
            pulisci();
            try {
                ActivityCompat.requestPermissions(app, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                bluetooth.cerca();
                app.getCerca().setImageDrawable(ContextCompat.getDrawable(app, R.drawable.stop));
                ricerca = true;
            }
            catch (Exception e)
            {app.getMessaggio().setText(R.string.error);}
        }
        else
            inRicerca();
    }

    private void connetti(int i)
    {
        inRicerca();
        try {
            Intent openPage1 = new Intent(app, ConnectionActivity.class);
            openPage1.putExtra("nome", nomi.get(i));
            openPage1.putExtra("mac", mac.get(i));
            pulisci();
            app.setCollegato();
            app.getMessaggio().setText(R.string.connection);
            app.startActivity(openPage1);
        } catch (Exception e){
            app.getMessaggio().setText(R.string.error);
        }
    }

    private void elimina(int i)
    {
        try {
            BluetoothDevice mmDevice=bluetooth.getDevice(mac.get(i));
            Method m = mmDevice.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(mmDevice, (Object[]) null);
            app.getMessaggio().setText(R.string.dissociato);
            pulisci();
        } catch (Exception e) {
            app.getMessaggio().setText(R.string.error);
        }
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.associati:
                dispositivi_associati();
                break;
            case R.id.cerca:
                cerca();
                break;
            case R.id.connetti:
                connetti((int) view.getTag());
                break;
            case R.id.elimina:
                elimina((int) view.getTag());
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            accendi();
            cerca();
        }
        else
            spegni();
    }

    private void updateList(){
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(app, android.R.layout.simple_list_item_1, nomi);
            app.getNomi().setAdapter(adapter);
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(app, android.R.layout.simple_list_item_1, mac);
            app.getAddress().setAdapter(adapter1);
        }
        catch (Exception e){
            app.getMessaggio().setText(R.string.error);
        }
    }

    private void pulisci(){
        mac.clear();
        nomi.clear();
        app.getBottoni().removeAllViews();
        updateList();
    }
}
