package com.prova.gui.device.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import com.prova.bluetooth.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AscoltatoreConnectionActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener, DialogInterface.OnClickListener  {

    private ConnectionActivity app;
    private ArrayList<String> messaggi;
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invia:
                inviaMessaggio(app.getTesto().getText().toString());
                break;
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

    public void inviaMessaggio(String mex){
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

    private void disegnaRettangolo(int larghezza, int altezza){
        ArrayList<String> messaggi = new ArrayList<>();
        for (int i = 0; i < larghezza; i++)
            messaggi.add("mxs1");
        for (int i = 0; i < altezza; i++)
            messaggi.add("mys1");
        for (int i = 0; i < larghezza; i++)
            messaggi.add("mxg1");
        for (int i = 0; i < altezza; i++)
            messaggi.add("myg1");
        addMex(messaggi);
    }

    private void disegnaTriangolo(int base){
        ArrayList<String> messaggi = new ArrayList<>();
        ArrayList<String> messaggiRitorno = new ArrayList<>();
        boolean skipFirst = false;
        long a = Math.round((base*Math.sqrt(3))/2);
        int altezza = (int) a;
        for (int i = 0; i < base; i++)
            messaggi.add("mxs1");
        for(int i = 0; i < 2; i++) {
            messaggi.add("mys1");
            messaggi.add("mxg1");
        }
        messaggi.add("mys1");
        messaggi.add("mys1");
        messaggi.add("mxg1");
        messaggi.add("mys1");
        messaggi.add("mxg1");
        altezza = altezza - 5;
        int giri = altezza / 7;
        for (int i = 0; i < giri; i++) {
            for (int j = 0; j< 3; j++) {
                messaggi.add("mys1");
                messaggi.add("mys1");
                messaggi.add("mxg1");
            }
            messaggi.add("mys1");
            messaggi.add("mxg1");
        }
        altezza = altezza - giri * 7;
        switch (altezza){
            case 0:
                messaggi.remove(messaggi.size()-1);
                messaggi.remove(messaggi.size()-1);
                messaggi.remove(messaggi.size()-1);
                messaggi.add("mys1");
                messaggi.add("mxg1");
                break;
            case 1:
                messaggi.remove(messaggi.size()-1);
                messaggi.add("mys1");
                messaggi.add("mxg1");
                skipFirst = true;
                break;
            case 2:
                messaggi.remove(messaggi.size()-1);
                messaggi.add("mys1");
                messaggi.add("mys1");
                messaggi.add("mxg1");
                if (base % 2 == 1){
                    messaggi.add("mxg1");
                    skipFirst = true;
                }
                break;
            case 3:
                messaggi.add("mys1");
                messaggi.add("mys1");
                messaggi.add("mys1");
                messaggi.add("mxg1");
                skipFirst = true;
                break;
            case 4:
                messaggi.remove(messaggi.size()-1);
                messaggi.add("mys1");
                messaggi.add("mxg1");
                messaggi.add("mys1");
                messaggi.add("mys1");
                messaggi.add("mys1");
                messaggi.add("mxg1");
                if (base % 2 == 1){
                    messaggi.add("mxg1");
                    skipFirst = true;
                }
                break;
            case 5:
                messaggi.add("mys1");
                messaggi.add("mys1");
                messaggi.add("mxg1");
                messaggi.add("mys1");
                messaggi.add("mys1");
                messaggi.add("mys1");
                messaggi.add("mxg1");
                skipFirst = true;
                break;
            case 6:
                for (int j = 0; j< 2; j++) {
                    messaggi.add("mys1");
                    messaggi.add("mys1");
                    messaggi.add("mys1");
                    messaggi.add("mxg1");
                }
                if (base % 2 == 1){
                    messaggi.add("mxg1");
                    skipFirst = true;
                }
                break;
        }
        int i;
        if (skipFirst && base % 2 == 1)
            i = messaggi.size() - 2;
        else
            i = messaggi.size() - 1;
        for (; i > base - 1; i--){
            if (messaggi.get(i).equals("mys1"))
                messaggiRitorno.add("myg1");
            else
                messaggiRitorno.add(messaggi.get(i));
        }
        messaggi.addAll(messaggiRitorno);
        addMex(messaggi);
    }

    private void disegnaParallelo(int base, int altezza, int sfasamento){
        ArrayList<String> messaggi = new ArrayList<>();
        for (int i = 0; i < base; i++)
            messaggi.add("mxs1");
        if (altezza == sfasamento) {
            for (int i = 0; i < (altezza-1); i++) {
                messaggi.add("mys1");
                messaggi.add("mxs1");
            }
            messaggi.add("mxs1");
            messaggi.add("mys1");
        }
        for (int i = 0; i < base; i++)
            messaggi.add("mxg1");
        if (altezza == sfasamento) {
            for (int i = 0; i < (altezza-1); i++) {
                messaggi.add("myg1");
                messaggi.add("mxg1");
            }
            messaggi.add("mxg1");
            messaggi.add("myg1");
        }
        addMex(messaggi);
    }

    private void disegnaCerchio(int raggio){
        ArrayList<String> messaggi = new ArrayList<>();
        int myRaggio = raggio;
        for(int i = 1; i <= raggio; i++){
             long y = Math.round(Math.sqrt(raggio*raggio - i*i ));
             if (y == myRaggio)
                 messaggi.add("mxs1");
             if (y < myRaggio) {
                 for (int j = 0; j < myRaggio - y; j++)
                    messaggi.add("myg1");
                 messaggi.add("mxs1");
                 myRaggio = myRaggio - (myRaggio - (int)y);
             }
        }
        for (int j = 0; j < myRaggio; j++)
            messaggi.add("myg1");
        myRaggio = raggio;
        for(int i = 1; i <= raggio; i++){
            long x = Math.round(Math.sqrt(raggio*raggio - i*i ));
            if (x == myRaggio)
                messaggi.add("myg1");
            if (x < myRaggio) {
                for (int j = 0; j < myRaggio - x; j++)
                    messaggi.add("mxg1");
                messaggi.add("myg1");
                myRaggio = myRaggio - (myRaggio - (int)x);
            }
        }
        for (int j = 0; j < myRaggio; j++)
            messaggi.add("mxg1");
        myRaggio = raggio;
        for(int i = 1; i <= raggio; i++){
            long y = Math.round(Math.sqrt(raggio*raggio - i*i ));
            if (y == myRaggio)
                messaggi.add("mxg1");
            if (y < myRaggio) {
                for (int j = 0; j < myRaggio - y; j++)
                    messaggi.add("mys1");
                messaggi.add("mxg1");
                myRaggio = myRaggio - (myRaggio - (int)y);
            }
        }
        for (int j = 0; j < myRaggio; j++)
            messaggi.add("mys1");
        myRaggio = raggio;
        for(int i = 1; i <= raggio; i++){
            long x = Math.round(Math.sqrt(raggio*raggio - i*i ));
            if (x == myRaggio)
                messaggi.add("mys1");
            if (x < myRaggio) {
                for (int j = 0; j < myRaggio - x; j++)
                    messaggi.add("mxs1");
                messaggi.add("mys1");
                myRaggio = myRaggio - (myRaggio - (int)x);
            }
        }
        for (int j = 0; j < myRaggio; j++)
            messaggi.add("mxs1");
        addMex(messaggi);
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
                        if (coordinata > lunghezze[1])
                            coordinata = lunghezze[1];
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
                    disegnaRettangolo(Integer.parseInt(app.getInput().get(3).getText().toString()), Integer.parseInt(app.getInput().get(4).getText().toString()));
                    break;
                case 2:
                    disegnaTriangolo(Integer.parseInt(app.getInput().get(5).getText().toString()));
                    break;
                case 3:
                    disegnaParallelo(Integer.parseInt(app.getInput().get(6).getText().toString()),Integer.parseInt(app.getInput().get(7).getText().toString()),Integer.parseInt(app.getInput().get(8).getText().toString()));
                    break;
                case 4:
                    disegnaCerchio(Integer.parseInt(app.getInput().get(9).getText().toString()));
                    break;
            }
        }
    }
}
