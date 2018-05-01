package com.prova.gui.device;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import com.prova.bluetooth.R;

public class AscoltatoreConnectionActivity implements View.OnClickListener {

    private ConnectionActivity app;
    private TextView t;

    AscoltatoreConnectionActivity(ConnectionActivity app)
    {
        this.app=app;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invia:
                t = new TextView(app);
                if (!app.getBluetooth().invia(app.getTesto().getText().toString())) {
                    t.setText(R.string.error);
                    app.getOutput().addView(t);
                }
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
                    String mex = "RX: " + app.getBluetooth().getH().getMexRicevuto();
                    t.setText(mex);
                    app.getOutput().addView(t);
                    app.getBluetooth().getH().setMexRicevuto("");
                }
                else
                    createThread();
            }
        }, 100);
    }
}
