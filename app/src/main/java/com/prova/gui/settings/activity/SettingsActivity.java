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

public class SettingsActivity extends AppCompatActivity {

    private EditText mandrino;
    private EditText velocita;
    private EditText lunghezzaX;
    private EditText lunghezzaY;
    private EditText lunghezzaZ;
    private ManageXml manageXml;

    public EditText getMandrino() { return mandrino; }
    public EditText getLunghezzaX() { return lunghezzaX; }
    public EditText getLunghezzaY() { return lunghezzaY; }
    public EditText getLunghezzaZ() { return lunghezzaZ; }
    public EditText getVelocita() { return velocita; }
    public ManageXml getManageXml() { return manageXml; }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Button salva = findViewById(R.id.salva);
        mandrino = findViewById(R.id.mandrino);
        velocita = findViewById(R.id.velocita);
        lunghezzaX = findViewById(R.id.lunghezzaX);
        lunghezzaY = findViewById(R.id.lunghezzaY);
        lunghezzaZ = findViewById(R.id.lunghezzaZ);
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
        mandrino.setText(Integer.toString(manageXml.getDiametro()));
        velocita.setText(Integer.toString(manageXml.getVelocita()));
        lunghezzaX.setText(Integer.toString(manageXml.getLunghezze().get(0)));
        lunghezzaY.setText(Integer.toString(manageXml.getLunghezze().get(1)));
        lunghezzaZ.setText(Integer.toString(manageXml.getLunghezze().get(2)));
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
