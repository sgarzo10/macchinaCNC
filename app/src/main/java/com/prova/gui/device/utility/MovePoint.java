package com.prova.gui.device.utility;

import android.util.Log;

import com.prova.gui.device.activity.ConnectionActivity;

public class MovePoint extends Thread {

    private ConnectionActivity app;
    private JoystickView jv;

    MovePoint(ConnectionActivity app, JoystickView jv){
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
        float myX = (((jv.getX()-67)/538)-0.5f)*3;
        float myY = (((jv.getY()-87)/538)-0.5f)*3;
        Log.i("X CALCOLATA", Float.toString(myX));
        Log.i("Y CALCOLATA", Float.toString(myY));
        app.getQuadratoView().drawPoint(app.getQuadratoView().getCurrentX() + myX, app.getQuadratoView().getCurrentY() + myY);
    }

    @Override
    public void run() {
        while (true) {
            if (jv.isContinua())
                spostaPuntino();
        }
    }
}
