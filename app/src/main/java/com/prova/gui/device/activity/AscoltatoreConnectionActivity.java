package com.prova.gui.device.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.prova.bluetooth.R;

import java.lang.reflect.Array;

public class AscoltatoreConnectionActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener  {

    private ConnectionActivity app;
    private int[] lunghezze;
    private int[] posizioni;

    public int[] getLunghezze(){ return lunghezze;}
    public int[] getPosizioni(){ return posizioni;}

    AscoltatoreConnectionActivity(ConnectionActivity app)
    {
        this.app=app;
        lunghezze = new int[3];
        posizioni = new int[3];
        for(int i=0;i<3;i++){
            lunghezze[i] = 0;
            posizioni[i] = 0;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invia:
                if (!app.getBluetooth().invia(app.getTesto().getText().toString()))
                    app.addView(app.getResources().getString(R.string.error));
                break;
            case R.id.dove:
                if (!app.getBluetooth().invia("dove all"))
                    app.addView(app.getResources().getString(R.string.error));
                break;
            case R.id.seriale:
                if (!app.getBluetooth().invia("setSe blu"))
                    app.addView(app.getResources().getString(R.string.error));
                break;
            case R.id.lunghezze:
                if (!app.getBluetooth().invia("lung all"))
                    app.addView(app.getResources().getString(R.string.error));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            app.getRotazione_attiva().setText(app.getResources().getString(R.string.rot_on));
            if (!app.getBluetooth().invia("ruota on"))
                app.addView(app.getResources().getString(R.string.error));
        }
        else {
            app.getRotazione_attiva().setText(app.getResources().getString(R.string.rot_off));
            if (!app.getBluetooth().invia("ruota off"))
                app.addView(app.getResources().getString(R.string.error));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if(e.getAction() != MotionEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.sali:
                    if (!app.getBluetooth().invia("sali"))
                        app.addView(app.getResources().getString(R.string.error));
                    break;
                case R.id.scendi:
                    if (!app.getBluetooth().invia("scendi"))
                        app.addView(app.getResources().getString(R.string.error));
                    break;
            }
        }
        return false;
    }
}
