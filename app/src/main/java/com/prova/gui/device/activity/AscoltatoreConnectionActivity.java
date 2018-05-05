package com.prova.gui.device.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.prova.bluetooth.R;

public class AscoltatoreConnectionActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener  {

    private ConnectionActivity app;

    AscoltatoreConnectionActivity(ConnectionActivity app)
    {
        this.app=app;
    }

    private void createThread(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (!app.getBluetooth().getH().getMexRicevuto().equals("")) {
                    addView("RX: " + app.getBluetooth().getH().getMexRicevuto());
                    app.getBluetooth().getH().setMexRicevuto("");
                }
                else
                    createThread();
            }
        }, 100);
    }

    private void addView(String s){
        TextView t = new TextView(app);
        t.setText(s);
        app.getOutput().addView(t);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invia:
                if (!app.getBluetooth().invia(app.getTesto().getText().toString()))
                    addView(app.getResources().getString(R.string.error));
                else
                    createThread();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            app.getRotazione_attiva().setText(app.getResources().getString(R.string.rot_on));
        else
            app.getRotazione_attiva().setText(app.getResources().getString(R.string.rot_off));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if(e.getAction() != MotionEvent.ACTION_UP) {
            switch (v.getId()) {
                case R.id.sali:
                    if (!app.getBluetooth().invia("sali"))
                        addView(app.getResources().getString(R.string.error));
                    break;
                case R.id.scendi:
                    if (!app.getBluetooth().invia("scendi"))
                        addView(app.getResources().getString(R.string.error));
                    break;
            }
        }
        return false;
    }
}
