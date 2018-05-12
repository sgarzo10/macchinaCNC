package com.prova.gui.device.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import com.prova.bluetooth.R;

public class AscoltatoreConnectionActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener  {

    private ConnectionActivity app;
    private String messaggio;
    private int[] lunghezze;
    private int[] posizioni;
    private boolean posso;
    private boolean crea;

    public int[] getLunghezze(){ return lunghezze; }
    public int[] getPosizioni(){ return posizioni; }
    public String getMessaggio() { return  messaggio; }

    AscoltatoreConnectionActivity(ConnectionActivity app)
    {
        this.app=app;
        posso = true;
        crea = true;
        messaggio = "";
        lunghezze = new int[3];
        posizioni = new int[3];
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
                inviaMessaggio("dove all");
                break;
            case R.id.seriale:
                inviaMessaggio("setSe blu");
                break;
            case R.id.lunghezze:
                inviaMessaggio("lung all");
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            app.getRotazione_attiva().setText(app.getResources().getString(R.string.rot_on));
            messaggio = "ruota on";
            if (!app.getBluetooth().invia(messaggio))
                app.addView(app.getResources().getString(R.string.error));
        }
        else {
            app.getRotazione_attiva().setText(app.getResources().getString(R.string.rot_off));
            messaggio = "ruota off";
            if (!app.getBluetooth().invia(messaggio))
                app.addView(app.getResources().getString(R.string.error));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if(e.getAction() != MotionEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.sali:
                    messaggio = "muovi z giu 1 32";
                    sendTemporizzata();
                    break;
                case R.id.scendi:
                    messaggio = "muovi z su 1 32";
                    sendTemporizzata();
                    break;
            }
        }
        return false;
    }

    private void sendTemporizzata(){
        if (posso){
            posso = false;
            if (!app.getBluetooth().invia(messaggio))
                app.addView(app.getResources().getString(R.string.error));
        }
        else {
            if (crea) {
                crea = false;
                Handler handler = new Handler();
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
        messaggio = mex;
        if (!app.getBluetooth().invia(messaggio))
            app.addView(app.getResources().getString(R.string.error));
    }
}
