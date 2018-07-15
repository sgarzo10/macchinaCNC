package com.prova.gui.device.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import com.prova.bluetooth.R;
import com.prova.gui.device.utility.DrawFigure;
import com.prova.gui.settings.activity.SettingsActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AscoltatoreConnectionActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener, DialogInterface.OnClickListener  {

    private ConnectionActivity app;
    private ArrayList<String> messaggi;
    private DrawFigure drawFigure;
    private int[] lunghezze;
    private int[] posizioni;
    private Map<String, Integer> map;

    public int[] getLunghezze(){ return lunghezze; }
    public int[] getPosizioni(){ return posizioni; }
    public ConnectionActivity getApp() { return app; }
    public ArrayList<String> getMessaggi() { return messaggi; }

    AscoltatoreConnectionActivity(ConnectionActivity app)
    {
        this.app=app;
        lunghezze = new int[3];
        posizioni = new int[3];
        messaggi = new ArrayList<>();
        for(int i=0;i<3;i++){
            lunghezze[i] = -1;
            posizioni[i] = -1;
        }
        drawFigure = new DrawFigure(this);
        map = new HashMap<>();
        map.put("x", 0);
        map.put("y", 1);
        map.put("z", 2);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear:
                app.getQuadratoView().pulisci();
                break;
            case R.id.posiziona:
                for (int i =  0; i < 3; i++)
                    app.getInput().get(i).setText(Integer.toString(posizioni[i]));
                app.getDialog().get(0).show();
                break;
            case R.id.linea:
                app.getDialog().get(1).show();
                break;
            case R.id.curva:
                app.getDialog().get(2).show();
                break;
            case R.id.rettangolo:
                app.getDialog().get(3).show();
                break;
            case R.id.triangolo:
                app.getDialog().get(4).show();
                break;
            case R.id.parallelo:
                app.getDialog().get(5).show();
                break;
            case R.id.cerchio:
                app.getDialog().get(6).show();
                break;
            case R.id.trapezio:
                app.getDialog().get(7).show();
                break;
            case R.id.reset:
                app.getDialog().get(8).show();
                break;
            case R.id.settings:
                try {
                    Intent openPage1 = new Intent(app, SettingsActivity.class);
                    app.startActivity(openPage1);
                } catch (Exception e){
                    Log.e("EXCEPTION FOUND", e.getMessage());
                }
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
                    inviaMessaggio("mzg1");
                    break;
                case R.id.scendi:
                    inviaMessaggio("mzs1");
                    break;
            }
        }
        return false;
    }

    public void addMex(ArrayList<String> mex){
        if (messaggi.size() == 0) {
           messaggi.addAll(mex);
           if (!app.getBluetooth().invia(messaggi.get(0)))
                app.addView(app.getResources().getString(R.string.error));
        }
    }

    void inviaMessaggio(String mex){
        if (messaggi.size() == 0) {
            messaggi.add(mex);
            if (!app.getBluetooth().invia(messaggi.get(0)))
                app.addView(app.getResources().getString(R.string.error));
        }
    }

    public void shiftMessaggi(){
        for (int i = 0; i < messaggi.size() - 1; i++)
            messaggi.set(i, messaggi.get(i + 1));
        messaggi.remove(messaggi.size() - 1);
        if (messaggi.size() > 0)
            app.getBluetooth().invia(messaggi.get(0));
    }

    public void simulaAvanzamento(String messaggio, boolean addView, int old){
        String asse = messaggio.substring(1, 2);
        String dir = messaggio.substring(2, 3);
        int mm;
        if (!messaggio.substring(3, messaggio.length()).contains("."))
            mm = Integer.parseInt(messaggio.substring(3, messaggio.length()));
        else
            mm = Integer.parseInt(messaggio.substring(3, messaggio.indexOf(".")));
        if (dir.equals("s")) {
            posizioni[map.get(asse)] = posizioni[map.get(asse)] + mm;
            if (posizioni[map.get(asse)] > lunghezze[map.get(asse)])
                posizioni[map.get(asse)] = lunghezze[map.get(asse)];
        } else {
            posizioni[map.get(asse)] = posizioni[map.get(asse)] - mm;
            if (posizioni[map.get(asse)] < 0)
                posizioni[map.get(asse)] = 0;
        }
        if (addView) {
            app.getPosizioni().setText(String.format(app.getResources().getString(R.string.output_posizioni), posizioni[0], posizioni[1], posizioni[2]));
            if (asse.equals("x") || asse.equals("y"))
                app.getQuadratoView().drawLine(asse, old, map.get(asse));
            else
                app.getQuadratoView().pulisci();
        }
    }

    public void simulaDisegno(ArrayList<String> messaggi){
        for (int i = 0; i < messaggi.size(); i++)
            simulaAvanzamento(messaggi.get(i), false, 0);
    }

    public ArrayList<String> posiziona(int x, int y, int z){
        @SuppressLint("UseSparseArrays")
        Map<Integer, String> map = new HashMap<>();
        ArrayList<String> messaggi = new ArrayList<>();
        int[] coordinate = {x, y, z};
        map.put(0, "x");
        map.put(1, "y");
        map.put(2, "z");
        for (int i = 0; i < 3; i++) {
            int coordinata = coordinate[i];
            if (coordinata > lunghezze[i])
                coordinata = lunghezze[i];
            if (coordinata - posizioni[i] > 0) {
                if (coordinata > posizioni[i])
                    messaggi.add("m" + map.get(i) + "s" + Integer.toString(coordinata - posizioni[i]));
                else
                    messaggi.add("m" + map.get(i) + "g" + Integer.toString(posizioni[i] - coordinata));
            }
        }
        return  messaggi;
    }

    @Override
    @SuppressLint("UseSparseArrays")
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        if (which == -1){
            int j;
            boolean trovato = false;
            for (j = 0; j < app.getDialog().size() && !trovato; j++) {
                if (app.getDialog().get(j).toString().equals(dialog.toString()))
                    trovato = true;
            }
            j = j -1;
            switch (j){
                case 0:
                    ArrayList<String> messaggi = posiziona(Integer.parseInt(app.getInput().get(0).getText().toString()), Integer.parseInt(app.getInput().get(1).getText().toString()), Integer.parseInt(app.getInput().get(2).getText().toString()));
                    if(messaggi.size() > 0)
                        addMex(messaggi);
                    break;
                case 1:
                    addMex(drawFigure.disegnaLineaProfonda(Integer.parseInt(app.getInput().get(3).getText().toString()), Integer.parseInt(app.getInput().get(4).getText().toString()), Integer.parseInt(app.getInput().get(5).getText().toString())));
                    break;
                case 2:
                    String[] s = calcolaStringhe(Integer.parseInt(app.getInput().get(7).getText().toString()));
                    addMex(drawFigure.disegnaSemiCerchioProfondo(Integer.parseInt(app.getInput().get(6).getText().toString()), s[0], s[1], Integer.parseInt(app.getInput().get(8).getText().toString())));
                    break;
                case 3:
                    drawFigure.disegnaRettangolo(Integer.parseInt(app.getInput().get(9).getText().toString()), Integer.parseInt(app.getInput().get(10).getText().toString()), Integer.parseInt(app.getInput().get(11).getText().toString()), app.getCheck().get(0).isChecked());
                    break;
                case 4:
                    drawFigure.disegnaTriangolo(Integer.parseInt(app.getInput().get(12).getText().toString()), Integer.parseInt(app.getInput().get(13).getText().toString()), Integer.parseInt(app.getInput().get(14).getText().toString()), Integer.parseInt(app.getInput().get(15).getText().toString()), app.getCheck().get(1).isChecked());
                    break;
                case 5:
                    drawFigure.disegnaParallelo(Integer.parseInt(app.getInput().get(16).getText().toString()),Integer.parseInt(app.getInput().get(17).getText().toString()),Integer.parseInt(app.getInput().get(18).getText().toString()), Integer.parseInt(app.getInput().get(19).getText().toString()), app.getCheck().get(2).isChecked());
                    break;
                case 6:
                    drawFigure.disegnaCerchio(Integer.parseInt(app.getInput().get(20).getText().toString()), Integer.parseInt(app.getInput().get(21).getText().toString()), app.getCheck().get(3).isChecked());
                    break;
                case 7:
                    drawFigure.disegnaTrapezio(Integer.parseInt(app.getInput().get(22).getText().toString()), Integer.parseInt(app.getInput().get(23).getText().toString()), Integer.parseInt(app.getInput().get(24).getText().toString()), Integer.parseInt(app.getInput().get(25).getText().toString()), Integer.parseInt(app.getInput().get(26).getText().toString()), Integer.parseInt(app.getInput().get(27).getText().toString()), app.getCheck().get(4).isChecked());
                    break;
                case 8:
                    reset(app.getCheck().get(5).isChecked(), app.getCheck().get(6).isChecked(), app.getCheck().get(7).isChecked());
                    break;
            }
        }
    }

    private String[] calcolaStringhe(int quadrante){
        String [] s = {"", ""};
        if (quadrante == 1) {
            s[0] = "mxg";
            s[1] = "myg";
        }
        if (quadrante == 2) {
            s[0] = "myg";
            s[1] = "mxs";
        }
        if (quadrante == 3) {
            s[0] = "mxs";
            s[1] = "mys";
        }
        if (quadrante == 4) {
            s[0] = "mys";
            s[1] = "mxg";
        }
        return s;
    }

    private void reset(boolean x, boolean y, boolean z){
        ArrayList<String> messaggi = new ArrayList<>();
        if (x)
            messaggi.add("rex");
        if(y)
            messaggi.add("rey");
        if (z)
            messaggi.add("rez");
        if (messaggi.size() > 0)
            addMex(messaggi);
    }
}
