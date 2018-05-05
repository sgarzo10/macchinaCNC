package com.prova.gui.device;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import com.prova.bluetooth.R;

public class AscoltatoreConnectionActivity implements View.OnClickListener {

    private ConnectionActivity app;

    AscoltatoreConnectionActivity(ConnectionActivity app)
    {
        this.app=app;
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
}
