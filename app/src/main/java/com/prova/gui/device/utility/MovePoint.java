package com.prova.gui.device.utility;

import android.util.Log;
import com.prova.gui.device.activity.ConnectionActivity;

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
        String messaggio = "";
        /*Log.i("X CALCOLATA", Float.toString(myX));
        Log.i("Y CALCOLATA", Float.toString(myY));
        app.getQuadratoView().drawPoint(app.getQuadratoView().getCurrentX() + myX, app.getQuadratoView().getCurrentY() + myY);*/
        if (Math.round(myY) == 0 && Math.round(myX) == 2)
            messaggio = "mxs1";
        if (Math.round(myY) == 0 && Math.round(myX) == -2)
            messaggio = "mxg1";
        if (Math.round(myY) == -2 && Math.round(myX) == 0)
            messaggio = "mys1";
        if (Math.round(myY) == 2 && Math.round(myX) == 0)
            messaggio = "myg1";
        /*if (Math.round(myY) == 1 && Math.round(myX) == 1) {
            app.getAscoltatore().getMessaggi().add("myg1");
            app.getAscoltatore().getMessaggi().add("mxs1");
        }
        if (Math.round(myY) == -1 && Math.round(myX) == 1) {
            app.getAscoltatore().getMessaggi().add("mys1");
            app.getAscoltatore().getMessaggi().add("mxs1");
        }
        if (Math.round(myY) == 1 && Math.round(myX) == -1) {
            app.getAscoltatore().getMessaggi().add("myg1");
            app.getAscoltatore().getMessaggi().add("mxg1");
        }
        if (Math.round(myY) == -1 && Math.round(myX) == -1) {
            app.getAscoltatore().getMessaggi().add("mys1");
            app.getAscoltatore().getMessaggi().add("mxg1");
        }*/
        if(!messaggio.equals(""))
            app.getAscoltatore().sendTemporizzata(messaggio);
    }

    @Override
    public void run() {
        while (true) {
            if (jv.isContinua())
                spostaPuntino();
        }
    }
}
