package com.prova.gui.settings.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.prova.bluetooth.R;
import com.prova.gui.settings.utility.ManageXml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private EditText mandrino;
    private EditText velocita;
    private ArrayList<EditText> lunghezze;
    private ArrayList<EditText> precisioni;
    private ArrayList<EditText> giriMillimetro;
    private ManageXml manageXml;

    public EditText getMandrino() { return mandrino; }
    public EditText getVelocita() { return velocita; }
    public ArrayList<EditText> getLunghezze() { return lunghezze; }
    public ArrayList<EditText> getPrecisioni() { return precisioni; }
    public ArrayList<EditText> getGiriMillimetro() { return giriMillimetro; }
    public ManageXml getManageXml() { return manageXml; }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        lunghezze = new ArrayList<>();
        precisioni = new ArrayList<>();
        giriMillimetro = new ArrayList<>();
        Button salva = findViewById(R.id.salva);
        mandrino = findViewById(R.id.mandrino);
        velocita = findViewById(R.id.velocita);
        lunghezze.add((EditText) findViewById(R.id.lunghezzaX));
        lunghezze.add((EditText) findViewById(R.id.lunghezzaY));
        lunghezze.add((EditText) findViewById(R.id.lunghezzaZ));
        precisioni.add((EditText) findViewById(R.id.precisioneX));
        precisioni.add((EditText) findViewById(R.id.precisioneY));
        precisioni.add((EditText) findViewById(R.id.precisioneZ));
        giriMillimetro.add((EditText) findViewById(R.id.giriX));
        giriMillimetro.add((EditText) findViewById(R.id.giriY));
        giriMillimetro.add((EditText) findViewById(R.id.giriZ));
        manageXml = new ManageXml();
        AscoltatoreSettingsActivity ascoltatore = new AscoltatoreSettingsActivity(this);
        salva.setOnClickListener(ascoltatore);
        File f = new File(getFilesDir(), "config.xml");
        if (f.exists()) {
            try {
                manageXml.setIst(openFileInput("config.xml"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            manageXml.readXml(true);
        }
        else{
            manageXml.setXrp(getResources().getXml(R.xml.config));
            manageXml.readXml(false);
        }
        mandrino.setText(Double.toString(manageXml.getDiametro()));
        velocita.setText(Integer.toString(manageXml.getVelocita()));
        for (int i = 0; i< 2; i++) {
            lunghezze.get(i).setText(Integer.toString(manageXml.getLunghezze().get(i)));
            precisioni.get(i).setText(Float.toString(manageXml.getPrecisioni().get(i)));
            giriMillimetro.get(i).setText(Integer.toString(manageXml.getGiriMillimetro().get(i)));
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
