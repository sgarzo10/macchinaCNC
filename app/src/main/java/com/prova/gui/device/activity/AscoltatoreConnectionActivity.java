package com.prova.gui.device.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import com.prova.bluetooth.R;
import com.prova.gui.device.utility.DrawFigure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AscoltatoreConnectionActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener, DialogInterface.OnClickListener  {

    private ConnectionActivity app;
    private ArrayList<String> messaggi;
    private DrawFigure drawFigure;
    private int[] lunghezze;
    private int[] posizioni;

    public int[] getLunghezze(){ return lunghezze; }
    public int[] getPosizioni(){ return posizioni; }
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear:
                app.getQuadratoView().pulisci();
                break;
            case R.id.posiziona:
                app.getDialog().get(0).show();
                break;
            case R.id.resetx:
                inviaMessaggio("rex");
                break;
            case R.id.resety:
                inviaMessaggio("rey");
                break;
            case R.id.resetz:
                inviaMessaggio("rez");
                break;
            case R.id.reseta:
                ArrayList<String> messaggi = new ArrayList<>();
                messaggi.add("rex");
                messaggi.add("rey");
                messaggi.add("rez");
                addMex(messaggi);
                break;
            case R.id.rettangolo:
                app.getDialog().get(1).show();
                break;
            case R.id.triangolo:
                app.getDialog().get(2).show();
                break;
            case R.id.parallelo:
                app.getDialog().get(3).show();
                break;
            case R.id.cerchio:
                app.getDialog().get(4).show();
                break;
            case R.id.trapezio:
                app.getDialog().get(5).show();
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

    @Override
    @SuppressLint("UseSparseArrays")
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        if (which == -1){
            ArrayList<String> messaggi = new ArrayList<>();
            int j;
            boolean trovato = false;
            for (j = 0; j < app.getDialog().size() && !trovato; j++) {
                if (app.getDialog().get(j).toString().equals(dialog.toString()))
                    trovato = true;
            }
            j = j -1;
            switch (j){
                case 0:
                    Map<Integer, String> map = new HashMap<>();
                    map.put(0, "x");
                    map.put(1, "y");
                    map.put(2, "z");
                    for (int i = 0; i < 3; i++) {
                        int coordinata = Integer.parseInt(app.getInput().get(i).getText().toString());
                        if (coordinata > lunghezze[i])
                            coordinata = lunghezze[i];
                        if (coordinata > posizioni[i]){
                            for (int k = 0; k < (coordinata - posizioni[i]); k++)
                                messaggi.add("m" + map.get(i) + "s1");
                        }
                        else{
                            for (int k = 0; k < (posizioni[i] - coordinata); k++)
                                messaggi.add("m" + map.get(i) + "g1");
                        }
                    }
                    if(messaggi.size() > 0)
                        addMex(messaggi);
                    break;
                case 1:
                    drawFigure.disegnaRettangolo(Integer.parseInt(app.getInput().get(3).getText().toString()), Integer.parseInt(app.getInput().get(4).getText().toString()), app.getCheck().get(0).isChecked());
                    break;
                case 2:
                    drawFigure.disegnaTriangolo(Integer.parseInt(app.getInput().get(5).getText().toString()), Integer.parseInt(app.getInput().get(6).getText().toString()), Integer.parseInt(app.getInput().get(7).getText().toString()), app.getCheck().get(1).isChecked());
                    break;
                case 3:
                    drawFigure.disegnaParallelo(Integer.parseInt(app.getInput().get(8).getText().toString()),Integer.parseInt(app.getInput().get(9).getText().toString()),Integer.parseInt(app.getInput().get(10).getText().toString()), app.getCheck().get(2).isChecked());
                    break;
                case 4:
                    drawFigure.disegnaCerchio(Integer.parseInt(app.getInput().get(11).getText().toString()), app.getCheck().get(3).isChecked());
                    break;
                case 5:
                    drawFigure.disegnaTrapezio(Integer.parseInt(app.getInput().get(12).getText().toString()), Integer.parseInt(app.getInput().get(13).getText().toString()), Integer.parseInt(app.getInput().get(14).getText().toString()), Integer.parseInt(app.getInput().get(15).getText().toString()), Integer.parseInt(app.getInput().get(16).getText().toString()), app.getCheck().get(4).isChecked());
                    break;
            }
        }
    }
}
