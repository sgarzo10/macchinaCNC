package com.prova.gui.device.utility;

import android.util.Log;

import com.prova.bluetooth.R;
import com.prova.gui.device.activity.ConnectionActivity;

import java.util.ArrayList;

public class MovePoint extends Thread {

    private ConnectionActivity app;
    private JoystickView jv;

    public MovePoint(ConnectionActivity app, JoystickView jv){
        this.app = app;
        this.jv = jv;
    }

    /*
    67 = valore della x tutta sinistra
    538 + 67 = valore della x tutto a destra
    87 = valore della Y tutta in alto
    538 + 87 = valore della y tutta in basso
    @TODO calcola i valori in base allo schermo e non inserirli statici
     */
    private void spostaPuntino(){
        float myX = (((jv.getX()-67)/538)-0.5f)*4;
        float myY = (((jv.getY()-87)/538)-0.5f)*4;
        ArrayList<String> messaggi = new ArrayList<>();
        /*Log.i("X CALCOLATA", Integer.toString(Math.round(myX)));
        Log.i("Y CALCOLATA", Integer.toString(Math.round(myY)));
        app.getAscoltatore().getPosizioni()[0] = app.getAscoltatore().getPosizioni()[0] + Math.round(myX);
        app.getAscoltatore().getPosizioni()[1] = app.getAscoltatore().getPosizioni()[1] - Math.round(myY);
        app.getQuadratoView().drawPoint(app.getAscoltatore().getPosizioni()[0], app.getAscoltatore().getPosizioni()[1]);
        app.getPosizioni().setText(String.format(app.getResources().getString(R.string.output_posizioni), app.getAscoltatore().getPosizioni()[0], app.getAscoltatore().getPosizioni()[1], app.getAscoltatore().getPosizioni()[2]));
        */
        //
        if (Math.round(myX) > 0){
            for (int i = 0; i < Math.round(myX); i++)
                messaggi.add("mxs1");
        } else{
            for (int i = Math.round(myX); i < 0; i++)
                messaggi.add("mxg1");
        }
        if (Math.round(myY) > 0){
            for (int i = 0; i < Math.round(myY); i++)
                messaggi.add("myg1");
        } else{
            for (int i = Math.round(myY); i < 0; i++)
                messaggi.add("mys1");
        }
        if(messaggi.size() > 0)
            app.getAscoltatore().addMex(messaggi);
    }

    @Override
    public void run() {
        while (true) {
            if (jv.isContinua())
                spostaPuntino();
        }
    }
}
