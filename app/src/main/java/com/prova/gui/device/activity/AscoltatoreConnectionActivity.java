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
import java.util.Objects;

public class AscoltatoreConnectionActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener, DialogInterface.OnClickListener  {

    private ConnectionActivity app;
    private String messaggio;
    private DrawFigure drawFigure;
    private int[] lunghezze;
    private int[] giriMillimetro;
    private float[] posizioni;
    private Map<String, Integer> map;
    private Map<String, String> spostamenti;

    public int[] getLunghezze(){ return lunghezze; }
    public int[] getGiriMillimetro(){ return giriMillimetro; }
    public float[] getPosizioni(){ return posizioni; }
    public ConnectionActivity getApp() { return app; }
    public String getMessaggio() { return messaggio; }
    public void setMessaggio() { this.messaggio = ""; }
    public Map<String, String> getSpostamenti() { return spostamenti;}

    AscoltatoreConnectionActivity(ConnectionActivity app)
    {
        this.app=app;
        lunghezze = new int[3];
        giriMillimetro = new int[3];
        posizioni = new float[3];
        messaggio = "";
        for(int i=0;i<3;i++){
            lunghezze[i] = -1;
            posizioni[i] = -1;
        }
        drawFigure = new DrawFigure(this);
        map = new HashMap<>();
        map.put("x", 0);
        map.put("y", 1);
        map.put("z", 2);
        spostamenti = new HashMap<>();
        spostamenti.put("mxs", "1");
        spostamenti.put("mxg", "2");
        spostamenti.put("mys", "3");
        spostamenti.put("myg", "4");
        spostamenti.put("mzs", "5");
        spostamenti.put("mzg", "6");
        spostamenti.put("1", "mxs");
        spostamenti.put("2", "mxg");
        spostamenti.put("3", "mys");
        spostamenti.put("4", "myg");
        spostamenti.put("5", "mzs");
        spostamenti.put("6", "mzg");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.posiziona:
                for (int i =  0; i < 3; i++)
                    app.getInput().get(i).setText(Float.toString(posizioni[i]));
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
                    inviaMessaggio(spostamenti.get("mzg") + Long.toString(Math.round(giriMillimetro[2]*app.getManageXml().getPrecisioni().get(2))));
                    break;
                case R.id.scendi:
                    inviaMessaggio(spostamenti.get("mzs") + Long.toString(Math.round(giriMillimetro[2]*app.getManageXml().getPrecisioni().get(2))));
                    break;
            }
        }
        return false;
    }

    public void addMex(ArrayList<String> mex){
        if (Objects.equals(messaggio, "")) {
            StringBuilder finalMex = new StringBuilder();
            for (String messaggio: mex) {
                finalMex.append(messaggio).append("&");
            }
            messaggio = finalMex.toString().substring(0, finalMex.toString().length() - 1);
            app.getBluetooth().invia(messaggio);
        }
    }

    void inviaMessaggio(String mex){
        if (Objects.equals(messaggio, "")) {
            messaggio = mex;
            app.getBluetooth().invia(messaggio);
        }
    }

    public void simulaAvanzamento(String messaggio, boolean addView){
        String mex1, mex2, asse, dir, asse2, dir2;
        float giri, giri2;
        int count;
        if (messaggio.contains("-")){
            mex1 = messaggio.split("-")[0];
            mex2 = messaggio.split("-")[1].split("#")[0];
            count = Integer.parseInt(messaggio.split("-")[1].split("#")[1]);
            mex1 = spostamenti.get(mex1.substring(0,1)) + mex1.substring(1, mex1.length());
            mex2 = spostamenti.get(mex2.substring(0,1)) + mex2.substring(1, mex2.length());
            asse = mex1.substring(1, 2);
            dir = mex1.substring(2, 3);
            if (!mex1.substring(3, mex1.length()).contains("."))
                giri = Float.parseFloat(mex1.substring(3, mex1.length()));
            else
                giri = Float.parseFloat(mex1.substring(3, mex1.indexOf(".")));
            asse2 = mex2.substring(1, 2);
            dir2 = mex2.substring(2, 3);
            if (!mex2.substring(3, mex2.length()).contains("."))
                giri2 = Float.parseFloat(mex2.substring(3, mex2.length()));
            else
                giri2 = Float.parseFloat(mex2.substring(3, mex2.indexOf(".")));
            if (dir.equals("s")) {
                posizioni[map.get(asse)] = posizioni[map.get(asse)] + ((giri / giriMillimetro[map.get(asse)]) * count);
                if (posizioni[map.get(asse)] > lunghezze[map.get(asse)])
                    posizioni[map.get(asse)] = lunghezze[map.get(asse)];
            } else {
                posizioni[map.get(asse)] = posizioni[map.get(asse)] - ((giri / giriMillimetro[map.get(asse)]) * count);
                if (posizioni[map.get(asse)] < 0)
                    posizioni[map.get(asse)] = 0;
            }
            if (dir2.equals("s")) {
                posizioni[map.get(asse2)] = posizioni[map.get(asse2)] + ((giri2 / giriMillimetro[map.get(asse2)]) * count);
                if (posizioni[map.get(asse2)] > lunghezze[map.get(asse2)])
                    posizioni[map.get(asse2)] = lunghezze[map.get(asse2)];
            } else {
                posizioni[map.get(asse2)] = posizioni[map.get(asse2)] - ((giri2 / giriMillimetro[map.get(asse2)]) * count);
                if (posizioni[map.get(asse2)] < 0)
                    posizioni[map.get(asse2)] = 0;
            }
            if (addView) {
                app.getTextPosizioni().get(map.get(asse)).setText(String.format(app.getResources().getString(R.string.output_posizione), asse.toUpperCase(), app.getAscoltatore().getPosizioni()[map.get(asse)]));
                app.getTextPosizioni().get(map.get(asse2)).setText(String.format(app.getResources().getString(R.string.output_posizione), asse2.toUpperCase(), app.getAscoltatore().getPosizioni()[map.get(asse2)]));
            }
        } else {
            messaggio = spostamenti.get(messaggio.substring(0, 1)) + messaggio.substring(1, messaggio.length());
            asse = messaggio.substring(1, 2);
            dir = messaggio.substring(2, 3);
            if (!messaggio.substring(3, messaggio.length()).contains("."))
                giri = Float.parseFloat(messaggio.substring(3, messaggio.length()));
            else
                giri = Float.parseFloat(messaggio.substring(3, messaggio.indexOf(".")));
            if (dir.equals("s")) {
                posizioni[map.get(asse)] = posizioni[map.get(asse)] + (giri / giriMillimetro[map.get(asse)]);
                if (posizioni[map.get(asse)] > lunghezze[map.get(asse)])
                    posizioni[map.get(asse)] = lunghezze[map.get(asse)];
            } else {
                posizioni[map.get(asse)] = posizioni[map.get(asse)] - (giri / giriMillimetro[map.get(asse)]);
                if (posizioni[map.get(asse)] < 0)
                    posizioni[map.get(asse)] = 0;
            }
            if (addView)
                app.getTextPosizioni().get(map.get(asse)).setText(String.format(app.getResources().getString(R.string.output_posizione), asse.toUpperCase(), app.getAscoltatore().getPosizioni()[map.get(asse)]));
        }
    }

    public void simulaDisegno(ArrayList<String> messaggi){
        for (int i = 0; i < messaggi.size(); i++)
            simulaAvanzamento(messaggi.get(i), false);
    }

    public ArrayList<String> posiziona(double x, double y, float z){
        @SuppressLint("UseSparseArrays")
        Map<Integer, String> map = new HashMap<>();
        ArrayList<String> messaggi = new ArrayList<>();
        double[] coordinate = {x, y, z};
        map.put(0, "x");
        map.put(1, "y");
        map.put(2, "z");
        for (int i = 0; i < 3; i++) {
            double coordinata = coordinate[i];
            if (coordinata > lunghezze[i])
                coordinata = lunghezze[i];
            if (Math.abs(coordinata - posizioni[i]) > 0) {
                if (coordinata > posizioni[i])
                    messaggi.add(spostamenti.get("m" + map.get(i) + "s") + Long.toString(Math.round(giriMillimetro[i]*(coordinata - posizioni[i]))));
                else
                    messaggi.add(spostamenti.get("m" + map.get(i) + "g") + Long.toString(Math.round(giriMillimetro[i]*(posizioni[i] - coordinata))));
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
                    ArrayList<String> messaggi = posiziona(Float.parseFloat(app.getInput().get(0).getText().toString()), Float.parseFloat(app.getInput().get(1).getText().toString()), Float.parseFloat(app.getInput().get(2).getText().toString()));
                    if(messaggi.size() > 0) {
                        if (messaggi.size() > 1)
                            messaggi.add("da");
                        addMex(messaggi);
                    }
                    break;
                case 1:
                    addMex(drawFigure.disegnaLineaProfonda(Float.parseFloat(app.getInput().get(3).getText().toString()), Integer.parseInt(app.getInput().get(4).getText().toString()), Float.parseFloat(app.getInput().get(5).getText().toString())));
                    break;
                case 2:
                    String[] s = calcolaStringhe(Integer.parseInt(app.getInput().get(7).getText().toString()));
                    addMex(drawFigure.disegnaSemiCerchioProfondo(Float.parseFloat(app.getInput().get(6).getText().toString()), s[0], s[1], Float.parseFloat(app.getInput().get(8).getText().toString())));
                    break;
                case 3:
                    drawFigure.disegnaRettangolo(Float.parseFloat(app.getInput().get(9).getText().toString()), Float.parseFloat(app.getInput().get(10).getText().toString()), Float.parseFloat(app.getInput().get(11).getText().toString()), Float.parseFloat(app.getInput().get(12).getText().toString()), Float.parseFloat(app.getInput().get(13).getText().toString()), app.getCheck().get(0).isChecked());
                    break;
                case 4:
                    drawFigure.disegnaTriangolo(Float.parseFloat(app.getInput().get(14).getText().toString()), Float.parseFloat(app.getInput().get(15).getText().toString()), Float.parseFloat(app.getInput().get(16).getText().toString()), Float.parseFloat(app.getInput().get(17).getText().toString()), Float.parseFloat(app.getInput().get(18).getText().toString()), Float.parseFloat(app.getInput().get(19).getText().toString()), Float.parseFloat(app.getInput().get(20).getText().toString()), app.getCheck().get(1).isChecked());
                    break;
                case 5:
                    drawFigure.disegnaParallelo(Float.parseFloat(app.getInput().get(21).getText().toString()),Float.parseFloat(app.getInput().get(22).getText().toString()),Float.parseFloat(app.getInput().get(23).getText().toString()), Float.parseFloat(app.getInput().get(24).getText().toString()), Float.parseFloat(app.getInput().get(25).getText().toString()), Float.parseFloat(app.getInput().get(26).getText().toString()), app.getCheck().get(2).isChecked());
                    break;
                case 6:
                    drawFigure.disegnaCerchio(Float.parseFloat(app.getInput().get(27).getText().toString()), Float.parseFloat(app.getInput().get(28).getText().toString()), Float.parseFloat(app.getInput().get(29).getText().toString()), app.getCheck().get(3).isChecked());
                    break;
                case 7:
                    drawFigure.disegnaTrapezio(Float.parseFloat(app.getInput().get(22).getText().toString()), Float.parseFloat(app.getInput().get(23).getText().toString()), Float.parseFloat(app.getInput().get(24).getText().toString()), Float.parseFloat(app.getInput().get(25).getText().toString()), Float.parseFloat(app.getInput().get(26).getText().toString()), Float.parseFloat(app.getInput().get(27).getText().toString()), app.getCheck().get(4).isChecked());
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
        if (z)
            messaggi.add("rez");
        if (y)
            messaggi.add("rey");
        if (x)
            messaggi.add("rex");
        if (messaggi.size() > 0) {
            messaggi.add("da");
            addMex(messaggi);
        }
    }
}
