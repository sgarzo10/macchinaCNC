package com.prova.gui.device.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import com.prova.bluetooth.R;

import java.util.ArrayList;
import java.util.Arrays;

public class AscoltatoreConnectionActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener  {

    private ConnectionActivity app;
    private ArrayList<String> messaggi;
    private int[] lunghezze;
    private int[] posizioni;
    private boolean posso;
    private boolean crea;

    public int[] getLunghezze(){ return lunghezze; }
    public int[] getPosizioni(){ return posizioni; }
    public ArrayList<String> getMessaggi() { return messaggi; }

    AscoltatoreConnectionActivity(ConnectionActivity app)
    {
        this.app=app;
        posso = true;
        crea = true;
        lunghezze = new int[3];
        posizioni = new int[3];
        messaggi = new ArrayList<>();
        for(int i=0;i<3;i++){
            lunghezze[i] = -1;
            posizioni[i] = -1;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invia:
                inviaMessaggio(app.getTesto().getText().toString());
                break;
            case R.id.dove:
                inviaMessaggio("da");
                break;
            case R.id.seriale:
                inviaMessaggio("ssb");
                break;
            case R.id.lunghezze:
                inviaMessaggio("la");
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            app.getRotazione_attiva().setText(app.getResources().getString(R.string.rot_on));
            inviaMessaggio("ra");
        }
        else {
            app.getRotazione_attiva().setText(app.getResources().getString(R.string.rot_off));
            inviaMessaggio("rs");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if(e.getAction() != MotionEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.sali:
                    sendTemporizzata(new String[]{"mzg1"});
                    break;
                case R.id.scendi:
                    sendTemporizzata(new String[]{"mzs1"});
                    break;
            }
        }
        return false;
    }

    public void sendTemporizzata(String[] mex){
        if (posso){
            posso = false;
            messaggi.addAll(Arrays.asList(mex));
            if (!app.getBluetooth().invia(messaggi.get(0)))
                app.addView(app.getResources().getString(R.string.error));
        }
        else {
            if (crea) {
                crea = false;
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    public void run() {
                        posso = true;
                        crea = true;
                    }
                }, 500);
            }
        }
    }

    public void inviaMessaggio(String mex){
        messaggi.add(mex);
        if (!app.getBluetooth().invia(messaggi.get(0)))
            app.addView(app.getResources().getString(R.string.error));
    }

    public void shiftMessaggi(){
        for (int i = 0; i < messaggi.size() - 1; i++)
            messaggi.set(i, messaggi.get(i + 1));
        messaggi.remove(messaggi.size() - 1);
    }
}
