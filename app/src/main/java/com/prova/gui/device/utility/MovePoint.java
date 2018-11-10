package com.prova.gui.device.utility;

import com.prova.gui.device.activity.ConnectionActivity;
import com.prova.gui.device.view.JoystickView;

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
        float myX = (((jv.getX()-67)/538)-0.5f)*2;
        float myY = (((jv.getY()-87)/538)-0.5f)*2;
        ArrayList<String> messaggi = new ArrayList<>();
        //Log.i("X CALCOLATA", Integer.toString(Math.round(myX)));
        //Log.i("Y CALCOLATA", Integer.toString(Math.round(myY)));
        if (Math.round(myX) > 0){
            for (int i = 0; i < Math.round(myX); i++)
                messaggi.add(app.getAscoltatore().getSpostamenti().get("mxg") + Long.toString(Math.round(app.getAscoltatore().getGiriMillimetro()[0]*app.getManageXml().getPrecisioni().get(0))));
        } else{
            for (int i = Math.round(myX); i < 0; i++)
                messaggi.add(app.getAscoltatore().getSpostamenti().get("mxs") + Long.toString(Math.round(app.getAscoltatore().getGiriMillimetro()[0]*app.getManageXml().getPrecisioni().get(0))));
        }
        if (Math.round(myY) > 0){
            for (int i = 0; i < Math.round(myY); i++)
                messaggi.add(app.getAscoltatore().getSpostamenti().get("myg") + Long.toString(Math.round(app.getAscoltatore().getGiriMillimetro()[1]*app.getManageXml().getPrecisioni().get(1))));
        } else{
            for (int i = Math.round(myY); i < 0; i++)
                messaggi.add(app.getAscoltatore().getSpostamenti().get("mys") + Long.toString(Math.round(app.getAscoltatore().getGiriMillimetro()[1]*app.getManageXml().getPrecisioni().get(1))));
        }
        if(messaggi.size() > 0) {
            if (messaggi.size() > 1)
                messaggi.add("da");
            app.getAscoltatore().addMex(messaggi);
        }
    }

    @Override
    public void run() {
        while (!app.isKillMovePoint()) {
            if (jv.isContinua())
                spostaPuntino();
        }
    }
}
